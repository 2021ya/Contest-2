<template>
  <div class="plans-page">
    <div class="container">
      <div class="page-header">
        <h1>我的旅游计划</h1>
        <p>管理和查看您的所有旅游计划</p>
        <el-button type="primary" @click="$router.push('/planner')">
          <el-icon><Plus /></el-icon>
          创建新计划
        </el-button>
      </div>
      
      <div class="plans-content">
        <div class="plans-grid" v-if="plans.length > 0">
          <div
            v-for="plan in plans"
            :key="plan.id"
            class="plan-card"
          >
            <div class="plan-header">
              <h3>{{ plan.title }}</h3>
              <div class="plan-status">
                <el-tag :type="getStatusType(plan.status)">
                  {{ getStatusText(plan.status) }}
                </el-tag>
                <el-tag v-if="plan.aiGenerated" type="success" size="small">
                  AI生成
                </el-tag>
              </div>
            </div>
            
            <div class="plan-content">
              <p class="plan-description">{{ plan.description || '暂无描述' }}</p>
              
              <div class="plan-meta">
                <div class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ formatDate(plan.startDate) }} - {{ formatDate(plan.endDate) }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Money /></el-icon>
                  <span>¥{{ plan.budget }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ plan.destinations?.length || 0 }}个目的地</span>
                </div>
              </div>
              
              <div class="plan-destinations" v-if="plan.destinations && plan.destinations.length > 0">
                <h4>主要目的地：</h4>
                <div class="destination-tags">
                  <el-tag
                    v-for="destination in plan.destinations.slice(0, 3)"
                    :key="destination.id"
                    size="small"
                    type="info"
                  >
                    {{ destination.name }}
                  </el-tag>
                  <el-tag v-if="plan.destinations.length > 3" size="small" type="info">
                    +{{ plan.destinations.length - 3 }}个
                  </el-tag>
                </div>
              </div>
            </div>
            
            <div class="plan-footer">
              <div class="plan-actions">
                <el-button size="small" @click.stop="viewPlanDetails(plan)">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
                <el-button size="small" @click.stop="viewOnMap(plan)">
                  <el-icon><Location /></el-icon>
                  地图查看
                </el-button>
                <el-button size="small" type="danger" @click.stop="deletePlan(plan)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
              <div class="plan-date">
                <small>创建于 {{ formatDateTime(plan.createdAt) }}</small>
              </div>
            </div>
          </div>
        </div>
        
        <div class="empty-state" v-else>
          <div class="empty-content">
            <el-icon class="empty-icon"><Document /></el-icon>
            <h3>暂无旅游计划</h3>
            <p>开始创建您的第一个AI旅游计划吧！</p>
            <el-button type="primary" @click="$router.push('/planner')">
              <el-icon><Plus /></el-icon>
              创建计划
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 新的计划详情对话框 -->
    <el-dialog
      v-model="showPlanDetails"
      :title="currentPlan?.title || '计划详情'"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="currentPlan" class="new-plan-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <el-tag :type="getStatusType(currentPlan.status)">
              {{ getStatusText(currentPlan.status) }}
            </el-tag>
            <el-tag v-if="currentPlan.aiGenerated" type="success">
              AI生成
            </el-tag>
          </div>
        </div>
        
        <div class="detail-content">
          <div class="info-section">
            <h4>基本信息</h4>
            <div class="info-list">
              <div class="info-item">
                <span class="label">出发日期：</span>
                <span class="value">{{ formatDate(currentPlan.startDate) }}</span>
              </div>
              <div class="info-item">
                <span class="label">返回日期：</span>
                <span class="value">{{ formatDate(currentPlan.endDate) }}</span>
              </div>
              <div class="info-item">
                <span class="label">预算：</span>
                <span class="value">¥{{ currentPlan.budget }}</span>
              </div>
              <div class="info-item">
                <span class="label">创建时间：</span>
                <span class="value">{{ formatDateTime(currentPlan.createdAt) }}</span>
              </div>
              <div class="info-item">
                <span class="label">目的地数量：</span>
                <span class="value">{{ currentPlan.destinations?.length || 0 }}个</span>
              </div>
            </div>
          </div>
          
          <div v-if="currentPlan.description" class="description-section">
            <h4>计划描述</h4>
            <p>{{ currentPlan.description }}</p>
          </div>
          
          <div v-if="currentPlan.destinations && currentPlan.destinations.length > 0" class="destinations-section">
            <h4>目的地</h4>
            <div class="destination-list">
              <el-tag
                v-for="destination in currentPlan.destinations"
                :key="destination.id"
                size="small"
                type="info"
                style="margin: 5px"
              >
                第{{ destination.dayOrder }}天: {{ destination.name }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePlanDetails">关闭</el-button>
          <el-button type="primary" @click="viewOnMap(currentPlan)">
            <el-icon><Location /></el-icon>
            在地图上查看
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Calendar, Money, Location, View, Delete, Document } from '@element-plus/icons-vue'
import { useTravelPlanStore } from '@/stores/travelPlan'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

const plans = ref([])
const currentPlan = ref(null)
const showPlanDetails = ref(false)
const travelPlanStore = useTravelPlanStore()
const router = useRouter()

onMounted(async () => {
  await loadPlans()
})

const loadPlans = async () => {
  try {
    const plansData = await travelPlanStore.getTravelPlans()
    plans.value = plansData
  } catch (error) {
    console.error('加载计划失败:', error)
    ElMessage.error('加载计划失败')
  }
}

const viewPlanDetails = (plan) => {
  currentPlan.value = plan
  showPlanDetails.value = true
}

const closePlanDetails = () => {
  showPlanDetails.value = false
  // 延迟清空数据，确保对话框完全关闭
  setTimeout(() => {
    currentPlan.value = null
  }, 300)
}

const viewOnMap = (plan) => {
  // 跳转到地图页面并传递计划数据
  if (!plan) {
    ElMessage.error('无法在地图上查看，请先选择一个计划')
    return
  }
  if (!plan.id) {
    ElMessage.error('计划ID不存在，无法在地图上查看')
    return
  }
  router.push({
    name: 'Map',
    query: { planId: plan.id }
  })
}

const deletePlan = async (plan) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除计划"${plan.title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await travelPlanStore.deleteTravelPlan(plan.id)
    await loadPlans()
    ElMessage.success('计划删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除计划失败:', error)
      ElMessage.error('删除计划失败')
    }
  }
}

const getStatusType = (status) => {
  const types = {
    DRAFT: 'info',
    PLANNING: 'warning',
    CONFIRMED: 'success',
    COMPLETED: 'success',
    CANCELLED: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    DRAFT: '草稿',
    PLANNING: '规划中',
    CONFIRMED: '已确认',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return texts[status] || status
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const formatDateTime = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}
</script>

<style scoped>
@keyframes titleFlow {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.plans-page {
  background-image: url('@/assets/background.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  min-height: 100vh;
  padding: 20px 0;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
  padding-bottom: 20px;
}

.page-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  background: linear-gradient(90deg, #ffffff, #a3a3a3);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  display: inline-block;
  animation: fadeIn 1s ease-in-out;
}

.page-header p {
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 20px;
  font-size: 1.1rem;
}

.plans-content {
  max-width: 1200px;
  margin: 0 auto;
}

.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.plan-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 25px;
  transition: all 0.3s ease-in-out;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  color: white;
}

.plan-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.05), transparent);
  transition: all 0.5s ease-in-out;
}

.plan-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
  border-color: rgba(102, 126, 234, 0.5);
  background: rgba(255, 255, 255, 0.08);
}

.plan-card:hover::before {
  left: 100%;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.plan-header h3 {
  color: white;
  font-size: 1.4rem;
  font-weight: 600;
  margin: 0;
  flex: 1;
  margin-right: 10px;
  line-height: 1.3;
}

.plan-status {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.plan-content {
  margin-bottom: 20px;
}

.plan-description {
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 15px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.plan-meta {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.95rem;
}

.meta-item el-icon {
  color: #667eea;
}

.plan-destinations h4 {
  color: white;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.destination-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.destination-tags .el-tag {
  background: rgba(102, 126, 234, 0.2);
  border: 1px solid rgba(102, 126, 234, 0.3);
  color: #667eea;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  padding: 4px 10px;
}

.destination-tags .el-tag:hover {
  background: rgba(102, 126, 234, 0.4);
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.plan-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 15px;
}

.plan-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.plan-date {
  text-align: right;
  color: rgba(255, 255, 255, 0.5);
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.empty-content {
  text-align: center;
  max-width: 400px;
  padding: 40px 20px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.empty-icon {
  font-size: 6rem;
  color: rgba(102, 126, 234, 0.1);
  margin-bottom: 30px;
  filter: drop-shadow(0 0 10px rgba(102, 126, 234, 0.2));
}

.empty-content h3 {
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 15px;
  font-size: 1.5rem;
}

.empty-content p {
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 30px;
  font-size: 1.1rem;
}

/* 新的详情对话框样式 */
.new-plan-detail {
  color: #303133;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.new-plan-detail .detail-header {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e9ecef;
}

.new-plan-detail .detail-meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.new-plan-detail .detail-content {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}

.new-plan-detail .info-section,
.new-plan-detail .description-section,
.new-plan-detail .destinations-section {
  margin-bottom: 25px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03);
  transition: transform 0.2s ease;
}

.new-plan-detail .info-section:hover,
.new-plan-detail .description-section:hover,
.new-plan-detail .destinations-section:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.new-plan-detail h4 {
  color: #1f2329;
  font-size: 18px;
  margin-bottom: 15px;
  font-weight: 600;
  display: inline-block;
  position: relative;
  padding-bottom: 8px;
}

.new-plan-detail h4::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 3px;
}

.new-plan-detail .info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.new-plan-detail .info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px dashed #e9ecef;
  transition: all 0.2s ease;
}

.new-plan-detail .info-item:hover {
  background: rgba(102, 126, 234, 0.03);
  padding-left: 5px;
}

.new-plan-detail .info-item:last-child {
  border-bottom: none;
}

.new-plan-detail .info-item .label {
  color: #606266;
  font-weight: 500;
  min-width: 120px;
  font-size: 15px;
}

.new-plan-detail .info-item .value {
  color: #303133;
  flex: 1;
  text-align: right;
  font-size: 15px;
  font-weight: 500;
}

.new-plan-detail .description-section p {
  color: #303133;
  line-height: 1.8;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #667eea;
  font-size: 15px;
}

.new-plan-detail .destination-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 10px 0;
}

.new-plan-detail .destination-list .el-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  transition: all 0.2s ease;
  font-size: 14px;
  padding: 6px 12px;
}

.new-plan-detail .destination-list .el-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 3px 10px rgba(102, 126, 234, 0.3);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
  margin-top: 20px;
}

.dialog-footer el-button {
  border-radius: 8px;
  padding: 8px 20px;
  font-size: 15px;
  transition: all 0.2s ease;
}

.dialog-footer el-button[type="primary"] {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.dialog-footer el-button[type="primary"]:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

/* 旧的详情对话框样式 - 与plan-card保持一致 */
.plan-detail {
  max-height: 70vh;
  overflow-y: auto;
  color: white;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.detail-header {
  margin-bottom: 20px;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.detail-date {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.9rem;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h4 {
  color: white;
  margin-bottom: 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label {
  font-weight: bold;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
  margin-right: 8px;
}

.info-item .value {
  color: white;
  font-size: 1rem;
}

.itinerary {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.itinerary-day {
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.day-header h5 {
  color: white;
  margin: 0;
  flex: 1;
  margin-right: 10px;
}

.day-meta {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.day-content p {
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 15px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.attractions h6 {
  color: white;
  margin-bottom: 10px;
}

.attraction-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.attraction-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 15px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-left: 4px solid #667eea;
  transition: all 0.3s ease;
}

.attraction-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.attraction-info h6 {
  color: white;
  margin-bottom: 5px;
}

.attraction-info p {
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.attraction-meta {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .plans-grid {
    grid-template-columns: 1fr;
  }
  
  .plan-actions {
    flex-direction: column;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .day-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>



























