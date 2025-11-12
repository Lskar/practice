<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-content">
      <!-- 左侧介绍 -->
      <div class="intro-section">
        <div class="intro-content">
          <el-icon class="logo-icon" :size="60">
            <Van />
          </el-icon>
          <h1>智能购车咨询系统</h1>
          <p class="subtitle">基于大语言模型的个性化购车建议平台</p>
          <div class="features">
            <div class="feature-item">
              <el-icon :size="24"><Check /></el-icon>
              <span>专业车型推荐</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24"><Check /></el-icon>
              <span>智能对比分析</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24"><Check /></el-icon>
              <span>预算规划建议</span>
            </div>
            <div class="feature-item">
              <el-icon :size="24"><Check /></el-icon>
              <span>专业知识解答</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-section">
        <el-card class="login-card" shadow="never">
          <template #header>
            <div class="card-header">
              <h2>{{ isAdmin ? '管理员登录' : '用户登录' }}</h2>
              <p>欢迎回来，开始您的购车之旅</p>
            </div>
          </template>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            size="large"
          >
            <!-- 角色切换 -->
            <el-form-item>
              <el-radio-group v-model="isAdmin" class="role-selector">
                <el-radio-button :value="false">
                  <el-icon><User /></el-icon>
                  <span>用户登录</span>
                </el-radio-button>
                <el-radio-button :value="true">
                  <el-icon><Avatar /></el-icon>
                  <span>管理员</span>
                </el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
              <el-link type="primary" :underline="false" style="margin-left: auto;">
                忘记密码?
              </el-link>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                style="width: 100%"
                @click="handleLogin"
              >
                <el-icon v-if="!loading"><Right /></el-icon>
                {{ loading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>

            <el-divider>其他方式</el-divider>

            <div class="other-actions">
              <span v-if="!isAdmin">还没有账号？</span>
              <el-link
                v-if="!isAdmin"
                type="primary"
                :underline="false"
                @click="goToRegister"
              >
                立即注册
              </el-link>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Right, Check, Van, Avatar } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const isAdmin = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      
      // 模拟登录请求
      setTimeout(() => {
        // 这里应该调用实际的登录API
        // 暂时使用模拟数据
        const userData = {
          id: Date.now(),
          username: loginForm.username,
          role: isAdmin.value ? 'admin' : 'user',
          name: loginForm.username,
          phone: '',
          email: '',
          budget: '',
          preferredType: '',
          points: 0
        }

        userStore.login(userData)
        loading.value = false

        ElMessage.success('登录成功！')
        
        // 根据角色跳转到不同页面
        if (isAdmin.value) {
          router.push('/admin')
        } else {
          router.push('/home')
        }
      }, 1000)
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: 10%;
  animation-delay: 5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: -50px;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-content {
  position: relative;
  display: flex;
  width: 90%;
  max-width: 1200px;
  height: 600px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.intro-section {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px;
  display: flex;
  align-items: center;
  color: white;
}

.intro-content {
  width: 100%;
}

.logo-icon {
  margin-bottom: 20px;
  color: white;
}

.intro-section h1 {
  font-size: 36px;
  margin: 0 0 10px 0;
  font-weight: bold;
}

.subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.features {
  display: grid;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
}

.feature-item .el-icon {
  color: #4ade80;
}

.form-section {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 100%;
  max-width: 450px;
  border: none;
  background: transparent;
}

.login-card :deep(.el-card__header) {
  border: none;
  padding: 0 0 20px 0;
}

.login-card :deep(.el-card__body) {
  padding: 20px 0 0 0;
}

.card-header h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  color: #333;
}

.card-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.role-selector {
  width: 100%;
}

.role-selector :deep(.el-radio-button__inner) {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
}

.other-actions {
  text-align: center;
  color: #666;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .intro-section {
    display: none;
  }

  .login-content {
    width: 95%;
    height: auto;
    min-height: 500px;
  }

  .form-section {
    padding: 20px;
  }
}
</style>
