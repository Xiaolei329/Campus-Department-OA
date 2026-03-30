<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
          <span>文档共享中心</span>
          <el-upload action="/api/document/upload" :show-file-list="false" :on-success="handleSuccess" :headers="headers">
            <el-button type="primary">上传文档</el-button>
          </el-upload>
        </div>
      </template>
      <el-table :data="docs" style="width: 100%">
        <el-table-column prop="fileName" label="文件名" />
        <el-table-column prop="fileSize" label="文件大小" width="120">
          <template #default="scope">
            {{ formatSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileType" label="类型" width="100">
          <template #default="scope">
            <el-tag size="small" type="info">{{ scope.row.fileType ? scope.row.fileType.toUpperCase() : '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="上传者" width="120" />
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" link @click="handleDownload(scope.row)">下载附件</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import useUserStore from '@/store/user'

const formatSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i]
}

const userStore = useUserStore()
const docs = ref([])
const headers = ref({ Authorization: 'Bearer ' + userStore.token })

const getDocs = () => {
  request.get('/api/document/list').then(res => {
    docs.value = res.data
  })
}

const handleSuccess = (res) => {
  if (res.code === 200) {
    ElMessage.success('文件上传成功')
    getDocs()
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

const handleDownload = (row) => {
  if (!row.id) {
    ElMessage.error('文件信息丢失，无法下载')
    return
  }
  // 必须附加在浏览器层级打开，触发流式文件下载
  window.open('/api/document/download/' + row.id)
}

onMounted(() => {
  getDocs()
})
</script>

<style scoped>
.app-container { padding: 10px; }
</style>
