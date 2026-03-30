import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import useUserStore from '@/store/user'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '', // vite 已配置代理
  timeout: 10000 
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = 'Bearer ' + userStore.token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 与后端 ResultWrapper 对应 (code, msg, data)
    if (res.code === 200) {
      return res
    } else {
      ElMessage({
        message: res.msg || '系统错误',
        type: 'error',
        duration: 5 * 1000
      })
      // Token 过期或无权限
      if (res.code === 401 || res.code === 403) {
        const userStore = useUserStore()
        userStore.logout().then(() => {
          router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
        })
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    }
  },
  error => {
    ElMessage({
      message: error.response?.data?.msg || error.message || '系统错误',
      type: 'error',
      duration: 5 * 1000
    })
    // 拦截后端的 HTTP 原生 401/403 级别抛错（Token失效等）
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      const userStore = useUserStore()
      userStore.logout().then(() => {
        router.push(`/login?redirect=${router.currentRoute.value.fullPath}`)
      })
    }
    return Promise.reject(error)
  }
)

export default service
