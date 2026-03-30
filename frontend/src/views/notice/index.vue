<template>
  <div class="app-container">
    <el-card>
      <template #header>
        <div class="card-header" style="display:flex; justify-content:space-between; align-items:center;">
          <div style="display:flex; gap:10px;">
            <el-input v-model="queryParams.title" placeholder="请输入公告标题" style="width: 200px" clearable @blur="getList" @clear="getList"/>
            <el-button type="primary" icon="Search" @click="getList">搜索</el-button>
          </div>
          <el-button v-if="isAdminOrManager" type="primary" icon="Plus" @click="handleAdd">发布公告</el-button>
        </div>
      </template>

      <!-- 列表 -->
      <el-table v-loading="loading" :data="noticeList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'danger' : 'warning'">
              {{ scope.row.type === 1 ? '全员公告' : '部门公告' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布人" width="120" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="info" link @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="isAdminOrManager" type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="isAdminOrManager" type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 查看详情弹窗 -->
    <el-dialog :title="viewForm.title" v-model="viewVisible" width="600px" append-to-body>
      <div style="line-height: 1.6; font-size: 15px; color: #333; white-space: pre-wrap;">
        {{ viewForm.content }}
      </div>
      <div style="margin-top: 30px; text-align: right; color: #999; font-size: 13px;">
        发布人：{{ viewForm.publisherName }} &nbsp;&nbsp; 操作时间：{{ viewForm.updateTime || viewForm.createTime }}
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="viewVisible = false">确认收到</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 编辑/添加弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option label="全员公告" :value="1" />
            <el-option label="部门公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">直接发布</el-radio>
            <el-radio :label="0">存为草稿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入公告正文内容..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import useUserStore from '@/store/user'

const userStore = useUserStore()
const isAdminOrManager = computed(() => {
  const username = userStore.userInfo?.username
  return username === 'admin' || username === 'manager'
})

const loading = ref(false)
const noticeList = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  title: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({
  id: null,
  title: '',
  type: 1,
  status: 1,
  content: ''
})

const viewVisible = ref(false)
const viewForm = ref({})

const rules = {
  title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '内容不能为空', trigger: 'blur' }]
}

const getList = () => {
  loading.value = true
  request.get('/api/notice/list', { params: queryParams.value }).then(res => {
    noticeList.value = res.data.records
    total.value = res.data.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

const resetForm = () => {
  form.value = { id: null, title: '', type: 1, status: 1, content: '' }
  if (formRef.value) formRef.value.resetFields()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '发布公司公告'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  form.value = { ...row }
  dialogTitle.value = '修改公司公告'
  dialogVisible.value = true
}

const handleView = (row) => {
  viewForm.value = { ...row }
  viewVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('是否确认删除该公告?', '警告', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    .then(() => {
      return request.delete('/api/notice/delete/' + row.id)
    })
    .then(() => {
      ElMessage.success('删除成功')
      getList()
    })
}

const submitForm = () => {
  formRef.value.validate(valid => {
    if (valid) {
      if (form.value.id) {
        request.put('/api/notice/update', form.value).then(res => {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          getList()
        })
      } else {
        request.post('/api/notice/add', form.value).then(res => {
          ElMessage.success('发布成功')
          dialogVisible.value = false
          getList()
        })
      }
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.app-container {
  padding: 10px;
}
</style>
