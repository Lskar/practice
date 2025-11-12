<template>
  <div class="admin-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <el-icon :size="32"><Platform /></el-icon>
          <span>管理员控制台</span>
        </div>
        <div class="user-info">
          <el-dropdown>
            <div class="user-avatar-container">
              <el-avatar :size="40" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 侧边栏 -->
      <el-aside width="250px" class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          <el-menu-item index="consultations">
            <el-icon><DocumentCopy /></el-icon>
            <span>咨询记录</span>
          </el-menu-item>
          <el-menu-item index="users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="statistics">
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区 -->
      <el-main class="content">
        <!-- 数据概览 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard">
          <el-row :gutter="20" class="stats-row">
            <el-col :xs="24" :sm="12" :lg="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-content">
                  <el-icon :size="50" color="#409eff"><User /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ mockData.totalUsers }}</div>
                    <div class="stat-label">总用户数</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-content">
                  <el-icon :size="50" color="#67c23a"><ChatDotRound /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ mockData.totalConsultations }}</div>
                    <div class="stat-label">总咨询数</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-content">
                  <el-icon :size="50" color="#e6a23c"><Calendar /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ mockData.todayConsultations }}</div>
                    <div class="stat-label">今日咨询</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :lg="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-content">
                  <el-icon :size="50" color="#f56c6c"><TrendCharts /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ mockData.activeUsers }}</div>
                    <div class="stat-label">活跃用户</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 图表区域 -->
          <el-row :gutter="20" class="charts-row">
            <el-col :xs="24" :lg="12">
              <el-card shadow="never">
                <template #header>
                  <span>咨询趋势</span>
                </template>
                <div class="chart-placeholder">
                  <el-icon :size="100" color="#dcdfe6"><TrendCharts /></el-icon>
                  <p>图表区域（可集成 ECharts）</p>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :lg="12">
              <el-card shadow="never">
                <template #header>
                  <span>热门车型</span>
                </template>
                <div class="chart-placeholder">
                  <el-icon :size="100" color="#dcdfe6"><PieChart /></el-icon>
                  <p>图表区域（可集成 ECharts）</p>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 咨询记录 -->
        <div v-if="activeMenu === 'consultations'" class="consultations">
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <span>所有咨询记录</span>
                <el-button type="primary" :icon="Download">
                  导出数据
                </el-button>
              </div>
            </template>

            <el-table
              :data="consultationStore.consultations"
              style="width: 100%"
              stripe
            >
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="标题" min-width="150" />
              <el-table-column prop="carType" label="车型" width="100" />
              <el-table-column prop="budget" label="预算" width="120" />
              <el-table-column prop="aiModel" label="AI模型" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.aiModel === 'qwen' ? 'primary' : 'success'">
                    {{ row.aiModel === 'qwen' ? '通义千问' : '智谱AI' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="咨询时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    text
                    size="small"
                    @click="handleViewConsultation(row)"
                  >
                    查看
                  </el-button>
                  <el-button
                    type="danger"
                    text
                    size="small"
                    @click="handleDeleteConsultation(row.id)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeMenu === 'users'" class="users">
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <span>用户管理</span>
                <el-input
                  v-model="userSearch"
                  placeholder="搜索用户..."
                  :prefix-icon="Search"
                  style="width: 300px"
                  clearable
                />
              </div>
            </template>

            <el-table :data="mockUsers" style="width: 100%" stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="name" label="姓名" width="120" />
              <el-table-column prop="phone" label="手机号" width="130" />
              <el-table-column prop="email" label="邮箱" min-width="180" />
              <el-table-column prop="points" label="积分" width="80" />
              <el-table-column prop="consultCount" label="咨询次数" width="100" />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
                    {{ row.status === 'active' ? '正常' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="{ row }">
                  <el-button
                    type="primary"
                    text
                    size="small"
                    @click="handleEditUser(row)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    :type="row.status === 'active' ? 'warning' : 'success'"
                    text
                    size="small"
                    @click="handleToggleUserStatus(row)"
                  >
                    {{ row.status === 'active' ? '禁用' : '启用' }}
                  </el-button>
                  <el-button
                    type="danger"
                    text
                    size="small"
                    @click="handleDeleteUser(row.id)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>

        <!-- 数据统计 -->
        <div v-if="activeMenu === 'statistics'" class="statistics">
          <el-card shadow="never">
            <template #header>
              <span>数据统计分析</span>
            </template>

            <el-tabs v-model="statsTab">
              <el-tab-pane label="热门话题" name="topics">
                <el-table :data="mockTopics" style="width: 100%">
                  <el-table-column prop="topic" label="话题" />
                  <el-table-column prop="count" label="咨询次数" width="120" />
                  <el-table-column label="占比" width="200">
                    <template #default="{ row }">
                      <el-progress :percentage="row.percentage" />
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>

              <el-tab-pane label="热门车型" name="cars">
                <el-table :data="mockCarTypes" style="width: 100%">
                  <el-table-column prop="carType" label="车型" />
                  <el-table-column prop="count" label="咨询次数" width="120" />
                  <el-table-column label="占比" width="200">
                    <template #default="{ row }">
                      <el-progress :percentage="row.percentage" :color="row.color" />
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>

              <el-tab-pane label="预算分布" name="budget">
                <el-table :data="mockBudgets" style="width: 100%">
                  <el-table-column prop="range" label="预算范围" />
                  <el-table-column prop="count" label="用户数" width="120" />
                  <el-table-column label="占比" width="200">
                    <template #default="{ row }">
                      <el-progress :percentage="row.percentage" :color="row.color" />
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useConsultationStore } from '@/stores/consultation'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Platform,
  UserFilled,
  ArrowDown,
  SwitchButton,
  DataBoard,
  DocumentCopy,
  User,
  TrendCharts,
  ChatDotRound,
  Calendar,
  PieChart,
  Download,
  Search
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const consultationStore = useConsultationStore()

const activeMenu = ref('dashboard')
const userSearch = ref('')
const statsTab = ref('topics')

const mockData = reactive({
  totalUsers: 1286,
  totalConsultations: 3542,
  todayConsultations: 127,
  activeUsers: 856
})

const mockUsers = ref([
  {
    id: 1,
    username: 'user001',
    name: '张三',
    phone: '13800138000',
    email: 'zhangsan@example.com',
    points: 150,
    consultCount: 8,
    status: 'active'
  },
  {
    id: 2,
    username: 'user002',
    name: '李四',
    phone: '13800138001',
    email: 'lisi@example.com',
    points: 200,
    consultCount: 12,
    status: 'active'
  }
])

const mockTopics = ref([
  { topic: '家用SUV推荐', count: 568, percentage: 45 },
  { topic: '新能源车型对比', count: 432, percentage: 34 },
  { topic: '预算20万左右轿车', count: 265, percentage: 21 }
])

const mockCarTypes = ref([
  { carType: 'SUV', count: 1250, percentage: 42, color: '#409eff' },
  { carType: '轿车', count: 980, percentage: 33, color: '#67c23a' },
  { carType: 'MPV', count: 456, percentage: 15, color: '#e6a23c' },
  { carType: '其他', count: 296, percentage: 10, color: '#909399' }
])

const mockBudgets = ref([
  { range: '10万以下', count: 342, percentage: 18, color: '#909399' },
  { range: '10-20万', count: 756, percentage: 40, color: '#67c23a' },
  { range: '20-30万', count: 523, percentage: 28, color: '#409eff' },
  { range: '30万以上', count: 265, percentage: 14, color: '#f56c6c' }
])

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const handleViewConsultation = (row) => {
  ElMessage.info('查看咨询详情：' + row.title)
}

const handleDeleteConsultation = (id) => {
  ElMessageBox.confirm('确定要删除这条咨询记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功！')
  }).catch(() => {})
}

const handleEditUser = (row) => {
  ElMessage.info('编辑用户：' + row.username)
}

const handleToggleUserStatus = (row) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    row.status = row.status === 'active' ? 'disabled' : 'active'
    ElMessage.success(`${action}成功！`)
  }).catch(() => {})
}

const handleDeleteUser = (id) => {
  ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    ElMessage.success('删除成功！')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0 24px;
  height: 64px;
  line-height: 64px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.user-avatar-container {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.user-avatar-container:hover {
  background: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #333;
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.sidebar {
  background: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.menu {
  border: none;
  padding: 12px;
}

.menu .el-menu-item {
  border-radius: 8px;
  margin-bottom: 8px;
}

.content {
  padding: 24px;
  overflow-y: auto;
}

.dashboard,
.consultations,
.users,
.statistics {
  max-width: 1400px;
  margin: 0 auto;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.charts-row {
  margin-top: 24px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    display: none;
  }

  .content {
    padding: 12px;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .card-header .el-input {
    width: 100% !important;
  }
}
</style>
