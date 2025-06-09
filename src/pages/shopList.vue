<template>
    <div class="shop-list-container">
      <!-- 头部导航 -->
      <div class="header">
        <div class="header-back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="header-title">{{ typeName }}</div>
        <div class="header-search">
          <i class="el-icon-search"></i>
        </div>
      </div>
      
      <!-- 排序栏 -->
      <div class="sort-bar">
        <div class="sort-item">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="el-dropdown-link">
              {{ typeName }}<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item 
                v-for="t in types" 
                :key="t.id" 
                :command="t"
              >
                {{ t.name }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div 
          class="sort-item" 
          :class="{ active: params.sortBy === '' }"
          @click="sortAndQuery('')"
        >
          距离 <i class="el-icon-arrow-down el-icon--right"></i>
        </div>
        <div 
          class="sort-item" 
          :class="{ active: params.sortBy === 'comments' }"
          @click="sortAndQuery('comments')"
        >
          人气 <i class="el-icon-arrow-down el-icon--right"></i>
        </div>
        <div 
          class="sort-item" 
          :class="{ active: params.sortBy === 'score' }"
          @click="sortAndQuery('score')"
        >
          评分 <i class="el-icon-arrow-down el-icon--right"></i>
        </div>
      </div>
      
      <!-- 店铺列表 -->
      <div class="shop-list" @scroll="onScroll">
        <div 
          class="shop-box" 
          v-for="s in shops" 
          :key="s.id" 
          @click="toDetail(s.id)"
        >
          <div class="shop-img">
            <img :src="s.images" alt="店铺图片">
          </div>
          <div class="shop-info">
            <div class="shop-title shop-item">{{ s.name }}</div>
            <div class="shop-rate shop-item">
              <el-rate
                  disabled
                  :value="s.score / 20"
                  :max="5"
                  text-color="#F63"
                  show-score
                  clearable
              />
              <span>{{ s.comments }}条</span>
            </div>
            <div class="shop-area shop-item">
              <span>{{ s.area }}</span>
              <span v-if="s.distance">
                {{ formatDistance(s.distance) }}
              </span>
            </div>
            <div class="shop-avg shop-item">￥{{ s.avgPrice }} / 人</div>
            <div class="shop-address shop-item">
              <i class="el-icon-map-location"></i>
              <span>{{ s.address }}</span>
            </div>
          </div>
        </div>
        
        <!-- 加载提示 -->
        <div v-if="isLoading" class="loading-indicator">
          <i class="el-icon-loading"></i> 加载中...
        </div>
        <div v-if="noMoreData" class="no-more-data">
          没有更多店铺了
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import service from "../utlis/request";
  import {util} from "@/utlis/common";
  import { 
    Dropdown, 
    DropdownMenu, 
    DropdownItem, 
    Rate 
  } from 'element-ui';
  
  export default {
    name: 'ShopList',
    components: {
      'el-dropdown': Dropdown,
      'el-dropdown-menu': DropdownMenu,
      'el-dropdown-item': DropdownItem,
      'el-rate': Rate
    },
    data() {
      return {
        isLoading: false,         // 加载状态
        noMoreData: false,        // 是否还有更多数据
        types: [],                // 店铺类型列表
        shops: [],                // 店铺列表
        typeName: "",             // 当前类型名称
        params: {
          typeId: 0,              // 当前类型ID
          current: 1,             // 当前页码
          sortBy: "",             // 排序字段
          x: 120.149993,          // 经度
          y: 30.334229            // 纬度
        }
      };
    },
    created() {
      // 初始化数据
      this.params.typeId = util.getUrlParam("type");
      this.typeName = util.getUrlParam("name");
      if (!this.params.typeId) {
        console.log("null")
      }
      // 查询店铺类型和列表
      this.queryTypes();
      this.queryShops();
    },
    methods: {


      // 查询店铺类型
      async queryTypes() {
        try {
          const { data } = await service.get("/shop-type/list");
          this.types = data;
        } catch (err) {
          console.error('获取店铺类型失败', err);
          this.$message.error('加载店铺类型失败');
        }
      },
      
      // 查询店铺列表
      async queryShops() {
        this.isLoading = true;
        try {
          const url = !this.params.typeId ? "/shop/all" : "/shop/of/type";
          const { data } = await service.get(url, {
            params: this.params
          });

          if (!data || data.length === 0) {
            this.noMoreData = true;
            return;
          }
          
          // 处理图片数据
          const processedData = data.map(s => ({
            ...s,
            images: s.images.split(',')[0]
          }));
          
          // 合并数据
          this.shops = [...this.shops, ...processedData];
          this.isLoading = false;
        } catch (err) {
          console.error('获取店铺列表失败', err);
          this.$message.error('加载店铺列表失败');
          this.isLoading = false;
        }
      },
      
      // 处理类型选择
      handleCommand(t) {
        // 重置数据
        this.shops = [];
        this.params.current = 1;
        this.noMoreData = false;
        
        // 更新类型
        this.params.typeId = t.id;
        this.typeName = t.name;
        
        // 重新查询
        this.queryShops();
      },
      
      // 排序并查询
      sortAndQuery(sortBy) {
        // 重置数据
        this.shops = [];
        this.params.current = 1;
        this.params.sortBy = sortBy;
        this.noMoreData = false;
        
        // 重新查询
        this.queryShops();
      },
      
      // 返回上一页
      goBack() {
        history.back();
      },
      
      // 跳转到详情页
      toDetail(id) {
        this.$router.push({
          path: `/shop-detail/${id}`,
        })
      },
      
      // 滚动事件处理
      onScroll(e) {
        const scrollTop = e.target.scrollTop;
        const offsetHeight = e.target.offsetHeight;
        const scrollHeight = e.target.scrollHeight;
        
        // 触底加载更多
        if (scrollTop + offsetHeight + 1 > scrollHeight && !this.isLoading && !this.noMoreData) {
          this.params.current++;
          this.queryShops();
        }
      },
      
      // 格式化距离显示
      formatDistance(distance) {
        if (distance < 1000) {
          return `${distance.toFixed(1)}m`;
        } else {
          return `${(distance / 1000).toFixed(1)}km`;
        }
      }
    }
  };
  </script>
  
  <style scoped>
  /* 整体容器 */
  .shop-list-container {
    max-width: 500px;
    margin: 0 auto;
    background-color: #fff;
    height: 100vh;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
  
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
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  }
  
  .header-back-btn {
    font-size: 20px;
    color: #333;
    cursor: pointer;
    margin-right: 10px;
  }
  
  .header-title {
    flex: 1;
    font-size: 18px;
    font-weight: bold;
    text-align: center;
  }
  
  .header-search {
    font-size: 20px;
    color: #666;
    cursor: pointer;
  }
  
  /* 排序栏 */
  .sort-bar {
    display: flex;
    padding: 10px 0;
    background-color: #fff;
    border-bottom: 1px solid #f1f1f1;
    position: sticky;
    top: 60px;
    z-index: 90;
  }
  
  .sort-item {
    flex: 1;
    text-align: center;
    font-size: 14px;
    color: #666;
    padding: 8px 0;
    cursor: pointer;
    transition: all 0.3s;
  }
  
  .sort-item.active {
    color: #ff6633;
    font-weight: bold;
    border-bottom: 2px solid #ff6633;
  }
  
  .sort-item:hover {
    color: #ff6633;
  }
  
  /* 店铺列表 */
  .shop-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    background-color: #f5f5f5;
  }
  
  .shop-box {
    display: flex;
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    transition: transform 0.3s, box-shadow 0.3s;
    cursor: pointer;
  }
  
  .shop-box:hover {
    transform: translateY(-3px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
  
  .shop-img {
    width: 120px;
    height: 120px;
    flex-shrink: 0;
  }
  
  .shop-img img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .shop-info {
    flex: 1;
    padding: 10px 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  
  .shop-item {
    margin-bottom: 8px;
  }
  
  .shop-title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .shop-rate {
    display: flex;
    align-items: center;
  }
  
  .shop-rate span {
    margin-left: 10px;
    font-size: 12px;
    color: #999;
  }
  
  .shop-area {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    color: #666;
  }
  
  .shop-avg {
    font-size: 14px;
    color: #ff6633;
    font-weight: bold;
  }
  
  .shop-address {
    display: flex;
    align-items: center;
    font-size: 12px;
    color: #999;
  }
  
  .shop-address i {
    margin-right: 5px;
    font-size: 14px;
  }
  
  /* 加载提示 */
  .loading-indicator, .no-more-data {
    text-align: center;
    padding: 15px 0;
    color: #999;
    font-size: 14px;
  }
  
  .loading-indicator i {
    margin-right: 5px;
    animation: rotating 2s linear infinite;
  }
  
  @keyframes rotating {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
  </style>