<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="register-content">
      <el-card class="register-card" shadow="never">
        <template #header>
          <div class="card-header">
            <el-button
              :icon="ArrowLeft"
              circle
              @click="goBack"
              class="back-button"
            />
            <div class="header-text">
              <h2>用户注册</h2>
              <p>创建账号，开启智能购车之旅</p>
            </div>
          </div>
        </template>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-width="100px"
          size="large"
        >
          <el-steps :active="activeStep" align-center class="steps-container">
            <el-step title="账号信息" :icon="User" />
            <el-step title="个人信息" :icon="Edit" />
            <el-step title="购车偏好" :icon="Setting" />
          </el-steps>

          <!-- 第一步：账号信息 -->
          <div v-show="activeStep === 0" class="step-content">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
          </div>

          <!-- 第二步：个人信息 -->
          <div v-show="activeStep === 1" class="step-content">
            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="registerForm.name"
                placeholder="请输入真实姓名"
                clearable
              />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                :prefix-icon="Phone"
                clearable
              />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                :prefix-icon="Message"
                clearable
              />
            </el-form-item>
          </div>

          <!-- 第三步：购车偏好 -->
          <div v-show="activeStep === 2" class="step-content">
            <el-form-item label="购车预算" prop="budget">
              <el-select
                v-model="registerForm.budget"
                placeholder="请选择购车预算"
                style="width: 100%"
              >
                <el-option label="10万以下" value="0-10" />
                <el-option label="10-20万" value="10-20" />
                <el-option label="20-30万" value="20-30" />
                <el-option label="30-50万" value="30-50" />
                <el-option label="50万以上" value="50+" />
              </el-select>
            </el-form-item>

            <el-form-item label="偏好车型" prop="preferredType">
              <el-select
                v-model="registerForm.preferredType"
                placeholder="请选择偏好车型"
                style="width: 100%"
              >
                <el-option label="SUV" value="SUV" />
                <el-option label="轿车" value="sedan" />
                <el-option label="MPV" value="MPV" />
                <el-option label="跑车" value="sports" />
                <el-option label="越野车" value="offroad" />
              </el-select>
            </el-form-item>

            <el-form-item label="使用场景" prop="useCase">
              <el-checkbox-group v-model="registerForm.useCase">
                <el-checkbox value="commute">通勤</el-checkbox>
                <el-checkbox value="family">家庭</el-checkbox>
                <el-checkbox value="business">商务</el-checkbox>
                <el-checkbox value="travel">旅行</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="燃料偏好" prop="fuelType">
              <el-radio-group v-model="registerForm.fuelType">
                <el-radio value="gasoline">燃油</el-radio>
                <el-radio value="electric">电动</el-radio>
                <el-radio value="hybrid">混动</el-radio>
                <el-radio value="any">不限</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>

          <!-- 操作按钮 -->
          <el-form-item class="button-group">
            <el-button v-if="activeStep > 0" @click="prevStep">
              <el-icon><ArrowLeft /></el-icon>
              上一步
            </el-button>
            <el-button
              v-if="activeStep < 2"
              type="primary"
              @click="nextStep"
            >
              下一步
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button
              v-if="activeStep === 2"
              type="primary"
              :loading="loading"
              @click="handleRegister"
            >
              <el-icon v-if="!loading"><Check /></el-icon>
              {{ loading ? '注册中...' : '完成注册' }}
            </el-button>
          </el-form-item>

          <div class="footer-actions">
            <span>已有账号？</span>
            <el-link type="primary" :underline="false" @click="goToLogin">
              立即登录
            </el-link>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User,
  Lock,
  Phone,
  Message,
  Edit,
  Setting,
  ArrowLeft,
  ArrowRight,
  Check
} from '@element-plus/icons-vue'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)
const activeStep = ref(0)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  email: '',
  budget: '',
  preferredType: '',
  useCase: [],
  fuelType: 'any'
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (registerForm.confirmPassword !== '') {
      registerFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度应为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const nextStep = async () => {
  if (!registerFormRef.value) return

  const fieldsToValidate = {
    0: ['username', 'password', 'confirmPassword'],
    1: ['name', 'phone', 'email']
  }

  const fields = fieldsToValidate[activeStep.value]
  
  try {
    await registerFormRef.value.validateField(fields)
    activeStep.value++
  } catch (error) {
    console.log('Validation failed:', error)
  }
}

const prevStep = () => {
  activeStep.value--
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true

      // 模拟注册请求
      setTimeout(() => {
        loading.value = false
        ElMessage.success('注册成功！请登录')
        router.push('/login')
      }, 1500)
    }
  })
}

const goBack = () => {
  router.back()
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow-y: auto;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.background-decoration {
  position: fixed;
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

.register-content {
  position: relative;
  width: 100%;
  max-width: 800px;
  z-index: 1;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  border: none;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-card :deep(.el-card__header) {
  background: transparent;
  border: none;
  padding: 30px 30px 20px;
}

.register-card :deep(.el-card__body) {
  padding: 20px 30px 30px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-button {
  flex-shrink: 0;
}

.header-text h2 {
  margin: 0 0 5px 0;
  font-size: 28px;
  color: #333;
}

.header-text p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.steps-container {
  margin-bottom: 40px;
}

.step-content {
  min-height: 300px;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.button-group {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.button-group :deep(.el-form-item__content) {
  justify-content: center;
}

.footer-actions {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    padding: 20px 10px;
  }

  .register-card :deep(.el-card__header),
  .register-card :deep(.el-card__body) {
    padding: 20px;
  }

  .header-text h2 {
    font-size: 24px;
  }

  :deep(.el-form-item__label) {
    width: 80px !important;
  }
}
</style>
