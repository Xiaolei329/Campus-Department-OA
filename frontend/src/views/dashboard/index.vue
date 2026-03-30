<template>
  <div class="dashboard-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1 class="text-gradient">欢迎回来，{{ userInfo.realName || userInfo.username || 'Admin' }}</h1>
        <p>今天是 {{ currentDate }}。一览您的专属办公门户，掌控全局数据。</p>
      </div>
    </div>

    <!-- 数据卡片 -->
    <el-row :gutter="24" class="stat-section">
      <el-col :span="6">
        <el-card shadow="never" class="premium-stat-card card-danger">
          <div class="stat-content">
            <div class="icon-wrapper"><el-icon><Warning /></el-icon></div>
            <div class="stat-info">
              <div class="stat-title">我的待办</div>
              <div class="stat-value">{{ stats.todoCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="premium-stat-card card-success">
          <div class="stat-content">
            <div class="icon-wrapper"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-title">已办结审批</div>
              <div class="stat-value">{{ stats.doneCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="premium-stat-card card-primary">
          <div class="stat-content">
            <div class="icon-wrapper"><el-icon><Document /></el-icon></div>
            <div class="stat-info">
              <div class="stat-title">名下总任务</div>
              <div class="stat-value">{{ stats.taskCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="premium-stat-card card-warning">
          <div class="stat-content">
            <div class="icon-wrapper"><el-icon><Loading /></el-icon></div>
            <div class="stat-info">
              <div class="stat-title">处理中任务</div>
              <div class="stat-value">{{ stats.activeTaskCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 下半部分动态视图 -->
    <el-row :gutter="24" class="activity-section">
      <el-col :span="16">
        <el-card shadow="never" class="timeline-card">
          <template #header>
            <div class="card-header-styled">
              <span class="title">近期业务动态</span>
              <el-tag effect="light" round>实时监控</el-tag>
            </div>
          </template>
          <div class="timeline-wrapper">
            <el-timeline v-if="activities.length > 0">
              <el-timeline-item v-for="(act, index) in activities" :key="index" :timestamp="act.createTime" placement="top" color="#6366f1">
                <div class="activity-box">
                  <h4>{{ act.title }}</h4>
                  <p>{{ act.content || '任务进行中...' }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="平稳运行，暂无动态" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="never" class="quick-links-card">
          <template #header>
            <div class="card-header-styled">
              <span class="title">常用功能</span>
            </div>
          </template>
          <div class="grid-links">
            <div class="grid-link-item" @click="router.push('/workflow/apply')">
              <div class="link-icon bg-blue"><el-icon><Position /></el-icon></div>
              <span>发起审批</span>
            </div>
            <div class="grid-link-item" @click="router.push('/collab/task')">
              <div class="link-icon bg-pink"><el-icon><Odometer /></el-icon></div>
              <span>任务下发</span>
            </div>
            <div class="grid-link-item" @click="router.push('/system/user')">
              <div class="link-icon bg-purple"><el-icon><User /></el-icon></div>
              <span>团队架构</span>
            </div>
            <div class="grid-link-item" @click="router.push('/collab/doc')">
              <div class="link-icon bg-orange"><el-icon><FolderOpened /></el-icon></div>
              <span>知识库</span>
            </div>
          </div>
        </el-card>

        <!-- 系统公告面版 -->
        <el-card shadow="never" class="notice-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header-styled">
              <span class="title">最新公告</span>
              <el-button type="primary" link @click="router.push('/notice/index')">查看更多</el-button>
            </div>
          </template>
          <div class="notice-list-wrapper">
            <div 
              class="notice-item" 
              v-for="notice in noticeList" 
              :key="notice.id"
              @click="handleViewNotice(notice)"
            >
              <el-tag size="small" :type="notice.type === 1 ? 'danger' : 'warning'" style="margin-right: 8px; flex-shrink: 0;">
                {{ notice.type === 1 ? '全员' : '部门' }}
              </el-tag>
              <span class="notice-title-text">{{ notice.title }}</span>
              <span class="notice-date-text">{{ notice.createTime ? notice.createTime.substring(5, 10) : '' }}</span>
            </div>
            <el-empty v-if="noticeList.length === 0" description="暂无最新公告" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- ECharts 数据可视化 -->
    <el-row :gutter="24" style="margin-top: 30px;">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header-styled"><span class="title">请假类型分布</span></div>
          </template>
          <div ref="pieChartRef" style="width: 100%; height: 320px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header-styled"><span class="title">任务完成情况</span></div>
          </template>
          <div ref="barChartRef" style="width: 100%; height: 320px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查看公告详情弹窗 -->
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import useUserStore from '@/store/user'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})

const activities = ref([])
const stats = ref({ todoCount: 0, doneCount: 0, taskCount: 0, activeTaskCount: 0 })

const noticeList = ref([])
const viewVisible = ref(false)
const viewForm = ref({})

// ECharts DOM 引用
const pieChartRef = ref(null)
const barChartRef = ref(null)

const currentDate = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })

const getActivities = () => {
  request.get('/api/task/list', { params: { pageNum: 1, pageSize: 4 } }).then(res => {
    activities.value = res.data.records
  })
}

const getStats = () => {
  request.get('/api/dashboard/stats').then(res => {
    if(res.code === 200) stats.value = res.data
  })
}

const getNotices = () => {
  request.get('/api/notice/list', { params: { pageNum: 1, pageSize: 6 } }).then(res => {
    noticeList.value = res.data.records || []
  })
}

const handleViewNotice = (notice) => {
  viewForm.value = { ...notice }
  viewVisible.value = true
}

/**
 * 获取图表数据并用 ECharts 渲染
 */
const getChartData = () => {
  request.get('/api/dashboard/charts').then(res => {
    if (res.code === 200) {
      nextTick(() => {
        renderPieChart(res.data.leavePieData || [])
        renderBarChart(res.data.taskBarCategories || [], res.data.taskBarValues || [])
      })
    }
  })
}

/** 渲染饼图：请假类型分布 */
const renderPieChart = (data) => {
  if (!pieChartRef.value) return
  const chart = echarts.init(pieChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c}次 ({d}%)' },
    legend: { bottom: '0%', left: 'center', textStyle: { color: '#86868b' } },
    color: ['#007aff', '#ff9500', '#ff3b30', '#34c759'],
    series: [{
      name: '请假类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%', color: '#1d1d1f' },
      data: data.length > 0 ? data : [{ name: '暂无数据', value: 0 }]
    }]
  })
  // 窗口大小变化时自适应
  window.addEventListener('resize', () => chart.resize())
}

/** 渲染柱状图：任务完成情况 */
const renderBarChart = (categories, values) => {
  if (!barChartRef.value) return
  const chart = echarts.init(barChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: { color: '#86868b' },
      axisLine: { lineStyle: { color: 'rgba(0,0,0,0.04)' } }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: { color: '#86868b' },
      splitLine: { lineStyle: { color: 'rgba(0,0,0,0.04)' } }
    },
    series: [{
      name: '任务数量',
      type: 'bar',
      barWidth: '30%',
      data: values,
      itemStyle: {
        borderRadius: [6, 6, 0, 0],
        color: '#007aff'
      }
    }]
  })
  window.addEventListener('resize', () => chart.resize())
}

onMounted(() => {
  getActivities()
  getStats()
  getNotices()
  getChartData()
})
</script>

<style scoped>
.dashboard-container {
  animation: fadeIn 0.6s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 欢迎横幅 */
.welcome-banner {
  padding: 40px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0,0,0,0.04);
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
}
.banner-content {
  position: relative;
  z-index: 2;
}
.welcome-banner h1 {
  margin: 0 0 10px 0;
  font-size: 36px;
  font-weight: 700;
  letter-spacing: -1px;
  color: #1d1d1f;
}
.welcome-banner p {
  margin: 0;
  color: #86868b;
  font-size: 16px;
  font-weight: 500;
}

/* 保证跨浏览器的文字可见性，使用实体颜色 */
.text-gradient-safe {
  color: #4f46e5;
  font-weight: 800;
}

/* 数据卡片 */
.premium-stat-card {
  border-radius: 16px !important;
  color: #1d1d1f;
  position: relative;
  overflow: hidden;
  border: 1px solid rgba(0,0,0,0.04) !important;
  background: rgba(255, 255, 255, 0.9);
}
.stat-content {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 2;
}
.icon-wrapper {
  width: 54px; height: 54px;
  border-radius: 14px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  margin-right: 20px;
}
.stat-info {
  flex: 1;
}
.stat-title {
  font-size: 14px;
  color: #86868b;
  font-weight: 500;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  color: #1d1d1f;
}

.card-danger .icon-wrapper { color: #ff3b30; background: rgba(255, 59, 48, 0.1); }
.card-success .icon-wrapper { color: #34c759; background: rgba(52, 199, 89, 0.1); }
.card-primary .icon-wrapper { color: #007aff; background: rgba(0, 122, 255, 0.1); }
.card-warning .icon-wrapper { color: #ff9500; background: rgba(255, 149, 0, 0.1); }

/* 头部统一样式 */
.card-header-styled {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header-styled .title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

/* 动态区块 */
.activity-section {
  margin-top: 30px;
}
.timeline-wrapper {
  padding: 10px 0;
}
.activity-box {
  background: #f8fafc;
  padding: 15px 20px;
  border-radius: 12px;
  border-left: 4px solid #6366f1;
}
.activity-box h4 {
  margin: 0 0 5px 0;
  color: #334155;
}
.activity-box p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

/* 极速入口 */
.grid-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}
.grid-link-item {
  background: transparent;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.2s;
  border: none;
}
.grid-link-item:hover {
  background: rgba(0,0,0,0.03);
}
.link-icon {
  width: 44px; height: 44px;
  border-radius: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
  margin-bottom: 8px;
  background: #f5f5f7;
}
.bg-blue { color: #007aff; }
.bg-pink { color: #ff2d55; }
.bg-purple { color: #af52de; }
.bg-orange { color: #ff9500; }
.grid-link-item span {
  font-size: 13px;
  font-weight: 500;
  color: #1d1d1f;
}

/* 最新公告区块 */
.notice-list-wrapper {
  padding: 5px 0;
}
.notice-item {
  display: flex;
  align-items: center;
  padding: 12px 10px;
  border-bottom: 1px dashed #f1f5f9;
  cursor: pointer;
  transition: background-color 0.3s;
  border-radius: 6px;
}
.notice-item:hover {
  background-color: #f8fafc;
}
.notice-item:last-child {
  border-bottom: none;
}
.notice-title-text {
  flex: 1;
  font-size: 14px;
  color: #334155;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 15px;
}
.notice-date-text {
  font-size: 13px;
  color: #94a3b8;
  flex-shrink: 0;
}
</style>
