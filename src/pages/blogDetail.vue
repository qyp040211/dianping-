<template>
    <div id="app">
      <!-- 头部导航 -->
      <div class="header">
        <div class="header-back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="header-title">分享详情&nbsp;&nbsp;&nbsp;</div>
      </div>
  
      <!-- 主内容区域 -->
      <div class="content-wrapper">
        <!-- 轮播图 -->
        <div class="swiper-container" ref="swiper"
             @touchstart="moveStart"
             @touchmove="moving"
             @touchend="moveEnd">
          <div class="swiper-wrapper">
            <div class="swiper-item" v-for="(img, i) in blog.images" :key="i">
              <img :src="img" alt="" style="width: 100%" height="100%">
            </div>
          </div>
        </div>
  
        <!-- 用户信息 -->
        <div class="basic">
          <div class="basic-icon" @click="toOtherInfo">
            <img :src="blog.icon || '/imgs/icons/default-icon.png'" alt="">
          </div>
          <div>
            <el-tag
                :key="uscore"
                type="success"
                effect="light"
                round
            >{{getStatus(uscore)}}</el-tag>
            <el-tag
                :key="typeList[tag]"
                :type="tagColor[0]"
                effect=" light"
            >
              {{  typeList[tag]}}
            </el-tag>
          </div>
          <div class="basic-info">
            <div class="name">{{ blog.name }}</div>
            <span class="time">{{ formatTime(blog.createTime) }}</span>
          </div>
          <div style="width: 20%">
            <div class="logout-btn" @click="follow" v-show="!user || user.id !== blog.userId">
              {{ followed ? '取消关注' : '关注' }}
            </div>
          </div>
        </div>
  
        <!-- 博客内容 -->
        <div class="blog-text" v-html="blog.content"></div>
  
        <!-- 店铺信息 -->
        <div class="shop-basic">
          <div class="shop-icon">
            <img :src="shop.image" alt="">
          </div>
          <div style="width: 80%">
            <div class="name">{{ shop.name }}</div>
            <div>
              <el-rate :v-model="shop.score/10"></el-rate>
            </div>
            <div class="shop-avg">￥{{ shop.avgPrice }}/人</div>
          </div>
        </div>
  
        <!-- 点赞区域 -->
        <div class="zan-box">
          <div>
            <svg t="1646634642977" class="icon" viewBox="0 0 1024 1024" version="1.1" 
                 xmlns="http://www.w3.org/2000/svg" p-id="2187" width="20" height="20">
              <path d="M160 944c0 8.8-7.2 16-16 16h-32c-26.5 0-48-21.5-48-48V528c0-26.5 21.5-48 48-48h32c8.8 0 16 7.2 16 16v448zM96 416c-53 0-96 43-96 96v416c0 53 43 96 96 96h96c17.7 0 32-14.3 32-32V448c0-17.7-14.3-32-32-32H96zM505.6 64c16.2 0 26.4 8.7 31 13.9 4.6 5.2 12.1 16.3 10.3 32.4l-23.5 203.4c-4.9 42.2 8.6 84.6 36.8 116.4 28.3 31.7 68.9 49.9 111.4 49.9h271.2c6.6 0 10.8 3.3 13.2 6.1s5 7.5 4 14l-48 303.4c-6.9 43.6-29.1 83.4-62.7 112C815.8 944.2 773 960 728.9 960h-317c-33.1 0-59.9-26.8-59.9-59.9v-455c0-6.1 1.7-12 5-17.1 69.5-109 106.4-234.2 107-364h41.6z m0-64h-44.9C427.2 0 400 27.2 400 60.7c0 127.1-39.1 251.2-112 355.3v484.1c0 68.4 55.5 123.9 123.9 123.9h317c122.7 0 227.2-89.3 246.3-210.5l47.9-303.4c7.8-49.4-30.4-94.1-80.4-94.1H671.6c-50.9 0-90.5-44.4-84.6-95l23.5-203.4C617.7 55 568.7 0 505.6 0z" 
                    p-id="2188" :fill="blog.isLike ? '#ff6633' : '#82848a'"></path>
            </svg>
          </div>
          <div class="zan-list">
            <div class="user-icon-mini" v-for="u in likes" :key="u.id">
              <img :src="u.icon || '/imgs/icons/default-icon.png'" alt="">
            </div>
            <div style="margin-left:10px;text-align: center;line-height: 24px;">
              {{ blog.liked }}人点赞
            </div>
          </div>
        </div>
        
        <div class="blog-divider"></div>
  
        <!-- 评论区域 -->
        <div class="blog-comments">
          <div class="comments-head">
            <div>网友评价 <span>（{{comments.length}}）</span></div>
            <div><i class="el-icon-arrow-right"></i></div>
          </div>
          <div class="comment-list">
            <div class="comment-box" v-for="(comment) in comments" :key="comment.id">
              <div class="comment-icon">
                <img :src="comment.icon || '/imgs/icons/default-icon.png'" alt="">
              </div>
              <div class="comment-info">
                <div class="comment-user">haiahai</div>
                <div style="padding: 5px 0; font-size: 14px">
                  {{ comment.content }}
                </div>
              </div>
            </div>
            <div style="display: flex; justify-content: space-between;padding: 15px 0; border-top: 1px solid #f1f1f1; margin-top: 10px;">
              <div>{{ comments.length }}</div>
              <div><i class="el-icon-arrow-right"></i></div>
            </div>
          </div>
        </div>
        <div class="blog-divider"></div>
      </div>
  
      <!-- 底部操作栏 -->
      <div class="foot" id="bottom">
        <div class="foot-box">
          <div class="foot-view" @click="addLike">
            <svg t="1646634642977" class="icon" viewBox="0 0 1024 1024" version="1.1" 
                 xmlns="http://www.w3.org/2000/svg" p-id="2187" width="26" height="26">
              <path d="M160 944c0 8.8-7.2 16-16 16h-32c-26.5 0-48-21.5-48-48V528c0-26.5 21.5-48 48-48h32c8.8 0 16 7.2 16 16v448zM96 416c-53 0-96 43-96 96v416c0 53 43 96 96 96h96c17.7 0 32-14.3 32-32V448c0-17.7-14.3-32-32-32H96zM505.6 64c16.2 0 26.4 8.7 31 13.9 4.6 5.2 12.1 16.3 10.3 32.4l-23.5 203.4c-4.9 42.2 8.6 84.6 36.8 116.4 28.3 31.7 68.9 49.9 111.4 49.9h271.2c6.6 0 10.8 3.3 13.2 6.1s5 7.5 4 14l-48 303.4c-6.9 43.6-29.1 83.4-62.7 112C815.8 944.2 773 960 728.9 960h-317c-33.1 0-59.9-26.8-59.9-59.9v-455c0-6.1 1.7-12 5-17.1 69.5-109 106.4-234.2 107-364h41.6z m0-64h-44.9C427.2 0 400 27.2 400 60.7c0 127.1-39.1 251.2-112 355.3v484.1c0 68.4 55.5 123.9 123.9 123.9h317c122.7 0 227.2-89.3 246.3-210.5l47.9-303.4c7.8-49.4-30.4-94.1-80.4-94.1H671.6c-50.9 0-90.5-44.4-84.6-95l23.5-203.4C617.7 55 568.7 0 505.6 0z" 
                    p-id="2188" :fill="blog.isLike ? '#ff6633' : '#82848a'"></path>
            </svg>
            <span :class="{liked: blog.isLike}">{{ blog.liked }}</span>
          </div>
        </div>
        <div class="foot-box" @click="addDislike">
          <div class="foot-view">
            <svg t="1749397827899" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3551" width="26" height="26"><path d="M208.601846 51.761486H104.311931c-28.824361 0-52.170557 23.192603-52.170557 51.812172V414.190702c0 28.568372 23.346196 51.760974 52.170557 51.760974h104.289915v-414.19019z m52.119359 0v417.466849a103.931531 103.931531 0 0 1 76.540709 80.022159l85.141939 422.893816 51.044205-12.645856a84.88595 84.88595 0 0 0 64.611621-82.274863v-204.074424a103.931531 103.931531 0 0 1 104.289916-103.573147h277.338474a51.965765 51.965765 0 0 0 51.453788-60.311006l-69.52661-414.19019a52.016963 52.016963 0 0 0-51.453788-43.313338H260.721205z m-25.752493 466.05356H104.311931A103.931531 103.931531 0 0 1 0.022015 414.190702V103.573658A103.931531 103.931531 0 0 1 104.311931 0.000512h745.849528c50.993007 0 94.511136 36.606426 102.907575 86.524279l69.475413 414.241388a103.931531 103.931531 0 0 1-102.856378 120.570816h-277.338474c-28.773163 0-52.119359 23.192603-52.119359 51.812172v204.074424a136.698122 136.698122 0 0 1-104.136322 132.499903l-51.044205 12.697054a52.119359 52.119359 0 0 1-63.792457-40.087876l-85.141939-422.893816a52.068161 52.068161 0 0 0-51.146601-41.62381z" fill="#000000" p-id="3552"></path></svg>  <span :class="{liked: blog.isDislike}">{{ blog.disliked }}</span>
          </div>
        </div>
        <!-- 评论 -->
        <div class="foot-box" @click="showCommentInput = true">
          <div class="foot-view">
            <svg t="1749378056740" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2359" width="32" height="32"><path d="M512 512m-48 0a48 48 0 1 0 96 0 48 48 0 1 0-96 0Z" p-id="2360"></path><path d="M712 512m-48 0a48 48 0 1 0 96 0 48 48 0 1 0-96 0Z" p-id="2361"></path><path d="M312 512m-48 0a48 48 0 1 0 96 0 48 48 0 1 0-96 0Z" p-id="2362"></path><path d="M925.2 338.4c-22.6-53.7-55-101.9-96.3-143.3-41.3-41.3-89.5-73.8-143.3-96.3C630.6 75.7 572.2 64 512 64h-2c-60.6 0.3-119.3 12.3-174.5 35.9-53.3 22.8-101.1 55.2-142 96.5-40.9 41.3-73 89.3-95.2 142.8-23 55.4-34.6 114.3-34.3 174.9 0.3 69.4 16.9 138.3 48 199.9v152c0 25.4 20.6 46 46 46h152.1c61.6 31.1 130.5 47.7 199.9 48h2.1c59.9 0 118-11.6 172.7-34.3 53.5-22.3 101.6-54.3 142.8-95.2 41.3-40.9 73.8-88.7 96.5-142 23.6-55.2 35.6-113.9 35.9-174.5 0.3-60.9-11.5-120-34.8-175.6z m-151.1 438C704 845.8 611 884 512 884h-1.7c-60.3-0.3-120.2-15.3-173.1-43.5l-8.4-4.5H188V695.2l-4.5-8.4C155.3 633.9 140.3 574 140 513.7c-0.4-99.7 37.7-193.3 107.6-263.8 69.8-70.5 163.1-109.5 262.8-109.9h1.7c50 0 98.5 9.7 144.2 28.9 44.6 18.7 84.6 45.6 119 80 34.3 34.3 61.3 74.4 80 119 19.4 46.2 29.1 95.2 28.9 145.8-0.6 99.6-39.7 192.9-110.1 262.7z" p-id="2363"></path></svg>
          </div>
        </div>
      <!-- 评论输入弹窗 -->
      <el-dialog
        title="发表评论"
        :visible.sync="showCommentInput"
        width="80%"
        append-to-body
      >
        <el-input
          type="textarea"
          v-model="newComment"
          rows="4"
          placeholder="写下你的评论..."
        ></el-input>
        <span slot="footer" class="dialog-footer">
          <el-button @click="cancelComment">取消</el-button>
          <el-button type="primary" @click="submitComment">发布</el-button>
        </span>
      </el-dialog>
      </div>
    </div>
  </template>

  <script>
  import service from "../utlis/request";
  import {UserType} from "@/utlis/userType";
  export default {
    name: 'BlogDetail',
    props: {  // 使用对象语法
      id: {
        type: [String, Number], // 参数类型
        required: true
      }
    },
    data() {
      return {
        tagColor:['info','primary', 'success'],
        typeList:UserType,
        uscore:"",
        tag :"",
        blogerId:'',
        comments:[],
        rateValue:4.5,
        blog: {
          images: [],
          content: '',
          liked: 0,
          isLike: false,
          disliked: 0,
          isDislike: false,
          createTime: new Date(),
          userId: null,
          name: String
        },
        getStatus(n) {
          if (n < 50) return '评论小白';
          if (n < 110) return '野生评论员';
          return '专业评论员';
        },
      async addDislike() {
        try {
          await service.put(`/blog/dislike/${this.blog.id}`);
          const { data } = await service.get(`/blog/${this.blog.id}`);
          data.images = data.images.split(',');
          this.blog = data;
        } catch (error) {
          this.$message.error(error.message || '踩操作失败');
        }
      },
        UserType : ["游客", "实名用户","本校学生"],
        shop: {
          image: '',
          name: '',
          score: 0,
          avgPrice: 0
        },
        isLiked: false,
        likes: [],
        user: null,
        followed: false,
        // 评论输入相关
        showCommentInput: false,
        newComment: '',
        // 轮播图相关数据
        width: 0,
        duration: 300,
        active: 0,
        start: { x: 0, y: 0 },
        move: { x: 0, y: 0 },
        sensitivity: 60,
        resistance: 0.3,
        isMoving: false
      };
    },
    created() {
      const blogId = this.$route.params.id;
      this.queryBlogById(blogId);
      this.queryCommentsByBlogId(blogId);
      this.checkLogin()
    },
    mounted() {
      this.initSwiper();
    },
    methods: {
      // 初始化轮播图
      initSwiper() {
        this.container = this.$refs.swiper;
        this.items = this.container.querySelectorAll('.swiper-item');
        this.updateItemWidth();
        this.setTransform();
        this.setTransition('none');
      },

      // 轮播图相关方法
      updateItemWidth() {
        this._width = this.container.offsetWidth || document.documentElement.offsetWidth;
      },
      setTransform(offset = 0) {
        Array.from(this.items).forEach((item, i) => {
          const distance = (i - this.active) * this._width + offset;
          item.style.transform = `translate3d(${distance}px, 0, 0)`;
        });
      },
      setTransition(duration = this.duration) {
        const transition = typeof duration === 'number' ? `${duration}ms` : duration;
        Array.from(this.items).forEach(item => {
          item.style.transition = transition;
        });
      },
      moveStart(e) {
        this.start.x = e.changedTouches[0].pageX;
        this.start.y = e.changedTouches[0].pageY;
        this.setTransition('none');
      },
      moving(e) {
        e.preventDefault();
        const distanceX = e.changedTouches[0].pageX - this.start.x;
        const distanceY = e.changedTouches[0].pageY - this.start.y;

        if (Math.abs(distanceX) > Math.abs(distanceY)) {
          this.isMoving = true;
          this.move.x = this.start.x + distanceX;
          this.move.y = this.start.y + distanceY;

          let adjustedDistance = distanceX;
          if ((this.active === 0 && distanceX > 0) ||
              (this.active === this.items.length - 1 && distanceX < 0)) {
            adjustedDistance = distanceX * this.resistance;
          }

          this.setTransform(adjustedDistance);
        }
      },
      moveEnd() {
        if (this.isMoving) {
          const distance = this.move.x - this.start.x;
          if (Math.abs(distance) > this.sensitivity) {
            distance < 0 ? this.next() : this.prev();
          } else {
            this.back();
          }
          this.reset();
          this.isMoving = false;
        }
      },
      next() {
        this.go(this.active + 1);
      },
      prev() {
        this.go(this.active - 1);
      },
      reset() {
        this.start = { x: 0, y: 0 };
        this.move = { x: 0, y: 0 };
      },
      back() {
        this.setTransition();
        this.setTransform();
      },
      go(index) {
        this.active = Math.max(0, Math.min(index, this.items.length - 1));
        this.setTransition();
        this.setTransform();
      },

      // 应用功能方法
      goBack() {
        history.back();
      },
      toOtherInfo() {
        const url = this.user?.id === this.blog.userId
          ? '/index/info'
          : `/index/user-profile?id=${this.blog.userId}`;
        location.href = url;
      },
      async queryBlogById(id) {
        try {
          const { data } = await service.get(`/blog/${id}`);
          data.images = data.images.split(',');
          this.blog = data;
          console.log("data",data.userId);
          this.blogerId = data.userId
          this.queryBlogerInfo(this.blogerId)
          this.queryShopById(data.shopId);
          this.queryLikeList(id);
          this.queryLoginUser();
        } catch (error) {
          this.$message.error('加载博客失败');
        }
      },
      async queryBlogerInfo(id){

        const { data } = await service.get(`/user/info/${id}`);
        console.log(data);
        this.uscore = data.credits
        this.tag = data.identityTag
      },
      async queryCommentsByBlogId(blogId) {
        try {
          const { data } = await service.get(`/blog-comments/of/blog/${blogId}`);
          this.comments = data;  // 假设返回的是评论数组
        } catch (error) {
          this.$message.error('加载评论失败');
        }
      },
      async queryShopById(shopId) {
        try {
          const { data } = await service.get(`/shop/${shopId}`);
          data.image = data.images?.split(',')[0] || '';
          this.shop = data;
        } catch (error) {
          this.$message.error('加载店铺信息失败');
        }
      },
      async queryLikeList(id) {
          const { data } = await service.get(`/blog/likes/${id}`);
          this.likes = data;
          // 从本地存储中获取当前用户的 id
          const localId = sessionStorage.getItem("id");
          // 判断当前用户是否在点赞列表中
          this.blog.isLike = data?.some(user => String(user.id) === String(localId));


      },
      async checkLogin() {

        try {
          const { data } = await service.get("/user/me");
          if (data) {
            this.isLogin = true;
            sessionStorage.setItem("id", data.id);
          }
        } catch (err) {
          this.$message.error('请先登录');
          setTimeout(() => this.$router.push('/login2'), 1000);
        }
      },
      async addLike() {
        try {
          await service.put(`/blog/like/${this.blog.id}`);
          const { data } = await service.get(`/blog/${this.blog.id}`);
          data.images = data.images.split(',');
          this.blog = data;

          this.queryLikeList(this.blog.id);
        } catch (error) {
          this.$message.error(error.message || '点赞操作失败');
        }
      },
      async isFollowed() {
        try {
          const { data } = await service.get(`/follow/or/not/${this.blog.userId}`);
          this.followed = data;
        } catch (error) {
          this.$message.error('获取关注状态失败');
        }
      },
      async follow() {
        try {
          await service.put(`/follow/${this.blog.userId}/${!this.followed}`);
          this.$message.success(this.followed ? '已取消关注' : '已关注');
          this.followed = !this.followed;
        } catch (error) {
          this.$message.error('关注操作失败');
        }
      },
      formatTime(dateStr) {
        const date = new Date(dateStr);
        return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
      },
      async queryLoginUser() {
        try {
          const { data } = await service.get('/user/me');
          this.user = data;
          if (this.user.id !== this.blog.userId) {
            this.isFollowed();
          }
        } catch (error) {
          console.log('获取用户信息失败', error);
        }
      },
      async submitComment() {
        if (!this.newComment.trim()) {
          this.$message.warning('评论内容不能为空');
          return;
        }
        try {
          await service.post(`/blog-comments`, {
            blogId: this.blog.id,
            content: this.newComment
          });
          this.$message.success('评论成功');
          this.newComment = '';
          this.showCommentInput = false;
          await this.queryCommentsByBlogId(this.blog.id); // 等待评论刷新
          this.$nextTick(() => {
            location.hash = '#bottom';
          });
        } catch (error) {
          this.$message.error('评论失败');
        }
      },
      cancelComment() {
        this.showCommentInput = false;
        this.newComment = '';
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

  .foot-view span {
    font-size: 12px;
  }
  .liked {
    color: #ff6633;
  }
  .swiper-container {
    position: relative;
    overflow: hidden;
  }
  .swiper-wrapper {
    display: flex;
    transition: transform 0.3s ease;
  }
  .swiper-item {
    flex-shrink: 0;
    width: 100%;
  }

  /* 以下是原有CSS样式 */
  .content-wrapper {
    height: 85%;
    overflow-y: scroll;
    overflow-x: hidden;
  }
  .basic {
    display: flex;
    padding: 15px;
    align-items: center;
    border-bottom: 1px solid #f1f1f1;
  }
  .basic-icon img {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-right: 15px;
  }
  .basic-info {
    flex-grow: 1;
  }
  .blog-text {
    padding: 15px;
    line-height: 1.6;
  }
  .shop-basic {
    display: flex;
    padding: 15px;
    border-top: 1px solid #f1f1f1;
    border-bottom: 1px solid #f1f1f1;
  }
  .shop-icon img {
    width: 60px;
    height: 60px;
    border-radius: 5px;
    margin-right: 15px;
  }
  .zan-box {
    display: flex;
    padding: 15px;
    align-items: center;
  }
  .zan-list {
    display: flex;
    align-items: center;
    margin-left: 15px;
  }
  .user-icon-mini img {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    margin-left: -10px;
    border: 2px solid #fff;
  }
  .blog-divider {
    height: 10px;
    background-color: #f5f5f5;
  }
  .comments-head {
    display: flex;
    justify-content: space-between;
    padding: 15px;
    border-bottom: 1px solid #f1f1f1;
  }
  .comment-box {
    display: flex;
    padding: 15px;
  }
  .comment-icon img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 15px;
  }
  .comment-images img {
    width: 80px;
    height: 80px;
    margin-right: 10px;
    border-radius: 5px;
    object-fit: cover;
  }
  .foot {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    background: #fff;
    padding: 10px;
    box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
  }
  .foot-box {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .foot-view {
    display: flex;
    flex-direction: column;
    align-items: center;
    cursor: pointer;
  }
  .comment-input-box {
    padding: 15px;
    border-top: 1px solid #f1f1f1;
    background-color: #fff;
  }
  </style>