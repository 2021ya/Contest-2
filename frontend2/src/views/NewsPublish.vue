<template>
  <div class="news-publish">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">发布新闻</h1>
      <p class="page-description">创建和发布新的旅游新闻资讯</p>
    </div>

    <!-- 发布表单 -->
    <div class="publish-form">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="news-form"
      >
        <!-- 基本信息 -->
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="新闻标题" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="请输入新闻标题"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="新闻分类" prop="category">
                <el-select
                  v-model="form.category"
                  placeholder="请选择分类"
                  style="width: 100%"
                >
                  <el-option label="旅游资讯" value="TRAVEL_NEWS" />
                  <el-option label="景点公告" value="ATTRACTION_NOTICE" />
                  <el-option label="活动通知" value="ACTIVITY_NOTICE" />
                  <el-option label="政策解读" value="POLICY_NEWS" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="作者" prop="author">
                <el-input
                  v-model="form.author"
                  placeholder="请输入作者姓名"
                  maxlength="20"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="发布状态" prop="status">
                <el-select
                  v-model="form.status"
                  placeholder="请选择状态"
                  style="width: 100%"
                >
                  <el-option label="草稿" value="DRAFT" />
                  <el-option label="立即发布" value="PUBLISHED" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="新闻摘要" prop="summary">
            <el-input
              v-model="form.summary"
              type="textarea"
              :rows="3"
              placeholder="请输入新闻摘要，简要描述新闻内容"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="标签">
            <el-tag
              v-for="tag in form.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
              style="margin-right: 8px; margin-bottom: 8px;"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-if="inputVisible"
              ref="inputRef"
              v-model="inputValue"
              size="small"
              style="width: 90px;"
              @keyup.enter="handleInputConfirm"
              @blur="handleInputConfirm"
            />
            <el-button
              v-else
              size="small"
              @click="showInput"
              style="margin-left: 8px;"
            >
              + 添加标签
            </el-button>
          </el-form-item>
        </div>

        <!-- 内容编辑 -->
        <div class="form-section">
          <h3 class="section-title">新闻内容</h3>
          <el-form-item label="正文内容" prop="content">
            <div class="editor-container">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="15"
                placeholder="请输入新闻正文内容，支持HTML格式"
                style="font-family: 'Courier New', monospace;"
              />
              <div class="editor-tips">
                <p>💡 编辑提示：</p>
                <ul>
                  <li>支持HTML标签，如 &lt;p&gt;、&lt;br&gt;、&lt;strong&gt; 等</li>
                  <li>可以使用 &lt;img&gt; 标签插入图片</li>
                  <li>建议使用 &lt;p&gt; 标签分段</li>
                </ul>
              </div>
            </div>
          </el-form-item>
        </div>

        <!-- 发布设置 -->
        <div class="form-section">
          <h3 class="section-title">发布设置</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="发布时间">
                <el-date-picker
                  v-model="form.publishTime"
                  type="datetime"
                  placeholder="选择发布时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="置顶设置">
                <el-switch
                  v-model="form.isTop"
                  active-text="置顶"
                  inactive-text="普通"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="封面图片">
            <el-upload
              class="cover-uploader"
              :action="uploadAction"
              :show-file-list="false"
              :on-success="handleCoverSuccess"
              :before-upload="beforeCoverUpload"
            >
              <img v-if="form.coverImage" :src="form.coverImage" class="cover-image" />
              <el-icon v-else class="cover-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tips">
              <p>建议尺寸：800x400px，支持 JPG、PNG 格式</p>
            </div>
          </el-form-item>
        </div>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <el-button size="large" @click="handleSaveDraft">
            <el-icon><Document /></el-icon>
            保存草稿
          </el-button>
          <el-button size="large" type="primary" @click="handlePublish">
            <el-icon><Upload /></el-icon>
            发布新闻
          </el-button>
          <el-button size="large" @click="handleCancel">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { Plus, Document, Upload, Close } from '@element-plus/icons-vue'

const router = useRouter()

// 表单引用
const formRef = ref()
const inputRef = ref()
const inputVisible = ref(false)
const inputValue = ref('')

// 上传配置
const uploadAction = 'http://localhost:3002/upload'

// 表单数据
const form = reactive({
  title: '',
  category: '',
  author: '',
  status: 'DRAFT',
  summary: '',
  content: '',
  tags: [],
  publishTime: '',
  isTop: false,
  coverImage: ''
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择新闻分类', trigger: 'change' }
  ],
  author: [
    { required: true, message: '请输入作者姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '作者姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择发布状态', trigger: 'change' }
  ],
  summary: [
    { required: true, message: '请输入新闻摘要', trigger: 'blur' },
    { min: 10, max: 200, message: '摘要长度在 10 到 200 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入新闻内容', trigger: 'blur' },
    { min: 50, message: '内容至少 50 个字符', trigger: 'blur' }
  ]
}

// 标签处理
const removeTag = (tag) => {
  const index = form.tags.indexOf(tag)
  if (index > -1) {
    form.tags.splice(index, 1)
  }
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    inputRef.value?.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value && !form.tags.includes(inputValue.value)) {
    form.tags.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

// 图片上传处理
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleCoverSuccess = (response) => {
  if (response.success) {
    form.coverImage = response.url
    ElMessage.success('封面图片上传成功')
  } else {
    ElMessage.error('封面图片上传失败')
  }
}

// 表单操作
const handleSaveDraft = async () => {
  try {
    await formRef.value.validate()
    
    // 模拟保存草稿
    const draftData = {
      ...form,
      status: 'DRAFT',
      id: Date.now(),
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    console.log('保存草稿:', draftData)
    ElMessage.success('草稿保存成功')
    
    // 跳转到新闻管理页面
    router.push('/news')
  } catch (error) {
    ElMessage.error('请检查表单信息')
  }
}

const handlePublish = async () => {
  try {
    await formRef.value.validate()
    
    // 确认发布
    await ElMessageBox.confirm('确定要发布这条新闻吗？', '确认发布', {
      confirmButtonText: '确定发布',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 模拟发布新闻
    const publishData = {
      ...form,
      status: 'PUBLISHED',
      id: Date.now(),
      publishTime: form.publishTime || new Date().toISOString(),
      views: 0,
      likes: 0,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    console.log('发布新闻:', publishData)
    ElMessage.success('新闻发布成功')
    
    // 跳转到新闻管理页面
    router.push('/news')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('请检查表单信息')
    }
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要离开吗？未保存的内容将丢失。', '确认离开', {
      confirmButtonText: '确定离开',
      cancelButtonText: '继续编辑',
      type: 'warning'
    })
    
    router.push('/news')
  } catch {
    // 用户取消离开
  }
}
</script>

<style scoped>
.news-publish {
  padding: 0;
}

.publish-form {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  backdrop-filter: blur(10px) !important;
  padding: 30px !important;
}

.form-section {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--border-primary);
}

.form-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  color: var(--primary-color) !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  margin-bottom: 20px !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3) !important;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  margin-right: 12px;
  border-radius: 2px;
}

.news-form {
  max-width: 100%;
}

.editor-container {
  position: relative;
}

.editor-tips {
  margin-top: 10px;
  padding: 15px;
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  color: #e0e0e0 !important;
  font-size: 12px !important;
}

.editor-tips p {
  margin: 0 0 8px 0 !important;
  color: var(--primary-color) !important;
  font-weight: 500 !important;
}

.editor-tips ul {
  margin: 0 !important;
  padding-left: 20px !important;
}

.editor-tips li {
  margin-bottom: 4px !important;
  color: #b0b0b0 !important;
}

.cover-uploader {
  border: 2px dashed var(--border-primary) !important;
  border-radius: 8px !important;
  width: 200px !important;
  height: 120px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  transition: all 0.3s ease !important;
  cursor: pointer !important;
}

.cover-uploader:hover {
  border-color: var(--primary-color) !important;
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
}

.cover-image {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
  border-radius: 6px !important;
}

.cover-uploader-icon {
  font-size: 28px !important;
  color: var(--primary-color) !important;
}

.upload-tips {
  margin-top: 8px !important;
  color: #888888 !important;
  font-size: 12px !important;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid var(--border-primary);
}

/* 表单元素样式覆盖 */
.news-publish :deep(.el-form-item__label) {
  color: #e0e0e0 !important;
  font-weight: 500 !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
}

.news-publish :deep(.el-input__wrapper) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.news-publish :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.news-publish :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.news-publish :deep(.el-input__inner) {
  color: #e0e0e0 !important;
  background: transparent !important;
}

.news-publish :deep(.el-input__inner::placeholder) {
  color: #888888 !important;
}

.news-publish :deep(.el-textarea__inner) {
  color: #e0e0e0 !important;
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.news-publish :deep(.el-textarea__inner:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.news-publish :deep(.el-textarea__inner:focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.news-publish :deep(.el-textarea__inner::placeholder) {
  color: #888888 !important;
}

.news-publish :deep(.el-select .el-input__wrapper) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.news-publish :deep(.el-select .el-input__wrapper:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.news-publish :deep(.el-select .el-input__wrapper.is-focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.news-publish :deep(.el-date-editor) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 8px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.news-publish :deep(.el-date-editor:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.2) !important;
}

.news-publish :deep(.el-date-editor.is-active) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(var(--primary-rgb), 0.2) !important;
}

.news-publish :deep(.el-date-editor .el-input__inner) {
  color: #e0e0e0 !important;
  background: transparent !important;
}

.news-publish :deep(.el-date-editor .el-input__inner::placeholder) {
  color: #888888 !important;
}

.news-publish :deep(.el-button) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border: 1px solid var(--primary-color) !important;
  color: #ffffff !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.3) !important;
  transition: all 0.3s ease !important;
  font-weight: 500 !important;
}

.news-publish :deep(.el-button:hover) {
  background: linear-gradient(135deg, var(--secondary-color), var(--primary-color)) !important;
  box-shadow: 0 6px 16px rgba(var(--primary-rgb), 0.4) !important;
  transform: translateY(-2px) !important;
}

.news-publish :deep(.el-button:active) {
  transform: translateY(0) !important;
  box-shadow: 0 2px 8px rgba(var(--primary-rgb), 0.3) !important;
}

.news-publish :deep(.el-button--primary) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-color: var(--primary-color) !important;
}

.news-publish :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, var(--secondary-color), var(--primary-color)) !important;
}

.news-publish :deep(.el-tag) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border: 1px solid var(--primary-color) !important;
  color: #ffffff !important;
  border-radius: 6px !important;
}

.news-publish :deep(.el-tag .el-tag__close) {
  color: #ffffff !important;
}

.news-publish :deep(.el-tag .el-tag__close:hover) {
  background: rgba(255, 255, 255, 0.2) !important;
}

.news-publish :deep(.el-switch__core) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
}

.news-publish :deep(.el-switch.is-checked .el-switch__core) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-color: var(--primary-color) !important;
}

@media (max-width: 768px) {
  .publish-form {
    padding: 20px !important;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 12px;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
}
</style>
