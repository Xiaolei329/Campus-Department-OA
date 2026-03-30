<template>
  <el-container class="app-wrapper">
    <!-- 左侧菜单 -->
    <el-aside width="240px" class="aside-container">
      <div class="logo">
        <span class="logo-text text-gradient">OA System</span>
      </div>
      <el-menu
        active-text-color="#ffffff"
        background-color="transparent"
        text-color="#515154"
        :default-active="route.path"
        router
      >
        <template v-for="item in routes" :key="item.path">
          <template v-if="!item.hidden && item.children && checkPermission(item)">
            <el-sub-menu :index="item.path">
              <template #title>
                <el-icon><component :is="item.meta?.icon || 'Menu'"/></el-icon>
                <span>{{ item.meta?.title || item.name }}</span>
              </template>
              <el-menu-item 
                v-for="child in item.children" 
                :key="child.path" 
                :index="(item.path === '/' ? '' : item.path) + '/' + child.path"
              >
                <el-icon><component :is="child.meta?.icon || 'Document'"/></el-icon>
                <template #title>{{ child.meta?.title }}</template>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </template>
      </el-menu>
    </el-aside>

    <!-- 右侧主体 -->
    <el-container class="main-container">
      <!-- 顶部 Header -->
      <el-header class="navbar">
        <div class="left-menu">
          <span class="breadcrumb">{{ route.meta.title || '首页' }}</span>
        </div>
        <div class="right-menu">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link" style="cursor: pointer; display: flex; align-items: center">
              <el-avatar size="small" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              <span style="margin-left: 8px">{{ userInfo.realName || userInfo.username }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容展示区 -->
      <el-main>
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { constantRoutes } from '@/router'
import useUserStore from '@/store/user'
import { ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const routes = computed(() => constantRoutes)
const userInfo = computed(() => userStore.userInfo)

const checkPermission = (item) => {
  if (item.meta && item.meta.roles) {
    // 只有 admin 才能看到受保护的路由
    if (!userInfo.value || userInfo.value.username !== 'admin') {
      return false
    }
  }
  return true
}

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定注销并退出系统吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout().then(() => {
        router.push('/login')
      })
    })
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.app-wrapper {
  height: 100vh;
  width: 100%;
}
.aside-container {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  height: 100%;
  border-right: 1px solid rgba(0, 0, 0, 0.06);
  z-index: 10;
}
.logo {
  height: 64px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.5px;
  color: #1d1d1f;
  background-color: transparent;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}
.el-menu {
  border-right: none;
  background-color: transparent !important;
}
.aside-container .el-menu-item, .aside-container .el-sub-menu__title {
  color: #515154 !important;
  font-weight: 500;
  border-radius: 10px;
  margin: 4px 12px;
  height: 40px;
  line-height: 40px;
}
.aside-container .el-menu-item:hover, .aside-container .el-sub-menu__title:hover {
  background-color: rgba(0, 0, 0, 0.04) !important;
  color: #1d1d1f !important;
}
.aside-container .el-menu-item.is-active {
  background: var(--el-color-primary) !important;
  color: #ffffff !important;
  font-weight: 600;
}
.main-container {
  display: flex;
  flex-direction: column;
}
.navbar {
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  z-index: 9;
}
.breadcrumb {
  font-size: 17px;
  font-weight: 600;
  color: #1d1d1f;
}
.el-main {
  background-color: transparent;
  padding: 0; 
  height: calc(100vh - 64px);
  overflow-y: auto;
}
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
