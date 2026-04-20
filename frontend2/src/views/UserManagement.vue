<template>
  <div class="user-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <p class="page-description">管理系统用户信息和权限</p>
    </div>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="searchForm.email"
            placeholder="请输入邮箱"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="活跃" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加用户
      </el-button>
      <el-button type="danger" :disabled="!selectedUsers.length" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        批量删除
      </el-button>
    </div>

    <!-- 用户表格 -->
    <div class="content-card">
      <div class="card-header">用户列表</div>
      <div class="card-body">
        <el-table
          :data="filteredUsers"
          v-loading="loading"
          @selection-change="handleSelectionChange"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="头像" width="80">
            <template #default="{ row }">
              <el-avatar :size="40" :src="row.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="email" label="邮箱" width="200" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
                {{ row.status === 'active' ? '活跃' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="role" label="角色" width="100" />
          <el-table-column prop="createdAt" label="注册时间" width="160" />
          <el-table-column prop="lastLogin" label="最后登录" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                size="small"
                :type="row.status === 'active' ? 'warning' : 'success'"
                @click="handleToggleStatus(row)"
              >
                {{ row.status === 'active' ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalUsers"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
            <el-option label="VIP用户" value="vip" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio label="active">活跃</el-radio>
            <el-radio label="inactive">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedUsers = ref([])
const userFormRef = ref()

// 搜索表单
const searchForm = reactive({
  username: '',
  email: '',
  status: ''
})

// 用户表单
const userForm = reactive({
  id: null,
  username: '',
  email: '',
  phone: '',
  password: '',
  role: 'user',
  status: 'active'
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 模拟用户数据
const users = ref([
  {
    id: 1,
    username: 'zhangsan',
    email: 'zhangsan@example.com',
    phone: '13800138001',
    role: 'user',
    status: 'active',
    avatar: '',
    createdAt: '2024-01-15 10:30:00',
    lastLogin: '2024-03-15 14:30:00'
  },
  {
    id: 2,
    username: 'lisi',
    email: 'lisi@example.com',
    phone: '13800138002',
    role: 'vip',
    status: 'active',
    avatar: '',
    createdAt: '2024-02-20 09:15:00',
    lastLogin: '2024-03-14 16:45:00'
  },
  {
    id: 3,
    username: 'wangwu',
    email: 'wangwu@example.com',
    phone: '13800138003',
    role: 'user',
    status: 'inactive',
    avatar: '',
    createdAt: '2024-03-01 14:20:00',
    lastLogin: '2024-03-10 11:30:00'
  },
  {
    id: 4,
    username: 'admin',
    email: 'admin@example.com',
    phone: '13800138004',
    role: 'admin',
    status: 'active',
    avatar: '',
    createdAt: '2024-01-01 00:00:00',
    lastLogin: '2024-03-15 15:00:00'
  },
  {
    id: 5,
    username: 'testuser',
    email: 'test@example.com',
    phone: '13800138005',
    role: 'user',
    status: 'active',
    avatar: '',
    createdAt: '2024-03-10 16:30:00',
    lastLogin: '2024-03-15 12:00:00'
  }
])

// 计算属性
const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '添加用户')
const totalUsers = computed(() => users.value.length)

// 过滤后的用户列表
const filteredUsers = computed(() => {
  let result = users.value

  if (searchForm.username) {
    result = result.filter(user => 
      user.username.toLowerCase().includes(searchForm.username.toLowerCase())
    )
  }

  if (searchForm.email) {
    result = result.filter(user => 
      user.email.toLowerCase().includes(searchForm.email.toLowerCase())
    )
  }

  if (searchForm.status) {
    result = result.filter(user => user.status === searchForm.status)
  }

  return result
})

// 方法
const handleSearch = () => {
  currentPage.value = 1
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.email = ''
  searchForm.status = ''
  currentPage.value = 1
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(userForm, { ...row, password: '' })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = users.value.findIndex(user => user.id === row.id)
    if (index > -1) {
      users.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    selectedUsers.value.forEach(user => {
      const index = users.value.findIndex(u => u.id === user.id)
      if (index > -1) {
        users.value.splice(index, 1)
      }
    })
    
    selectedUsers.value = []
    ElMessage.success('批量删除成功')
  } catch {
    // 用户取消删除
  }
}

const handleToggleStatus = (row) => {
  row.status = row.status === 'active' ? 'inactive' : 'active'
  ElMessage.success(`用户已${row.status === 'active' ? '启用' : '禁用'}`)
}

const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const handleSubmit = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        if (isEdit.value) {
          // 编辑用户
          const index = users.value.findIndex(user => user.id === userForm.id)
          if (index > -1) {
            users.value[index] = { ...users.value[index], ...userForm }
          }
          ElMessage.success('用户信息更新成功')
        } else {
          // 添加用户
          const newUser = {
            ...userForm,
            id: Date.now(),
            avatar: '',
            createdAt: new Date().toLocaleString(),
            lastLogin: '从未登录'
          }
          users.value.unshift(newUser)
          ElMessage.success('用户添加成功')
        }
        
        dialogVisible.value = false
        resetForm()
      } catch (error) {
        ElMessage.error('操作失败，请重试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  Object.assign(userForm, {
    id: null,
    username: '',
    email: '',
    phone: '',
    password: '',
    role: 'user',
    status: 'active'
  })
  if (userFormRef.value) {
    userFormRef.value.clearValidate()
  }
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.user-management {
  padding: 0;
  background: transparent;
}

/* 搜索表单样式 - 使用主题色变量 */
.search-form {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  padding: 24px !important;
  border-radius: 16px !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3) !important;
  margin-bottom: 24px !important;
  backdrop-filter: blur(10px) !important;
  position: relative !important;
  overflow: hidden !important;
}

.search-form::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color), var(--accent-color));
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.action-buttons {
  margin-bottom: 24px;
  display: flex;
  gap: 12px;
}

/* 表格样式覆盖 - 使用主题深色背景 */
.user-management :deep(.el-table) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5) !important;
}

.user-management :deep(.el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-bottom: 2px solid var(--primary-color) !important;
}

.user-management :deep(.el-table__header th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
  font-weight: 600 !important;
  text-align: center !important;
  border: none !important;
  border-right: 1px solid rgba(255, 255, 255, 0.2) !important;
  padding: 16px 8px !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3) !important;
}

.user-management :deep(.el-table__header th:last-child) {
  border-right: none !important;
}

/* 确保表头单元格没有白色背景 */
.user-management :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.user-management :deep(.el-table__header-wrapper .el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.user-management :deep(.el-table__header-wrapper .el-table__header th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

/* 强制覆盖所有可能的表头白色背景 */
.user-management :deep(.el-table thead) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.user-management :deep(.el-table thead tr) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.user-management :deep(.el-table thead tr th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
}

/* 覆盖Element Plus默认样式 */
.user-management :deep(.el-table__header-wrapper .el-table__header thead tr th) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
}

/* 确保表头容器也是深色 */
.user-management :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.user-management :deep(.el-table__header-wrapper .el-table__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
}

.user-management :deep(.el-table__body) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
}

.user-management :deep(.el-table__body td) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  color: #e0e0e0 !important;
  border: none !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  padding: 12px 8px !important;
  text-align: center !important;
}

/* 表格行悬停效果 */
.user-management :deep(.el-table__body tr:hover td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 20px rgba(var(--primary-rgb), 0.3) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 表格选中行样式 */
.user-management :deep(.el-table__row.current-row td) {
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
  color: var(--primary-color) !important;
  box-shadow: inset 0 0 15px rgba(var(--primary-rgb), 0.4) !important;
  border-bottom: 1px solid var(--primary-color) !important;
}

/* 按钮样式覆盖 - 使用主题色变量 */
.user-management :deep(.el-button--small) {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  color: var(--text-primary) !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  font-weight: 500 !important;
  position: relative !important;
  overflow: hidden !important;
}

.user-management :deep(.el-button--small::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.5s ease;
}

.user-management :deep(.el-button--small:hover::before) {
  left: 100%;
}

.user-management :deep(.el-button--small:hover) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-color: var(--primary-color) !important;
  color: #ffffff !important;
  box-shadow: 0 4px 20px rgba(var(--primary-rgb), 0.4) !important;
  transform: translateY(-2px) !important;
}

/* 特殊按钮类型 */
.user-management :deep(.el-button--small.el-button--success) {
  background: linear-gradient(135deg, #10b981, #059669) !important;
  border-color: #10b981 !important;
  color: #ffffff !important;
}

.user-management :deep(.el-button--small.el-button--success:hover) {
  background: linear-gradient(135deg, #059669, #047857) !important;
  box-shadow: 0 4px 20px rgba(16, 185, 129, 0.4) !important;
}

.user-management :deep(.el-button--small.el-button--danger) {
  background: linear-gradient(135deg, #ef4444, #dc2626) !important;
  border-color: #ef4444 !important;
  color: #ffffff !important;
}

.user-management :deep(.el-button--small.el-button--danger:hover) {
  background: linear-gradient(135deg, #dc2626, #b91c1c) !important;
  box-shadow: 0 4px 20px rgba(239, 68, 68, 0.4) !important;
}

.user-management :deep(.el-button--small.el-button--warning) {
  background: linear-gradient(135deg, #f59e0b, #d97706) !important;
  border-color: #f59e0b !important;
  color: #ffffff !important;
}

.user-management :deep(.el-button--small.el-button--warning:hover) {
  background: linear-gradient(135deg, #d97706, #b45309) !important;
  box-shadow: 0 4px 20px rgba(245, 158, 11, 0.4) !important;
}

/* 复选框样式 - 使用主题色变量 */
.user-management :deep(.el-checkbox__inner) {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 6px !important;
  transition: all 0.3s ease !important;
}

.user-management :deep(.el-checkbox__inner:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 12px rgba(var(--primary-rgb), 0.3) !important;
}

.user-management :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 15px rgba(var(--primary-rgb), 0.4) !important;
}

.user-management :deep(.el-checkbox__input.is-checked .el-checkbox__inner::after) {
  border-color: #ffffff !important;
  border-width: 2px !important;
}

/* 标签样式 - 使用主题色变量 */
.user-management :deep(.el-tag) {
  border-radius: 20px !important;
  font-weight: 500 !important;
  padding: 4px 12px !important;
  border: none !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2) !important;
}

.user-management :deep(.el-tag--success) {
  background: linear-gradient(135deg, #10b981, #059669) !important;
  color: #ffffff !important;
}

.user-management :deep(.el-tag--danger) {
  background: linear-gradient(135deg, #ef4444, #dc2626) !important;
  color: #ffffff !important;
}

/* 头像样式 */
.user-management :deep(.el-avatar) {
  border: 2px solid var(--border-primary) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3) !important;
  transition: all 0.3s ease !important;
}

.user-management :deep(.el-avatar:hover) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 15px rgba(var(--primary-rgb), 0.4) !important;
  transform: scale(1.1) !important;
}

/* 分页样式 - 使用深色主题背景 */
.user-management :deep(.pagination-container) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 12px !important;
  padding: 20px !important;
  margin-top: 24px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.4) !important;
}

.user-management :deep(.el-pagination) {
  justify-content: center !important;
}

.user-management :deep(.el-pagination .el-pager li) {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  color: var(--text-primary) !important;
  border-radius: 8px !important;
  margin: 0 4px !important;
  transition: all 0.3s ease !important;
}

.user-management :deep(.el-pagination .el-pager li:hover) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.3) !important;
}

.user-management :deep(.el-pagination .el-pager li.is-active) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
  box-shadow: 0 0 15px rgba(var(--primary-rgb), 0.4) !important;
}

.user-management :deep(.el-pagination .btn-prev),
.user-management :deep(.el-pagination .btn-next) {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  color: var(--text-primary) !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
}

.user-management :deep(.el-pagination .btn-prev:hover),
.user-management :deep(.el-pagination .btn-next:hover) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
  box-shadow: 0 4px 12px rgba(var(--primary-rgb), 0.3) !important;
}

/* 对话框样式覆盖 */
.user-management :deep(.el-dialog) {
  background: linear-gradient(135deg, var(--bg-primary), var(--bg-secondary)) !important;
  border: 1px solid var(--border-primary) !important;
  border-radius: 16px !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5) !important;
}

.user-management :deep(.el-dialog__header) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  color: #ffffff !important;
  border-radius: 16px 16px 0 0 !important;
  padding: 20px 24px !important;
}

.user-management :deep(.el-dialog__title) {
  color: #ffffff !important;
  font-weight: 600 !important;
}

.user-management :deep(.el-dialog__body) {
  background: transparent !important;
  color: var(--text-primary) !important;
  padding: 24px !important;
}

.user-management :deep(.el-dialog__footer) {
  background: transparent !important;
  border-top: 1px solid var(--border-primary) !important;
  padding: 16px 24px !important;
}

/* 表单样式覆盖 */
/* 搜索表单标签样式 */
.user-management .search-form :deep(.el-form-item__label) {
  color: #e0e0e0 !important;
  font-weight: 500 !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
}

/* 搜索表单内的输入框样式 */
.user-management .search-form :deep(.el-input__inner) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  color: #e0e0e0 !important;
  border-radius: 8px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
}

.user-management .search-form :deep(.el-input__inner:focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 12px rgba(var(--primary-rgb), 0.3) !important;
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
}

.user-management .search-form :deep(.el-input__inner::placeholder) {
  color: #888888 !important;
}

.user-management .search-form :deep(.el-select .el-input__inner) {
  background: linear-gradient(135deg, #1a1a2e, #16213e) !important;
  border: 1px solid var(--border-primary) !important;
  color: #e0e0e0 !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
}

.user-management .search-form :deep(.el-select .el-input__inner:focus) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 12px rgba(var(--primary-rgb), 0.3) !important;
  background: linear-gradient(135deg, #2a2a4e, #1e2a4e) !important;
}

.user-management :deep(.el-radio__label) {
  color: var(--text-primary) !important;
}

.user-management :deep(.el-radio__input.is-checked .el-radio__inner) {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color)) !important;
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 10px rgba(var(--primary-rgb), 0.3) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-form .el-form {
    display: block;
  }
  
  .search-form .el-form-item {
    display: block;
    margin-bottom: 16px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 8px;
  }
  
  .user-management :deep(.el-table__body td) {
    padding: 8px 4px !important;
    font-size: 12px !important;
  }
}
</style>
