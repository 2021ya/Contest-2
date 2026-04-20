<template>
  <div class="planner">
    <div class="container">
      <div class="page-header">
        <h1>AI智能旅游规划</h1>
        <p>填写您的旅游偏好，AI将为您生成个性化的旅游路线</p>
      </div>

      <div class="planner-content">
        <div class="form-section">
          <el-card class="form-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Edit /></el-icon>
                <span>旅游偏好设置</span>
              </div>
            </template>
            
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-width="120px"
              class="travel-form"
            >
              <el-form-item label="计划标题" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="请输入旅游计划标题"
                  clearable
                />
              </el-form-item>

              <el-form-item label="目的地" prop="destination">
                <el-input
                  v-model="form.destination"
                  placeholder="请输入目的地，如：北京、上海、杭州等"
                  clearable
                />
              </el-form-item>

              <el-form-item label="出发日期" prop="startDate">
                <el-date-picker
                  v-model="form.startDate"
                  type="date"
                  placeholder="选择出发日期"
                  style="width: 100%"
                  :disabled-date="disabledStartDate"
                  @change="calculateDays"
                />
              </el-form-item>

              <el-form-item label="返回日期" prop="endDate">
                <el-date-picker
                  v-model="form.endDate"
                  type="date"
                  placeholder="选择返回日期"
                  style="width: 100%"
                  :disabled-date="disabledEndDate"
                  @change="calculateDays"
                />
              </el-form-item>

              <el-form-item label="预算范围" prop="budget">
                <el-input-number
                  v-model="form.budget"
                  :min="0"
                  :max="100000"
                  placeholder="请输入预算金额"
                  style="width: 100%"
                />
                <span class="form-tip">单位：元</span>
              </el-form-item>

              <el-form-item label="旅游天数" prop="days">
                <el-input-number
                  v-model="form.days"
                  :min="1"
                  :max="30"
                  placeholder="请输入旅游天数"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="旅游风格" prop="travelStyle">
                <el-select
                  v-model="form.travelStyle"
                  placeholder="请选择旅游风格"
                  style="width: 100%"
                >
                  <el-option label="休闲度假" value="relax" />
                  <el-option label="文化探索" value="culture" />
                  <el-option label="美食之旅" value="food" />
                  <el-option label="自然风光" value="nature" />
                  <el-option label="冒险体验" value="adventure" />
                  <el-option label="购物娱乐" value="shopping" />
                </el-select>
              </el-form-item>

              <el-form-item label="兴趣爱好">
                <el-checkbox-group v-model="form.interests">
                  <el-checkbox label="历史古迹">历史古迹</el-checkbox>
                  <el-checkbox label="自然风光">自然风光</el-checkbox>
                  <el-checkbox label="美食体验">美食体验</el-checkbox>
                  <el-checkbox label="购物娱乐">购物娱乐</el-checkbox>
                  <el-checkbox label="文化艺术">文化艺术</el-checkbox>
                  <el-checkbox label="户外运动">户外运动</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="团队人数" prop="groupSize">
                <el-input-number
                  v-model="form.groupSize"
                  :min="1"
                  :max="20"
                  placeholder="请输入团队人数"
                  style="width: 100%"
                />
              </el-form-item>

              <el-form-item label="住宿类型" prop="accommodationType">
                <el-select
                  v-model="form.accommodationType"
                  placeholder="请选择住宿类型"
                  style="width: 100%"
                >
                  <el-option label="经济型酒店" value="budget" />
                  <el-option label="商务酒店" value="business" />
                  <el-option label="豪华酒店" value="luxury" />
                  <el-option label="民宿" value="homestay" />
                  <el-option label="青旅" value="hostel" />
                </el-select>
              </el-form-item>

              <el-form-item label="交通方式" prop="transportation">
                <el-select
                  v-model="form.transportation"
                  placeholder="请选择主要交通方式"
                  style="width: 100%"
                >
                  <el-option label="飞机" value="flight" />
                  <el-option label="高铁" value="train" />
                  <el-option label="自驾" value="drive" />
                  <el-option label="公共交通" value="public" />
                </el-select>
              </el-form-item>

              <el-form-item label="计划描述">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请描述您的特殊需求或期望..."
                />
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  :loading="loading"
                  @click="generatePlan"
                  class="generate-btn"
                >
                  <el-icon><MagicStick /></el-icon>
                  {{ loading ? 'AI正在规划中...' : '生成AI旅游计划' }}
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>

        <div class="result-section" v-if="result">
          <el-card class="result-card">
            <template #header>
              <div class="card-header">
                <el-icon><Document /></el-icon>
                <span>AI生成的旅游计划</span>
              </div>
            </template>
            
            <div class="plan-result">
              <h3>{{ result.title }}</h3>
              <p class="plan-description">{{ result.description }}</p>
              
              <div class="plan-info">
                <el-tag type="info">出发：{{ formatDate(result.startDate) }}</el-tag>
                <el-tag type="info">返回：{{ formatDate(result.endDate) }}</el-tag>
                <el-tag type="success">预算：¥{{ result.budget }}</el-tag>
                <el-tag type="warning">AI生成</el-tag>
              </div>

              <div class="destinations" v-if="result.destinations && result.destinations.length > 0">
                <h4>行程安排</h4>
                <div class="destination-scroll-container">
                  <div class="destination-list">
                    <div
                      v-for="destination in result.destinations"
                      :key="destination.id"
                      class="destination-item"
                    >
                      <div class="day-header">
                        <el-icon><Calendar /></el-icon>
                        <span>第{{ destination.dayOrder }}天 - {{ destination.name }}</span>
                      </div>
                      
                      <div class="attractions" v-if="destination.attractions && destination.attractions.length > 0">
                        <div
                          v-for="attraction in destination.attractions"
                          :key="attraction.id"
                          class="attraction-item"
                        >
                          <div class="attraction-info">
                            <h5>{{ attraction.name }}</h5>
                            <p>{{ attraction.description }}</p>
                            <div class="attraction-meta">
                              <el-tag size="small" type="success">评分: {{ attraction.rating }}</el-tag>
                              <el-tag size="small" type="info">时长: {{ attraction.visitDuration }}分钟</el-tag>
                              <el-tag size="small" type="warning" v-if="attraction.ticketPrice > 0">
                                门票: ¥{{ attraction.ticketPrice }}
                              </el-tag>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div v-else class="no-attractions">
                        <p>暂无景点信息</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="no-destinations">
                <h4>行程安排</h4>
                <p>正在生成详细的行程安排，请稍候...</p>
                <el-button type="primary" @click="regeneratePlan">
                  <el-icon><Refresh /></el-icon>
                  重新生成详细计划
                </el-button>
              </div>

              <div class="plan-actions">
                <el-button type="primary" @click="savePlan">
                  <el-icon><Download /></el-icon>
                  保存计划
                </el-button>
                <el-button type="success" @click="regeneratePlan">
                  <el-icon><Refresh /></el-icon>
                  重新生成
                </el-button>
                <el-button @click="viewOnMap">
                  <el-icon><Location /></el-icon>
                  在地图上查看
                </el-button>
                <el-button @click="editPlan">
                  <el-icon><Edit /></el-icon>
                  编辑计划
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, MagicStick, Document, Calendar, Download, Location, Refresh } from '@element-plus/icons-vue'
import { useTravelPlanStore } from '@/stores/travelPlan'
import dayjs from 'dayjs'

const formRef = ref()
const loading = ref(false)
const result = ref(null)
const travelPlanStore = useTravelPlanStore()

const form = reactive({
  title: '',
  destination: '',
  startDate: null,
  endDate: null,
  budget: null,
  days: null,
  travelStyle: '',
  interests: [],
  groupSize: null,
  accommodationType: '',
  transportation: '',
  description: ''
})

const rules = {
  title: [
    { required: true, message: '请输入计划标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度应在2-50个字符之间', trigger: 'blur' }
  ],
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' },
    { min: 2, max: 20, message: '目的地长度应在2-20个字符之间', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择出发日期', trigger: 'change' },
    { 
      validator: (rule, value, callback) => {
        if (value && form.endDate && new Date(value) >= new Date(form.endDate)) {
          callback(new Error('出发日期必须早于返回日期'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ],
  endDate: [
    { required: true, message: '请选择返回日期', trigger: 'change' },
    { 
      validator: (rule, value, callback) => {
        if (value && form.startDate && new Date(value) <= new Date(form.startDate)) {
          callback(new Error('返回日期必须晚于出发日期'))
        } else if (value && form.startDate) {
          const daysDiff = Math.ceil((new Date(value) - new Date(form.startDate)) / (1000 * 60 * 60 * 24))
          if (daysDiff > 30) {
            callback(new Error('旅游天数不能超过30天'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ],
  budget: [
    { required: true, message: '请输入预算', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value < 100) {
          callback(new Error('预算不能少于100元'))
        } else if (value > 100000) {
          callback(new Error('预算不能超过100,000元'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  days: [
    { required: true, message: '请输入旅游天数', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value < 1) {
          callback(new Error('旅游天数不能少于1天'))
        } else if (value > 30) {
          callback(new Error('旅游天数不能超过30天'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  travelStyle: [
    { required: true, message: '请选择旅游风格', trigger: 'change' }
  ],
  groupSize: [
    { required: true, message: '请输入团队人数', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value < 1) {
          callback(new Error('团队人数不能少于1人'))
        } else if (value > 20) {
          callback(new Error('团队人数不能超过20人'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  accommodationType: [
    { required: true, message: '请选择住宿类型', trigger: 'change' }
  ],
  transportation: [
    { required: true, message: '请选择交通方式', trigger: 'change' }
  ]
}

const generatePlan = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    // 显示生成进度
    ElMessage.info('AI正在分析您的需求，请稍候...')
    
    // 准备请求数据，确保日期格式正确
    const requestData = {
      title: String(form.title || ''),
      destination: String(form.destination || ''),
      startDate: form.startDate ? dayjs(form.startDate).format('YYYY-MM-DDTHH:mm:ss') : null,
      endDate: form.endDate ? dayjs(form.endDate).format('YYYY-MM-DDTHH:mm:ss') : null,
      budget: Number(form.budget) || 0,
      days: Number(form.days) || 1,
      travelStyle: String(form.travelStyle || ''),
      interests: Array.isArray(form.interests) ? form.interests : [],
      groupSize: Number(form.groupSize) || 1,
      accommodationType: String(form.accommodationType || ''),
      transportation: String(form.transportation || ''),
      description: String(form.description || '')
    }
    
    console.log('原始表单数据:', form)
    console.log('发送请求数据:', requestData)
    console.log('数据类型检查:', {
      title: typeof requestData.title,
      destination: typeof requestData.destination,
      budget: typeof requestData.budget,
      days: typeof requestData.days
    })
    
    // 调用AI规划服务
    const response = await travelPlanStore.generateTravelPlan(requestData)
    result.value = response
    
    ElMessage.success({
      message: 'AI旅游计划生成成功！',
      duration: 3000,
      showClose: true
    })
  } catch (error) {
    console.error('生成计划失败:', error)
    
    // 根据错误类型显示不同的错误信息
    if (error.response) {
      const status = error.response.status
      if (status === 400) {
        ElMessage.error('请求参数有误，请检查填写的信息')
        console.error('请求数据:', form)
        console.error('错误详情:', error.response.data)
      } else if (status === 500) {
        ElMessage.error('服务器内部错误，请稍后重试')
      } else {
        ElMessage.error('网络错误，请检查网络连接')
      }
    } else if (error.message && error.message.includes('validation')) {
      ElMessage.error('表单验证失败，请检查填写的信息')
    } else {
      ElMessage.error('生成计划失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

const savePlan = () => {
  if (result.value) {
    travelPlanStore.saveTravelPlan(result.value)
    ElMessage.success('计划保存成功！')
  }
}

const viewOnMap = () => {
  // 跳转到地图页面并传递计划数据
  this.$router.push({
    name: 'Map',
    query: { planId: result.value.id }
  })
}

const regeneratePlan = async () => {
  if (!result.value) return
  
  try {
    ElMessage.info('正在重新生成旅游计划...')
    loading.value = true
    
    // 重新生成计划
    const response = await travelPlanStore.generateTravelPlan(form)
    result.value = response
    
    ElMessage.success('旅游计划重新生成成功！')
  } catch (error) {
    console.error('重新生成计划失败:', error)
    ElMessage.error('重新生成计划失败，请重试')
  } finally {
    loading.value = false
  }
}

const editPlan = () => {
  if (!result.value) return
  
  // 将结果数据填充到表单中，允许用户编辑
  form.title = result.value.title
  form.destination = result.value.destinations?.[0]?.name || ''
  form.startDate = result.value.startDate
  form.endDate = result.value.endDate
  form.budget = result.value.budget
  form.description = result.value.description
  
  // 滚动到表单顶部
  document.querySelector('.form-section')?.scrollIntoView({ behavior: 'smooth' })
  
  ElMessage.info('计划数据已填充到表单中，您可以进行修改后重新生成')
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 禁用过去的日期
const disabledStartDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 禁用今天之前的日期
}

const disabledEndDate = (time) => {
  if (form.startDate) {
    return time.getTime() <= new Date(form.startDate).getTime()
  }
  return time.getTime() < Date.now() - 8.64e7
}

// 自动计算旅游天数
const calculateDays = () => {
  if (form.startDate && form.endDate) {
    const start = new Date(form.startDate)
    const end = new Date(form.endDate)
    const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1
    if (days > 0 && days <= 30) {
      form.days = days
    }
  }
}
</script>

<style scoped>
@keyframes cardJumpOut {
  0% {
    transform: translateY(-5px) scale(1);
    opacity: 1;
    z-index: 10;
  }
  50% {
    transform: translateY(-20px) scale(1.05);
    opacity: 0.9;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  }
  100% {
    transform: translateY(-50px) scale(1.1);
    opacity: 0;
  }
}

@keyframes titleFlow {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.planner {
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
}

.page-header h1 {
  font-size: 2.5rem;
  color: white;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  background: linear-gradient(90deg, rgba(255,255,255,0), #fff 51.33%, rgba(255,255,255,0));
  background-size: 200% 100%;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleFlow 10s ease-in-out infinite;
}

.page-header p {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.85);
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
  background: linear-gradient(90deg, rgba(255,255,255,0), #fff 51.33%, rgba(255,255,255,0));
  background-size: 200% 100%;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: titleFlow 10s ease-in-out infinite;
}

.planner-content {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
}

.form-section, .result-section {
  min-height: 700px;
  display: flex;
  flex-direction: column;
}

.form-card {
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  height: 100%;
  color: white;
}

.form-card:hover {
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.result-card {
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  height: 100%;
  color: white;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.2rem;
  font-weight: bold;
  color: white;
}

.travel-form {
  max-width: 600px;
}

.form-tip {
  margin-left: 10px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
}

.generate-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
}

.plan-result h3 {
  color: white;
  margin-bottom: 10px;
}

.plan-description {
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 20px;
  line-height: 1.6;
}

.plan-info {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.destinations h4 {
  color: white;
  margin-bottom: 20px;
  font-size: 1.3rem;
}

.destination-scroll-container {
  max-height: 400px;
  overflow-y: auto;
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 10px;
  background: rgba(255, 255, 255, 0.05);
}

.destination-scroll-container::-webkit-scrollbar {
  width: 8px;
}

.destination-scroll-container::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.destination-scroll-container::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}

.destination-scroll-container::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

.destination-item {
  margin-bottom: 30px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.day-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  color: white;
  margin-bottom: 15px;
}

.attraction-item {
  margin-bottom: 15px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-left: 4px solid #667eea;
}

.attraction-info h5 {
  color: white;
  margin-bottom: 8px;
}

.attraction-info p {
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 10px;
  line-height: 1.5;
}

.attraction-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.plan-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
  justify-content: center;
}

@media (min-width: 1200px) {
  .planner-content {
    grid-template-columns: 1fr 1fr;
  }
}

.no-destinations {
  text-align: center;
  padding: 40px 20px;
  color: rgba(255, 255, 255, 0.85);
}

.no-destinations h4 {
  color: white;
  margin-bottom: 15px;
}

.no-attractions {
  text-align: center;
  padding: 20px;
  color: rgba(255, 255, 255, 0.7);
  font-style: italic;
}

@media (max-width: 768px) {
  .page-header h1 {
    font-size: 2rem;
  }
  
  .form-section, .result-section {
    min-height: auto;
  }
  
  .plan-info {
    flex-direction: column;
  }
  
  .plan-actions {
    flex-direction: column;
  }
  
  .destination-scroll-container {
    max-height: 300px;
  }
}
</style>
























