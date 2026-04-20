<template>
  <div id="app">
    <!-- 科技感装饰元素 -->
    <div class="data-stream"></div>
    <div class="tech-decoration" style="top: 20%; left: 10%;"></div>
    <div class="tech-decoration" style="top: 60%; right: 15%;"></div>
    <div class="tech-decoration" style="bottom: 30%; left: 20%;"></div>
    
    <!-- 浮动粒子 -->
    <div class="particle" style="left: 10%; animation-delay: 0s;"></div>
    <div class="particle" style="left: 30%; animation-delay: 2s;"></div>
    <div class="particle" style="left: 50%; animation-delay: 4s;"></div>
    <div class="particle" style="left: 70%; animation-delay: 6s;"></div>
    <div class="particle" style="left: 90%; animation-delay: 8s;"></div>
    
    <router-view />
  </div>
</template>

<script setup>
// 应用根组件
</script>

<style>
#app {
  height: 100vh;
}
</style>

