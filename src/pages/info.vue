<template>
    <div id="app">
      <!-- 头部导航 -->
      <div class="header">
        <div class="header-back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="header-title">个人主页&nbsp;&nbsp;&nbsp;</div>
      </div>

      <!-- 用户基本信息 -->
      <div class="basic">
        <div class="basic-icon">
          <img :src="user.icon || '/imgs/icons/default-icon.png'" alt="用户头像">
        </div>
        <div class="basic-info">
          <div class="name">{{ user.nickName }}</div>
          <span>杭州</span>
          <div class="edit-btn" @click="toEdit">
            编辑资料
          </div>
        </div>
        <div class="logout-btn" @click="logout">
          退出登录
        </div>
      </div>

      <!-- 用户身份（美化+可点击） -->
      <div
        class="user-role"
        @click="showRoleDialog = true"
        style="padding: 12px 15px; background-color: #fff; font-size: 14px; color: #444; border-bottom: 1px solid #f1f1f1; display: flex; justify-content: space-between; align-items: center; cursor: pointer;"
      >
        <span>当前身份：{{ roles[info.identityTag] }}</span>
        <i class="el-icon-arrow-right" style="color: #999;"></i>
      </div>

      <!-- 弹窗选择身份 -->
      <el-dialog title="选择你的身份" :visible.sync="showRoleDialog" width="50%">
        <el-radio-group v-model="info.identityTag" style="display: flex; flex-direction: column;align-items: start; gap: 10px;">
          <el-radio v-for="(r,index) in roles " :label="index" :key="index">{{ r }}</el-radio>
        </el-radio-group>
        <el-input
          v-if="info.identityTag === 1 || info.identityTag === 2"
          v-model="info.verificationInfo"
          placeholder="请输入身份验证信息"
          style="margin-top: 15px;"
        ></el-input>
        <span slot="footer" class="dialog-footer">
          <el-button @click="showRoleDialog = false">取消</el-button>
          <el-button type="primary" @click="updateRole">确认</el-button>
        </span>
      </el-dialog>
      <!-- 个人简介 -->
      <div class="introduce">
        <span v-if="info.introduce">{{ info.introduce }}</span>
        <span v-else>
          添加个人简介，让大家更好的认识你 <i class="el-icon-edit"></i>
        </span>
      </div>

      <!-- 用户身份 -->
      <!-- <div
          class="user-role"
          v-if="true"
          style="padding: 10px 15px; background-color: #fff; font-size: 14px; color: #444; border-bottom: 1px solid #f1f1f1;"
      >
        当前身份：游客
      </div> -->

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
                <div class="blog-title">{{ b.title }}</div>
                <div class="blog-liked">
                  <img src="/imgs/thumbup.png" alt="点赞"> {{ b.liked }}
                </div>
                <div class="blog-delete" @click="deleteBlog(b)">
                  <i class="el-icon-delete"></i>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 评价标签页 -->
          <el-tab-pane label="评价" name="2">
            <div class="empty-tab">暂无评价内容</div>
          </el-tab-pane>

          <!-- 优惠券标签页 -->
          <el-tab-pane label="优惠卷(2)" name="3">
            <div class="coupon-card" @click="toCouponDetail">
              <svg t="1749405128689" class="icon" viewBox="0 0 1081 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2370" width="32" height="32"><path d="M604.612892 557.077773l-53.348196 0 0-41.484234 53.348196 0c8.059039 0 14.611963-6.975693 14.611963-15.510347 0-8.613924-6.579347-15.510347-14.611963-15.510347l-14.347732 0 26.95154-52.212004c3.910616-6.209424 1.558962-14.083501-5.23177-17.67704-6.790731-3.567116-15.431078-1.426846-19.341694 4.756154l-33.715849 65.13289-45.500542 0-33.583733-65.265005c-3.910616-6.209424-12.524539-8.376116-19.341694-4.729731-6.817154 3.593539-9.142385 11.441193-5.23177 17.67704l26.977964 52.212004-14.321309 0c-8.059039 0-14.611963 6.975693-14.611963 15.510347s6.579347 15.510347 14.611963 15.510347l53.427466 0 0 41.484234-53.348196 0c-8.059039 0-14.611963 6.975693-14.611963 15.510347 0 8.613924 6.552924 15.510347 14.611963 15.510347l53.348196 0 0 41.484234c0 9.063116 6.711462 16.461578 14.955463 16.461578 8.296847 0 14.955463-7.398462 14.955463-16.461578l0-41.484234 53.348196 0c8.059039 0 14.611963-6.975693 14.611963-15.510347C619.198431 564.000619 612.671931 557.077773 604.612892 557.077773L604.612892 557.077773zM370.39872 453.367188l10.38427 0 0 155.499819-10.38427 0L370.39872 453.367188zM696.961552 453.367188l10.38427 0 0 155.499819-10.38427 0L696.961552 453.367188zM536.309233 105.798008c-233.421479 0-422.769263 189.294937-422.769263 422.769263s189.294937 422.769263 422.769263 422.769263 422.769263-189.294937 422.769263-422.769263S769.730712 105.798008 536.309233 105.798008L536.309233 105.798008zM785.108944 443.009341l0 39.581772c-22.063271 3.752077-38.841926 22.82954-38.841926 45.976157 0 23.067348 16.857924 42.250503 38.841926 45.976157l0 117.31847c0 8.613924-6.975693 15.510347-15.510347 15.510347L303.019869 707.372245c-8.534654 0-15.510347-6.975693-15.510347-15.510347l0-117.31847c22.063271-3.752077 38.841926-22.82954 38.841926-45.976157s-16.857924-42.250503-38.841926-45.976157l0-117.265624c0-8.534654 6.975693-15.510347 15.510347-15.510347l466.499458 0c8.534654 0 15.510347 6.975693 15.510347 15.510347l0 77.683852L785.108944 443.009341z" fill="#EB5F1A" p-id="2371"></path></svg>
              <div class="coupon-details">
                <div class="coupon-store">鲜花店</div>
                <div class="coupon-amount">￥10 优惠券</div>
                <div class="coupon-date">生效日期：2025-06-01</div>
              </div>
            </div>
          </el-tab-pane>

          <!-- 关注标签页 -->
          <el-tab-pane label="关注(0)" name="4">
            <div class="blog-list" @scroll="onScroll">
              <div class="blog-box" v-for="b in blogs2" :key="b.id">
                <div class="blog-img2" @click="toBlogDetail(b)">
                  <img :src="b.img" alt="关注内容图片">
                </div>
                <div class="blog-title">{{ b.title }}</div>
                <div class="blog-foot">
                  <div class="blog-user-icon">
                    <img :src="b.icon || '/imgs/icons/default-icon.png'" alt="用户头像">
                  </div>
                  <div class="blog-user-name">{{ b.name }}</div>
                  <div class="blog-liked" @click="addLike(b)">
                    <svg t="1646634642977" class="icon" viewBox="0 0 1024 1024" version="1.1"
                         xmlns="http://www.w3.org/2000/svg" p-id="2187" width="14" height="14">
                      <path
                        d="M160 944c0 8.8-7.2 16-16 16h-32c-26.5 0-48-21.5-48-48V528c0-26.5 21.5-48 48-48h32c8.8 0 16 7.2 16 16v448zM96 416c-53 0-96 43-96 96v416c0 53 43 96 96 96h96c17.7 0 32-14.3 32-32V448c0-17.7-14.3-32-32-32H96zM505.6 64c16.2 0 26.4 8.7 31 13.9 4.6 5.2 12.1 16.3 10.3 32.4l-23.5 203.4c-4.9 42.2 8.6 84.6 36.8 116.4 28.3 31.7 68.9 49.9 111.4 49.9h271.2c6.6 0 10.8 3.3 13.2 6.1s5 7.5 4 14l-48 303.4c-6.9 43.6-29.1 83.4-62.7 112C815.8 944.2 773 960 728.9 960h-317c-33.1 0-59.9-26.8-59.9-59.9v-455c0-6.1 1.7-12 5-17.1 69.5-109 106.4-234.2 107-364h41.6z m0-64h-44.9C427.2 0 400 27.2 400 60.7c0 127.1-39.1 251.2-112 355.3v484.1c0 68.4 55.5 123.9 123.9 123.9h317c122.7 0 227.2-89.3 246.3-210.5l47.9-303.4c7.8-49.4-30.4-94.1-80.4-94.1H671.6c-50.9 0-90.5-44.4-84.6-95l23.5-203.4C617.7 55 568.7 0 505.6 0z"
                        p-id="2188" :fill="b.isLike ? '#ff6633' : '#82848a'"></path>
                    </svg>
                    {{ b.liked }}
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

    </div>
  </template>

  <script>
  import service from "../utlis/request";
  import {UserType} from "@/utlis/userType";
  export default {
    name: 'UserProfile',
    components: {
      // FootBar
    },
    data() {
      return {
        user: {}, // 用户信息
        activeName: "1", // 当前激活的标签页
        info: { identityTag: 0, verificationInfo: '' }, // 用户详情信息
        blogs: [], // 用户笔记列表
        blogs2: [], // 关注的人的博客列表
        params: {
          minTime: 0, // 上一次拉取到的时间戳
          offset: 0, // 偏移量
        },
        isReachBottom: false, // 是否滚动到底部
        isLoading: false, // 防止滚动加载时重复请求
        showRoleDialog: false,
        roles: UserType
      };
    },
    watch: {
      showRoleDialog() {
        let ct = sessionStorage.getItem("ct");
        this.info.verificationInfo = ct
      }
    },
    created() {
      this.queryUser();
    },
    methods: {
      async deleteBlog(b) {
        try {
          await this.$confirm('确定要删除该笔记吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          });
          await service.delete(`/blog/${b.id}`);
          this.$message.success('删除成功');
          this.queryBlogs(); // 重新加载列表
        } catch (err) {
          if (err !== 'cancel') {
            this.$message.error('删除失败');
          }
        }
      },
      // 查询用户信息
      async queryUser() {
        try {
          const { data } = await service.get("/user/me");
          this.user = data;

          this.queryUserInfo();
          this.queryBlogs();
        } catch (err) {
          this.$router.push('/login')
          this.$message.error('获取用户信息失败');
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
          const { data } = await service.get("/blog/of/me");
          this.blogs = data;
        } catch (err) {
          this.$message.error('获取笔记列表失败');
        }
      },

      // 查询关注的人的博客
      async queryBlogsOfFollow(clear = false) {
        if (this.isLoading) return;

        this.isLoading = true;
        try {
          if (clear) {
            this.params.offset = 0;
            this.params.minTime = new Date().getTime() + 1;
          }

          const { minTime, offset: os } = this.params;
          const params = {
            offset: os,
            lastId: minTime || new Date().getTime() + 1
          };

          const { data } = await service.get("/blog/of/follow", { params });
          if (!data) return;

          const { list, ...newParams } = data;
          const formattedList = list.map(b => ({
            ...b,
            img: b.images.split(",")[0] // 取第一张图片作为封面
          }));

          this.blogs2 = clear ? formattedList : [...this.blogs2, ...formattedList];
          this.params = newParams;
        } catch (err) {
          console.error('获取关注内容失败', err);
        } finally {
          this.isLoading = false;
        }
      },

      // 标签页切换事件
      handleClick(tab) {
        if (tab.name === '4') {
          this.queryBlogsOfFollow(true);
        }
      },

      // 点赞功能
      async addLike(b) {
        try {
          await service.put(`/blog/like/${b.id}`);
          await this.queryBlogById(b);
        } catch (err) {
          this.$message.error('点赞失败');
          // 失败时前端临时增加点赞数（用户体验优化）
          b.liked++;
          b.isLike = true;
        }
      },

      // 查询单个博客详情（用于更新点赞状态）
      async queryBlogById(b) {
        try {
          const { data } = await service.get(`/blog/${b.id}`);
          b.liked = data.liked;
          b.isLike = data.isLike;
        } catch (err) {
          this.$message.error('更新点赞状态失败');
        }
      },

      // 滚动事件处理（无限滚动）
      onScroll(e) {
        const { scrollTop, offsetHeight, scrollHeight } = e.target;

        // 滚动到顶部时刷新
        if (scrollTop === 0) {
          this.queryBlogsOfFollow(true);
        }
        // 滚动到底部时加载更多
        else if (scrollTop + offsetHeight >= scrollHeight - 50 && !this.isReachBottom) {
          this.isReachBottom = true;
          this.queryBlogsOfFollow();
        } else {
          this.isReachBottom = false;
        }
      },

      // 返回上一页
      goBack() {
        history.back();
      },

      // 跳转到编辑资料页面
      toEdit() {
        this.$router.push('/index/UserProfileEdit')
      },
      toCouponDetail(){
        this.$router.push('/voucher')
      },
      // 退出登录
      async logout() {
        try {
          await service.post("/user/logout");
          sessionStorage.removeItem("token");
          location.href = "/";
        } catch (err) {
          this.$message.error('退出登录失败');
        }
      },

      // 跳转到博客详情页
      toBlogDetail(b) {
        this.$router.push(`/blog-detail?id=${b.id}`);
      },

      // 更新身份
      async updateRole() {
        console.log("in",this.info)
        const form = {
          userId: 1011,
          city: "廣州",
          introduce: "熱愛生活的後端工程師",
          identityTag: this.info.identityTag,
          gender: 1,
          birthday: "1995-07-15"
        }
        try {
          await service.post(`/user/info`, form);
          // this.$message.success('身份更新成功');
          // 保存 info 到本地

        } catch (err) {
          // this.$message.error('身份更新失败');
        }
        sessionStorage.setItem("ct", this.info.verificationInfo);
        this.showRoleDialog = false;
        this.$message.success("修改成功！请重新登录")
        this.$router.push("/login1")

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
  }

  .basic-info {
    flex: 1;
  }

  .name {
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 5px;
  }

  .edit-btn {
    display: inline-block;
    padding: 5px 10px;
    background-color: #f8f8f8;
    border-radius: 15px;
    font-size: 12px;
    color: #666;
    margin-top: 8px;
    cursor: pointer;
  }

  .logout-btn {
    padding: 8px 15px;
    background-color: #ff6633;
    color: white;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
  }

  /* 个人简介 */
  .introduce {
    padding: 15px;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
    color: #666;
    font-size: 14px;
  }

  .introduce span {
    display: flex;
    align-items: center;
  }

  /* 内容区域 */
  .content {
    height: calc(100vh - 250px);
    background-color: #f5f5f5;
  }

  /* 笔记列表样式 */
  .blog-item {
    display: flex;
    padding: 15px;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
  }

  .blog-img {
    width: 100px;
    height: 100px;
    border-radius: 8px;
    overflow: hidden;
    margin-right: 15px;
  }

  .blog-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .blog-info {
    flex: 1;
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
    justify-content: end;
    margin-right: 15px;
    font-size: 14px;
    color: #666;
  }

  .blog-liked img {
    width: 16px;
    height: 16px;
    margin-right: 5px;
  }

  /* 关注内容样式 */
  .blog-list {
    height: 100%;
    overflow-y: auto;
    padding: 10px;
  }

  .blog-box {
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .blog-img2 {
    width: 100%;
    height: 180px;
    overflow: hidden;
  }

  .blog-img2 img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .blog-title {
    padding: 10px;
    font-size: 15px;
    font-weight: bold;
    color: #333;
  }

  .blog-foot {
    display: flex;
    align-items: center;
    padding: 0 10px 10px;
  }

  .blog-user-icon {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 8px;
  }

  .blog-user-icon img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .blog-user-name {
    flex: 1;
    font-size: 12px;
    color: #666;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .blog-liked {
    display: flex;
    align-items: center;
    font-size: 12px;
    color: #999;
    cursor: pointer;
  }

  .blog-liked svg {
    margin-right: 4px;
  }

  /* 空标签页提示 */
  .empty-tab {
    text-align: center;
    padding: 50px 0;
    color: #999;
    font-size: 16px;
  }

  /* Element UI 标签页样式调整 */
  .el-tabs {
    height: 100%;
  }

  .el-tab-pane {
    height: 100%;
  }

  .el-tabs__header {
    height: 10%;
    background-color: #fff;
  }

  .el-tabs__content {
    height: 90%;
    overflow-y: auto;
  }

  .el-tabs--bottom .el-tabs__item.is-bottom:nth-child(2),
  .el-tabs--bottom .el-tabs__item.is-top:nth-child(2),
  .el-tabs--top .el-tabs__item.is-bottom:nth-child(2),
  .el-tabs--top .el-tabs__item.is-top:nth-child(2) {
    padding-left: 15px;
  }

  .coupon-card {
    display: flex;
    flex-direction: row;
    align-items: center;
    background-color: #fff;
    border-radius: 10px;
    margin: 10px 15px;
    padding: 10px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  }

  .coupon-img {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    margin-right: 15px;
    object-fit: cover;
    flex-shrink: 0;
  }

  .coupon-details {
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex: 1;
  }

  .coupon-store {
    font-size: 15px;
    font-weight: bold;
    color: #333;
    margin-bottom: 6px;
  }

  .coupon-amount {
    font-size: 14px;
    color: #ff6633;
    margin-bottom: 4px;
  }

  .coupon-date {
    font-size: 12px;
    color: #999;
  }


  .blog-delete {
    margin-top: 1rem;
    font-size: 14px;
    color: #ff4d4f;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: end;
    margin-right: 30px;
  }
  </style>

