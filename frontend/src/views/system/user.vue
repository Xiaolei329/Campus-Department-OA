<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
          <span>组织与用户管理</span>
          <el-button type="primary" @click="openAddDialog">+ 添加用户</el-button>
        </div>
      </template>
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="用户ID" width="80" />
        <el-table-column prop="username" label="登录账号" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 'admin'" type="danger">管理员</el-tag>
            <el-tag v-else-if="scope.row.role === 'manager'" type="warning">主管</el-tag>
            <el-tag v-else type="info">员工</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="status" label="状态" width="80">
           <template #default="scope">
             <el-tag type="success" v-if="scope.row.status === 1">正常</el-tag>
             <el-tag type="danger" v-else>禁用</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑配置</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" title="编辑用户信息" width="450px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" style="width: 100%">
            <el-option label="超级管理员" value="admin" />
            <el-option label="部门主管" value="manager" />
            <el-option label="普通员工" value="staff" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户状态">
          <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="submitLoading">保存修改</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增用户对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加新用户" width="480px">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="90px">
        <el-form-item label="登录账号" prop="username">
          <el-input v-model="addForm.username" placeholder="如：zhangsan（英文字母，用于登录）" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="addForm.realName" placeholder="如：张三" />
        </el-form-item>
        <el-form-item label="初始密码">
          <el-input v-model="addForm.password" placeholder="留空则默认为 123456" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="addForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="部门主管" value="manager" />
            <el-option label="普通员工" value="staff" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addForm.phone" placeholder="选填" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="addForm.email" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAdd" :loading="addLoading">确认创建</el-button>
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
const editForm = ref({ id: null, realName: '', status: 1, role: 'staff' })

// 新增用户相关
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref(null)
const addForm = ref({
  username: '',
  realName: '',
  password: '',
  role: 'staff',
  phone: '',
  email: ''
})

const addRules = {
  username: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const getUserList = () => {
  loading.value = true
  request.get('/api/user/list').then(res => {
    tableData.value = res.data.records
  }).finally(() => {
    loading.value = false
  })
}

const handleEdit = (row) => {
  editForm.value = { ...row }
  dialogVisible.value = true
}

const submitEdit = () => {
  submitLoading.value = true
  request.post('/api/user/update', editForm.value).then(res => {
    ElMessage.success('配置已更新生效')
    dialogVisible.value = false
    getUserList()
  }).finally(() => {
    submitLoading.value = false
  })
}

const openAddDialog = () => {
  addForm.value = { username: '', realName: '', password: '', role: 'staff', phone: '', email: '' }
  addDialogVisible.value = true
}

const submitAdd = () => {
  addFormRef.value.validate((valid) => {
    if (valid) {
      addLoading.value = true
      request.post('/api/user/add', addForm.value).then(res => {
        ElMessage.success(res.data || '用户创建成功')
        addDialogVisible.value = false
        getUserList()
      }).finally(() => {
        addLoading.value = false
      })
    }
  })
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.app-container { padding: 10px; }
</style>
