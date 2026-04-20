// 模拟数据文件
export const mockUsers = [
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
]

export const mockTravelPlans = [
  {
    id: 1,
    title: '北京三日游',
    description: '探索北京的历史文化和现代风貌',
    startDate: '2024-03-15T00:00:00',
    endDate: '2024-03-17T23:59:59',
    budget: 3000,
    days: 3,
    status: 'CONFIRMED',
    aiGenerated: true,
    user: { id: 1, username: 'zhangsan' },
    destinations: [
      {
        id: 1,
        name: '北京',
        city: '北京',
        rating: 4.8,
        visitDuration: 480,
        estimatedCost: 1000
      }
    ],
    attractions: [
      {
        id: 1,
        name: '故宫博物院',
        type: 'MUSEUM',
        rating: 4.8,
        visitDuration: 180,
        ticketPrice: 60
      },
      {
        id: 2,
        name: '天安门广场',
        type: 'MONUMENT',
        rating: 4.6,
        visitDuration: 60,
        ticketPrice: 0
      }
    ],
    createdAt: '2024-03-10T10:00:00',
    updatedAt: '2024-03-10T10:00:00'
  },
  {
    id: 2,
    title: '上海周末游',
    description: '体验上海的现代都市魅力',
    startDate: '2024-03-22T00:00:00',
    endDate: '2024-03-24T23:59:59',
    budget: 2500,
    days: 2,
    status: 'PLANNING',
    aiGenerated: true,
    user: { id: 2, username: 'lisi' },
    destinations: [
      {
        id: 2,
        name: '上海',
        city: '上海',
        rating: 4.7,
        visitDuration: 360,
        estimatedCost: 800
      }
    ],
    attractions: [
      {
        id: 3,
        name: '外滩',
        type: 'MONUMENT',
        rating: 4.7,
        visitDuration: 90,
        ticketPrice: 0
      }
    ],
    createdAt: '2024-03-12T14:30:00',
    updatedAt: '2024-03-12T14:30:00'
  },
  {
    id: 3,
    title: '杭州西湖游',
    description: '感受江南水乡的美丽风光',
    startDate: '2024-04-01T00:00:00',
    endDate: '2024-04-03T23:59:59',
    budget: 2000,
    days: 3,
    status: 'IN_PROGRESS',
    aiGenerated: false,
    user: { id: 3, username: 'wangwu' },
    destinations: [
      {
        id: 3,
        name: '杭州',
        city: '杭州',
        rating: 4.9,
        visitDuration: 480,
        estimatedCost: 600
      }
    ],
    attractions: [
      {
        id: 4,
        name: '西湖',
        type: 'PARK',
        rating: 4.8,
        visitDuration: 240,
        ticketPrice: 0
      }
    ],
    createdAt: '2024-03-20T09:15:00',
    updatedAt: '2024-03-20T09:15:00'
  }
]

export const mockAttractions = [
  {
    id: 1,
    name: '故宫博物院',
    description: '明清两朝的皇家宫殿，世界文化遗产',
    city: '北京',
    address: '北京市东城区景山前街4号',
    type: 'MUSEUM',
    rating: 4.8,
    visitDuration: 180,
    ticketPrice: 60,
    openingHours: '08:30-17:00',
    phone: '010-85007421',
    website: 'https://www.dpm.org.cn',
    imageUrl: 'https://example.com/forbidden-city.jpg'
  },
  {
    id: 2,
    name: '天安门广场',
    description: '世界最大的城市广场',
    city: '北京',
    address: '北京市东城区东长安街',
    type: 'MONUMENT',
    rating: 4.6,
    visitDuration: 60,
    ticketPrice: 0,
    openingHours: '全天开放',
    phone: '',
    website: '',
    imageUrl: 'https://example.com/tiananmen.jpg'
  },
  {
    id: 3,
    name: '外滩',
    description: '上海最著名的观光景点，万国建筑博览群',
    city: '上海',
    address: '上海市黄浦区中山东一路',
    type: 'MONUMENT',
    rating: 4.7,
    visitDuration: 90,
    ticketPrice: 0,
    openingHours: '全天开放',
    phone: '',
    website: '',
    imageUrl: 'https://example.com/bund.jpg'
  },
  {
    id: 4,
    name: '东方明珠',
    description: '上海地标性建筑，468米高塔',
    city: '上海',
    address: '上海市浦东新区世纪大道1号',
    type: 'BUILDING',
    rating: 4.5,
    visitDuration: 120,
    ticketPrice: 160,
    openingHours: '08:00-21:30',
    phone: '021-58791888',
    website: 'https://www.orientalpearltower.com',
    imageUrl: 'https://example.com/oriental-pearl.jpg'
  },
  {
    id: 5,
    name: '西湖',
    description: '世界文化遗产，中国最著名的湖泊',
    city: '杭州',
    address: '浙江省杭州市西湖区',
    type: 'PARK',
    rating: 4.8,
    visitDuration: 240,
    ticketPrice: 0,
    openingHours: '全天开放',
    phone: '',
    website: '',
    imageUrl: 'https://example.com/west-lake.jpg'
  },
  {
    id: 6,
    name: '雷峰塔',
    description: '白蛇传说的发源地',
    city: '杭州',
    address: '浙江省杭州市西湖区南山路15号',
    type: 'MONUMENT',
    rating: 4.5,
    visitDuration: 90,
    ticketPrice: 40,
    openingHours: '08:00-17:30',
    phone: '0571-87179603',
    website: '',
    imageUrl: 'https://example.com/leifeng-tower.jpg'
  },
  {
    id: 7,
    name: '迪士尼乐园',
    description: '中国大陆首座迪士尼主题乐园',
    city: '上海',
    address: '上海市浦东新区川沙新镇',
    type: 'ENTERTAINMENT',
    rating: 4.6,
    visitDuration: 480,
    ticketPrice: 399,
    openingHours: '09:00-21:00',
    phone: '400-180-0000',
    website: 'https://www.shanghaidisneyresort.com',
    imageUrl: 'https://example.com/disney.jpg'
  },
  {
    id: 8,
    name: '灵隐寺',
    description: '江南著名古刹',
    city: '杭州',
    address: '浙江省杭州市西湖区法云弄1号',
    type: 'RELIGIOUS',
    rating: 4.6,
    visitDuration: 120,
    ticketPrice: 45,
    openingHours: '07:00-18:00',
    phone: '0571-87968665',
    website: '',
    imageUrl: 'https://example.com/lingyin-temple.jpg'
  }
]

export const mockStatistics = {
  overview: {
    totalUsers: 12580,
    totalPlans: 3420,
    totalAttractions: 1250,
    totalRevenue: 1256000
  },
  userGrowth: {
    '7d': {
      dates: ['3-9', '3-10', '3-11', '3-12', '3-13', '3-14', '3-15'],
      values: [120, 200, 150, 80, 70, 110, 90]
    },
    '30d': {
      dates: ['2-15', '2-22', '3-1', '3-8', '3-15'],
      values: [820, 932, 901, 934, 1290]
    },
    '90d': {
      dates: ['12-15', '1-15', '2-15', '3-15'],
      values: [620, 720, 820, 920]
    }
  },
  planStatus: [
    { value: 45, name: '已完成' },
    { value: 30, name: '进行中' },
    { value: 20, name: '计划中' },
    { value: 5, name: '已取消' }
  ],
  destinationRanking: {
    cities: ['北京', '上海', '杭州', '成都', '西安', '广州', '深圳', '苏州'],
    values: [35, 25, 20, 15, 12, 10, 8, 6]
  },
  revenue: {
    '6m': {
      months: ['10月', '11月', '12月', '1月', '2月', '3月'],
      values: [82000, 93200, 90100, 93400, 129000, 133000]
    },
    '1y': {
      months: ['4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月', '1月', '2月', '3月'],
      values: [62000, 72000, 82000, 93200, 90100, 93400, 129000, 133000, 145000, 158000, 162000, 175000]
    }
  },
  userActivity: {
    hours: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00'],
    values: [120, 200, 150, 80, 70, 110, 90]
  },
  attractionTypes: [
    { value: 25, name: '博物馆' },
    { value: 20, name: '公园' },
    { value: 18, name: '古迹' },
    { value: 15, name: '建筑' },
    { value: 12, name: '宗教场所' },
    { value: 10, name: '其他' }
  ]
}

export const mockTopAttractions = [
  { rank: 1, name: '故宫博物院', city: '北京', type: 'MUSEUM', rating: 4.8, visitCount: 12580, revenue: 754800 },
  { rank: 2, name: '外滩', city: '上海', type: 'MONUMENT', rating: 4.7, visitCount: 11200, revenue: 0 },
  { rank: 3, name: '西湖', city: '杭州', type: 'PARK', rating: 4.8, visitCount: 10800, revenue: 0 },
  { rank: 4, name: '东方明珠', city: '上海', type: 'BUILDING', rating: 4.5, visitCount: 9600, revenue: 1536000 },
  { rank: 5, name: '天安门广场', city: '北京', type: 'MONUMENT', rating: 4.6, visitCount: 9200, revenue: 0 },
  { rank: 6, name: '迪士尼乐园', city: '上海', type: 'ENTERTAINMENT', rating: 4.6, visitCount: 8800, revenue: 3511200 },
  { rank: 7, name: '雷峰塔', city: '杭州', type: 'MONUMENT', rating: 4.5, visitCount: 7200, revenue: 288000 },
  { rank: 8, name: '灵隐寺', city: '杭州', type: 'RELIGIOUS', rating: 4.6, visitCount: 6800, revenue: 306000 }
]

export const mockTopUsers = [
  { rank: 1, username: 'zhangsan', email: 'zhangsan@example.com', planCount: 15, loginDays: 45, lastLogin: '2024-03-15 14:30', status: 'active' },
  { rank: 2, username: 'lisi', email: 'lisi@example.com', planCount: 12, loginDays: 38, lastLogin: '2024-03-14 16:45', status: 'active' },
  { rank: 3, username: 'wangwu', email: 'wangwu@example.com', planCount: 10, loginDays: 32, lastLogin: '2024-03-13 11:30', status: 'active' },
  { rank: 4, username: 'zhaoliu', email: 'zhaoliu@example.com', planCount: 8, loginDays: 28, lastLogin: '2024-03-12 09:15', status: 'active' },
  { rank: 5, username: 'qianqi', email: 'qianqi@example.com', planCount: 6, loginDays: 25, lastLogin: '2024-03-10 15:20', status: 'active' }
]


