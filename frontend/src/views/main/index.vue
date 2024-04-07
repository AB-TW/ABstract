<script lang="ts" setup>
import AiChat from "@/components/AiChat.vue"
</script>

<template>
  <div>
    <div class="main-container">
      <div style="width: 600px; height: 600px">
        <el-image src="/introduce.png" fit="scale-down" />
      </div>
      <div class="chatContainer">
        <ai-chat />
      </div>
    </div>
  </div>
</template>
<style scoped>
.main-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: flex-start;
  vertical-align: top;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  margin-top: 20px;
}
.chatContainer {
  width: 50%;
  margin-left: 50px;
}
</style>
