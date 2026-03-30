<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>发起请假</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width: 600px">
        <el-form-item label="请假类型" prop="leaveType">
          <el-select v-model="form.leaveType" placeholder="请选择请假类型" style="width: 100%">
            <el-option label="年假" :value="1" />
            <el-option label="事假" :value="2" />
            <el-option label="病假" :value="3" />
            <el-option label="调休" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="起止时间" prop="dateRange">
          <el-date-picker
            v-model="form.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="请假事由" prop="reason">
          <el-input v-model="form.reason" type="textarea" rows="4" placeholder="请详细描述请假事由" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitApply" :loading="loading">提交审批</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  leaveType: null,
  dateRange: [],
  reason: ''
})

const rules = {
  leaveType: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
  dateRange: [{ required: true, message: '请选择起止时间', trigger: 'change' }],
  reason: [{ required: true, message: '请填写请假事由', trigger: 'blur' }]
}

const submitApply = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // 构造给后端的数据
      const apiData = {
        leaveType: form.leaveType,
        startTime: form.dateRange[0],
        endTime: form.dateRange[1],
        reason: form.reason
      }
      
      request.post('/api/workflow/leave/apply', apiData).then(res => {
        ElMessage.success('申请提交成功！审批流已发起。')
        resetForm()
      }).finally(() => {
        loading.value = false
      })
    }
  })
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
    form.dateRange = []
  }
}
</script>

<style scoped>
.app-container {
  padding: 10px;
}
</style>
