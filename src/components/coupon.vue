<script >
export default {
  name: "couponCom",
}

</script>

<template>
  <div class="coupon-card">
    <div class="coupon-shape">
      <div class="coupon-notch top-notch"></div>
      <div class="coupon-main">
        <svg class="coupon-icon" viewBox="0 0 1081 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
          <path d="M604.612892 557.077773l-53.348196 0 0-41.484234 53.348196 0c8.059039 0 14.611963-6.975693 14.611963-15.510347 0-8.613924-6.579347-15.510347-14.611963-15.510347l-14.347732 0 26.95154-52.212004c3.910616-6.209424 1.558962-14.083501-5.23177-17.67704-6.790731-3.567116-15.431078-1.426846-19.341694 4.756154l-33.715849 65.13289-45.500542 0-33.583733-65.265005c-3.910616-6.209424-12.524539-8.376116-19.341694-4.729731-6.817154 3.593539-9.142385 11.441193-5.23177 17.67704l26.977964 52.212004-14.321309 0c-8.059039 0-14.611963 6.975693-14.611963 15.510347s6.579347 15.510347 14.611963 15.510347l53.427466 0 0 41.484234-53.348196 0c-8.059039 0-14.611963 6.975693-14.611963 15.510347 0 8.613924 6.552924 15.510347 14.611963 15.510347l53.348196 0 0 41.484234c0 9.063116 6.711462 16.461578 14.955463 16.461578 8.296847 0 14.955463-7.398462 14.955463-16.461578l0-41.484234 53.348196 0c8.059039 0 14.611963-6.975693 14.611963-15.510347C619.198431 564.000619 612.671931 557.077773 604.612892 557.077773L604.612892 557.077773zM370.39872 453.367188l10.38427 0 0 155.499819-10.38427 0L370.39872 453.367188zM696.961552 453.367188l10.38427 0 0 155.499819-10.38427 0L696.961552 453.367188zM536.309233 105.798008c-233.421479 0-422.769263 189.294937-422.769263 422.769263s189.294937 422.769263 422.769263 422.769263 422.769263-189.294937 422.769263-422.769263S769.730712 105.798008 536.309233 105.798008L536.309233 105.798008zM785.108944 443.009341l0 39.581772c-22.063271 3.752077-38.841926 22.82954-38.841926 45.976157 0 23.067348 16.857924 42.250503 38.841926 45.976157l0 117.31847c0 8.613924-6.975693 15.510347-15.510347 15.510347L303.019869 707.372245c-8.534654 0-15.510347-6.975693-15.510347-15.510347l0-117.31847c22.063271-3.752077 38.841926-22.82954 38.841926-45.976157s-16.857924-42.250503-38.841926-45.976157l0-117.265624c0-8.534654 6.975693-15.510347 15.510347-15.510347l466.499458 0c8.534654 0 15.510347 6.975693 15.510347 15.510347l0 77.683852L785.108944 443.009341z" fill="currentColor"></path>
        </svg>
        <div class="coupon-details">
          <div class="coupon-store">鲜花店</div>
          <div class="coupon-amount">￥10 优惠券</div>
          <div class="coupon-date">生效日期：2025-06-01</div>
        </div>
      </div>
      <div class="coupon-notch bottom-notch"></div>
    </div>
    <button class="coupon-button">立即领取</button>
  </div>


</template>
<style>
.coupon-card {
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  width: 320px;
  perspective: 1000px;
}

.coupon-shape {
  position: relative;
  background: linear-gradient(135deg, #FF9A9E 0%, #FAD0C4 100%);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 10px 20px rgba(235, 95, 26, 0.2);
  color: #fff;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.coupon-card:hover .coupon-shape {
  transform: translateY(-5px);
}

.coupon-main {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 2;
}

.coupon-icon {
  width: 48px;
  height: 48px;
  margin-right: 16px;
  color: rgba(255, 255, 255, 0.9);
  flex-shrink: 0;
}

.coupon-details {
  flex-grow: 1;
}

.coupon-store {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 6px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.coupon-amount {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
}

.coupon-date {
  font-size: 14px;
  opacity: 0.9;
}

.coupon-notch {
  position: absolute;
  width: 100%;
  height: 12px;
  left: 0;
  background-color: #fff;
  border-radius: 0 0 50% 50%;
}

.top-notch {
  top: 0;
  border-radius: 50% 50% 0 0;
}

.bottom-notch {
  bottom: 0;
}

.coupon-button {
  width: 100%;
  padding: 12px;
  margin-top: 15px;
  background: #fff;
  color: #EB5F1A;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(235, 95, 26, 0.2);
}

.coupon-button:hover {
  background: #EB5F1A;
  color: #fff;
  transform: translateY(-2px);
}

.coupon-button:active {
  transform: translateY(0);
}
</style>
<script setup lang="ts">
</script>