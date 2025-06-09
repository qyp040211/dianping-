<template>
<div>
  <div class="mainpage">
    <router-view />
  </div>
  <div class="foot">
    <div class="foot-box" :class="{active: activeBtn === 1}" @click="toPage(1)">
      <div class="foot-view"><i class="el-icon-s-home"></i></div>
      <div class="foot-text">首页</div>
    </div>
    <div class="foot-box" :class="{active: activeBtn === 2}" @click="toPage(2)">
      <div class="foot-view"><i class="el-icon-map-location"></i></div>
      <div class="foot-text">地图</div>
    </div>
    <div class="foot-box" @click="toPage(0)">
      <img class="add-btn" src="/img/add.png" alt="">
    </div>
    <div class="foot-box" :class="{active: activeBtn === 3}" @click="toPage(3)">
      <div class="foot-view"><i class="el-icon-chat-dot-round"></i></div>
      <div class="foot-text">消息</div>
    </div>
    <div class="foot-box" :class="{active: activeBtn === 4}" @click="toPage(4)">
      <div class="foot-view"><i class="el-icon-user"></i></div>
      <div class="foot-text">我的</div>
    </div>
  </div>
</div>
</template>

<script>
//

import service from "@/utlis/request";

export default {
  name: 'FootBar',
  data() {
    return {
      userS: 0,
      isLogin: false,
      activeBtn: 1,
    }
  },
  methods: {
    async checkLogin() {

      try {
        const {data} = await service.get("/user/me");
        if (data) {
          this.isLogin = true;
          sessionStorage.setItem("id", data.id);
        }
      } catch (err) {
        this.$message.error('请先登录');
        setTimeout(() => this.$router.push('/login2'), 1000);
      }
    },
    async toPage(i) {
      if (this.activeBtn === i) return; // 当前页面不跳转

      await this.checkLogin()

      if (i === 0) {


        let id = sessionStorage.getItem("id");
        const {data} = await service.get(`/user/info/${id}`);
        if (data.identityTag === 0) {
          try {
            await this.$confirm('您尚未实名认证，是否前往认证？', '实名认证提醒', {
              confirmButtonText: '前往认证',
              cancelButtonText: '取消',
              type: 'warning'
            });
            this.$router.push('/index/info');
          } catch (e) {
            // 用户取消，不做处理
          }
        } else {
          this.$router.push('/blog-edit');
        }




      } else if (i === 4) {

        this.$router.push('/index/info');

      } else if (i === 1) {
        this.$router.push('/');
      } else if (i === 2) {
        this.$router.push('/index/map');
      } else if (i === 3) {
        this.$router.push('/index/messages');
      }

      this.activeBtn = i
    },

  },

}
</script>

<style scoped>
.foot {
  display: flex;
  justify-content: space-around;
  align-items: center;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.foot-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex: 1;
}

.foot-view {
  font-size: 20px;
  margin-bottom: 2px;
}

.foot-text {
  font-size: 12px;
}

.add-btn {
  width: 40px;
  height: 40px;
}

.active {
  color: #409EFF; /* Element UI 主色 */
}

.active .foot-view {
  font-weight: bold;
}

.active .foot-text {
  font-weight: bold;
}
</style>