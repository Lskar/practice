<template>
  <div class="consultation-history">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon :size="24"><Tickets /></el-icon>
          <span>咨询历史</span>
          <el-tag type="info">共 {{ consultations.length }} 条记录</el-tag>
        </div>
      </template>

      <el-empty v-if="consultations.length === 0" description="暂无咨询记录">
        <el-button type="primary" @click="$emit('new-consultation')">
          开始咨询
        </el-button>
      </el-empty>

      <div v-else>
        <!-- 筛选和搜索 -->
        <div class="filter-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索咨询记录..."
            :prefix-icon="Search"
            clearable
            style="width: 300px"
          />
          <el-select
            v-model="filterCarType"
            placeholder="车型筛选"
            clearable
            style="width: 150px"
          >
            <el-option label="全部车型" value="" />
            <el-option label="SUV" value="SUV" />
            <el-option label="轿车" value="轿车" />
            <el-option label="MPV" value="MPV" />
            <el-option label="跑车" value="跑车" />
            <el-option label="越野车" value="越野车" />
          </el-select>
        </div>

        <!-- 咨询记录列表 -->
        <div class="consultation-list">
          <el-card
            v-for="item in filteredConsultations"
            :key="item.id"
            class="consultation-item"
            shadow="hover"
          >
            <div class="item-header">
              <div class="item-title">
                <el-icon :size="20" color="#409eff"><DocumentChecked /></el-icon>
                <h3>{{ item.title }}</h3>
                <el-tag v-if="item.rating" type="success" size="small">
                  <el-icon><Star /></el-icon>
                  {{ item.rating }}分
                </el-tag>
              </div>
              <div class="item-time">
                <el-icon><Clock /></el-icon>
                {{ formatDate(item.createdAt) }}
              </div>
            </div>

            <div class="item-info">
              <el-tag>{{ item.budget }}</el-tag>
              <el-tag type="success">{{ item.carType }}</el-tag>
              <el-tag type="warning">{{ item.fuelType }}</el-tag>
              <el-tag
                v-for="scene in item.useCase"
                :key="scene"
                type="info"
              >
                {{ scene }}
              </el-tag>
            </div>

            <div class="item-description">
              <p><strong>需求描述：</strong>{{ item.description }}</p>
            </div>

            <el-collapse v-if="item.result" class="result-collapse">
              <el-collapse-item title="查看AI分析结果" name="result">
                <div class="result-detail">
                  <div class="result-section">
                    <h4>
                      <el-icon><Star /></el-icon>
                      推荐车型
                    </h4>
                    <p>{{ item.result.recommendation }}</p>
                  </div>
                  <div class="result-section">
                    <h4>
                      <el-icon><DataAnalysis /></el-icon>
                      详细分析
                    </h4>
                    <p>{{ item.result.analysis }}</p>
                  </div>
                  <div class="result-section">
                    <h4>
                      <el-icon><Money /></el-icon>
                      预算建议
                    </h4>
                    <p>{{ item.result.budgetAdvice }}</p>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>

            <div class="item-actions">
              <el-button
                type="primary"
                text
                @click="handleViewDetail(item)"
              >
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button
                type="success"
                text
                @click="handleReuse(item)"
              >
                <el-icon><RefreshRight /></el-icon>
                重新咨询
              </el-button>
              <el-button
                type="danger"
                text
                @click="handleDelete(item.id)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </el-card>
        </div>

        <!-- 分页 -->
        <el-pagination
          v-if="filteredConsultations.length > pageSize"
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredConsultations.length"
          layout="prev, pager, next, jumper"
          style="margin-top: 24px; justify-content: center"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentItem?.title"
      width="800px"
    >
      <el-descriptions v-if="currentItem" :column="2" border>
        <el-descriptions-item label="咨询时间">
          {{ formatDate(currentItem.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="AI模型">
          {{ currentItem.aiModel === 'qwen' ? '通义千问' : '智谱AI' }}
        </el-descriptions-item>
        <el-descriptions-item label="购车预算">
          {{ currentItem.budget }}
        </el-descriptions-item>
        <el-descriptions-item label="偏好车型">
          {{ currentItem.carType }}
        </el-descriptions-item>
        <el-descriptions-item label="燃料类型">
          {{ currentItem.fuelType }}
        </el-descriptions-item>
        <el-descriptions-item label="使用场景">
          {{ currentItem.useCase.join('、') }}
        </el-descriptions-item>
        <el-descriptions-item label="品牌偏好" :span="2">
          {{ currentItem.brands.join('、') || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="需求描述" :span="2">
          {{ currentItem.description }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentItem.rating" label="评分" :span="2">
          <el-rate v-model="currentItem.rating" disabled />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="detailDialogVisible = false">
          关闭
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Tickets,
  Search,
  DocumentChecked,
  Star,
  Clock,
  DataAnalysis,
  Money,
  View,
  RefreshRight,
  Delete
} from '@element-plus/icons-vue'

const props = defineProps({
  consultations: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['new-consultation', 'reuse'])

const searchKeyword = ref('')
const filterCarType = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const detailDialogVisible = ref(false)
const currentItem = ref(null)

const filteredConsultations = computed(() => {
  let result = props.consultations

  // 关键词搜索
  if (searchKeyword.value) {
    result = result.filter(item =>
      item.title.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
      item.description.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 车型筛选
  if (filterCarType.value) {
    result = result.filter(item => item.carType === filterCarType.value)
  }

  return result
})

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const handleViewDetail = (item) => {
  currentItem.value = item
  detailDialogVisible.value = true
}

const handleReuse = (item) => {
  emit('reuse', item)
  ElMessage.success('已复用咨询信息，请修改后提交')
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这条咨询记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用删除API
    ElMessage.success('删除成功！')
  }).catch(() => {})
}
</script>

<style scoped>
.consultation-history {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: bold;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.consultation-list {
  display: grid;
  gap: 16px;
}

.consultation-item {
  transition: all 0.3s;
}

.consultation-item:hover {
  transform: translateY(-2px);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.item-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-title h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.item-time {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 14px;
}

.item-info {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.item-description {
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.item-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.result-collapse {
  margin-bottom: 16px;
  border: none;
}

.result-detail {
  padding: 12px 0;
}

.result-section {
  margin-bottom: 16px;
}

.result-section h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  margin-bottom: 8px;
  font-size: 14px;
}

.result-section p {
  color: #666;
  line-height: 1.8;
  white-space: pre-line;
  margin: 0;
}

.item-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
  }

  .filter-bar .el-input,
  .filter-bar .el-select {
    width: 100% !important;
  }

  .item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .item-actions {
    flex-direction: column;
  }
}
</style>
