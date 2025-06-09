<template>
    <div class="login-container">
      <!-- 头部导航 -->
      <div class="header">
        <div class="header-back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="header-title">手机号码快捷登录&nbsp;&nbsp;&nbsp;</div>
      </div>
      
      <!-- 登录表单 -->
      <div class="content">
        <div class="login-form">
          <div class="phone-input-group">
            <el-input 
              placeholder="请输入手机号" 
              v-model="form.phone"
              class="phone-input"
            ></el-input>
            
            <el-button 
              type="success" 
              :disabled="disabled"
              @click="sendCode"
              class="code-btn"
            >
              {{ codeBtnMsg }}
            </el-button>
          </div>
  
          <div class="input-spacer"></div>
          
          <el-input 
            placeholder="请输入验证码" 
            v-model="form.code"
            class="code-input"
          ></el-input>
          
          <div class="register-hint">
            未注册的手机号码验证后自动创建账户
          </div>
          
          <el-button 
            @click="login" 
            class="login-btn"
          >
            登录
          </el-button>
          
          <div class="password-login-link">
            <router-link to="/login2" >密码登录</router-link>
          </div>
        </div>
        
        <!-- 协议同意区域 -->
        <div class="login-radio">
          <div class="radio-container">
            <input 
              type="radio" 
              name="readed" 
              v-model="radio" 
              value="1"
              id="agreementRadio"
            >
            <label for="agreementRadio"></label>
          </div>
          <div class="agreement-text">
            我已阅读并同意
            <a href="javascript:void(0)">《大众点评用户服务协议[](@replace=10001)》</a>、
            <a href="javascript:void(0)">《隐私政策[](@replace=10002)》</a>
            等，接受免除或者限制责任、诉讼管辖约定等粗体标示条款
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import service from "../utlis/request";
  import {Button, Input, Message} from 'element-ui';
  
  export default {
    name: 'LoginPage',
    components: {
      'el-button': Button,
      'el-input': Input
    },
    data() {
      return {
        radio: "",
        disabled: false, // 发送短信按钮状态
        codeBtnMsg: "发送验证码", // 按钮提示文本
        form: {
          phone: "",
          code: ""
        }
      };
    },
    methods: {
      // 登录方法
      async login() {
        if (!this.radio) {
          this.$message.error("请先确认阅读用户协议！");
          return;
        }
        
        // 表单验证
        if (!this.form.phone || !this.form.code) {
          this.$message.error("手机号和验证码不能为空！");
          return;
        }
        
        try {
          const { data } = await service.post("/user/login", this.form);

          if (data) {
            // 保存用户token到sessionStorage
            sessionStorage.setItem("phone", this.form.phone);
            sessionStorage.setItem("token", data);
            console.log("登录成功的token",data);
            // 跳转到首页
            await this.$router.push("/index");
          }
        } catch (err) {
          this.$message.error(err.response?.data?.message || "登录失败");
        }
      },
      
      // 返回上一页
      goBack() {
        history.back();
      },
      
      // 发送验证码
      async sendCode() {
        if (!this.form.phone) {
          this.$message.error("手机号不能为空");
          return;
        }
        
        // 验证手机号格式
        if (!/^1[3-9]\d{9}$/.test(this.form.phone)) {
          this.$message.error("请输入正确的手机号码");
          return;
        }
        
        try {
          const res = await service.post("/user/code?phone=" + this.form.phone);
          if (res.success === true) {
            Message({
              message:"验证码发送成功",
              type: 'success',
              duration: 5 * 1000
            })
          }
          console.log(res);
          // 禁用按钮并开始倒计时
          this.disabled = true;
          this.startCountdown();
        } catch (err) {
          this.$message.error(err.response?.data?.message || "验证码发送失败");
        }
      },
      
      // 倒计时功能
      startCountdown() {
        let count = 60;
        this.codeBtnMsg = `${count}秒后可重发`;
        
        const timer = setInterval(() => {
          count--;
          this.codeBtnMsg = `${count}秒后可重发`;
          
          if (count <= 0) {
            clearInterval(timer);
            this.disabled = false;
            this.codeBtnMsg = "发送验证码";
          }
        }, 1000);
      }
    }
  };
  </script>
  
  <style scoped>
  /* 登录容器 */
  .login-container {
    max-width: 500px;
    margin: 0 auto;
    padding: 20px;
    background-color: #fff;
    min-height: 100vh;
    box-sizing: border-box;
  }
  
  /* 头部样式 */
  .header {
    display: flex;
    align-items: center;
    padding: 15px 0;
    margin-bottom: 30px;
    position: relative;
  }
  
  .header-back-btn {
    font-size: 20px;
    color: #333;
    cursor: pointer;
    position: absolute;
    left: 0;
  }
  
  .header-title {
    flex: 1;
    text-align: center;
    font-size: 18px;
    font-weight: bold;
  }
  
  /* 表单样式 */
  .login-form {
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }
  
  .phone-input-group {
    display: flex;
    justify-content: space-between;
    gap: 10px;
  }
  
  .phone-input {
    flex: 2;
  }
  
  .code-btn {
    flex: 1;
    background-color: #f63;
    border-color: #f63;
    color: white;
  }
  
  .code-btn:disabled {
    background-color: #ccc;
    border-color: #ccc;
  }
  
  .input-spacer {
    height: 15px;
  }
  
  .code-input {
    width: 100%;
  }
  
  .register-hint {
    text-align: center;
    color: #8c939d;
    margin: 15px 0;
    font-size: 14px;
  }
  
  .login-btn {
    width: 100%;
    background-color: #f63;
    color: #fff;
    border: none;
    height: 45px;
    font-size: 16px;
    margin-top: 10px;
  }
  
  .login-btn:hover {
    background-color: #e55;
  }
  
  .password-login-link {
    text-align: right;
    color: #333;
    margin: 15px 0;
  }
  
  .password-login-link a {
    color: #f63;
    text-decoration: none;
  }
  
  /* 协议区域 */
  .login-radio {
    display: flex;
    align-items: flex-start;
    margin-top: 30px;
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 8px;
  }
  
  .radio-container {
    margin-right: 10px;
  }
  
  .agreement-text {
    font-size: 13px;
    color: #666;
    line-height: 1.5;
  }
  
  .agreement-text a {
    color: #f63;
    text-decoration: none;
  }
  
  /* 响应式调整 */
  @media (max-width: 480px) {
    .phone-input-group {
      flex-direction: column;
    }
    
    .phone-input, .code-btn {
      width: 100%;
    }
  }
  </style>