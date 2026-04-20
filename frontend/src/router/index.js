import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Planner from '@/views/Planner.vue'
import Plans from '@/views/Plans.vue'
import Map from '@/views/Map.vue'
import Attractions from '@/views/Attractions.vue'
import ChinaMap from '@/views/ChinaMap.vue'
import ProvinceDetail from '@/views/ProvinceDetail.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/planner',
    name: 'Planner',
    component: Planner
  },
  {
    path: '/plans',
    name: 'Plans',
    component: Plans
  },
  {
    path: '/map',
    name: 'Map',
    component: Map
  },
  {
    path: '/attractions',
    name: 'Attractions',
    component: Attractions
  },
  {
    path: '/china-map',
    name: 'ChinaMap',
    component: ChinaMap
  },
  {
    path: '/province/:name',
    name: 'ProvinceDetail',
    component: ProvinceDetail,
    props: true
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
