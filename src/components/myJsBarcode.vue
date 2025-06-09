<script>
import JsBarcode from "jsbarcode";

export default {
  props: {
    code: {
      type: String,
      default: "#87AI8e9907y"
    }
  },
  mounted() {
    JsBarcode(this.$refs.barcodeRef, this.code, {
      format: "CODE128",
      lineColor: "#000",
      width: 2,
      height: 100,
      displayValue: true,
    });
  },
  methods: {
    goBack() {
      history.back();
    }
  }
}
</script>

<template>
  <div class="myJsBarcode" style="width: 100%;display: flex;flex-direction: column;justify-content: start">
    <div class="header">
      <div class="header-back-btn" @click="goBack">
        <i class="el-icon-arrow-left"></i>
      </div>
      <div class="header-title">您的优惠卷</div>
    </div>
    <div class="codeBox">
      <svg ref="barcodeRef" class="code"></svg>

    </div>
  </div>
</template>

<style scoped>
.myJsBarcode {
  margin: 20px;
  text-align: center;
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

.codeBox{
  width: 100%;
  height: 60vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.code{
  transform:rotate(90deg) ;
}
</style>