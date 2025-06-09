<script>
import service from "@/utlis/request"; // 若使用 service，请改为 import service from '@/utils/request'

export default {
  name: 'uploadPage',
  data() {
    return {
      selectedFile: null
    }
  },
  methods: {
    handleFileChange(event) {
      this.selectedFile = event.target.files[0]
    },
    async uploadImage() {
      if (!this.selectedFile) {
        alert('请先选择一张图片')
        return
      }

      const formData = new FormData()
      formData.append('file', this.selectedFile)

      try {
        const { data } = await service.post('/upload/blog', formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        alert('上传成功')
        console.log(data)
      } catch (error) {
        console.error('上传失败', error)
        alert('上传失败')
      }
    }
  }
}
</script>

<template>
  <div >
    <input type="file" accept="image/*" @change="handleFileChange" />
    <button @click="uploadImage">上传图片</button>
  </div>
</template>

<style scoped>

</style>