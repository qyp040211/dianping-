<script>
import UploadPage from "@/components/upload.vue";

export default {
  name: 'MessagePage',
  components: {UploadPage},
  data() {
    return {
      string:''
    }
  }
}
</script>

<template>
<el-header><el-input /> <upload-page v-model="string" /></el-header>
</template>

<style scoped>

</style>