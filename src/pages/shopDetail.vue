<template>
    <div class="shop-detail-container">
      <!-- 头部导航 -->
      <div class="header">
        <div class="header-back-btn" @click="goBack">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="header-title"></div>
        <div class="header-share">...</div>
      </div>
      
      <!-- 顶部装饰条 -->
      <div class="top-bar"></div>
      
      <!-- 店铺基本信息 -->
      <div class="shop-info-box">
        <div class="shop-title">{{ shop.name }}</div>
        <div class="shop-rate">
          <el-rate
            disabled
            v-model="shop.score"
            text-color="#F63"
            show-score
            clearable
          />
          <span>{{ shop.comments }}条</span>
        </div>
        <div class="shop-rate-info">口味:4.9 环境:4.8 服务:4.7</div>
        <div class="shop-rank">
          <img src="/imgs/bd.png" width="63" height="20" alt="">
          <span>拱墅区好评榜第3名</span>
          <div><i class="el-icon-arrow-right"></i></div>
        </div>
        <div class="shop-images">
          <div v-for="(s, i) in shop.images" :key="i">
            <img :src="s" alt="店铺图片">
          </div>
        </div>
        <div class="shop-address">
          <div><i class="el-icon-map-location"></i></div>
          <span>{{ shop.address }}</span>
          <div class="address-divider">|</div>
          <div class="address-icon">
            <img src="https://p0.meituan.net/travelcube/bf684aa196c870810655e45b1e52ce843484.png@24w_16h_40q" alt="位置图标">
          </div>
          <div class="address-icon">
            <img src="https://p0.meituan.net/travelcube/9277ace32123e0c9f59dedf4407892221566.png@24w_24h_40q" alt="分享图标">
          </div>
        </div>
      </div>
      
      <!-- 分隔线 -->
      <div class="shop-divider"></div>
      
      <!-- 营业时间 -->
      <div class="shop-open-time">
        <span><i class="el-icon-watch"></i></span>
        <div>营业时间</div>
        <div>{{ shop.openHours }}</div>
        <span class="line-right">查看详情 <i class="el-icon-arrow-right"></i></span>
      </div>
      
      <!-- 分隔线 -->
      <div class="shop-divider"></div>
      
      <!-- 优惠券区域 -->
      <div class="shop-voucher">
        <div class="voucher-header">
          <span class="voucher-icon">券</span>
          <span class="voucher-title">代金券</span>
        </div>
        <div 
          class="voucher-box" 
          v-for="v in validVouchers" 
          :key="v.id"
        >
          <div class="voucher-circle">
            <div class="voucher-b"></div>
            <div class="voucher-b"></div>
            <div class="voucher-b"></div>
          </div>
          <div class="voucher-left">
            <div class="voucher-title">{{ v.title }}</div>
            <div class="voucher-subtitle">{{ v.subTitle }}</div>
            <div class="voucher-price">
              <div>￥ {{ formatPrice(v.payValue) }}</div>
              <span>{{ ((v.payValue * 10) / v.actualValue).toFixed(1) }}折</span>
            </div>
          </div>
          <div class="voucher-right">
            <div v-if="v.type" class="seckill-box">
              <div 
                class="voucher-btn" 
                :class="{'disable-btn': isNotBegin(v) || v.stock < 1}" 
                @click="seckill(v)"
              >
                限时抢购
              </div>
              <div class="seckill-stock">剩余 <span>{{ v.stock }}</span> 张</div>
              <div class="seckill-time">{{ formatTime(v) }}</div>
            </div>
            <div class="voucher-btn" v-else>抢购</div>
          </div>
        </div>
      </div>
      
      <!-- 分隔线 -->
      <div class="shop-divider"></div>
      
      <!-- 用户评价区域 -->
      <div class="shop-comments">
        <div class="comments-head">
          <div>网友评价 <span>（119）</span></div>
          <div><i class="el-icon-arrow-right"></i></div>
        </div>
        <div class="comment-tags">
          <div class="tag" v-for="(tag, index) in commentTags" :key="index">
            {{ tag }}
          </div>
        </div>
        <div class="comment-list">
          <div class="comment-box" v-for="i in 3" :key="i">
            <div class="comment-icon">
              <img src="https://p0.meituan.net/userheadpicbackend/57e44d6eba01aad0d8d711788f30a126549507.jpg%4048w_48h_1e_1c_1l%7Cwatermark%3D0" alt="用户头像">
            </div>
            <div class="comment-info">
              <div class="comment-user">叶小乙 <span>Lv5</span></div>
              <div class="comment-rate">
                打分
                <el-rate  v-model="rateValue"></el-rate>
              </div>
              <div class="comment-content">
                某平台上买的券，价格可以当工作餐吃，虽然价格便宜，但是这家店一点都没有...
              </div>
              <div class="comment-images">
                <img 
                  v-for="(img, idx) in commentImages" 
                  :key="idx" 
                  :src="img" 
                  alt="评论图片"
                >
              </div>
              <div class="comment-stats">
                浏览641 &nbsp;&nbsp;&nbsp;&nbsp;评论5
              </div>
            </div>
          </div>
          <div class="view-all-comments">
            <div>查看全部119条评价</div>
            <div><i class="el-icon-arrow-right"></i></div>
          </div>
        </div>
      </div>
      
      <!-- 分隔线 -->
      <div class="shop-divider"></div>
      
      <!-- 版权信息 -->
      <div class="copyright">
        copyright ©2021 hai.com
      </div>
    </div>
  </template>
  
  <script>
  import service from "../utlis/request";

  export default {
    name: 'ShopDetail',
    data() {
      return {
        rateValue:4.2,
        shop: {
          name: '',
          score: 0,
          comments: 0,
          address: '',
          openHours: '',
          images: []
        },
        vouchers: [],
        commentTags: [
          '味道赞(19)', '牛肉赞(16)', '菜品不错(11)', 
          '回头客(4)', '分量足(4)', '停车方便(3)',
          '海鲜棒(3)', '饮品赞(3)', '朋友聚餐(6)'
        ],
        commentImages: [
          'https://qcloud.dpfile.com/pc/6T7MfXzx7USPIkSy7jzm40qZSmlHUF2jd-FZUL6WpjE9byagjLlrseWxnl1LcbuSGybIjx5eX6WNgCPvcASYAw.jpg',
          'https://qcloud.dpfile.com/pc/sZ5q-zgglv4VXEWU71xCFjnLM_jUHq-ylq0GKivtrz3JksWQ1f7oBWZsxm1DWgcaGybIjx5eX6WNgCPvcASYAw.jpg',
          'https://qcloud.dpfile.com/pc/xZy6W4NwuRFchlOi43DVLPFsx7KWWvPqifE1JTe_jreqdsBYA9CFkeSm2ZlF0OVmGybIjx5eX6WNgCPvcASYAw.jpg',
          'https://qcloud.dpfile.com/pc/xZy6W4NwuRFchlOi43DVLPFsx7KWWvPqifE1JTe_jreqdsBYA9CFkeSm2ZlF0OVmGybIjx5eX6WNgCPvcASYAw.jpg'
        ]
      };
    },
    computed: {
      // 只显示有效的优惠券
      validVouchers() {
        return this.vouchers.filter(v => !this.isEnd(v));
      }
    },
    created() {
      const shopId = this.$route.params.id;
      this.queryShopById(shopId);
      this.queryVoucher(shopId);
    },
    methods: {
      // 返回上一页
      goBack() {
        history.back();
      },
      formatPrice(val, toCents = false) {
        // 处理空值
        if (val === null || val === undefined || val === "") {
          return null;
        }

        // 统一转为字符串处理
        let strVal = typeof val === 'number' ? val.toString() : val;

        // 验证是否为有效数字
        if (!/^-?\d*\.?\d*$/.test(strVal)) {
          return null;
        }

        // 处理负号
        const isNegative = strVal.startsWith('-');
        strVal = strVal.replace('-', '');

        // 分割整数和小数部分
        const [integerPart, decimalPart = ''] = strVal.split('.');

        if (toCents) {
          // 转换为分（整数）
          const cents = decimalPart.length >= 2
              ? decimalPart.substring(0, 2)
              : decimalPart + '0'.repeat(2 - decimalPart.length);

          const totalCents = parseInt(integerPart + cents, 10);
          return isNegative ? -totalCents : totalCents;
        } else {
          // 格式化为标准价格字符串（两位小数）
          const formattedDecimal = decimalPart.length >= 2
              ? decimalPart.substring(0, 2)
              : decimalPart + '0'.repeat(2 - decimalPart.length);

          return (isNegative ? '-' : '') + integerPart + '.' + formattedDecimal;
        }
      },
      // 查询店铺详情
      async queryShopById(shopId) {
        try {
          const { data } = await service.get(`/shop/${shopId}`);
          data.images = data.images.split(",");
          data.score = data.score / 10;
          this.shop = data;

        } catch (error) {
          this.$message.error('获取店铺信息失败');
        }
      },

      // 查询优惠券
      async queryVoucher(shopId) {
        try {
          const { data } = await service.get(`/voucher/list/${shopId}`);
          this.vouchers = data;
        } catch (error) {
          this.$message.error('获取优惠券失败');
        }
      },

      // 格式化时间显示
      formatTime(v) {
        const formatDate = (date) => {
          const month = date.getMonth() + 1;
          const day = date.getDate();
          const hours = date.getHours();
          const minutes = this.formatMinutes(date.getMinutes());
          return `${month}月${day}日 ${hours}:${minutes}`;
        };

        const begin = new Date(v.beginTime);
        const end = new Date(v.endTime);
        return `${formatDate(begin)} ~ ${formatDate(end)}`;
      },

      // 格式化分钟数
      formatMinutes(m) {
        return m < 10 ? `0${m}` : m;
      },

      // 检查优惠券是否未开始
      isNotBegin(v) {
        return new Date(v.beginTime).getTime() > Date.now();
      },

      // 检查优惠券是否已结束
      isEnd(v) {
        return new Date(v.endTime).getTime() < Date.now();
      },

      // 秒杀抢购
      async seckill(v) {
        // 检查登录状态
        if (!localStorage.getItem('token')) {
          this.$message.error("请先登录");
          setTimeout(() => {
            location.href = "/login.html";
          }, 200);
          return;
        }

        // 检查优惠券状态
        if (this.isNotBegin(v)) {
          this.$message.error("优惠券抢购尚未开始！");
          return;
        }

        if (this.isEnd(v)) {
          this.$message.error("优惠券抢购已经结束！");
          return;
        }

        if (v.stock < 1) {
          this.$message.error("库存不足，请刷新再试试！");
          return;
        }

        try {
          const { data } = await service.post(`/voucher-order/seckill/${v.id}`);
          this.$message.success(`抢购成功，订单id：${data}`);
        } catch (error) {
          this.$message.error('抢购失败');
        }
      }





    }
  };
  </script>
  
  <style scoped>
  /* 整体布局 */
  .shop-detail-container {
    max-width: 500px;
    margin: 0 auto;
    background-color: #fff;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  }
  
  /* 头部样式 */
  .header {
    display: flex;
    align-items: center;
    padding: 15px;
    background-color: #fff;
    position: sticky;
    top: 0;
    z-index: 100;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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
  
  .header-share {
    font-size: 24px;
    color: #666;
  }
  
  /* 顶部装饰条 */
  .top-bar {
    height: 5px;
    background: linear-gradient(90deg, #ff6633, #ff9f00);
  }
  
  /* 店铺信息区域 */
  .shop-info-box {
    padding: 15px;
  }
  
  .shop-title {
    font-size: 22px;
    font-weight: bold;
    margin-bottom: 10px;
    color: #333;
  }
  
  .shop-rate {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }
  
  .shop-rate span {
    margin-left: 10px;
    font-size: 14px;
    color: #666;
  }
  
  .shop-rate-info {
    font-size: 14px;
    color: #666;
    margin-bottom: 15px;
  }
  
  .shop-rank {
    display: flex;
    align-items: center;
    padding: 8px;
    background-color: #f8f8f8;
    border-radius: 4px;
    margin-bottom: 15px;
  }
  
  .shop-rank span {
    margin-left: 10px;
    font-size: 14px;
    color: #666;
    flex: 1;
  }
  
  .shop-images {
    display: flex;
    overflow-x: auto;
    gap: 10px;
    margin-bottom: 15px;
    padding-bottom: 5px;
  }
  
  .shop-images div {
    flex: 0 0 auto;
    width: 120px;
    height: 90px;
    border-radius: 4px;
    overflow: hidden;
  }
  
  .shop-images img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .shop-address {
    display: flex;
    align-items: center;
    padding: 10px 0;
    border-top: 1px solid #f1f1f1;
  }
  
  .shop-address span {
    flex: 1;
    font-size: 14px;
    color: #666;
    margin-left: 8px;
  }
  
  .address-divider {
    width: 10px;
    text-align: center;
    color: #e1e2e3;
    margin: 0 10px;
  }
  
  .address-icon {
    margin: 0 5px;
  }
  
  /* 分隔线 */
  .shop-divider {
    height: 8px;
    background-color: #f5f5f5;
  }
  
  /* 营业时间 */
  .shop-open-time {
    display: flex;
    align-items: center;
    padding: 15px;
    font-size: 14px;
    color: #333;
  }
  
  .shop-open-time > * {
    margin-right: 10px;
  }
  
  .shop-open-time div:nth-child(2) {
    font-weight: bold;
  }
  
  .shop-open-time div:nth-child(3) {
    flex: 1;
    color: #666;
  }
  
  .line-right {
    color: #ff6633;
    font-weight: bold;
  }
  
  /* 优惠券区域 */
  .shop-voucher {
    padding: 15px;
  }
  
  .voucher-header {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
  }
  
  .voucher-icon {
    display: inline-block;
    width: 20px;
    height: 20px;
    background-color: #ff6633;
    color: white;
    text-align: center;
    line-height: 20px;
    border-radius: 3px;
    margin-right: 8px;
    font-size: 12px;
  }
  
  .voucher-box {
    position: relative;
    display: flex;
    border: 1px solid #ff6633;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 15px;
    padding: 15px;
    background: linear-gradient(to right, #fff8f5, #fff);
  }
  
  .voucher-circle {
    position: absolute;
    top: 0;
    bottom: 0;
    right: 100px;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
  }
  
  .voucher-b {
    width: 10px;
    height: 10px;
    background-color: #f5f5f5;
    border-radius: 50%;
    border: 1px solid #ff6633;
  }
  
  .voucher-left {
    flex: 1;
    padding-right: 15px;
    border-right: 1px dashed #ff6633;
  }
  
  .voucher-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 5px;
    color: #333;
  }
  
  .voucher-subtitle {
    font-size: 12px;
    color: #999;
    margin-bottom: 10px;
  }
  
  .voucher-price {
    display: flex;
    align-items: baseline;
  }
  
  .voucher-price div {
    font-size: 24px;
    font-weight: bold;
    color: #ff6633;
    margin-right: 10px;
  }
  
  .voucher-price span {
    font-size: 12px;
    color: #ff6633;
    background-color: #ffebe0;
    padding: 2px 5px;
    border-radius: 3px;
  }
  
  .voucher-right {
    width: 100px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
  
  .voucher-btn {
    width: 80px;
    height: 30px;
    background-color: #ff6633;
    color: white;
    text-align: center;
    line-height: 30px;
    border-radius: 15px;
    font-size: 14px;
    cursor: pointer;
  }
  
  .disable-btn {
    background-color: #ccc;
    cursor: not-allowed;
  }
  
  .seckill-box {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .seckill-stock {
    font-size: 12px;
    color: #666;
    margin: 5px 0;
  }
  
  .seckill-stock span {
    color: #ff6633;
    font-weight: bold;
  }
  
  .seckill-time {
    font-size: 12px;
    color: #ff6633;
    background-color: #ffebe0;
    padding: 2px 5px;
    border-radius: 3px;
  }
  
  /* 用户评价区域 */
  .shop-comments {
    padding: 0 15px;
  }
  
  .comments-head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 0;
    font-size: 16px;
    font-weight: bold;
  }
  
  .comments-head span {
    color: #666;
    font-weight: normal;
  }
  
  .comment-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 15px;
  }
  
  .tag {
    padding: 5px 10px;
    background-color: #f5f5f5;
    border-radius: 15px;
    font-size: 12px;
    color: #666;
  }
  
  .comment-list {
    border-top: 1px solid #f1f1f1;
    padding-top: 15px;
  }
  
  .comment-box {
    display: flex;
    margin-bottom: 20px;
  }
  
  .comment-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 10px;
  }
  
  .comment-icon img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .comment-info {
    flex: 1;
  }
  
  .comment-user {
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  .comment-user span {
    font-weight: normal;
    color: #ff9f00;
    margin-left: 5px;
  }
  
  .comment-rate {
    display: flex;
    align-items: center;
    font-size: 12px;
    color: #666;
    margin-bottom: 5px;
  }
  
  .comment-content {
    padding: 5px 0;
    font-size: 14px;
    color: #333;
    line-height: 1.5;
  }
  
  .comment-images {
    display: flex;
    gap: 5px;
    margin: 10px 0;
  }
  
  .comment-images img {
    width: 70px;
    height: 70px;
    border-radius: 4px;
    object-fit: cover;
  }
  
  .comment-stats {
    font-size: 12px;
    color: #999;
  }
  
  .view-all-comments {
    display: flex;
    justify-content: space-between;
    padding: 15px 0;
    border-top: 1px solid #f1f1f1;
    margin-top: 10px;
    color: #666;
    font-size: 14px;
  }
  
  /* 版权信息 */
  .copyright {
    text-align: center;
    padding: 20px 0;
    font-size: 12px;
    color: #999;
  }
  </style>