<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <el-icon :size="32"><Van /></el-icon>
          <span>智能购车咨询</span>
        </div>
        <div class="user-info">
          <el-dropdown>
            <div class="user-avatar-container">
              <el-avatar :size="40" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.name || userStore.userInfo?.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="showProfileDialog = true">
                  <el-icon><User /></el-icon>
                  个人资料
                </el-dropdown-item>
                <el-dropdown-item @click="showPointsDialog = true">
                  <el-icon><Star /></el-icon>
                  我的积分: {{ userStore.userInfo?.points || 0 }}
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
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
            <el-icon><HomeFilled /></el-icon>
            <span>首页概览</span>
          </el-menu-item>
          <el-menu-item index="consult">
            <el-icon><ChatDotRound /></el-icon>
            <span>新建咨询</span>
          </el-menu-item>
          <el-menu-item index="history">
            <el-icon><Tickets /></el-icon>
            <span>咨询历史</span>
          </el-menu-item>
          <el-menu-item index="profile">
            <el-icon><Setting /></el-icon>
            <span>个人设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区 -->
      <el-main class="content">
        <!-- 首页概览 -->
        <div v-if="activeMenu === 'dashboard'" class="dashboard">
          <!-- 欢迎横幅 -->
          <div class="welcome-banner">
            <div class="banner-content">
              <h1>欢迎回来，{{ userStore.userInfo?.name || userStore.userInfo?.username }}！</h1>
              <p>让AI帮您找到最适合的爱车</p>
              <el-button type="primary" size="large" @click="activeMenu = 'consult'">
                <el-icon><Plus /></el-icon>
                开始新咨询
              </el-button>
            </div>
            <div class="banner-image">
              <el-icon :size="200" color="#667eea"><Van /></el-icon>
            </div>
          </div>

          <!-- 统计卡片 -->
          <el-row :gutter="20" class="stats-row">
            <el-col :xs="24" :sm="12" :md="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-content">
                  <el-icon :size="40" color="#409eff"><ChatDotRound /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ userConsultations.length }}</div>
                    <div class="stat-label">咨询次数</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-content">
                  <el-icon :size="40" color="#67c23a"><Star /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ userStore.userInfo?.points || 0 }}</div>
                    <div class="stat-label">我的积分</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-content">
                  <el-icon :size="40" color="#e6a23c"><TrendCharts /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ userStore.userInfo?.budget || '未设置' }}</div>
                    <div class="stat-label">购车预算(万)</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <el-card shadow="hover" class="stat-card">
                <div class="stat-content">
                  <el-icon :size="40" color="#f56c6c"><Van /></el-icon>
                  <div class="stat-info">
                    <div class="stat-value">{{ userStore.userInfo?.preferredType || '未设置' }}</div>
                    <div class="stat-label">偏好车型</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 最近咨询 -->
          <el-card shadow="never" class="recent-consultations">
            <template #header>
              <div class="card-header">
                <span>最近咨询</span>
                <el-link type="primary" @click="activeMenu = 'history'">查看全部</el-link>
              </div>
            </template>
            <el-empty v-if="userConsultations.length === 0" description="还没有咨询记录，开始第一次咨询吧！" />
            <el-timeline v-else>
              <el-timeline-item
                v-for="item in userConsultations.slice(0, 5)"
                :key="item.id"
                :timestamp="formatDate(item.createdAt)"
                placement="top"
              >
                <el-card>
                  <h4>{{ item.title || '购车咨询' }}</h4>
                  <p class="consultation-summary">
                    预算: {{ item.budget }} | 车型: {{ item.carType }} | 场景: {{ item.useCase }}
                  </p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </el-card>
        </div>

        <!-- 新建咨询 -->
        <ConsultationForm v-if="activeMenu === 'consult'" @success="handleConsultSuccess" />

        <!-- 咨询历史 -->
        <ConsultationHistory v-if="activeMenu === 'history'" :consultations="userConsultations" />

        <!-- 个人设置 -->
        <ProfileSettings v-if="activeMenu === 'profile'" />
      </el-main>
    </el-container>

    <!-- 个人资料对话框 -->
    <el-dialog v-model="showProfileDialog" title="个人资料" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ userStore.userInfo?.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userStore.userInfo?.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userStore.userInfo?.phone || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userStore.userInfo?.email || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="购车预算">{{ userStore.userInfo?.budget || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="偏好车型">{{ userStore.userInfo?.preferredType || '未设置' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="showProfileDialog = false">确定</el-button>
      </template>
    </el-dialog>

    <!-- 积分对话框 -->
    <el-dialog v-model="showPointsDialog" title="我的积分" width="500px">
      <div class="points-content">
        <div class="points-display">
          <el-icon :size="60" color="#f59e0b"><Star /></el-icon>
          <div class="points-value">{{ userStore.userInfo?.points || 0 }}</div>
          <div class="points-label">当前积分</div>
        </div>
        <el-divider />
        <div class="points-tips">
          <h4>如何获得积分？</h4>
          <ul>
            <li>完成咨询：每次咨询获得 10 积分</li>
            <li>提供反馈：为咨询结果评分获得 5 积分</li>
            <li>推荐好友：好友注册获得 20 积分</li>
          </ul>
          <h4>积分用途</h4>
          <ul>
            <li>兑换汽车周边礼品</li>
            <li>参与抽奖活动</li>
            <li>获取专属优惠</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="showPointsDialog = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useConsultationStore } from '@/stores/consultation'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Van,
  UserFilled,
  User,
  ArrowDown,
  Star,
  SwitchButton,
  HomeFilled,
  ChatDotRound,
  Tickets,
  Setting,
  Plus,
  TrendCharts
} from '@element-plus/icons-vue'
import ConsultationForm from '@/components/ConsultationForm.vue'
import ConsultationHistory from '@/components/ConsultationHistory.vue'
import ProfileSettings from '@/components/ProfileSettings.vue'

const router = useRouter()
const userStore = useUserStore()
const consultationStore = useConsultationStore()

const activeMenu = ref('dashboard')
const showProfileDialog = ref(false)
const showPointsDialog = ref(false)

const userConsultations = computed(() => {
  return consultationStore.getUserConsultations(userStore.userInfo?.id)
})

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

const handleConsultSuccess = () => {
  activeMenu.value = 'history'
  ElMessage.success('咨询已提交！')
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  consultationStore.restoreFromLocalStorage()
})
</script>

<style scoped>
.home-container {
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
  color: #667eea;
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

.dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px;
  color: white;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.banner-content h1 {
  margin: 0 0 12px 0;
  font-size: 32px;
}

.banner-content p {
  margin: 0 0 24px 0;
  font-size: 16px;
  opacity: 0.9;
}

.banner-image {
  opacity: 0.3;
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
  gap: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.recent-consultations {
  margin-top: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.consultation-summary {
  color: #666;
  font-size: 14px;
  margin-top: 8px;
}

.points-content {
  text-align: center;
}

.points-display {
  padding: 20px;
}

.points-value {
  font-size: 48px;
  font-weight: bold;
  color: #f59e0b;
  margin: 12px 0;
}

.points-label {
  font-size: 16px;
  color: #666;
}

.points-tips {
  text-align: left;
}

.points-tips h4 {
  margin: 16px 0 8px 0;
  color: #333;
}

.points-tips ul {
  margin: 0;
  padding-left: 24px;
  color: #666;
}

.points-tips li {
  margin: 8px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    display: none;
  }

  .welcome-banner {
    flex-direction: column;
    text-align: center;
  }

  .banner-image {
    display: none;
  }
}
</style>
