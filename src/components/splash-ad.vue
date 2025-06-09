<template>
  <div class="splash-ad" v-if="visible" @click="goto">
    <img src="@/assets/splash.jpg" alt="广告图" class="ad-image" />
    <button class="skip-btn" @click="closeAd">跳过 {{ time }}s</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      visible: true,
      time: 3,
      timer: null
    }
  },
  mounted() {
    this.timer = setInterval(() => {
      this.time--
      if (this.time <= 0) {
        this.closeAd()
      }
    }, 1000)
  },
  methods: {
    name:"splash-ad",
    closeAd() {
      this.visible = false
      clearInterval(this.timer)
      this.$emit('ad-finished')
    },
    goto(){
      window.location.href = 'https://www.guet.edu.cn/xjzx/2024/0429/c1590a122464/page.psp'
    }
  }
}
</script>

<style scoped>
.splash-ad {
  position: fixed;
  top: 0; left: 0;
  width: 100vw;
  height: 100vh;
  background: #fff;
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}
.ad-image {
  max-width: 80%;
  max-height: 80%;
}
.skip-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 6px 12px;
}
</style>