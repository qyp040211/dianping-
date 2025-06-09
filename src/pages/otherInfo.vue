<template>
    <div id="app">
      <!-- 头部导航 -->
      <div class="header">
        <div class="header-back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="header-title">&nbsp;&nbsp;&nbsp;</div>
      </div>
      
      <!-- 用户基本信息 -->
      <div class="basic">
        <div class="basic-icon">
          <img :src="user.icon || '/imgs/icons/default-icon.png'" alt="用户头像">
        </div>
        <div class="basic-info">
          <div class="name">{{ user.nickName }}</div>
          <span>杭州</span>
        </div>
        <div class="follow-btn" @click="follow">
          {{ followed ? "取消关注" : "关注" }}
        </div>
      </div>
      
      <!-- 个人简介 -->
      <div class="introduce">
        <span v-if="info.introduce">{{ info.introduce }}</span>
        <span v-else>这个人很懒，什么都没有留下</span>
      </div>
      
      <!-- 内容区域 - 标签页 -->
      <div class="content">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <!-- 笔记标签页 -->
          <el-tab-pane label="笔记" name="1">
            <div v-for="b in blogs" :key="b.id" class="blog-item">
              <div class="blog-img">
                <img :src="b.images.split(',')[0]" alt="笔记图片">
              </div>
              <div class="blog-info">
                <div class="blog-title" v-html="b.title"></div>
                <div class="blog-liked">
                  <img src="/imgs/thumbup.png" alt="点赞"> {{ b.liked }}
                </div>
                <div class="blog-comments">
                  <i class="el-icon-chat-dot-round"></i> {{ b.comments }}
                </div>
              </div>
            </div>
            
            <!-- 无内容提示 -->
            <div v-if="blogs.length === 0" class="empty-tab">
              <i class="el-icon-notebook-2"></i>
              <p>该用户还没有发布任何笔记</p>
            </div>
          </el-tab-pane>
          
          <!-- 共同关注标签页 -->
          <el-tab-pane label="共同关注" name="2">
            <div class="common-follow-header">你们都关注了：</div>
            <div class="follow-info" v-for="u in commonFollows" :key="u.id">
              <div class="follow-info-icon" @click="toOtherInfo(u.id)">
                <img :src="u.icon || '/imgs/icons/default-icon.png'" alt="用户头像">
              </div>
              <div class="follow-info-name">
                <div class="name">{{ u.nickName }}</div>
              </div>
              <div class="follow-info-btn" @click="toOtherInfo(u.id)">
                去主页看看
              </div>
            </div>
            
            <!-- 无共同关注提示 -->
            <div v-if="commonFollows.length === 0" class="empty-tab">
              <i class="el-icon-user"></i>
              <p>你们还没有共同关注的好友</p>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

    </div>
  </template>
  
  <script>
  import service from "../utlis/request";
  import {util} from "@/utlis/common";
  
  export default {
    name: 'UserProfile',
    data() {
      return {
        user: {},          // 当前查看的用户信息
        loginUser: {},      // 登录用户信息
        activeName: "1",    // 当前激活的标签页
        info: {},           // 用户详情信息
        blogs: [],          // 用户笔记列表
        followed: false,    // 是否已关注该用户
        commonFollows: []   // 共同关注列表
      };
    },
    created() {
      this.queryUser();
      this.queryLoginUser();
    },
    methods: {
      // 查询当前查看的用户信息
      async queryUser() {
        try {
          const id = util.getUrlParam("id");
          const { data } = await service.get(`/user/${id}`);
          this.user = data;
          await this.queryUserInfo();
          await this.queryBlogs();
          await this.isFollowed();
        } catch (err) {
          console.error('获取用户信息失败', err);
          this.$message.error('加载用户信息失败');
        }
      },
      
      // 查询登录用户信息
      async queryLoginUser() {
        try {
          const { data } = await service.get("/user/me");
          this.loginUser = data;
        } catch (err) {
          console.error('获取登录用户信息失败', err);
        }
      },
      
      // 查询用户详情信息
      async queryUserInfo() {
        try {
          const { data } = await service.get(`/user/info/${this.user.id}`);
          if (data) {
            this.info = data;
            sessionStorage.setItem("userInfo", JSON.stringify(data));
          }
        } catch (err) {
          this.$message.error('获取用户详情失败');
        }
      },
      
      // 查询用户笔记
      async queryBlogs() {
        try {
          const { data } = await service.get("/blog/of/user", {
            params: { id: this.user.id, current: 1 }
          });
          this.blogs = data;
        } catch (err) {
          this.$message.error('获取笔记列表失败');
        }
      },
      
      // 检查是否已关注该用户
      async isFollowed() {
        try {
          const { data } = await service.get(`/follow/or/not/${this.user.id}`);
          this.followed = data;
        } catch (err) {
          this.$message.error('获取关注状态失败');
        }
      },
      
      // 查询共同关注
      async queryCommonFollow() {
        try {
          const { data } = await service.get(`/follow/common/${this.user.id}`);
          this.commonFollows = data;
        } catch (err) {
          this.$message.error('获取共同关注列表失败');
        }
      },
      
      // 关注/取消关注操作
      async follow() {
        try {
          await service.put(`/follow/${this.user.id}/${!this.followed}`);
          this.$message.success(this.followed ? "已取消关注" : "已关注");
          this.followed = !this.followed;
        } catch (err) {
          this.$message.error('操作失败');
        }
      },
      
      // 标签页切换事件
      handleClick(tab) {
        if (tab.name === '2') {
          this.queryCommonFollow();
        }
      },
      
      // 跳转到其他用户主页
      toOtherInfo(id) {
        // location.href = `/other-info.html?id=${id}`;
        this.$router.push(`/other-info?id=${id}`)
      },
      
      // 返回上一页
      goBack() {
        history.back();
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
  
  /* 用户基本信息区域 */
  .basic {
    display: flex;
    padding: 20px 15px;
    align-items: center;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
  }
  
  .basic-icon img {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    margin-right: 15px;
    border: 2px solid #f8f8f8;
    object-fit: cover;
  }
  
  .basic-info {
    flex: 1;
  }
  
  .name {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  .follow-btn {
    padding: 8px 15px;
    background-color: #ff6633;
    color: white;
    border-radius: 20px;
    font-size: 14px;
    cursor: pointer;
    min-width: 80px;
    text-align: center;
  }
  
  /* 个人简介 */
  .introduce {
    padding: 15px;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
    color: #666;
    font-size: 14px;
  }
  
  /* 内容区域 */
  .content {
    height: calc(100vh - 250px);
    background-color: #f5f5f5;
    overflow-y: auto;
  }
  
  /* 笔记列表样式 */
  .blog-item {
    display: flex;
    padding: 15px;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
    transition: background-color 0.3s;
  }
  
  .blog-item:hover {
    background-color: #f9f9f9;
  }
  
  .blog-img {
    width: 100px;
    height: 100px;
    border-radius: 8px;
    overflow: hidden;
    margin-right: 15px;
    flex-shrink: 0;
  }
  
  .blog-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .blog-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  
  .blog-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  
  .blog-liked, .blog-comments {
    display: inline-flex;
    align-items: center;
    margin-right: 15px;
    font-size: 14px;
    color: #666;
  }
  
  .blog-liked img {
    width: 16px;
    height: 16px;
    margin-right: 5px;
  }
  
  /* 共同关注区域 */
  .common-follow-header {
    padding: 15px;
    font-size: 16px;
    font-weight: bold;
    color: #333;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
  }
  
  .follow-info {
    display: flex;
    align-items: center;
    padding: 15px;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
  }
  
  .follow-info-icon {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 15px;
    cursor: pointer;
  }
  
  .follow-info-icon img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .follow-info-name {
    flex: 1;
  }
  
  .follow-info-name .name {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  .follow-info-btn {
    padding: 8px 15px;
    background-color: #f8f8f8;
    border-radius: 15px;
    font-size: 14px;
    color: #666;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .follow-info-btn:hover {
    background-color: #eee;
  }
  
  /* 空状态提示 */
  .empty-tab {
    text-align: center;
    padding: 50px 0;
    color: #999;
    font-size: 16px;
  }
  
  .empty-tab i {
    font-size: 50px;
    margin-bottom: 15px;
    color: #ddd;
  }
  
  /* Element UI 标签页样式调整 */
  .el-tabs {
    height: 100%;
  }
  
  .el-tab-pane {
    height: 100%;
    overflow-y: auto;
  }
  
  .el-tabs__header {
    background-color: #fff;
    margin: 0;
  }
  
  .el-tabs__nav-wrap::after {
    background-color: transparent;
  }
  
  .el-tabs__item {
    font-size: 16px;
    font-weight: bold;
  }
  
  .el-tabs--bottom .el-tabs__item.is-bottom:nth-child(2),
  .el-tabs--bottom .el-tabs__item.is-top:nth-child(2),
  .el-tabs--top .el-tabs__item.is-bottom:nth-child(2),
  .el-tabs--top .el-tabs__item.is-top:nth-child(2) {
    padding-left: 15px;
  }
  </style>