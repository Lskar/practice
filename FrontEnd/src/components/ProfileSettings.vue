<template>
  <div class="profile-settings">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon :size="24"><Setting /></el-icon>
          <span>个人设置</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="settings-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-form
            ref="basicFormRef"
            :model="basicForm"
            :rules="basicRules"
            label-width="120px"
            size="large"
          >
            <el-form-item label="用户名">
              <el-input v-model="basicForm.username" disabled />
            </el-form-item>

            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="basicForm.name"
                placeholder="请输入真实姓名"
                clearable
              />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="basicForm.phone"
                placeholder="请输入手机号"
                clearable
              />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="basicForm.email"
                placeholder="请输入邮箱地址"
                clearable
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleUpdateBasic">
                <el-icon><CircleCheck /></el-icon>
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 安全设置 -->
        <el-tab-pane label="安全设置" name="security">
          <el-form
            ref="securityFormRef"
            :model="securityForm"
            :rules="securityRules"
            label-width="120px"
            size="large"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="securityForm.oldPassword"
                type="password"
                placeholder="请输入原密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="securityForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="securityForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword">
                <el-icon><Lock /></el-icon>
                修改密码
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div class="danger-zone">
            <h3>
              <el-icon color="#f56c6c"><Warning /></el-icon>
              危险操作
            </h3>
            <p>以下操作不可恢复，请谨慎操作</p>
            <el-button type="danger" plain @click="handleDeleteAccount">
              <el-icon><Delete /></el-icon>
              注销账号
            </el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Setting,
  CircleCheck,
  Lock,
  Warning,
  Delete
} from '@element-plus/icons-vue'

const userStore = useUserStore()

const activeTab = ref('basic')
const basicFormRef = ref()
const securityFormRef = ref()

const basicForm = reactive({
  username: '',
  name: '',
  phone: '',
  email: ''
})

const securityForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const basicRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入新密码'))
  } else {
    if (securityForm.confirmPassword !== '') {
      securityFormRef.value.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== securityForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const securityRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePass, trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ]
}

const handleUpdateBasic = async () => {
  if (!basicFormRef.value) return

  await basicFormRef.value.validate((valid) => {
    if (valid) {
      userStore.updateUserInfo(basicForm)
      ElMessage.success('基本信息已更新！')
    }
  })
}

const handleUpdatePassword = async () => {
  if (!securityFormRef.value) return

  await securityFormRef.value.validate((valid) => {
    if (valid) {
      // 这里应该调用修改密码API
      ElMessage.success('密码修改成功，请重新登录！')
      securityForm.oldPassword = ''
      securityForm.newPassword = ''
      securityForm.confirmPassword = ''
    }
  })
}

const handleDeleteAccount = () => {
  ElMessageBox.confirm(
    '注销账号后，所有数据将被永久删除且无法恢复。确定要继续吗？',
    '警告',
    {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(() => {
    // 这里应该调用注销账号API
    ElMessage.error('账号已注销')
  }).catch(() => {})
}

onMounted(() => {
  // 加载用户信息
  if (userStore.userInfo) {
    Object.assign(basicForm, {
      username: userStore.userInfo.username,
      name: userStore.userInfo.name || '',
      phone: userStore.userInfo.phone || '',
      email: userStore.userInfo.email || ''
    })
  }
})
</script>

<style scoped>
.profile-settings {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: bold;
}

.settings-tabs {
  min-height: 400px;
}

.danger-zone {
  padding: 24px;
  border: 2px solid #f56c6c;
  border-radius: 8px;
  background: #fef0f0;
}

.danger-zone h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px 0;
  color: #f56c6c;
}

.danger-zone p {
  color: #666;
  margin: 0 0 16px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  :deep(.el-form-item__label) {
    width: 100px !important;
  }
}
</style>
