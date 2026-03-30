<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
          <span>请假审批记录</span>
          <div>
            <el-input v-model="queryParams.reason" placeholder="输入事由模糊搜索" style="width: 250px; margin-right: 10px;" clearable @keyup.enter="handleSearch" />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button type="success" @click="handleExport">导出 Excel</el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="请假单号" width="100" />
        <el-table-column prop="userName" label="发起人" width="120" />
        <el-table-column prop="leaveType" label="请假类别" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.leaveType === 1" type="success">年假</el-tag>
            <el-tag v-else-if="scope.row.leaveType === 2" type="primary">事假</el-tag>
            <el-tag v-else-if="scope.row.leaveType === 3" type="warning">病假</el-tag>
            <el-tag v-else-if="scope.row.leaveType === 4" type="info">调休</el-tag>
            <el-tag v-else>{{ scope.row.leaveType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="申请起始" width="160" />
        <el-table-column prop="endTime" label="申请截止" width="160" />
        <el-table-column prop="reason" label="申请事由详述" min-width="200" />
        <el-table-column prop="status" label="请假状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning" effect="dark">审批中</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success" effect="dark">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="danger" effect="dark">已驳回</el-tag>
            <el-tag v-else-if="scope.row.status === 3" type="info" effect="dark">已撤销</el-tag>
            <el-tag v-else type="info">未知状态</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 20px; text-align: right;">
        <el-pagination 
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="queryParams.pageSize"
          v-model:current-page="queryParams.pageNum"
          @current-change="getRecordList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  reason: ''
})

const getRecordList = () => {
  loading.value = true
  request.get('/api/workflow/leave/list', { params: queryParams.value }).then(res => {
    tableData.value = res.data.records
    total.value = res.data.total
  }).finally(() => {
    loading.value = false
  })
}

const handleSearch = () => {
  queryParams.value.pageNum = 1
  getRecordList()
}

/**
 * 导出 Excel —— 通过 axios 发起 blob 请求，触发浏览器真实文件下载
 */
const handleExport = () => {
  ElMessage.info('正在生成 Excel 文件，请稍候...')
  const token = localStorage.getItem('token')
  axios({
    url: '/api/workflow/leave/export',
    method: 'GET',
    responseType: 'blob',
    headers: { 'Authorization': 'Bearer ' + token }
  }).then(res => {
    // 创建一个临时下载链接
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = '请假记录导出.xlsx'
    link.click()
    URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功！')
  }).catch(() => {
    ElMessage.error('导出失败，请重试')
  })
}

onMounted(() => {
  getRecordList()
})
</script>

<style scoped>
.app-container { padding: 10px; }
</style>
