<script >
import service from "@/utlis/request";

export default {
  async mounted() {
    await this.getMyBlog()
  },
  name: "myblogCom",
  methods: {
    async getMyBlog() {
      const {data} = service.get('/blog/of/me?current=1')
      console.log(data)
    }
  },
}
</script>

<template>
<div >hello</div>
</template>

<style scoped>

</style>