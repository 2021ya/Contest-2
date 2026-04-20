<template>
  <div class="china-map">
    <!-- 顶部导航 -->
    <div class="header">
      <button @click="goBack" class="back-btn">
        ← 返回首页
      </button>
      <h1>去哪旅行</h1>
      <div class="header-info">
        <span>点击任意省份查看详情</span>
      </div>
    </div>

    <!-- 地图容器 -->
    <div class="map-container">
      <div ref="mapRef" class="map-chart"></div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-overlay">
        <div class="loading-spinner"></div>
        <p>正在加载地图...</p>
      </div>
    </div>


    <!-- 省份信息面板 -->
    <div v-if="selectedProvince" class="province-info">
      <div class="info-card">
        <h3>{{ selectedProvince.name }}</h3>
        <p>{{ selectedProvince.description }}</p>
        <button @click="goToProvince" class="explore-btn">
          探索 {{ selectedProvince.name }}
        </button>
      </div>
    </div>

    <!-- 悬停信息面板 -->
    <div v-if="hoveredProvince" class="hover-info-panel">
      <div class="hover-info-card">
        <div class="hover-header">
          <h3>{{ hoveredProvince.name }}</h3>
          <span class="hover-close" @click="clearHoveredProvince">×</span>
        </div>
        <div class="hover-content">
          <p class="hover-description">{{ hoveredProvince.description }}</p>
          <div class="hover-stats">
            <div class="stat-item">
              <span class="stat-label">面积</span>
              <span class="stat-value">{{ hoveredProvince.area }}万km²</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">人口</span>
              <span class="stat-value">{{ hoveredProvince.population }}万人</span>
            </div>
          </div>
          <div class="hover-attractions">
            <h4>著名景点</h4>
            <div class="attractions-list">
              <div 
                v-for="attraction in hoveredProvince.attractions" 
                :key="attraction.id"
                class="attraction-item"
              >
                <span class="attraction-icon">{{ attraction.icon }}</span>
                <div class="attraction-details">
                  <span class="attraction-name">{{ attraction.name }}</span>
                  <span class="attraction-type">{{ attraction.type }}</span>
                </div>
              </div>
            </div>
          </div>
          <button @click="goToProvinceDetail" class="hover-explore-btn">
            查看详情
          </button>
        </div>
      </div>
    </div>

    <!-- 向上滚动触发区域 -->
    <div class="scroll-trigger-section" v-show="showScrollTrigger">
      <div class="scroll-trigger-content">
        <div class="scroll-indicator">
          <span>向上滚动返回首页</span>
          <div class="scroll-arrow">↑</div>
        </div>
        <div class="loading-bar" :class="{ active: isTransitioning }">
          <div class="loading-progress"></div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

export default {
  name: 'ChinaMap',
  setup() {
    const router = useRouter()
    const mapRef = ref(null)
    const loading = ref(true)
    const selectedProvince = ref(null)
    const hoveredProvince = ref(null)
    const scrollTriggerSection = ref(null)
    const isTransitioning = ref(false)
    const showScrollTrigger = ref(false)
    let chart = null

    // 省份数据
    const provinces = {
      '北京': { 
        name: '北京', 
        description: '首都北京，历史文化名城',
        area: '1.64',
        population: '2189',
        attractions: [
          { id: 1, name: '故宫博物院', icon: '🏛️', type: '历史建筑' },
          { id: 2, name: '天安门广场', icon: '🏛️', type: '历史建筑' },
          { id: 3, name: '长城', icon: '🏔️', type: '自然景观' },
          { id: 4, name: '颐和园', icon: '🏞️', type: '皇家园林' }
        ]
      },
      '天津': { 
        name: '天津', 
        description: '海河之滨，现代化港口城市',
        area: '1.19',
        population: '1387',
        attractions: [
          { id: 1, name: '天津之眼', icon: '🎡', type: '现代建筑' },
          { id: 2, name: '古文化街', icon: '🏮', type: '历史文化' },
          { id: 3, name: '五大道', icon: '🏘️', type: '历史建筑' },
          { id: 4, name: '海河', icon: '🌊', type: '自然景观' }
        ]
      },
      '河北': { 
        name: '河北', 
        description: '燕赵大地，历史文化悠久',
        area: '18.88',
        population: '7461',
        attractions: [
          { id: 1, name: '承德避暑山庄', icon: '🏛️', type: '皇家园林' },
          { id: 2, name: '山海关', icon: '🏔️', type: '历史建筑' },
          { id: 3, name: '北戴河', icon: '🌊', type: '海滨度假' },
          { id: 4, name: '正定古城', icon: '🏘️', type: '历史文化' }
        ]
      },
      '山西': { 
        name: '山西', 
        description: '晋商故里，煤炭资源丰富',
        area: '15.67',
        population: '3492',
        attractions: [
          { id: 1, name: '平遥古城', icon: '🏘️', type: '历史文化' },
          { id: 2, name: '五台山', icon: '⛩️', type: '宗教圣地' },
          { id: 3, name: '云冈石窟', icon: '🏛️', type: '历史建筑' },
          { id: 4, name: '壶口瀑布', icon: '🌊', type: '自然景观' }
        ]
      },
      '内蒙古': { 
        name: '内蒙古', 
        description: '草原天堂，马背上的民族',
        area: '118.3',
        population: '2405',
        attractions: [
          { id: 1, name: '呼伦贝尔大草原', icon: '🌾', type: '自然景观' },
          { id: 2, name: '成吉思汗陵', icon: '🏛️', type: '历史建筑' },
          { id: 3, name: '响沙湾', icon: '🏜️', type: '自然景观' },
          { id: 4, name: '阿尔山', icon: '🏔️', type: '自然景观' }
        ]
      },
      '辽宁': { 
        name: '辽宁', 
        description: '东北重镇，工业基础雄厚',
        area: '14.8',
        population: '4259',
        attractions: [
          { id: 1, name: '沈阳故宫', icon: '🏛️', type: '历史建筑' },
          { id: 2, name: '大连星海广场', icon: '🌊', type: '城市景观' },
          { id: 3, name: '本溪水洞', icon: '🏔️', type: '自然景观' },
          { id: 4, name: '千山', icon: '🏔️', type: '自然景观' }
        ]
      },
      '吉林': { 
        name: '吉林', 
        description: '长白山麓，冰雪世界',
        area: '18.74',
        population: '2407',
        attractions: [
          { id: 1, name: '长白山天池', icon: '🏞️', type: '自然景观' },
          { id: 2, name: '吉林雾凇', icon: '❄️', type: '自然景观' },
          { id: 3, name: '净月潭', icon: '🏞️', type: '自然景观' },
          { id: 4, name: '高句丽王城', icon: '🏛️', type: '历史建筑' }
        ]
      },
      '黑龙江': { 
        name: '黑龙江', 
        description: '北大仓，黑土地上的明珠',
        area: '47.3',
        population: '3185',
        attractions: [
          { id: 1, name: '哈尔滨冰雪大世界', icon: '❄️', type: '主题公园' },
          { id: 2, name: '五大连池', icon: '🏞️', type: '自然景观' },
          { id: 3, name: '镜泊湖', icon: '🏞️', type: '自然景观' },
          { id: 4, name: '中央大街', icon: '🏘️', type: '历史文化' }
        ]
      },
      '上海': { 
        name: '上海', 
        description: '东方明珠，国际大都市',
        area: '0.63',
        population: '2487',
        attractions: [
          { id: 1, name: '外滩', icon: '🌃', type: '城市景观' },
          { id: 2, name: '东方明珠', icon: '🗼', type: '现代建筑' },
          { id: 3, name: '豫园', icon: '🏮', type: '古典园林' },
          { id: 4, name: '迪士尼乐园', icon: '🎠', type: '主题公园' }
        ]
      },
      '江苏': { 
        name: '江苏', 
        description: '江南水乡，鱼米之乡',
        area: '10.72',
        population: '8475',
        attractions: [
          { id: 1, name: '苏州园林', icon: '🏞️', type: '古典园林' },
          { id: 2, name: '南京夫子庙', icon: '🏛️', type: '历史文化' },
          { id: 3, name: '周庄古镇', icon: '🏘️', type: '古镇' },
          { id: 4, name: '瘦西湖', icon: '🏞️', type: '自然景观' }
        ]
      },
      '浙江': { 
        name: '浙江', 
        description: '人间天堂，丝绸之府',
        area: '10.55',
        population: '6457',
        attractions: [
          { id: 1, name: '西湖', icon: '🏞️', type: '自然景观' },
          { id: 2, name: '乌镇', icon: '🏮', type: '古镇' },
          { id: 3, name: '普陀山', icon: '⛩️', type: '宗教圣地' },
          { id: 4, name: '千岛湖', icon: '🏞️', type: '自然景观' }
        ]
      },
      '安徽': { 
        name: '安徽', 
        description: '徽商故里，黄山奇景',
        area: '14.01',
        population: '6103',
        attractions: [
          { id: 1, name: '黄山', icon: '🏔️', type: '自然景观' },
          { id: 2, name: '宏村', icon: '🏘️', type: '古镇' },
          { id: 3, name: '九华山', icon: '⛩️', type: '宗教圣地' },
          { id: 4, name: '天柱山', icon: '🏔️', type: '自然景观' }
        ]
      },
      '福建': { 
        name: '福建', 
        description: '八闽大地，海上丝绸之路',
        area: '12.4',
        population: '4154',
        attractions: [
          { id: 1, name: '鼓浪屿', icon: '🏝️', type: '自然景观' },
          { id: 2, name: '武夷山', icon: '🏔️', type: '自然景观' },
          { id: 3, name: '土楼', icon: '🏘️', type: '历史建筑' },
          { id: 4, name: '三坊七巷', icon: '🏘️', type: '历史文化' }
        ]
      },
      '江西': { 
        name: '江西', 
        description: '赣江之滨，红色革命圣地',
        area: '16.69',
        population: '4519',
        attractions: [
          { id: 1, name: '庐山', icon: '🏔️', type: '自然景观' },
          { id: 2, name: '井冈山', icon: '🏔️', type: '红色旅游' },
          { id: 3, name: '三清山', icon: '🏔️', type: '自然景观' },
          { id: 4, name: '滕王阁', icon: '🏛️', type: '历史建筑' }
        ]
      },
      '山东': { 
        name: '山东', 
        description: '齐鲁大地，孔孟之乡',
        area: '15.79',
        population: '10153',
        attractions: [
          { id: 1, name: '泰山', icon: '🏔️', type: '自然景观' },
          { id: 2, name: '孔庙', icon: '🏛️', type: '历史建筑' },
          { id: 3, name: '青岛栈桥', icon: '🌉', type: '城市景观' },
          { id: 4, name: '趵突泉', icon: '💧', type: '自然景观' }
        ]
      },
      '河南': { 
        name: '河南', 
        description: '中原大地，华夏文明发源地',
        area: '16.7',
        population: '9937',
        attractions: [
          { id: 1, name: '少林寺', icon: '⛩️', type: '宗教圣地' },
          { id: 2, name: '龙门石窟', icon: '🏛️', type: '历史建筑' },
          { id: 3, name: '云台山', icon: '🏔️', type: '自然景观' },
          { id: 4, name: '清明上河园', icon: '🏛️', type: '历史文化' }
        ]
      },
      '湖北': { 
        name: '湖北', 
        description: '荆楚大地，千湖之省',
        area: '18.59',
        population: '5775',
        attractions: [
          { id: 1, name: '黄鹤楼', icon: '🏛️', type: '历史建筑' },
          { id: 2, name: '武当山', icon: '⛩️', type: '宗教圣地' },
          { id: 3, name: '神农架', icon: '🏔️', type: '自然景观' },
          { id: 4, name: '三峡大坝', icon: '🏗️', type: '现代建筑' }
        ]
      },
      '湖南': { 
        name: '湖南', 
        description: '潇湘大地，伟人故里',
        area: '21.18',
        population: '6644',
        attractions: [
          { id: 1, name: '张家界', icon: '🏔️', type: '自然景观' },
          { id: 2, name: '凤凰古城', icon: '🏘️', type: '古镇' },
          { id: 3, name: '岳阳楼', icon: '🏛️', type: '历史建筑' },
          { id: 4, name: '衡山', icon: '🏔️', type: '自然景观' }
        ]
      },
      '广东': { 
        name: '广东', 
        description: '岭南大地，改革开放前沿',
        area: '17.97',
        population: '12601',
        attractions: [
          { id: 1, name: '广州塔', icon: '🗼', type: '现代建筑' },
          { id: 2, name: '深圳湾公园', icon: '🌊', type: '城市公园' },
          { id: 3, name: '丹霞山', icon: '🏔️', type: '自然景观' },
          { id: 4, name: '开平碉楼', icon: '🏘️', type: '历史建筑' }
        ]
      },
      '广西': { 
        name: '广西', 
        description: '八桂大地，山水甲天下',
        area: '23.76',
        population: '5013',
        attractions: [
          { id: 1, name: '桂林山水', icon: '🏞️', type: '自然景观' },
          { id: 2, name: '阳朔', icon: '🏞️', type: '自然景观' },
          { id: 3, name: '龙胜梯田', icon: '🌾', type: '自然景观' },
          { id: 4, name: '北海银滩', icon: '🌊', type: '海滨度假' }
        ]
      },
      '海南': { 
        name: '海南', 
        description: '天涯海角，热带海岛',
        area: '3.54',
        population: '1008',
        attractions: [
          { id: 1, name: '天涯海角', icon: '🌊', type: '自然景观' },
          { id: 2, name: '亚龙湾', icon: '🏖️', type: '海滨度假' },
          { id: 3, name: '蜈支洲岛', icon: '🏝️', type: '自然景观' },
          { id: 4, name: '南山寺', icon: '⛩️', type: '宗教圣地' }
        ]
      },
      '重庆': { 
        name: '重庆', 
        description: '山城重庆，火锅之都',
        area: '8.24',
        population: '3205',
        attractions: [
          { id: 1, name: '洪崖洞', icon: '🏘️', type: '历史文化' },
          { id: 2, name: '解放碑', icon: '🏛️', type: '城市景观' },
          { id: 3, name: '长江索道', icon: '🚠', type: '城市景观' },
          { id: 4, name: '武隆天坑', icon: '🏔️', type: '自然景观' }
        ]
      },
      '四川': { 
        name: '四川', 
        description: '天府之国，熊猫故乡',
        area: '48.6',
        population: '8367',
        attractions: [
          { id: 1, name: '九寨沟', icon: '🏞️', type: '自然景观' },
          { id: 2, name: '峨眉山', icon: '🏔️', type: '自然景观' },
          { id: 3, name: '都江堰', icon: '🏛️', type: '历史建筑' },
          { id: 4, name: '大熊猫基地', icon: '🐼', type: '动物园' }
        ]
      },
      '贵州': { 
        name: '贵州', 
        description: '多彩贵州，避暑之都',
        area: '17.61',
        population: '3856',
        attractions: [
          { id: 1, name: '黄果树瀑布', icon: '🌊', type: '自然景观' },
          { id: 2, name: '西江千户苗寨', icon: '🏘️', type: '民族文化' },
          { id: 3, name: '梵净山', icon: '🏔️', type: '自然景观' },
          { id: 4, name: '镇远古镇', icon: '🏘️', type: '古镇' }
        ]
      },
      '云南': { 
        name: '云南', 
        description: '彩云之南，春城昆明',
        area: '39.41',
        population: '4721',
        attractions: [
          { id: 1, name: '丽江古城', icon: '🏘️', type: '古镇' },
          { id: 2, name: '石林', icon: '🏔️', type: '自然景观' },
          { id: 3, name: '大理古城', icon: '🏘️', type: '古镇' },
          { id: 4, name: '西双版纳', icon: '🌴', type: '自然景观' }
        ]
      },
      '西藏': { 
        name: '西藏', 
        description: '世界屋脊，雪域高原',
        area: '122.84',
        population: '364',
        attractions: [
          { id: 1, name: '布达拉宫', icon: '🏛️', type: '历史建筑' },
          { id: 2, name: '珠穆朗玛峰', icon: '🏔️', type: '自然景观' },
          { id: 3, name: '纳木错', icon: '🏞️', type: '自然景观' },
          { id: 4, name: '大昭寺', icon: '⛩️', type: '宗教圣地' }
        ]
      },
      '陕西': { 
        name: '陕西', 
        description: '三秦大地，古都西安',
        area: '20.56',
        population: '3953',
        attractions: [
          { id: 1, name: '兵马俑', icon: '🏛️', type: '历史建筑' },
          { id: 2, name: '华山', icon: '🏔️', type: '自然景观' },
          { id: 3, name: '大雁塔', icon: '🏛️', type: '历史建筑' },
          { id: 4, name: '法门寺', icon: '⛩️', type: '宗教圣地' }
        ]
      },
      '甘肃': { 
        name: '甘肃', 
        description: '陇原大地，丝绸之路要道',
        area: '42.58',
        population: '2502',
        attractions: [
          { id: 1, name: '莫高窟', icon: '🏛️', type: '历史建筑' },
          { id: 2, name: '嘉峪关', icon: '🏔️', type: '历史建筑' },
          { id: 3, name: '张掖丹霞', icon: '🏔️', type: '自然景观' },
          { id: 4, name: '麦积山石窟', icon: '🏛️', type: '历史建筑' }
        ]
      },
      '青海': { 
        name: '青海', 
        description: '三江源头，高原明珠',
        area: '72.23',
        population: '592',
        attractions: [
          { id: 1, name: '青海湖', icon: '🏞️', type: '自然景观' },
          { id: 2, name: '茶卡盐湖', icon: '🏞️', type: '自然景观' },
          { id: 3, name: '塔尔寺', icon: '⛩️', type: '宗教圣地' },
          { id: 4, name: '门源油菜花', icon: '🌾', type: '自然景观' }
        ]
      },
      '宁夏': { 
        name: '宁夏', 
        description: '塞上江南，回族自治区',
        area: '6.64',
        population: '720',
        attractions: [
          { id: 1, name: '沙湖', icon: '🏞️', type: '自然景观' },
          { id: 2, name: '西夏王陵', icon: '🏛️', type: '历史建筑' },
          { id: 3, name: '沙坡头', icon: '🏜️', type: '自然景观' },
          { id: 4, name: '六盘山', icon: '🏔️', type: '自然景观' }
        ]
      },
      '新疆': { 
        name: '新疆', 
        description: '天山南北，瓜果之乡',
        area: '166.49',
        population: '2585',
        attractions: [
          { id: 1, name: '天山天池', icon: '🏞️', type: '自然景观' },
          { id: 2, name: '喀纳斯湖', icon: '🏞️', type: '自然景观' },
          { id: 3, name: '吐鲁番', icon: '🏜️', type: '自然景观' },
          { id: 4, name: '伊犁河谷', icon: '🌾', type: '自然景观' }
        ]
      },
      '台湾': { 
        name: '台湾', 
        description: '宝岛台湾，美丽富饶',
        area: '3.6',
        population: '2357',
        attractions: [
          { id: 1, name: '日月潭', icon: '🏞️', type: '自然景观' },
          { id: 2, name: '阿里山', icon: '🏔️', type: '自然景观' },
          { id: 3, name: '台北101', icon: '🗼', type: '现代建筑' },
          { id: 4, name: '垦丁', icon: '🏖️', type: '海滨度假' }
        ]
      },
      '香港': { 
        name: '香港', 
        description: '东方之珠，国际金融中心',
        area: '0.11',
        population: '747',
        attractions: [
          { id: 1, name: '维多利亚港', icon: '🌊', type: '城市景观' },
          { id: 2, name: '太平山顶', icon: '🏔️', type: '城市景观' },
          { id: 3, name: '迪士尼乐园', icon: '🎠', type: '主题公园' },
          { id: 4, name: '海洋公园', icon: '🐠', type: '主题公园' }
        ]
      },
      '澳门': { 
        name: '澳门', 
        description: '莲花宝地，中西文化交融',
        area: '0.03',
        population: '68',
        attractions: [
          { id: 1, name: '大三巴牌坊', icon: '🏛️', type: '历史建筑' },
          { id: 2, name: '澳门塔', icon: '🗼', type: '现代建筑' },
          { id: 3, name: '威尼斯人', icon: '🏛️', type: '现代建筑' },
          { id: 4, name: '妈阁庙', icon: '⛩️', type: '宗教圣地' }
        ]
      }
    }

    const initMap = async () => {
      try {
        console.log('开始初始化地图...')
        console.log('地图容器元素:', mapRef.value)
        
        if (!mapRef.value) {
          console.error('地图容器元素未找到')
          return
        }

        // 创建地图实例
        chart = echarts.init(mapRef.value)
        console.log('ECharts实例创建成功:', chart)

        // 注册中国地图
        console.log('开始加载地图数据...')
        const response = await fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
        const geoJson = await response.json()
        echarts.registerMap('china', geoJson)
        console.log('地图数据加载成功')

        // 配置地图选项
        const option = {
          backgroundColor: 'transparent',
          geo: {
            map: 'china',
            roam: true,
            zoom: 1.2,
            center: [105, 36],
            itemStyle: {
              areaColor: '#4a90e2',
              borderColor: '#fff',
              borderWidth: 1,
              shadowColor: 'rgba(0, 0, 0, 0.3)',
              shadowBlur: 10
            },
            emphasis: {
              itemStyle: {
                areaColor: '#ff6b6b',
                borderColor: '#fff',
                borderWidth: 2,
                shadowColor: 'rgba(255, 107, 107, 0.5)',
                shadowBlur: 20
              },
              label: {
                show: true,
                color: '#fff',
                fontSize: 12,
                fontWeight: 'bold'
              }
            },
            select: {
              itemStyle: {
                areaColor: '#4ecdc4',
                borderColor: '#fff',
                borderWidth: 2
              }
            }
          },
          series: [
            {
              type: 'map',
              map: 'china',
              geoIndex: 0,
              data: Object.keys(provinces).map(name => ({
                name,
                value: Math.random() * 100
              })),
              itemStyle: {
                areaColor: '#4a90e2',
                borderColor: '#fff',
                borderWidth: 1
              },
              emphasis: {
                itemStyle: {
                  areaColor: '#ff6b6b'
                }
              },
              select: {
                itemStyle: {
                  areaColor: '#4ecdc4'
                }
              }
            }
          ]
        }

        console.log('设置地图配置...')
        chart.setOption(option)
        console.log('地图配置设置完成')

        // 添加点击事件
        chart.on('click', (params) => {
          if (params.componentType === 'series') {
            const provinceName = params.name
            console.log('点击省份:', provinceName)
            selectedProvince.value = provinces[provinceName]
            // 直接跳转到省份详情页面
            router.push(`/province/${provinceName}`)
          }
        })

        // 添加鼠标悬停事件
        chart.on('mouseover', (params) => {
          if (params.componentType === 'series') {
            const provinceName = params.name
            console.log('鼠标悬停省份:', provinceName)
            console.log('省份数据:', provinces[provinceName])
            
            // 显示悬停信息
            if (provinces[provinceName]) {
              hoveredProvince.value = provinces[provinceName]
              console.log('设置悬停省份:', hoveredProvince.value)
            } else {
              console.log('未找到省份数据:', provinceName)
            }
            
            chart.dispatchAction({
              type: 'highlight',
              seriesIndex: 0,
              dataIndex: params.dataIndex
            })
          }
        })

        chart.on('mouseout', (params) => {
          if (params.componentType === 'series') {
            // 延迟清除悬停信息，避免快速移动时闪烁
            setTimeout(() => {
              if (hoveredProvince.value && hoveredProvince.value.name === params.name) {
                hoveredProvince.value = null
              }
            }, 100)
            
            chart.dispatchAction({
              type: 'downplay',
              seriesIndex: 0,
              dataIndex: params.dataIndex
            })
          }
        })

        loading.value = false
        console.log('地图初始化完成')
      } catch (error) {
        console.error('地图加载失败:', error)
        loading.value = false
      }
    }

    const goBack = () => {
      router.push('/')
    }

    const goToProvince = () => {
      if (selectedProvince.value) {
        router.push(`/province/${selectedProvince.value.name}`)
      }
    }

    const clearHoveredProvince = () => {
      hoveredProvince.value = null
    }

    const goToProvinceDetail = () => {
      if (hoveredProvince.value) {
        router.push(`/province/${hoveredProvince.value.name}`)
      }
    }

    // 向上滚动监听函数
    const handleScroll = () => {
      // 检测向上滚动到页面顶部
      const scrollTop = window.pageYOffset || document.documentElement.scrollTop
      
      // 显示/隐藏滚动触发区域
      showScrollTrigger.value = scrollTop <= 100
      
      // 当滚动到页面顶部附近时触发
      if (scrollTop <= 30) {
        if (!isTransitioning.value) {
          isTransitioning.value = true
          console.log('触发向上滚动返回首页')
          
          // 延迟跳转，让用户看到过渡效果
          setTimeout(() => {
            router.push('/')
          }, 1500)
        }
      }
    }

    const handleResize = () => {
      if (chart) {
        chart.resize()
      }
    }

    onMounted(() => {
      initMap()
      window.addEventListener('resize', handleResize)
      window.addEventListener('scroll', handleScroll)
    })

    onUnmounted(() => {
      if (chart) {
        chart.dispose()
      }
      window.removeEventListener('resize', handleResize)
      window.removeEventListener('scroll', handleScroll)
    })

    return {
      mapRef,
      loading,
      selectedProvince,
      hoveredProvince,
      scrollTriggerSection,
      isTransitioning,
      showScrollTrigger,
      goBack,
      goToProvince,
      clearHoveredProvince,
      goToProvinceDetail
    }
  }
}
</script>
<style scoped>
.china-map {
  height: 100vh;
  background-image: url('@/tutu/login1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden; /* 防止页面滚动，让地图占满屏幕 */
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  color: white;
  z-index: 100;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(-5px);
}

.header h1 {
  font-size: 1.8rem;
  font-weight: 600;
}

.header-info {
  font-size: 0.9rem;
  opacity: 0.8;
}

.map-container {
  flex: 1;
  position: relative;
  margin: 1rem;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  height: calc(100vh - 120px); /* 全屏高度减去头部高度 */
}

.map-chart {
  width: 100%;
  height: 100%;
}


.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  z-index: 10;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.province-info {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 50;
}

.info-card {
  background: rgba(255, 255, 255, 0.95);
  padding: 1.5rem;
  border-radius: 15px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  animation: slideUp 0.3s ease;
}

.info-card h3 {
  color: #333;
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.info-card p {
  color: #666;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.explore-btn {
  background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
  border: none;
  color: white;
  padding: 0.8rem 2rem;
  border-radius: 25px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
}

.explore-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    padding: 1rem;
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .header h1 {
    font-size: 1.5rem;
  }
  
  .map-container {
    margin: 0.5rem;
  }
  
  .province-info {
    bottom: 1rem;
    left: 1rem;
    right: 1rem;
    transform: none;
  }
  
  .info-card {
    padding: 1rem;
  }
}

/* 滚动触发区域样式 */
.scroll-trigger-section {
  position: absolute;
  top: 1rem;
  right: 1rem;
  z-index: 100;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 1rem 2rem;
  color: white;
  text-align: center;
  animation: fadeInDown 0.6s ease;
}

.scroll-trigger-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.scroll-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.scroll-indicator span {
  font-size: 0.9rem;
  opacity: 0.9;
}

.scroll-arrow {
  font-size: 1.5rem;
  animation: bounceUp 2s infinite;
}

.loading-bar {
  width: 200px;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  overflow: hidden;
}

.loading-progress {
  height: 100%;
  background: linear-gradient(90deg, #ff6b6b, #4ecdc4);
  width: 0%;
  transition: width 1.5s ease;
}

.loading-bar.active .loading-progress {
  width: 100%;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

@keyframes bounceUp {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-8px);
  }
  60% {
    transform: translateY(-4px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .map-container {
    height: calc(100vh - 100px);
    margin: 0.5rem;
  }
  
  .scroll-trigger-section {
    top: 0.5rem;
    right: 0.5rem;
    padding: 0.8rem 1rem;
  }
  
  .loading-bar {
    width: 150px;
  }
}

/* 悬停信息面板样式 */
.hover-info-panel {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  z-index: 200;
  animation: slideInRight 0.3s ease;
}

.hover-info-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  max-width: 350px;
  max-height: 500px;
  overflow-y: auto;
}

.hover-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 15px 15px 0 0;
}

.hover-header h3 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.hover-close {
  font-size: 1.5rem;
  cursor: pointer;
  opacity: 0.8;
  transition: opacity 0.3s ease;
}

.hover-close:hover {
  opacity: 1;
}

.hover-content {
  padding: 1.5rem;
}

.hover-description {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.hover-stats {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 0.8rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-label {
  display: block;
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 0.3rem;
}

.stat-value {
  display: block;
  font-size: 1rem;
  font-weight: 600;
  color: #333;
}

.hover-attractions h4 {
  color: #333;
  font-size: 1rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.attractions-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  margin-bottom: 1.5rem;
}

.attraction-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  padding: 0.8rem;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background 0.3s ease;
}

.attraction-item:hover {
  background: #e9ecef;
}

.attraction-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.attraction-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.attraction-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #333;
}

.attraction-type {
  font-size: 0.8rem;
  color: #666;
}

.hover-explore-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  padding: 0.8rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.3s ease;
}

.hover-explore-btn:hover {
  transform: translateY(-2px);
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(100px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hover-info-panel {
    bottom: 1rem;
    right: 1rem;
    left: 1rem;
  }
  
  .hover-info-card {
    max-width: none;
    max-height: 400px;
  }
  
  .hover-stats {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>


