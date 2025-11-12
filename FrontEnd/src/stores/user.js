import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref(null)
  const isLoggedIn = ref(false)
  const userRole = ref('') // 'user' 或 'admin'

  // 登录
  const login = (userData) => {
    userInfo.value = userData
    isLoggedIn.value = true
    userRole.value = userData.role
    // 保存到localStorage
    localStorage.setItem('userInfo', JSON.stringify(userData))
    localStorage.setItem('isLoggedIn', 'true')
    localStorage.setItem('userRole', userData.role)
  }

  // 登出
  const logout = () => {
    userInfo.value = null
    isLoggedIn.value = false
    userRole.value = ''
    localStorage.removeItem('userInfo')
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('userRole')
  }

  // 从localStorage恢复登录状态
  const restoreLogin = () => {
    const savedUserInfo = localStorage.getItem('userInfo')
    const savedIsLoggedIn = localStorage.getItem('isLoggedIn')
    const savedUserRole = localStorage.getItem('userRole')
    
    if (savedIsLoggedIn === 'true' && savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo)
      isLoggedIn.value = true
      userRole.value = savedUserRole
    }
  }

  // 更新用户信息
  const updateUserInfo = (newInfo) => {
    userInfo.value = { ...userInfo.value, ...newInfo }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return {
    userInfo,
    isLoggedIn,
    userRole,
    login,
    logout,
    restoreLogin,
    updateUserInfo
  }
})
