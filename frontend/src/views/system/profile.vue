<template>
  <div class="app-container">
    <el-row :gutter="24">
      <!-- 左侧：头像上传卡片 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>个人头像</span>
          </template>
          <div style="text-align: center;">
            <el-avatar :size="120" :src="avatarUrl" style="margin-bottom: 20px;">
              <span style="font-size: 40px;">{{ (userInfo.realName || userInfo.username || 'U').charAt(0) }}</span>
            </el-avatar>
            <div>
              <el-upload
                action="/api/file/upload"
                :headers="uploadHeaders"
                name="file"
                :show-file-list="false"
                accept="image/*"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <el-button type="primary">上传新头像</el-button>
              </el-upload>
              <p style="color: #909399; font-size: 12px; margin-top: 10px;">支持 JPG、PNG 格式，大小不超过 2MB</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：个人信息编辑卡片 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          <el-form :model="userInfo" label-width="80px" style="max-width: 500px;">
            <el-form-item label="登录账号">
              <el-input :value="userInfo.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="userInfo.realName" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userInfo.phone" />
            </el-form-item>
            <el-form-item label="角色">
              <el-tag v-if="userInfo.role === 'admin'" type="danger">超级管理员</el-tag>
              <el-tag v-else-if="userInfo.role === 'manager'" type="warning">部门主管</el-tag>
              <el-tag v-else>普通员工</el-tag>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import useUserStore from '@/store/user'

const userStore = useUserStore()
const userInfo = ref({})
const saving = ref(false)

// 上传请求头
const uploadHeaders = computed(() => ({
  'Authorization': 'Bearer ' + localStorage.getItem('token')
}))

// 头像 URL
const avatarUrl = computed(() => {
  if (userInfo.value.avatar) {
    return userInfo.value.avatar
  }
  return ''
})

/** 获取当前用户信息 */
const getUserInfo = () => {
  request.get('/api/user/info').then(res => {
    if (res.code === 200) {
      userInfo.value = res.data
      // 同步最新用户信息到全局状态
      userStore.updateUserInfo(res.data)
    }
  })
}

/** 头像上传前校验 */
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

/** 头像上传成功回调 */
const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    const avatarPath = response.data
    // 将新头像路径保存到数据库
    request.post('/api/user/avatar', { avatar: avatarPath }).then(res => {
      if (res.code === 200) {
        userInfo.value.avatar = avatarPath
        // 同步头像到全局，右上角立刻生效
        userStore.updateUserInfo({ avatar: avatarPath })
        ElMessage.success('头像更新成功！')
      }
    })
  } else {
    ElMessage.error('文件上传失败：' + (response.msg || '未知错误'))
  }
}

/** 保存个人信息修改 */
const handleSave = () => {
  saving.value = true
  request.post('/api/user/update', {
    id: userInfo.value.id,
    realName: userInfo.value.realName,
    email: userInfo.value.email,
    phone: userInfo.value.phone
  }).then(res => {
    if (res.code === 200) {
      // 同步姓名等信息到全局状态
      userStore.updateUserInfo({
        realName: userInfo.value.realName,
        email: userInfo.value.email,
        phone: userInfo.value.phone
      })
      ElMessage.success('个人信息保存成功！')
    }
  }).finally(() => {
    saving.value = false
  })
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.app-container { padding: 20px; }
</style>
