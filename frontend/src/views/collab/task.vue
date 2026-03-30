<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
          <span>协同任务管理</span>
          <div>
            <el-button type="success" @click="handleExport">导出 Excel</el-button>
            <el-button type="primary" @click="dialogVisible = true">发布任务</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="任务名称" />
        <el-table-column prop="assigneeName" label="接收人" width="120"/>
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
             <el-tag type="info" v-if="scope.row.status === 0">未开始</el-tag>
             <el-tag type="warning" v-else-if="scope.row.status === 1">进行中</el-tag>
             <el-tag type="success" v-else>已完成</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="任务协同操作" width="180">
          <template #default="scope">
            <el-button v-if="scope.row.status === 0" size="small" type="primary" plain @click="changeStatus(scope.row.id, 1)">开始处理</el-button>
            <el-button v-if="scope.row.status === 1" size="small" type="success" plain @click="changeStatus(scope.row.id, 2)">标记完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布新任务" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="任务名称">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="任务内容">
          <el-input type="textarea" v-model="form.content" />
        </el-form-item>
        <el-form-item label="分派给">
          <el-select v-model="form.assigneeId" placeholder="请选择接收人" style="width: 100%">
             <el-option v-for="u in userList" :key="u.id" :label="u.realName + ' (' + u.username + ')'" :value="u.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitTask" :loading="submitLoading">提交发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const tableData = ref([])
const userList = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ title: '', content: '', assigneeId: null })

const getTaskList = () => {
  loading.value = true
  request.get('/api/task/list').then(res => {
    tableData.value = res.data.records 
  }).finally(() => {
    loading.value = false
  })
}

const getUserList = () => {
  request.get('/api/user/list?pageNum=1&pageSize=100').then(res => {
    userList.value = res.data.records
  })
}

const submitTask = () => {
  submitLoading.value = true
  request.post('/api/task/save', form.value).then(res => {
    ElMessage.success('任务发布成功')
    dialogVisible.value = false
    getTaskList()
  }).finally(() => {
    submitLoading.value = false
  })
}

const changeStatus = (id, newStatus) => {
  request.post('/api/task/updateStatus', { id, status: newStatus }).then(res => {
    ElMessage.success('真实状态变迁写入成功')
    getTaskList()
  })
}

const handleExport = () => {
  ElMessage.info('正在生成 Excel 文件，请稍候...')
  const token = localStorage.getItem('token')
  axios({
    url: '/api/task/export',
    method: 'GET',
    responseType: 'blob',
    headers: { 'Authorization': 'Bearer ' + token }
  }).then(res => {
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '任务派发记录.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功！')
  }).catch(() => {
    ElMessage.error('导出失败，请重试')
  })
}

onMounted(() => {
  getTaskList()
  getUserList()
})
</script>

<style scoped>
.app-container { padding: 10px; }
</style>
