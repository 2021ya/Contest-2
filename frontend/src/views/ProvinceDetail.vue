<template>
  <div class="province-detail">
    <!-- 顶部导航 -->
    <div class="header">
      <button @click="goBack" class="back-btn">
        ← 返回地图
      </button>
      <h1>{{ provinceName }}</h1>
      <div class="header-info">
        <span>{{ provinceData?.description || '探索美丽景点' }}</span>
      </div>
    </div>

    <!-- 省份信息卡片 -->
    <div class="province-card">
      <div class="card-content">
        <div class="province-header">
          <h2>{{ provinceName }}</h2>
          <p>{{ provinceData?.description || '欢迎来到' + provinceName }}</p>
        </div>
        <div class="stats">
          <div class="stat-item">
            <span class="stat-number">{{ attractions.length }}</span>
            <span class="stat-label">个景点</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ provinceData?.area || 'N/A' }}</span>
            <span class="stat-label">面积(万km²)</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ provinceData?.population || 'N/A' }}</span>
            <span class="stat-label">人口(万人)</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 景点网格 -->
    <div class="attractions-section">
      <h3>热门景点</h3>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>正在加载景点数据...</p>
      </div>
      
      <div v-else class="attractions-grid">
        <div 
          v-for="attraction in attractions" 
          :key="attraction.id"
          class="attraction-card"
          @click="selectAttraction(attraction)"
        >
          <div class="attraction-image">
            <img v-if="attraction.image" :src="attraction.image" :alt="attraction.name" class="attraction-img">
            <div v-else class="image-placeholder">
              {{ attraction.icon }}
            </div>
            <div class="attraction-badge">{{ attraction.type }}</div>
          </div>
          <div class="attraction-info">
            <h4>{{ attraction.name }}</h4>
            <p>{{ attraction.description }}</p>
            <div class="attraction-tags">
              <span 
                v-for="tag in attraction.tags" 
                :key="tag"
                class="tag"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 景点详情模态框 -->
    <div v-if="selectedAttraction" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <button @click="closeModal" class="modal-close">×</button>
        <div class="modal-header">
          <h3>{{ selectedAttraction.name }}</h3>
          <span class="modal-type">{{ selectedAttraction.type }}</span>
        </div>
        <div class="modal-body">
          <div class="modal-image">
            <div class="image-placeholder large">
              {{ selectedAttraction.icon }}
            </div>
          </div>
          <div class="modal-info">
            <p>{{ selectedAttraction.description }}</p>
            <div class="modal-details">
              <div class="detail-item">
                <strong>地址：</strong>{{ selectedAttraction.address }}
              </div>
              <div class="detail-item">
                <strong>开放时间：</strong>{{ selectedAttraction.hours }}
              </div>
              <div class="detail-item">
                <strong>门票价格：</strong>{{ selectedAttraction.price }}
              </div>
            </div>
            <div class="modal-tags">
              <span 
                v-for="tag in selectedAttraction.tags" 
                :key="tag"
                class="tag"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

export default {
  name: 'ProvinceDetail',
  props: {
    name: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const router = useRouter()
    const route = useRoute()
    const selectedAttraction = ref(null)
    const loading = ref(false)
    const apiAttractions = ref([])
    const API_BASE_URL = 'http://localhost:8080/api'

    const provinceName = computed(() => {
      const name = props.name || route.params.name
      console.log('省份名称:', name)
      return name
    })

    // 省份数据 - 包含正确的湖北省面积与人口数据
    const provinceDataMap = {
      '北京市': {
        description: '首都北京，历史文化名城',
        area: '1.64',
        population: '2189'
      },
      '天津': {
        description: '海河之滨，现代化港口城市',
        area: '1.19',
        population: '1387'
      },
      '河北': {
        description: '燕赵大地，历史文化悠久',
        area: '18.88',
        population: '7461'
      },
      '山西省': {
        description: '晋商故里，煤炭资源丰富',
        area: '15.67',
        population: '3492'
      },
      '内蒙古': {
        description: '草原天堂，马背上的民族',
        area: '118.3',
        population: '2405'
      },
      '辽宁省': {
        description: '东北重镇，工业基础雄厚',
        area: '14.8',
        population: '4259'
      },
      '吉林省': {
        description: '长白山麓，冰雪世界',
        area: '18.74',
        population: '2407'
      },
      '黑龙江省': {
        description: '北大仓，黑土地上的明珠',
        area: '47.3',
        population: '3185'
      },
      '上海省': {
        description: '东方明珠，国际大都市',
        area: '0.63',
        population: '2487'
      },
      '江苏省': {
        description: '江南水乡，鱼米之乡',
        area: '10.72',
        population: '8475'
      },
      '浙江省': {
        description: '人间天堂，丝绸之府',
        area: '10.55',
        population: '6457'
      },
      '安徽省': {
        description: '徽商故里，黄山奇景',
        area: '14.01',
        population: '6103'
      },
      '福建省': {
        description: '八闽大地，海上丝绸之路',
        area: '12.4',
        population: '4154'
      },
      '江西省': {
        description: '赣江之滨，红色革命圣地',
        area: '16.69',
        population: '4519'
      },
      '山东省': {
        description: '齐鲁大地，孔孟之乡',
        area: '15.79',
        population: '10153'
      },
      '河南省': {
        description: '中原大地，华夏文明发源地',
        area: '16.7',
        population: '9937'
      },
      '湖北省': {
        description: '荆楚大地，千湖之省',
        area: '18.59',
        population: '5838'
      },
      '湖南省': {
        description: '潇湘大地，伟人故里',
        area: '21.18',
        population: '6644'
      },
      '广东省': {
        description: '岭南大地，改革开放前沿',
        area: '17.97',
        population: '12601'
      },
      '广西省': {
        description: '八桂大地，山水甲天下',
        area: '23.76',
        population: '5013'
      },
      '海南省': {
        description: '天涯海角，热带海岛',
        area: '3.54',
        population: '1008'
      },
      '重庆省': {
        description: '山城重庆，火锅之都',
        area: '8.24',
        population: '3213'
      },
      '四川省': {
        description: '天府之国，熊猫故乡',
        area: '48.6',
        population: '8367'
      },
      '贵州省': {
        description: '多彩贵州，避暑之都',
        area: '17.61',
        population: '3856'
      },
      '云南省': {
        description: '彩云之南，春城昆明',
        area: '39.41',
        population: '4721'
      },
      '西藏省': {
        description: '世界屋脊，雪域高原',
        area: '122.84',
        population: '364'
      },
      '陕西省': {
        description: '三秦大地，古都西安',
        area: '20.56',
        population: '3953'
      },
      '甘肃省': {
        description: '陇原大地，丝绸之路要道',
        area: '42.58',
        population: '2502'
      },
      '青海省': {
        description: '三江源头，高原明珠',
        area: '72.23',
        population: '592'
      },
      '宁夏省': {
        description: '塞上江南，回族自治区',
        area: '6.64',
        population: '720'
      },
      '新疆省': {
        description: '天山南北，瓜果之乡',
        area: '166.49',
        population: '2585'
      },
      '台湾省': {
        description: '宝岛台湾，美丽富饶',
        area: '3.6',
        population: '2357'
      },
      '香港省': {
        description: '东方之珠，国际金融中心',
        area: '0.11',
        population: '748'
      },
      '澳门省': {
        description: '莲花宝地，中西文化交融',
        area: '0.03',
        population: '68'
      }
    }

    const provinceData = computed(() => provinceDataMap[provinceName.value])

    // 景点数据
    const attractionsData = {
      '北京': [
        {
          id: 1,
          name: '故宫博物院',
          icon: '🏛️',
          type: '历史建筑',
          description: '明清两朝的皇家宫殿，世界文化遗产',
          address: '北京市东城区景山前街4号',
          hours: '08:30-17:00',
          price: '60元',
          tags: ['世界遗产', '皇家建筑', '历史文化']
        },
        {
          id: 2,
          name: '天安门广场',
          icon: '🏛️',
          type: '历史建筑',
          description: '世界上最大的城市广场',
          address: '北京市东城区东长安街',
          hours: '全天开放',
          price: '免费',
          tags: ['地标建筑', '红色旅游', '免费景点']
        },
        {
          id: 3,
          name: '长城',
          icon: '🏔️',
          type: '自然景观',
          description: '世界文化遗产，中华民族的象征',
          address: '北京市怀柔区',
          hours: '08:00-17:00',
          price: '45元',
          tags: ['世界遗产', '自然景观', '必游景点']
        },
        {
          id: 4,
          name: '颐和园',
          icon: '🏞️',
          type: '皇家园林',
          description: '中国古典园林之首',
          address: '北京市海淀区新建宫门路19号',
          hours: '06:30-18:00',
          price: '30元',
          tags: ['皇家园林', '世界遗产', '古典建筑']
        },
        {
          id: 5,
          name: '天坛公园',
          icon: '⛩️',
          type: '历史建筑',
          description: '明清皇帝祭天的场所',
          address: '北京市东城区天坛路甲1号',
          hours: '06:00-22:00',
          price: '15元',
          tags: ['世界遗产', '皇家建筑', '历史文化']
        },
        {
          id: 6,
          name: '圆明园',
          icon: '🏛️',
          type: '历史遗址',
          description: '万园之园，历史见证',
          address: '北京市海淀区清华西路28号',
          hours: '07:00-19:30',
          price: '10元',
          tags: ['历史遗址', '皇家园林', '爱国主义教育']
        }
      ],
      '上海': [
        {
          id: 1,
          name: '外滩',
          icon: '🌃',
          type: '城市景观',
          description: '上海最著名的城市景观',
          address: '上海市黄浦区中山东一路',
          hours: '全天开放',
          price: '免费',
          tags: ['城市地标', '夜景', '免费景点']
        },
        {
          id: 2,
          name: '东方明珠',
          icon: '🗼',
          type: '现代建筑',
          description: '上海的地标性建筑',
          address: '上海市浦东新区世纪大道1号',
          hours: '08:00-21:30',
          price: '160元',
          tags: ['现代建筑', '观景台', '地标建筑']
        },
        {
          id: 3,
          name: '豫园',
          icon: '🏮',
          type: '古典园林',
          description: '明代私人花园',
          address: '上海市黄浦区安仁街132号',
          hours: '08:30-17:30',
          price: '40元',
          tags: ['古典园林', '历史文化', '传统建筑']
        },
        {
          id: 4,
          name: '迪士尼乐园',
          icon: '🎠',
          type: '主题公园',
          description: '上海迪士尼度假区',
          address: '上海市浦东新区川沙新镇',
          hours: '08:00-22:00',
          price: '399元',
          tags: ['主题公园', '亲子游', '娱乐']
        },
        {
          id: 5,
          name: '田子坊',
          icon: '🏘️',
          type: '文化创意',
          description: '上海最具创意的生活街区',
          address: '上海市黄浦区泰康路210弄',
          hours: '10:00-22:00',
          price: '免费',
          tags: ['文化创意', '艺术', '免费景点']
        }
      ],
      '广东': [
        {
          id: 1,
          name: '广州塔',
          icon: '🗼',
          type: '现代建筑',
          description: '广州新电视塔，小蛮腰',
          address: '广州市海珠区阅江西路222号',
          hours: '09:00-23:00',
          price: '150元',
          tags: ['现代建筑', '观景台', '地标建筑']
        },
        {
          id: 2,
          name: '深圳湾公园',
          icon: '🌊',
          type: '城市公园',
          description: '深圳湾畔的生态公园',
          address: '深圳市南山区滨海大道',
          hours: '全天开放',
          price: '免费',
          tags: ['城市公园', '海滨', '免费景点']
        },
        {
          id: 3,
          name: '丹霞山',
          icon: '🏔️',
          type: '自然景观',
          description: '世界自然遗产，丹霞地貌',
          address: '韶关市仁化县',
          hours: '08:00-18:00',
          price: '100元',
          tags: ['世界遗产', '自然景观', '地质奇观']
        },
        {
          id: 4,
          name: '开平碉楼',
          icon: '🏘️',
          type: '历史建筑',
          description: '世界文化遗产，中西合璧建筑',
          address: '江门市开平市',
          hours: '08:30-17:30',
          price: '80元',
          tags: ['世界遗产', '历史建筑', '华侨文化']
        },
        {
          id: 5,
          name: '长隆欢乐世界',
          icon: '🎢',
          type: '主题公园',
          description: '世界级主题乐园',
          address: '广州市番禺区汉溪大道东299号',
          hours: '09:30-18:00',
          price: '250元',
          tags: ['主题公园', '娱乐', '亲子游']
        }
      ],
      '浙江': [
        {
          id: 1,
          name: '西湖',
          icon: '🏞️',
          type: '自然景观',
          description: '世界文化遗产，人间天堂',
          address: '杭州市西湖区',
          hours: '全天开放',
          price: '免费',
          tags: ['世界遗产', '自然景观', '免费景点']
        },
        {
          id: 2,
          name: '乌镇',
          icon: '🏮',
          type: '古镇',
          description: '江南水乡古镇',
          address: '嘉兴市桐乡市',
          hours: '09:00-22:00',
          price: '150元',
          tags: ['古镇', '水乡', '历史文化']
        },
        {
          id: 3,
          name: '普陀山',
          icon: '⛩️',
          type: '宗教圣地',
          description: '中国佛教四大名山之一',
          address: '舟山市普陀区',
          hours: '06:00-18:00',
          price: '160元',
          tags: ['佛教圣地', '海岛', '宗教文化']
        },
        {
          id: 4,
          name: '千岛湖',
          icon: '🏞️',
          type: '自然景观',
          description: '天下第一秀水',
          address: '杭州市淳安县',
          hours: '08:00-17:00',
          price: '195元',
          tags: ['自然景观', '湖泊', '生态旅游']
        },
        {
          id: 5,
          name: '雁荡山',
          icon: '🏔️',
          type: '自然景观',
          description: '东南第一山',
          address: '温州市乐清市',
          hours: '06:00-18:00',
          price: '50元',
          tags: ['自然景观', '名山', '地质奇观']
        }
      ],
      '江苏': [
        {
          id: 1,
          name: '拙政园',
          icon: '🏞️',
          type: '古典园林',
          description: '苏州古典园林的代表',
          address: '苏州市姑苏区东北街178号',
          hours: '07:30-17:30',
          price: '70元',
          tags: ['世界遗产', '古典园林', '江南文化']
        },
        {
          id: 2,
          name: '中山陵',
          icon: '🏛️',
          type: '历史建筑',
          description: '中国近代建筑史上的重要陵墓',
          address: '南京市玄武区紫金山南麓',
          hours: '08:30-17:00',
          price: '免费',
          tags: ['历史建筑', '免费景点', '爱国主义教育']
        },
        {
          id: 3,
          name: '瘦西湖',
          icon: '🏞️',
          type: '自然景观',
          description: '扬州最著名的风景区',
          address: '扬州市邗江区大虹桥路28号',
          hours: '06:30-18:00',
          price: '100元',
          tags: ['自然景观', '古典园林', '江南水乡']
        },
        {
          id: 4,
          name: '周庄',
          icon: '🏘️',
          type: '古镇',
          description: '中国第一水乡',
          address: '苏州市昆山市周庄镇',
          hours: '08:00-21:00',
          price: '100元',
          tags: ['古镇', '水乡', '历史文化']
        }
      ],
      '四川': [
        {
          id: 1,
          name: '九寨沟',
          icon: '🏞️',
          type: '自然景观',
          description: '世界自然遗产，童话世界',
          address: '阿坝藏族羌族自治州九寨沟县',
          hours: '07:00-18:00',
          price: '220元',
          tags: ['世界遗产', '自然景观', '必游景点']
        },
        {
          id: 2,
          name: '峨眉山',
          icon: '🏔️',
          type: '自然景观',
          description: '中国佛教四大名山之一',
          address: '乐山市峨眉山市',
          hours: '06:00-18:00',
          price: '185元',
          tags: ['世界遗产', '佛教圣地', '名山']
        },
        {
          id: 3,
          name: '都江堰',
          icon: '🏛️',
          type: '历史建筑',
          description: '世界文化遗产，古代水利工程',
          address: '成都市都江堰市',
          hours: '08:00-18:00',
          price: '80元',
          tags: ['世界遗产', '历史建筑', '古代工程']
        },
        {
          id: 4,
          name: '大熊猫基地',
          icon: '🐼',
          type: '动物园',
          description: '世界著名的大熊猫繁育研究基地',
          address: '成都市成华区熊猫大道1375号',
          hours: '07:30-18:00',
          price: '58元',
          tags: ['动物园', '大熊猫', '亲子游']
        }
      ],
      '云南': [
        {
          id: 1,
          name: '丽江古城',
          icon: '🏘️',
          type: '古镇',
          description: '世界文化遗产，纳西族古城',
          address: '丽江市古城区',
          hours: '全天开放',
          price: '50元',
          tags: ['世界遗产', '古镇', '民族文化']
        },
        {
          id: 2,
          name: '石林',
          icon: '🏔️',
          type: '自然景观',
          description: '世界自然遗产，天下第一奇观',
          address: '昆明市石林彝族自治县',
          hours: '08:00-18:00',
          price: '175元',
          tags: ['世界遗产', '自然景观', '地质奇观']
        },
        {
          id: 3,
          name: '大理古城',
          icon: '🏘️',
          type: '古镇',
          description: '南诏国和大理国的都城',
          address: '大理白族自治州大理市',
          hours: '全天开放',
          price: '免费',
          tags: ['古镇', '历史文化', '免费景点']
        },
        {
          id: 4,
          name: '西双版纳',
          icon: '🌴',
          type: '自然景观',
          description: '热带雨林，动植物王国',
          address: '西双版纳傣族自治州',
          hours: '08:00-18:00',
          price: '65元',
          tags: ['自然景观', '热带雨林', '生态旅游']
        }
      ],
      '山东': [
        {
          id: 1,
          name: '泰山',
          icon: '🏔️',
          type: '自然景观',
          description: '五岳之首，世界文化与自然双重遗产',
          address: '泰安市泰山区',
          hours: '全天开放',
          price: '115元',
          tags: ['世界遗产', '名山', '必游景点']
        },
        {
          id: 2,
          name: '孔庙',
          icon: '🏛️',
          type: '历史建筑',
          description: '世界文化遗产，儒家文化圣地',
          address: '济宁市曲阜市',
          hours: '08:00-17:30',
          price: '140元',
          tags: ['世界遗产', '历史建筑', '儒家文化']
        },
        {
          id: 3,
          name: '青岛栈桥',
          icon: '🌉',
          type: '城市景观',
          description: '青岛的标志性建筑',
          address: '青岛市市南区太平路12号',
          hours: '全天开放',
          price: '免费',
          tags: ['城市地标', '海滨', '免费景点']
        },
        {
          id: 4,
          name: '趵突泉',
          icon: '💧',
          type: '自然景观',
          description: '天下第一泉',
          address: '济南市历下区趵突泉南路1号',
          hours: '07:00-19:00',
          price: '40元',
          tags: ['自然景观', '泉水', '历史文化']
        }
      ],
      '陕西': [
        {
          id: 1,
          name: '兵马俑',
          icon: '🏛️',
          type: '历史建筑',
          description: '世界文化遗产，世界第八大奇迹',
          address: '西安市临潼区秦陵北路',
          hours: '08:30-17:00',
          price: '120元',
          tags: ['世界遗产', '历史建筑', '必游景点']
        },
        {
          id: 2,
          name: '华山',
          icon: '🏔️',
          type: '自然景观',
          description: '五岳之一，奇险天下第一山',
          address: '渭南市华阴市',
          hours: '全天开放',
          price: '160元',
          tags: ['名山', '自然景观', '登山']
        },
        {
          id: 3,
          name: '大雁塔',
          icon: '🏛️',
          type: '历史建筑',
          description: '世界文化遗产，佛教圣地',
          address: '西安市雁塔区慈恩寺',
          hours: '08:00-17:30',
          price: '50元',
          tags: ['世界遗产', '历史建筑', '佛教文化']
        },
        {
          id: 4,
          name: '法门寺',
          icon: '⛩️',
          type: '宗教圣地',
          description: '世界佛教圣地',
          address: '宝鸡市扶风县',
          hours: '08:00-17:30',
          price: '100元',
          tags: ['佛教圣地', '历史建筑', '宗教文化']
        }
      ],
      '新疆': [
        {
          id: 1,
          name: '天山天池',
          icon: '🏞️',
          type: '自然景观',
          description: '世界自然遗产，天山明珠',
          address: '昌吉回族自治州阜康市',
          hours: '09:00-19:00',
          price: '155元',
          tags: ['世界遗产', '自然景观', '高山湖泊']
        },
        {
          id: 2,
          name: '喀纳斯湖',
          icon: '🏞️',
          type: '自然景观',
          description: '中国最美湖泊',
          address: '阿勒泰地区布尔津县',
          hours: '08:00-20:00',
          price: '230元',
          tags: ['自然景观', '湖泊', '生态旅游']
        },
        {
          id: 3,
          name: '吐鲁番',
          icon: '🏜️',
          type: '自然景观',
          description: '火洲，葡萄之乡',
          address: '吐鲁番市',
          hours: '08:00-20:00',
          price: '免费',
          tags: ['自然景观', '沙漠', '免费景点']
        },
        {
          id: 4,
          name: '伊犁河谷',
          icon: '🌾',
          type: '自然景观',
          description: '塞外江南',
          address: '伊犁哈萨克自治州',
          hours: '全天开放',
          price: '免费',
          tags: ['自然景观', '草原', '免费景点']
        }
      ],
      '湖北': [
        {
          id: 1,
          name: '黄鹤楼',
          icon: '🏯',
          type: '历史建筑',
          description: '江南三大名楼之一，武汉标志性建筑',
          address: '武汉市武昌区蛇山西坡特1号',
          hours: '08:30-17:30',
          price: '70元',
          tags: ['历史建筑', '文化遗产', '武汉地标'],
          image: '@/tutu/黄鹤楼.jpg'
        },
        {
          id: 2,
          name: '三峡大坝',
          icon: '🚧',
          type: '现代建筑',
          description: '世界最大的水利枢纽工程',
          address: '宜昌市夷陵区三斗坪镇',
          hours: '08:00-17:30',
          price: '105元',
          tags: ['现代建筑', '水利工程', '必游景点']
        },
        {
          id: 3,
          name: '武当山',
          icon: '⛰️',
          type: '自然景观',
          description: '道教圣地，世界文化遗产',
          address: '十堰市丹江口市武当山旅游经济特区',
          hours: '07:30-17:30',
          price: '130元',
          tags: ['世界遗产', '道教圣地', '自然景观']
        },
        {
          id: 4,
          name: '神农架',
          icon: '🌳',
          type: '自然景观',
          description: '世界自然遗产，野人传说之地',
          address: '神农架林区',
          hours: '07:30-17:30',
          price: '130元',
          tags: ['世界遗产', '自然景观', '生态旅游']
        },
        {
          id: 5,
          name: '恩施大峡谷',
          icon: '🏞️',
          type: '自然景观',
          description: '中国最美的大峡谷之一',
          address: '恩施土家族苗族自治州恩施市',
          hours: '08:00-16:00',
          price: '170元',
          tags: ['自然景观', '地质奇观', '峡谷风光']
        }
      ]
    }

    // API调用函数
    const loadProvinceAttractions = async () => {
      loading.value = true
      try {
        const encodedProvinceName = encodeURIComponent(provinceName.value)
        const url = `${API_BASE_URL}/province-attractions/province/${encodedProvinceName}`
        console.log('请求URL:', url)
        console.log('省份名称:', provinceName.value)
        console.log('编码后:', encodedProvinceName)
        
        const response = await axios.get(url)
        apiAttractions.value = response.data
        console.log('API返回数据:', response.data)
        console.log('景点数量:', response.data.length)
      } catch (error) {
        console.error('加载省份景点失败:', error)
        console.error('错误详情:', error.response?.data)
        // 如果API调用失败，使用静态数据作为备选
        apiAttractions.value = attractionsData[provinceName.value] || []
      } finally {
        loading.value = false
      }
    }

    const attractions = computed(() => {
      // 优先使用API数据，如果没有则使用静态数据
      if (apiAttractions.value.length > 0) {
        // 将API数据转换为前端需要的格式
        return apiAttractions.value.map(item => ({
          id: item.id,
          name: item.attractionName,
          icon: getAttractionIcon(item.attractionType),
          type: item.attractionType,
          description: item.description,
          address: item.address,
          hours: item.openingHours || '待定',
          price: item.ticketPrice ? `¥${item.ticketPrice}` : '免费',
          tags: [item.attractionType, item.isPopular ? '热门' : '', item.isRecommended ? '推荐' : ''].filter(Boolean)
        }))
      }
      
      // 使用静态数据作为备选
      const result = attractionsData[provinceName.value] || [
        {
          id: 1,
          name: '待开发景点',
          icon: '🚧',
          type: '建设中',
          description: '该省份的景点信息正在完善中',
          address: '待定',
          hours: '待定',
          price: '待定',
          tags: ['建设中']
        }
      ]
      console.log('使用静态数据:', result.length, '个景点')
      return result
    })

    // 根据景点类型获取图标
    const getAttractionIcon = (type) => {
      const iconMap = {
        'HISTORICAL': '🏛️',
        'NATURAL': '🏞️',
        'CULTURAL': '🏮',
        'MODERN': '🗼',
        'RELIGIOUS': '⛩️',
        'ENTERTAINMENT': '🎠',
        'SHOPPING': '🛍️',
        'FOOD': '🍜'
      }
      return iconMap[type] || '🏛️'
    }

    const goBack = () => {
      router.push('/china-map')
    }

    const selectAttraction = (attraction) => {
      // 跳转到外部链接
      window.open('http://www.cnhhl.com/index.php/', '_blank');
    }

    const closeModal = () => {
      selectedAttraction.value = null
    }

    onMounted(() => {
      // 页面加载动画
      document.body.style.overflow = 'hidden'
      setTimeout(() => {
        document.body.style.overflow = 'auto'
      }, 500)
      
      // 加载省份景点数据
      console.log('开始加载省份景点数据...')
      loadProvinceAttractions()
    })

    return {
      provinceName,
      provinceData,
      attractions,
      selectedAttraction,
      loading,
      goBack,
      selectAttraction,
      closeModal
    }
  }
}
</script>

<style scoped>
.province-detail {
  min-height: 100vh;
  background-image: url('@/tutu/login1.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-attachment: fixed;
  padding-bottom: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  color: white;
  position: sticky;
  top: 0;
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

.province-card {
  margin: 2rem;
  background-image: url('@/tutu/湖北.jpg');
  background-size: cover;
  background-position: center;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  animation: slideInDown 0.6s ease;
  color: white;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.province-header h2 {
  color: white;
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8);
}

.province-header p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.1rem;
  text-align: center;
  margin-bottom: 2rem;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.stats {
  display: flex;
  justify-content: center;
  gap: 3rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin-bottom: 0.5rem;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.stat-label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.7);
}

.attractions-section {
  margin: 2rem;
}

.attractions-section h3 {
  color: white;
  font-size: 2rem;
  text-align: center;
  margin-bottom: 2rem;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: white;
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

.attractions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.attraction-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 15px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  animation: slideInUp 0.6s ease;
}

.attraction-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
}

.attraction-image {
  position: relative;
  height: 200px;
  background: linear-gradient(135deg, #ff6b6b, #4ecdc4);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.attraction-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  font-size: 4rem;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.attraction-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(255, 255, 255, 0.9);
  color: #333;
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 600;
}

.attraction-info {
  padding: 1.5rem;
}

.attraction-info h4 {
  color: #333;
  font-size: 1.3rem;
  margin-bottom: 0.5rem;
}

.attraction-info p {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.attraction-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  background: #4a90e2;
  color: white;
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.8rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 2rem;
}

.modal-content {
  background: white;
  border-radius: 20px;
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  animation: modalSlideIn 0.3s ease;
}

.modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #666;
  z-index: 10;
}

.modal-close:hover {
  color: #333;
}

.modal-header {
  padding: 2rem 2rem 1rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  color: #333;
  font-size: 1.8rem;
  margin: 0;
}

.modal-type {
  background: #4a90e2;
  color: white;
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.8rem;
}

.modal-body {
  padding: 2rem;
}

.modal-image {
  text-align: center;
  margin-bottom: 1.5rem;
}

.image-placeholder.large {
  font-size: 6rem;
  color: #4a90e2;
}

.modal-info p {
  color: #666;
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.modal-details {
  margin-bottom: 1.5rem;
}

.detail-item {
  margin-bottom: 0.8rem;
  color: #333;
}

.detail-item strong {
  color: #4a90e2;
}

.modal-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
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
  
  .province-card {
    margin: 1rem;
    padding: 1.5rem;
  }
  
  .province-header h2 {
    font-size: 2rem;
  }
  
  .stats {
    gap: 1.5rem;
  }
  
  .attractions-section {
    margin: 1rem;
  }
  
  .attractions-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-overlay {
    padding: 1rem;
  }
  
  .modal-content {
    max-height: 90vh;
  }
  
  .modal-header,
  .modal-body {
    padding: 1.5rem;
  }
}
</style>
