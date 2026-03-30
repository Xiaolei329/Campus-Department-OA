import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || {}
  }),

  actions: {
    // 登录动作
    login(loginData) {
      return new Promise((resolve, reject) => {
        request.post('/api/auth/login', loginData).then(response => {
          const { token, user } = response.data
          this.token = token
          this.userInfo = user
          localStorage.setItem('token', token)
          localStorage.setItem('userInfo', JSON.stringify(user))
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 登出动作
    logout() {
      return new Promise((resolve, reject) => {
        // 先清理本地状态，防止一旦后端接口报错（如token已过期导致403）引发死循环无法登出
        const clearLocalState = () => {
          this.token = ''
          this.userInfo = {}
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          resolve()
        }

        request.post('/api/auth/logout').then(() => {
          clearLocalState()
        }).catch(error => {
          // 哪怕后端报错也必须清理前端状态
          clearLocalState()
        })
      })
    },

    /**
     * 局部更新用户信息（如头像、姓名等）
     * 同步到 Pinia 状态 + localStorage，使布局顶栏等所有组件实时响应
     */
    updateUserInfo(partialInfo) {
      this.userInfo = { ...this.userInfo, ...partialInfo }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    }
  }
})

export default useUserStore
