<template>
  <el-card shadow="never" class="consultation-form-card">
    <template #header>
      <div class="card-header">
        <el-icon :size="24"><ChatDotRound /></el-icon>
        <span>新建购车咨询</span>
      </div>
    </template>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      size="large"
    >
      <el-form-item label="咨询标题" prop="title">
        <el-input
          v-model="form.title"
          placeholder="请输入咨询标题，如：想买一辆家用SUV"
          clearable
        />
      </el-form-item>

      <el-form-item label="购车预算" prop="budget">
        <el-select
          v-model="form.budget"
          placeholder="请选择购车预算"
          style="width: 100%"
        >
          <el-option label="10万以下" value="0-10万" />
          <el-option label="10-20万" value="10-20万" />
          <el-option label="20-30万" value="20-30万" />
          <el-option label="30-50万" value="30-50万" />
          <el-option label="50万以上" value="50万以上" />
        </el-select>
      </el-form-item>

      <el-form-item label="偏好车型" prop="carType">
        <el-select
          v-model="form.carType"
          placeholder="请选择偏好车型"
          style="width: 100%"
        >
          <el-option label="SUV" value="SUV">
            <span>SUV - 运动型多用途车</span>
          </el-option>
          <el-option label="轿车" value="轿车">
            <span>轿车 - 商务家用</span>
          </el-option>
          <el-option label="MPV" value="MPV">
            <span>MPV - 多人出行</span>
          </el-option>
          <el-option label="跑车" value="跑车">
            <span>跑车 - 速度激情</span>
          </el-option>
          <el-option label="越野车" value="越野车">
            <span>越野车 - 户外探险</span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="使用场景" prop="useCase">
        <el-checkbox-group v-model="form.useCase">
          <el-checkbox value="通勤" label="通勤">
            日常上下班
          </el-checkbox>
          <el-checkbox value="家庭" label="家庭">
            家庭出行
          </el-checkbox>
          <el-checkbox value="商务" label="商务">
            商务接待
          </el-checkbox>
          <el-checkbox value="旅行" label="旅行">
            自驾旅行
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <el-form-item label="燃料类型" prop="fuelType">
        <el-radio-group v-model="form.fuelType">
          <el-radio-button value="燃油">燃油</el-radio-button>
          <el-radio-button value="电动">电动</el-radio-button>
          <el-radio-button value="混动">混动</el-radio-button>
          <el-radio-button value="不限">不限</el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="品牌偏好" prop="brands">
        <el-select
          v-model="form.brands"
          multiple
          placeholder="请选择品牌偏好（可多选）"
          style="width: 100%"
        >
          <el-option label="大众" value="大众" />
          <el-option label="丰田" value="丰田" />
          <el-option label="本田" value="本田" />
          <el-option label="比亚迪" value="比亚迪" />
          <el-option label="特斯拉" value="特斯拉" />
          <el-option label="宝马" value="宝马" />
          <el-option label="奔驰" value="奔驰" />
          <el-option label="奥迪" value="奥迪" />
          <el-option label="吉利" value="吉利" />
          <el-option label="长城" value="长城" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>

      <el-form-item label="详细需求" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="6"
          placeholder="请详细描述您的购车需求，如：家里有两个孩子，需要空间大的车..."
        />
      </el-form-item>

      <el-form-item label="选择AI模型" prop="aiModel">
        <el-radio-group v-model="form.aiModel">
          <el-radio value="qwen">阿里百炼（通义千问）</el-radio>
          <el-radio value="zhipu">智谱AI（GLM）</el-radio>
        </el-radio-group>
        <div class="model-tip">
          <el-icon><InfoFilled /></el-icon>
          <span>不同AI模型可能给出不同角度的建议</span>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          :icon="Search"
          @click="handleSubmit"
        >
          {{ loading ? 'AI分析中...' : '开始咨询' }}
        </el-button>
        <el-button size="large" @click="handleReset">
          <el-icon><RefreshRight /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 咨询结果 -->
    <el-card v-if="consultResult" class="result-card" shadow="hover">
      <template #header>
        <div class="result-header">
          <el-icon :size="24" color="#67c23a"><CircleCheckFilled /></el-icon>
          <span>AI咨询结果</span>
          <el-tag :type="form.aiModel === 'qwen' ? 'primary' : 'success'">
            {{ form.aiModel === 'qwen' ? '通义千问' : '智谱AI' }}
          </el-tag>
        </div>
      </template>

      <div class="result-content">
        <el-alert
          title="AI分析建议"
          type="success"
          :closable="false"
          show-icon
        />

        <div class="result-section">
          <h3>
            <el-icon><Star /></el-icon>
            推荐车型
          </h3>
          <p>{{ consultResult.recommendation }}</p>
        </div>

        <div class="result-section">
          <h3>
            <el-icon><DataAnalysis /></el-icon>
            详细分析
          </h3>
          <p>{{ consultResult.analysis }}</p>
        </div>

        <div class="result-section">
          <h3>
            <el-icon><Money /></el-icon>
            预算建议
          </h3>
          <p>{{ consultResult.budgetAdvice }}</p>
        </div>

        <el-divider />

        <div class="result-actions">
          <el-button type="primary" @click="handleSaveConsultation">
            <el-icon><DocumentChecked /></el-icon>
            保存咨询记录
          </el-button>
          <el-button @click="handleNewConsultation">
            <el-icon><Plus /></el-icon>
            新建咨询
          </el-button>
          <el-rate
            v-model="rating"
            :texts="['很差', '差', '一般', '满意', '非常满意']"
            show-text
            @change="handleRating"
          />
        </div>
      </div>
    </el-card>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import { useConsultationStore } from '@/stores/consultation'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound,
  Search,
  RefreshRight,
  InfoFilled,
  CircleCheckFilled,
  Star,
  DataAnalysis,
  Money,
  DocumentChecked,
  Plus
} from '@element-plus/icons-vue'

const emit = defineEmits(['success'])

const userStore = useUserStore()
const consultationStore = useConsultationStore()

const formRef = ref()
const loading = ref(false)
const consultResult = ref(null)
const rating = ref(0)

const form = reactive({
  title: '',
  budget: '',
  carType: '',
  useCase: [],
  fuelType: '不限',
  brands: [],
  description: '',
  aiModel: 'qwen'
})

const rules = {
  title: [
    { required: true, message: '请输入咨询标题', trigger: 'blur' }
  ],
  budget: [
    { required: true, message: '请选择购车预算', trigger: 'change' }
  ],
  carType: [
    { required: true, message: '请选择偏好车型', trigger: 'change' }
  ],
  useCase: [
    { type: 'array', required: true, message: '请至少选择一个使用场景', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请描述您的详细需求', trigger: 'blur' },
    { min: 10, message: '需求描述至少10个字符', trigger: 'blur' }
  ],
  aiModel: [
    { required: true, message: '请选择AI模型', trigger: 'change' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate((valid) => {
    if (valid) {
      loading.value = true

      // 模拟AI咨询
      setTimeout(() => {
        consultResult.value = {
          recommendation: generateRecommendation(),
          analysis: generateAnalysis(),
          budgetAdvice: generateBudgetAdvice()
        }
        loading.value = false
        ElMessage.success('咨询完成！')
        
        // 添加积分
        const currentPoints = userStore.userInfo?.points || 0
        userStore.updateUserInfo({ points: currentPoints + 10 })
      }, 2000)
    }
  })
}

const generateRecommendation = () => {
  const recommendations = {
    'SUV': '根据您的需求，推荐以下SUV车型：\n1. 比亚迪宋PLUS DM-i - 性价比高，油耗低\n2. 本田CR-V - 空间大，质量可靠\n3. 大众途观L - 品质优秀，保值率高',
    '轿车': '根据您的需求，推荐以下轿车车型：\n1. 丰田凯美瑞 - 省心耐用\n2. 大众帕萨特 - 商务首选\n3. 比亚迪秦PLUS - 新能源经济',
    'MPV': '根据您的需求，推荐以下MPV车型：\n1. 别克GL8 - 商务MPV标杆\n2. 本田奥德赛 - 家用MPV首选\n3. 丰田赛那 - 舒适空间大',
    '跑车': '根据您的需求，推荐以下跑车车型：\n1. 保时捷718 - 性能与品质兼具\n2. 奥迪TT - 时尚运动\n3. 宝马Z4 - 驾驶乐趣十足',
    '越野车': '根据您的需求，推荐以下越野车型：\n1. 丰田普拉多 - 越野之王\n2. 吉普牧马人 - 硬派越野\n3. 长城坦克300 - 国产精品'
  }
  return recommendations[form.carType] || '推荐车型将根据您的具体需求进行定制化分析。'
}

const generateAnalysis = () => {
  return `基于您的购车预算${form.budget}和${form.useCase.join('、')}的使用场景，${form.carType}是非常合适的选择。\n\n从燃料类型来看，${form.fuelType}能够满足您的日常使用需求。考虑到当前市场趋势，新能源车型在使用成本和环保方面具有明显优势。\n\n${form.brands.length > 0 ? `您偏好的品牌${form.brands.join('、')}在这个价位都有不错的选择。` : ''}建议您重点关注品牌口碑、售后服务和保值率等因素。`
}

const generateBudgetAdvice = () => {
  return `根据您的预算${form.budget}，建议：\n\n1. 裸车价格控制在预算的70-80%\n2. 预留15-20%用于购置税、保险等费用\n3. 保留5-10%作为后期装饰和改装预算\n\n此外，建议对比多家4S店报价，争取更优惠的价格。如果选择贷款购车，要仔细计算总成本。`
}

const handleReset = () => {
  formRef.value?.resetFields()
  consultResult.value = null
  rating.value = 0
}

const handleSaveConsultation = () => {
  const consultation = {
    userId: userStore.userInfo?.id,
    ...form,
    result: consultResult.value,
    rating: rating.value
  }
  
  consultationStore.addConsultation(consultation)
  ElMessage.success('咨询记录已保存！')
  
  if (rating.value > 0) {
    const currentPoints = userStore.userInfo?.points || 0
    userStore.updateUserInfo({ points: currentPoints + 5 })
    ElMessage.info('评分成功，获得5积分！')
  }
  
  emit('success')
}

const handleNewConsultation = () => {
  handleReset()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleRating = (value) => {
  if (value > 0) {
    ElMessage.success('感谢您的评价！')
  }
}
</script>

<style scoped>
.consultation-form-card {
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

.model-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}

.result-card {
  margin-top: 24px;
  animation: fadeIn 0.5s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.result-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: bold;
}

.result-content {
  padding: 12px 0;
}

.result-section {
  margin: 24px 0;
}

.result-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  margin-bottom: 12px;
}

.result-section p {
  color: #666;
  line-height: 1.8;
  white-space: pre-line;
}

.result-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.result-actions .el-rate {
  margin-left: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .consultation-form-card {
    margin: 0;
  }

  :deep(.el-form-item__label) {
    width: 100px !important;
  }

  .result-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .result-actions .el-rate {
    margin: 0;
  }
}
</style>
