<template>
    <div id="app">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <div class="city-btn">桂林 <i class="el-icon-arrow-down"></i></div>
        <div class="search-input">
          <el-input size="mini" placeholder="请输入商户名、地点">
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
        </div>
        <div class="header-icon" @click="toPage(4)">
          <i class="el-icon-user"></i>
        </div>
      </div>
  
      <!-- 分类列表 -->
      <div class="type-list">
        <div 
          class="type-box" 
          v-for="t in types" 
          :key="t.id" 
          @click="toShopList(t.id, t.name)"
        >
          <div class="type-view">
            <img :src="'/img/' + t.icon" :alt="t.name">
          </div>
          <div class="type-text">{{ t.name }}</div>
        </div>
      </div>
  
      <!-- 博客列表 -->
      <div class="blog-list" @scroll="onScroll">
        <div class="blog-box" v-for="b in blogs" :key="b.id">
          <div class="blog-img" @click="toBlogDetail(b)">
            <img :src="b.img" alt="博客图片">
          </div>
          <div class="blog-title">{{ b.title }}</div>
          <div class="blog-foot">
            <div class="blog-user-icon">
              <img :src="b.icon || '/img/icons/default-icon.png'" alt="用户头像">
            </div>
            <div class="blog-user-name">{{ b.name }}</div>
            <div class="blog-liked" @click="addLike(b)">
              <svg t="1646634642977" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2187" width="14" height="14">
                <path 
                  d="M160 944c0 8.8-7.2 16-16 16h-32c-26.5 0-48-21.5-48-48V528c0-26.5 21.5-48 48-48h32c8.8 0 16 7.2 16 16v448zM96 416c-53 0-96 43-96 96v416c0 53 43 96 96 96h96c17.7 0 32-14.3 32-32V448c0-17.7-14.3-32-32-32H96zM505.6 64c16.2 0 26.4 8.7 31 13.9 4.6 5.2 12.1 16.3 10.3 32.4l-23.5 203.4c-4.9 42.2 8.6 84.6 36.8 116.4 28.3 31.7 68.9 49.9 111.4 49.9h271.2c6.6 0 10.8 3.3 13.2 6.1s5 7.5 4 14l-48 303.4c-6.9 43.6-29.1 83.4-62.7 112C815.8 944.2 773 960 728.9 960h-317c-33.1 0-59.9-26.8-59.9-59.9v-455c0-6.1 1.7-12 5-17.1 69.5-109 106.4-234.2 107-364h41.6z m0-64h-44.9C427.2 0 400 27.2 400 60.7c0 127.1-39.1 251.2-112 355.3v484.1c0 68.4 55.5 123.9 123.9 123.9h317c122.7 0 227.2-89.3 246.3-210.5l47.9-303.4c7.8-49.4-30.4-94.1-80.4-94.1H671.6c-50.9 0-90.5-44.4-84.6-95l23.5-203.4C617.7 55 568.7 0 505.6 0z" 
                  p-id="2188" 
                  :fill="b.isLike ? '#ff6633' : '#82848a'"
                ></path>
              </svg>
              {{ b.liked }}
            </div>
          </div>
        </div>
      </div>

    </div>
  </template>
  
  <script>
  import service from "../utlis/request";
  export default {
    name: 'HomePage',
    components: {
      // FootBar
    },
    data() {
      return {
        isReachBottom: false,
        types: [], // 类型列表
        blogs: [], // 博客列表
        current: 1, // 当前页码
        isLoading: false // 防止滚动加载时重复请求
      };
    },
    created() {
      this.queryTypes();
      this.queryHotBlogsScroll();
    },
    methods: {
      // 查询分类列表
      async queryTypes() {
        try {
          const { data } = await service.get("/shop-type/list");
          this.types = data;
        } catch (err) {
          this.$message.error(err.message || '获取分类列表失败');
        }
      },
      
      // 查询热门博客（带滚动加载）
      async queryHotBlogsScroll() {
        if (this.isLoading) return;
        
        this.isLoading = true;
        try {
          // http://localhost:8083/blog/hot?current=1
          const { data } = await service.get("/blog/all");
          // const { data } = await service.get("/blog/hot?current=" + this.current);
          console.log(data)
          const formattedData = data.map(b => ({
            ...b,
            img: b.images.split(",")[0] // 取第一张图片作为封面
          }));
          
          this.blogs = [...this.blogs, ...formattedData];
          this.current++;
        } catch (err) {
          this.$message.error(err.message || '加载博客失败');
        } finally {
          this.isLoading = false;
        }
      },
      
      // 点赞功能
      async addLike(b) {
        try {
          await service.put("/blog/like/" + b.id);
          await this.queryBlogById(b);
        } catch (err) {
          this.$message.error(err.message || '点赞失败');
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
          this.$message.error(err.message || '更新点赞状态失败');
        }
      },
      
      // 滚动事件处理（无限滚动）
      onScroll(e) {
        const { scrollTop, offsetHeight, scrollHeight } = e.target;
        
        // 滚动到底部时加载更多
        if (scrollTop + offsetHeight >= scrollHeight - 50 && !this.isReachBottom) {
          this.isReachBottom = true;
          this.queryHotBlogsScroll();
        } else {
          this.isReachBottom = false;
        }
      },
      
      // 跳转到店铺列表页
      toShopList(id, name) {
        this.$router.push({
          path: '/shop-list',
          query: {
            type: id,
            name: name // 会自动编码
          }
        })
      },

      
      // 跳转到博客详情页
      toBlogDetail(b) {
        this.$router.push(`/blog-detail/${b.id}`);
      },
      
      // 跳转到其他页面
      toPage(pageId) {
        // 根据pageId跳转到不同页面
        // 示例：用户页面
        if (pageId === 4) {
          location.href = "/user.html";
        }
      }
    }
  };
  </script>
  
  <style scoped>
  /* 搜索栏样式 */
  .search-bar {
    display: flex;
    align-items: center;
    padding: 10px 15px;
    background-color: #fff;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    position: sticky;
    top: 0;
    z-index: 100;
  }
  
  .city-btn {
    font-size: 14px;
    padding: 5px 10px;
    color: #333;
    cursor: pointer;
  }
  
  .search-input {
    flex: 1;
    margin: 0 15px;
  }
  
  .header-icon {
    font-size: 20px;
    color: #ff6633;
    cursor: pointer;
  }
  
  /* 分类列表样式 */
  .type-list {
    display: flex;
    flex-wrap: wrap;
    padding: 15px 0;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
  }
  
  .type-box {
    width: 20%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 15px;
    cursor: pointer;
  }
  
  .type-view {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background-color: #f8f8f8;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .type-view img {
    width: 30px;
    height: 30px;
  }
  
  .type-text {
    font-size: 12px;
    margin-top: 5px;
    color: #666;
  }
  
  /* 博客列表样式 */
  .blog-list {
    height: fit-content;
    padding: 10px;
    background-color: #f5f5f5;
    display: flex;
    flex-wrap: wrap; /* 允许子元素换行 */
    justify-content: space-between; /* 子元素两端对齐 */
    align-items: flex-start; /* 顶部对齐 */
  }
  .blog-box {
    width: 48%; /* 保持48%宽度，留出4%的间隙 */
    /* 移除 flex: 1，因为它会使元素扩展填满空间 */
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }
  
  .blog-img {
    width: 100%;
    height: 180px;
    overflow: hidden;
  }
  
  .blog-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
    cursor: pointer;
  }
  
  .blog-img:hover img {
    transform: scale(1.05);
  }
  
  .blog-title {
    line-height: 1.5rem;
    height: 1.5rem;
    padding: 10px;
    font-size: 15px;
    font-weight: bold;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
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
  </style>