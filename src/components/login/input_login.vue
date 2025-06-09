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
        <el-input placeholder="请输入手机号" v-model="form.phone"></el-input>
        <div style="height: 5px"></div>
        <el-input
            placeholder="请输入密码"
            v-model="form.password"
            type="password"
            show-password
        ></el-input>
        <div style="text-align: center; color: #8c939d; margin: 5px 0">
          <a href="javascript:void(0)" @click="handleForgetPassword">忘记密码</a>
        </div>
        <el-button
            @click="handleLogin"
            style="width: 100%; background-color:#f63; color: #fff;"
            :loading="loading"
        >
          登录
        </el-button>
        <div style="text-align: right; color:#333333; margin: 5px 0">
          <router-link to="/sms-login">验证码登录</router-link>
        </div>
      </div>
      <div class="login-radio">
        <el-checkbox v-model="agreed">
          我已阅读并同意
          <a href="javascript:void(0)" @click="showUserAgreement">
            《大众点评用户服务协议》
          </a>、
          <a href="javascript:void(0)" @click="showPrivacyPolicy">
            《隐私政策》
          </a>
          等，接受免除或者限制责任、诉讼管辖约定等粗体标示条款
        </el-checkbox>
      </div>
    </div>

    <!-- 协议弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="80%">
      <div v-html="dialogContent"></div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      form: {
        phone: '',
        password: ''
      },
      agreed: false,
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      dialogContent: '',
      agreements: {
        user: '<p>这里是用户服务协议内容...</p>',
        privacy: '<p>这里是隐私政策内容...</p>'
      }
    }
  },
  methods: {
    handleLogin() {
      if (!this.agreed) {
        this.$message.error('请先确认阅读用户协议！')
        return
      }

      if (!this.form.phone) {
        this.$message.error('请输入手机号')
        return
      }

      if (!this.form.password) {
        this.$message.error('请输入密码')
        return
      }

      if (!/^1[3-9]\d{9}$/.test(this.form.phone)) {
        this.$message.error('请输入正确的手机号')
        return
      }

      this.loading = true

      this.$http.post('/user/login', this.form)
          .then(({ data }) => {
            sessionStorage.setItem('token', data)
            this.$router.push('/info')
          })
          .catch(err => {
            this.$message.error(err.response?.data?.message || '登录失败')
          })
          .finally(() => {
            this.loading = false
          })
    },
    goBack() {
      this.$router.go(-1)
    },
    handleForgetPassword() {
      this.$router.push('/forget-password')
    },
    showUserAgreement() {
      this.dialogTitle = '用户服务协议'
      this.dialogContent = this.agreements.user
      this.dialogVisible = true
    },
    showPrivacyPolicy() {
      this.dialogTitle = '隐私政策'
      this.dialogContent = this.agreements.privacy
      this.dialogVisible = true
    }
  }
}
</script>

<style scoped>
.login-container {
  max-width: 100%;
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.header-back-btn {
  margin-right: 15px;
  font-size: 20px;
  cursor: pointer;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
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

.login-radio >>> .el-checkbox__label {
  font-size: 12px;
}

.login-radio a {
  color: #f63;
  text-decoration: none;
}
</style>