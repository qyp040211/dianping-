<template>
    <div id="app">
      <!-- 头部导航 -->
      <div class="header">
        <div class="header-back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="header-title">资料编辑&nbsp;&nbsp;&nbsp;</div>
      </div>
      
      <!-- 资料编辑区域 -->
      <div class="edit-container">
        <!-- 基本信息区域 -->
        <div class="info-box">
          <div class="info-item" @click="editAvatar">
            <div class="info-label">头像</div>
            <div class="info-btn">
              <img width="35" :src="user.icon || '/imgs/icons/default-icon.png'" alt="用户头像">
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
          <div class="divider"></div>
          <div class="info-item" @click="editNickname">
            <div class="info-label">昵称</div>
            <div class="info-btn">
              <div>{{ user.nickName }}</div>
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
          <div class="divider"></div>
          <div class="info-item" @click="editIntroduce">
            <div class="info-label">个人介绍</div>
            <div class="info-btn">
              <div class="truncate-text">{{ info.introduce || '介绍一下自己' }}</div>
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
        </div>
        
        <!-- 详细信息区域 -->
        <div class="info-box">
          <div class="info-item" @click="editGender">
            <div class="info-label">性别</div>
            <div class="info-btn">
              <div>{{ info.gender || '选择' }}</div>
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
          <div class="divider"></div>
          <div class="info-item" @click="editCity">
            <div class="info-label">城市</div>
            <div class="info-btn">
              <div>{{ info.city || '选择' }}</div>
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
          <div class="divider"></div>
          <div class="info-item" @click="editBirthday">
            <div class="info-label">生日</div>
            <div class="info-btn">
              <div>{{ info.birthday || '添加' }}</div>
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
        </div>
        
        <!-- 账户信息区域 -->
        <div class="info-box">
          <div class="info-item" @click="viewPoints">
            <div class="info-label">我的积分</div>
            <div class="info-btn">
              <div>查看积分</div>
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
          <div class="divider"></div>
          <div class="info-item" @click="upgradeVIP">
            <div class="info-label">会员等级</div>
            <div class="info-btn">
              <div><a href="javascript:void(0)">成为VIP尊享特权</a></div>
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </template>
  
  <script>
  import service from "../utlis/request";
  export default {
    name: 'UserProfileEdit',
    data() {
      return {
        user: {},     // 用户基本信息
        info: {},     // 用户详细信息
      };
    },
    created() {
      this.checkLogin();
    },
    methods: {
      // 检查登录状态
      async checkLogin() {
        try {
          const { data } = await service.get("/user/me");
          this.user = data;
          
          // 从sessionStorage获取用户详情
          const userInfo = sessionStorage.getItem("userInfo");
          this.info = userInfo ? JSON.parse(userInfo) : {};
        } catch (err) {
          this.$message.error('请先登录');
          setTimeout(() => this.$router.push('/login2'), 1000);
        }
      },
      
      // 返回上一页
      goBack() {
        history.back();
      },
      
      // 编辑头像
      editAvatar() {
        this.$message.info('头像编辑功能开发中');
      },
      
      // 编辑昵称
      editNickname() {
        this.$prompt('请输入新昵称', '修改昵称', {
          inputValue: this.user.nickName,
          inputPattern: /^.{2,12}$/,
          inputErrorMessage: '昵称长度需在2-12个字符之间'
        }).then(async ({ value }) => {
          const userInfo = JSON.parse(sessionStorage.getItem("userInfo") || "{}");
          const userId = userInfo.id || this.user.id;
          try {
            const res = await service.post("/user/nickname", { userId, nickName: value });
            console.log("hhh",res);
            this.user.nickName = value;
            if (res.success === true) {
              this.$message.success('昵称更新成功');
            }else{
              this.$message.error('昵称更新失败');
            }

          } catch (err) {
            this.$message.error('昵称更新失败');
          }
        }).catch(() => {});
      },
      
      // 编辑个人介绍
      editIntroduce() {
        this.$prompt('请输入个人介绍', '修改个人介绍', {
          inputValue: this.info.introduce || '',
          inputType: 'textarea'
        }).then(({ value }) => {
          this.updateUserInfo('introduce', value);
        }).catch(() => {});
      },
      
      // 编辑性别
      editGender() {
        const options = [
          { value: '男', label: '男' },
          { value: '女', label: '女' },
          { value: '保密', label: '保密' }
        ];
        
        this.$prompt('请选择性别', '修改性别', {
          inputType: 'select',
          inputValue: this.info.gender || '',
          inputOptions: options
        }).then(({ value }) => {
          this.updateUserInfo('gender', value);
        }).catch(() => {});
      },
      
      // 编辑城市
      editCity() {
        this.$prompt('请输入所在城市', '修改城市', {
          inputValue: this.info.city || ''
        }).then(({ value }) => {
          this.updateUserInfo('city', value);
        }).catch(() => {});
      },
      
      // 编辑生日
      editBirthday() {
        this.$prompt('请输入生日（格式：YYYY-MM-DD）', '修改生日', {
          inputValue: this.info.birthday || '',
          inputPattern: /^\d{4}-\d{2}-\d{2}$/,
          inputErrorMessage: '请输入正确的日期格式（YYYY-MM-DD）'
        }).then(({ value }) => {
          this.updateUserInfo('birthday', value);
        }).catch(() => {});
      },
      
      // 查看积分
      viewPoints() {
        this.$message.info('积分查看功能开发中');
      },
      
      // 升级VIP
      upgradeVIP() {
        this.$message.info('VIP升级功能开发中');
      },
      
      // 更新用户信息
      async updateUserInfo(field, value) {
        try {
          // 更新本地数据
          this.info[field] = value;
          
          // 更新sessionStorage
          sessionStorage.setItem("userInfo", JSON.stringify(this.info));
          
          // 发送到服务器（这里假设有更新接口）
          // await service.put(`/user/info/${this.user.id}`, { [field]: value });
          
          this.$message.success('更新成功');
        } catch (err) {
          this.$message.error('更新失败');
        }
      }
    }
  };
  </script>
  
  <style scoped>
  /* 头部样式 */
  .header {
    display: flex;
    align-items: center;
    padding: 15px;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
    position: sticky;
    top: 0;
    z-index: 100;
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
  }
  
  /* 编辑容器 */
  .edit-container {
    padding: 15px;
    background-color: #f5f5f5;
    min-height: calc(100vh - 120px);
  }
  
  /* 信息盒子 */
  .info-box {
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  
  /* 信息项 */
  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .info-item:hover {
    background-color: #f9f9f9;
  }
  
  .info-label {
    font-size: 16px;
    color: #333;
  }
  
  .info-btn {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #999;
  }
  
  .info-btn img {
    border-radius: 50%;
    object-fit: cover;
  }
  
  /* 分隔线 */
  .divider {
    height: 1px;
    background-color: #f1f1f1;
    margin: 0 15px;
  }
  
  /* 文字截断 */
  .truncate-text {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 150px;
    text-align: right;
  }
  
  /* 链接样式 */
  a {
    color: #ff6633;
    text-decoration: none;
  }
  </style>