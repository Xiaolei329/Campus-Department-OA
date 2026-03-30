import router from './router'
import useUserStore from '@/store/user'

const whiteList = ['/login']

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const hasToken = userStore.token
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title + ' - 在线办公系统'
  }

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      next() // 可以通过请求后台接口实时鉴权以及拉取路由表
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})
