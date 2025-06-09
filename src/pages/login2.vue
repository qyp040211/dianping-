<template>
  <div class="login-container">
    <div class="header">
      <div class="header-back-btn" @click="goBack">
        <i class="el-icon-arrow-left"></i>
      </div>
      <div class="header-title">密码登录&nbsp;&nbsp;&nbsp;</div>
    </div>

    <div class="content">
      <div class="login-form">
        <el-input
            placeholder="请输入手机号"
            v-model="form.phone"
            @keyup.enter="login"
        ></el-input>

        <div style="height: 5px"></div>

        <el-input
            placeholder="请输入密码"
            v-model="form.password"
            type="password"
            @keyup.enter="login"
        ></el-input>

        <div style="text-align: center; color: #8c939d; margin: 5px 0">
          <a href="javascript:void(0)" @click="forgotPassword">忘记密码</a>
        </div>

        <el-button
            @click="login"
            :loading="loading"
            style="width: 100%; background-color: #f63; color: #fff;"
        >
          登录
        </el-button>

        <div style="text-align: right; color: #333333; margin: 5px 0">
          <router-link to="/login1">验证码登录</router-link>
        </div>
      </div>

      <div class="login-radio">
        <el-checkbox v-model="agreed">
          我已阅读并同意
          <a href="javascript:void(0)" @click="showAgreement('service')">
            《大众点评用户服务协议》
          </a>、
          <a href="javascript:void(0)" @click="showAgreement('privacy')">
            《隐私政策》
          </a>
          等，接受免除或者限制责任、诉讼管辖约定等粗体标示条款
        </el-checkbox>
      </div>
    </div>

    <!-- 协议弹窗 -->
    <el-dialog
        :title="dialogTitle"
        :visible.sync="showDialog"
        width="80%"
    >
      <div v-html="agreementContent"></div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'LoginPage',
  data() {
    return {
      form: {
        phone: '',
        password: ''
      },
      agreed: false,
      loading: false,
      showDialog: false,
      dialogTitle: '',
      agreementContent: '',
      agreements: {
        service: '<p>这里是用户服务协议内容...</p>',
        privacy: '<p>这里是隐私政策内容...</p>'
      }
    }
  },
  methods: {
    login() {
      if (!this.agreed) {
        this.$message.error("请先确认阅读用户协议！");
        return;
      }

      if (!this.form.phone) {
        this.$message.error("请输入手机号");
        return;
      }

      if (!this.form.password) {
        this.$message.error("请输入密码");
        return;
      }

      this.loading = true;

      this.$axios.post("/user/login", this.form)
          .then(({ data }) => {
            if (data) {
              // 保存用户信息
              this.$message.success("登录成功");
              sessionStorage.setItem("token", data);
              // 跳转到首页或原页面
              const redirect = this.$route.query.redirect || '/info';
              this.$router.push(redirect);
            }
          })
          .catch(err => {
            this.$message.error(err.response?.data?.message || "登录失败");
          })
          .finally(() => {
            this.loading = false;
          });
    },

    goBack() {
      this.$router.go(-1);
    },

    forgotPassword() {
      this.$router.push('/forgot-password');
    },

    showAgreement(type) {
      this.dialogTitle = type === 'service'
          ? '用户服务协议'
          : '隐私政策';
      this.agreementContent = this.agreements[type];
      this.showDialog = true;
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.header-back-btn {
  font-size: 20px;
  color: #333;
  cursor: pointer;
}

.header-title {
  flex: 1;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.login-form {
  margin-bottom: 20px;
}

.login-radio {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.login-radio a {
  color: #f63;
  text-decoration: none;
}

.el-input {
  margin-bottom: 15px;
}

.el-button {
  margin-top: 10px;
}

/* 覆盖Element UI样式 */
.el-radio__input.is-checked .el-radio__inner {
  border-color: #F63;
  background: #F63;
}

.el-input__inner:focus {
  border: 1px solid #F63;
}
</style>