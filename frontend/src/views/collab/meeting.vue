<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
          <span>会议记录表</span>
          <el-button type="primary" @click="dialogVisible = true">发布新会议</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="title" label="会议主题" />
        <el-table-column prop="room" label="会议室" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="participants" label="参会人员" />
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布新会议" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="会议主题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="场地设置">
          <el-select v-model="form.room" style="width:100%">
             <el-option label="A座101科技阶梯室" value="A座101科技阶梯室" />
             <el-option label="B座203执行总裁室" value="B座203执行总裁室" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" value-format="YYYY-MM-DD HH:mm:ss" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" value-format="YYYY-MM-DD HH:mm:ss" type="datetime" style="width:100%" />
        </el-form-item>
        <el-form-item label="通知对象">
           <el-input v-model="form.participants" placeholder="例如：产品线与开发部全体骨干" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitMeeting" :loading="submitLoading">发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const form = ref({ title: '', room: '', startTime: null, endTime: null, participants: '' })

const getMeetingList = () => {
  loading.value = true
  request.get('/api/meeting/list').then(res => {
    tableData.value = res.data
  }).finally(() => {
    loading.value = false
  })
}

const submitMeeting = () => {
  submitLoading.value = true
  request.post('/api/meeting/save', form.value).then(res => {
    ElMessage.success('发布成功')
    dialogVisible.value = false
    getMeetingList()
  }).finally(() => {
    submitLoading.value = false
  })
}

onMounted(() => {
  getMeetingList()
})
</script>

<style scoped>
.app-container { padding: 10px; }
</style>
