<template>
  <div id="app">
<div>
<!--  <SplashAd v-if="true" />-->
  <router-view />
</div>
  </div>
</template>

<script>
// import SplashAd from "./components/splash-ad.vue";
export default {
  name: 'App',
  components: {
    // SplashAd

  },
  data() {
    return {
      showAd: false
    }
  },
  mounted() {
    const lastTime = localStorage.getItem('time')
    const today = new Date()
    const todayDay = today.getDate()

    if (!lastTime) {
      this.showAd = true
      localStorage.setItem('time', todayDay)
    } else {

      const diff = todayDay - parseInt(lastTime)
      if (diff >= 2 || diff < 0) { // 跨月情况也考虑
        this.showAd = true
        localStorage.setItem('time', todayDay)


      }
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
