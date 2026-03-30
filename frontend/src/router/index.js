import { createRouter, createWebHistory } from 'vue-router'

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页工作台', icon: 'House' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/system/profile.vue'),
        meta: { title: '个人中心', icon: 'User' },
        hidden: true // 不在侧边栏显示，仅允许通过右上角下拉菜单进入
      }
    ]
  },
  {
    path: '/workflow',
    component: () => import('@/layout/index.vue'),
    name: 'Workflow',
    meta: { title: '工作流程', icon: 'Stamp' },
    children: [
      {
        path: 'apply',
        name: 'Apply',
        component: () => import('@/views/workflow/apply.vue'),
        meta: { title: '我的发起', icon: 'DocumentAdd' }
      },
      {
        path: 'todo',
        name: 'Todo',
        component: () => import('@/views/workflow/todo.vue'),
        meta: { title: '我的待办', icon: 'Checked' }
      },
      {
        path: 'leaveRecord',
        name: 'LeaveRecord',
        component: () => import('@/views/workflow/leaveRecord.vue'),
        meta: { title: '请假记录', icon: 'DocumentCopy' }
      }
    ]
  },
  {
    path: '/collab',
    component: () => import('@/layout/index.vue'),
    name: 'Collab',
    meta: { title: '协同办公', icon: 'Connection' },
    children: [
      {
        path: 'task',
        name: 'Task',
        component: () => import('@/views/collab/task.vue'),
        meta: { title: '任务管理', icon: 'List' }
      },
      {
        path: 'doc',
        name: 'Doc',
        component: () => import('@/views/collab/doc.vue'),
        meta: { title: '文档管理', icon: 'Folder' }
      },
      {
        path: 'meeting',
        name: 'Meeting',
        component: () => import('@/views/collab/meeting.vue'),
        meta: { title: '会议管理', icon: 'Calendar' }
      }
    ]
  },
  {
    path: '/notice',
    component: () => import('@/layout/index.vue'),
    name: 'NoticeRoot',
    meta: { title: '系统通告', icon: 'Bell' },
    children: [
      {
        path: 'index',
        name: 'Notice',
        component: () => import('@/views/notice/index.vue'),
        meta: { title: '公告管理', icon: 'ChatLineSquare' }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    name: 'System',
    meta: { title: '系统设置', icon: 'Setting', roles: ['admin'] },
    children: [
      {
        path: 'user',
        name: 'SysUser',
        component: () => import('@/views/system/user.vue'),
        meta: { title: '用户管理', icon: 'User' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes
})

export default router
