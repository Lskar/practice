import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useConsultationStore = defineStore('consultation', () => {
  // 咨询记录列表
  const consultations = ref([])
  
  // 添加咨询记录
  const addConsultation = (consultation) => {
    const newConsultation = {
      id: Date.now(),
      ...consultation,
      createdAt: new Date().toISOString()
    }
    consultations.value.unshift(newConsultation)
    saveToLocalStorage()
    return newConsultation
  }

  // 获取用户的咨询记录
  const getUserConsultations = (userId) => {
    return consultations.value.filter(c => c.userId === userId)
  }

  // 保存到localStorage
  const saveToLocalStorage = () => {
    localStorage.setItem('consultations', JSON.stringify(consultations.value))
  }

  // 从localStorage恢复数据
  const restoreFromLocalStorage = () => {
    const saved = localStorage.getItem('consultations')
    if (saved) {
      consultations.value = JSON.parse(saved)
    }
  }

  // 清空咨询记录
  const clearConsultations = () => {
    consultations.value = []
    localStorage.removeItem('consultations')
  }

  return {
    consultations,
    addConsultation,
    getUserConsultations,
    restoreFromLocalStorage,
    clearConsultations
  }
})
