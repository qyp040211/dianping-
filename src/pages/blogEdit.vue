<template>
    <div id="app">
      <!-- 头部导航 -->
      <div class="header">
        <div class="header-cancel-btn" @click="goBack">取消</div>
        <div class="header-title">&nbsp;&nbsp;发笔记<i class="el-icon-info"></i></div>
        <div class="header-commit">
          <div class="header-commit-btn" @click="submitBlog">发布</div>
        </div>
      </div>
      
      <!-- 图片上传区域 -->
      <div class="upload-box">
        <input type="file" @change="fileSelected" name="file" @submit.prevent ref="fileInput" style="display: none">
        <div class="upload-btn" @click="openFileDialog">
          <i class="el-icon-camera"></i>
          <div style="font-size: 12px;line-height: 12px">上传照片</div>
        </div>
        <div class="pic-list">
          <div class="pic-box" v-for="(f,i) in fileList" :key="i">
            <img :src="f" alt="">
            <i class="el-icon-close" @click="deletePic(i)"></i>
          </div>
        </div>
      </div>
      
      <!-- 标题和内容输入 -->
      <div class="blog-title">
        <input v-model="params.title" type="text" placeholder="填写标题更容易上首页哦~">
      </div>
      <div class="blog-content">
        <textarea v-model="params.content" placeholder="最近打卡了什么地方，有什么新奇体验呢？"></textarea>
      </div>
      
      <div class="divider"></div>
      
      <!-- 关联商户 -->
      <div class="blog-shop" @click="showDialog=true">
        <div class="shop-left">关联商户</div>
        <div v-if="selectedShop.name">{{selectedShop.name}}</div>
        <div v-else>去选择&nbsp;<i class="el-icon-arrow-right"></i></div>
      </div>
      
      <!-- 遮罩层 -->
      <div class="mask" v-show="showDialog" @click="showDialog=false"></div>
      
      <!-- 商户选择弹窗 -->
      <transition name="el-zoom-in-bottom">
        <div class="shop-dialog" v-show="showDialog">
          <div class="blog-shop">
            <div class="shop-left">关联商户</div>
          </div>
          <div class="search-bar">
            <div class="city-select">杭州 <i class="el-icon-arrow-down"></i></div>
            <div class="search-input">
              <i class="el-icon-search" @click="queryShops"></i>
              <input v-model="shopName" type="text" placeholder="搜索商户名称" @keyup.enter="queryShops">
            </div>
          </div>
          <div class="shop-list">
            <div v-for="s in shops" class="shop-item" @click="selectShop(s)" :key="s.id">
              <div class="shop-name">{{s.name}}</div>
              <div>{{s.area}}</div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </template>
  
  <script>

  import service from "../utlis/request";
  export default {
    name: 'BlogEditor',
    data() {
      return {
        fileList: [],       // 图片文件列表
        params: {           // 笔记参数
          title: '',
          content: ''
        },
        showDialog: false,  // 商户弹窗显示状态
        shops: [],          // 商户列表
        shopName: "",       // 搜索商户名称
        selectedShop: {},   // 选中的商户
      };
    },
    created() {
      this.queryShops();
    },
    methods: {
      // 查询商户列表
      async queryShops() {
        try {
          const { data } = await service.get(`/shop/of/name?name=${this.shopName}`);
          this.shops = data;
        } catch (error) {
          this.$message.error('获取商户列表失败');
        }
      },
      
      // 选择商户
      selectShop(s) {
        this.selectedShop = s;
        this.showDialog = false;
      },
      
      // 提交笔记
      async submitBlog() {
        if (!this.validateForm()) return;
        
        try {
          const data = {
            ...this.params,
            images: this.fileList.join(","),
            shopId: this.selectedShop.id
          };
          
          await service.post("/blog", data);
          this.$router.push('/index')
          this.$message.success("发布成功")
        } catch (error) {
          this.$message.error('发布笔记失败');
        }
      },
      
      // 表单验证
      validateForm() {
        if (!this.params.title.trim()) {
          this.$message.warning('请填写标题');
          return false;
        }
        
        if (!this.params.content.trim()) {
          this.$message.warning('请填写内容');
          return false;
        }
        
        if (!this.selectedShop.id) {
          this.$message.warning('请关联商户');
          return false;
        }
        
        return true;
      },
      
      // 打开文件选择对话框
      openFileDialog() {
        this.$refs.fileInput.click();
      },
      
      // 文件选择处理
      async fileSelected() {
        const file = this.$refs.fileInput.files[0];
        if (!file) return;
        try {
          const formData = new FormData();
          formData.append("file", file);
          // const { data } = await service.post("/upload/blog", formData, {
          //   headers: { "Content-Type": "multipart/form-data" }
          // });
          //"/imgs/blogs/blogs/0/1/c913055b-1da2-4765-90d7-a5579e1c5102.jpg"
          this.fileList.push("/imgs/blogs/5/7/5a78953d-621a-40e1-8525-f3532f4f810e.jpg");
          this.$message.success("图片上传成功");
        } catch (error) {
          this.$message.error("上传失败");
        }
      },

      // 通过弹窗选择本地图片并上传
      async uploadFromLocal() {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.onchange = async (e) => {
          const file = e.target.files[0];
          if (!file) return;

          try {
            const formData = new FormData();
            formData.append("file", file);

            const { data } = await service.post("/upload/blog", formData, {
              headers: { "Content-Type": "multipart/form-data" }
            });
            this.fileList.push(data);
            this.$message.success("图片上传成功");
          } catch (error) {
            this.$message.error("上传失败");
          }
        };
        input.click();
      },
      
      // 删除图片
      async deletePic(index) {
        try {
          await service.get(`/upload/blog/delete?name=${this.fileList[index]}`);
          this.fileList.splice(index, 1);
        } catch (error) {
          this.$message.error('删除图片失败');
        }
      },
      
      // 检查登录状态
      async checkLogin() {
        const token = sessionStorage.getItem("token");
        if (!token) {
          return this.$router.push("/login");

        }
        
        try {
          await service.get("/user/me");
        } catch (error) {
          this.$message.error('请先登录');
          // setTimeout(() => location.href = "login.html", 200);
        }
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
    justify-content: space-between;
    align-items: center;
    padding: 15px;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
    position: sticky;
    top: 0;
    z-index: 100;
  }
  
  .header-cancel-btn, .header-commit-btn {
    color: #ff6633;
    font-size: 16px;
    cursor: pointer;
  }
  
  .header-title {
    font-size: 18px;
    font-weight: bold;
    flex-grow: 1;
    text-align: center;
  }
  
  /* 上传区域 */
  .upload-box {
    padding: 15px;
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .upload-btn {
    width: 80px;
    height: 80px;
    border: 1px dashed #dcdfe6;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: #8c939d;
    cursor: pointer;
    background-color: #f8f8f8;
  }
  
  .pic-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .pic-box {
    position: relative;
    width: 80px;
    height: 80px;
  }
  
  .pic-box img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px;
  }
  
  .pic-box .el-icon-close {
    position: absolute;
    top: -8px;
    right: -8px;
    background: #fff;
    border-radius: 50%;
    cursor: pointer;
    color: #ff6633;
    font-size: 16px;
  }
  
  /* 输入区域 */
  .blog-title, .blog-content {
    padding: 0 15px;
  }
  
  .blog-title input {
    width: 100%;
    border: none;
    font-size: 18px;
    font-weight: bold;
    padding: 10px 0;
    outline: none;
  }
  
  .blog-content textarea {
    width: 100%;
    height: 200px;
    border: none;
    resize: none;
    font-size: 16px;
    line-height: 1.5;
    outline: none;
  }
  
  .divider {
    height: 10px;
    background-color: #f5f5f5;
  }
  
  /* 商户选择 */
  .blog-shop {
    display: flex;
    justify-content: space-between;
    padding: 15px;
    border-bottom: 1px solid #f1f1f1;
    cursor: pointer;
  }
  
  .shop-left {
    font-weight: bold;
  }
  
  /* 遮罩层 */
  .mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 999;
  }
  
  /* 商户弹窗 */
  .shop-dialog {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 70vh;
    background: #fff;
    border-radius: 16px 16px 0 0;
    z-index: 1000;
    padding: 15px;
    box-sizing: border-box;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
  
  .search-bar {
    display: flex;
    margin: 15px 0;
    gap: 10px;
  }
  
  .city-select {
    padding: 8px 12px;
    background: #f5f5f5;
    border-radius: 20px;
    display: flex;
    align-items: center;
  }
  
  .search-input {
    flex: 1;
    position: relative;
  }
  
  .search-input input {
    width: 100%;
    padding: 8px 12px 8px 35px;
    border: 1px solid #dcdfe6;
    border-radius: 20px;
    outline: none;
  }
  
  .search-input .el-icon-search {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #8c939d;
    cursor: pointer;
  }
  
  .shop-list {
    flex: 1;
    overflow-y: auto;
  }
  
  .shop-item {
    padding: 12px 0;
    border-bottom: 1px solid #f1f1f1;
    cursor: pointer;
  }
  
  .shop-name {
    font-weight: bold;
    margin-bottom: 5px;
  }
  </style>