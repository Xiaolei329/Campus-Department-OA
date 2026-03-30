<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的待办审批</span>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="操作记录ID" width="120" />
        <el-table-column prop="instanceId" label="审批单号" width="120" />
        <el-table-column prop="action" label="处理状态">
           <template #default="scope">
             <el-tag type="warning" v-if="scope.row.action === 0">待审批</el-tag>
             <el-tag type="success" v-else>已处理</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请事由详述" show-overflow-tooltip />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleApprove(scope.row)">同意</el-button>
            <el-button type="danger" size="small" @click="handleReject(scope.row)">驳回</el-button>
            <el-button type="info" size="small" @click="viewHistory(scope.row.instanceId)">查看审批记录</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="historyDialog" title="审批节点记录" width="600px">
      <el-timeline>
        <el-timeline-item v-for="(node, index) in historyList" :key="index" :timestamp="node.handleTime || '等待处理中'" :type="node.status === 2 ? 'success' : 'primary'">
          <h4>审批节点：{{ node.id }}</h4>
          <p>审批结果: 
             <el-tag v-if="node.action === 1" type="success">已同意</el-tag>
             <el-tag v-else-if="node.action === 2" type="danger">已驳回</el-tag>
             <el-tag v-else type="info">待处理</el-tag>
          </p>
          <p v-if="node.comment">审批意见: {{ node.comment }}</p>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const tableData = ref([])
const loading = ref(false)

const getTodoList = () => {
  loading.value = true
  request.get('/api/workflow/todo').then(res => {
    // MyBatis Plus 分页
    tableData.value = res.data.records 
  }).finally(() => {
    loading.value = false
  })
}

const handleApprove = (row) => {
  request.post('/api/workflow/todo/approve', { nodeId: row.id, action: 1 }).then(res => {
    ElMessage.success('审批已同意！')
    getTodoList() // 刷新同步
  })
}

const handleReject = (row) => {
  request.post('/api/workflow/todo/approve', { nodeId: row.id, action: 2 }).then(res => {
    ElMessage.warning('审批已驳回！')
    getTodoList() 
  })
}

const historyDialog = ref(false)
const historyList = ref([])

const viewHistory = (instanceId) => {
  request.get('/api/workflow/history', { params: { instanceId } }).then(res => {
    historyList.value = res.data
    historyDialog.value = true
  })
}

onMounted(() => {
  getTodoList()
})
</script>

<style scoped>
.app-container { padding: 10px; }
</style>
