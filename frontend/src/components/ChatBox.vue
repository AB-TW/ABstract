<template>
  <el-card class="container" shadow="hover">
    <div class="chatBot" :style="{ height: height }">
      <div v-for="(message, index) in messages" :key="index">
        <div class="message-user" v-if="message.sender === 'user'">
          <el-icon class="chat-icon">
            <UserFilled />
          </el-icon>
          <div class="user" v-html="message.text" />
        </div>
        <div class="message-bot" v-else>
          <el-icon class="chat-icon">
            <Cpu />
          </el-icon>
          <div class="bot" v-html="message.text" />
        </div>
      </div>
    </div>
  </el-card>
</template>
<script setup lang="ts">
import { reactive, ref } from "vue"
import { Cpu, UserFilled } from "@element-plus/icons-vue"
interface Message {
  sender: string
  text: string
}

const props = defineProps({
  modelValue: {
    type: Array<Message>,
    required: true,
    default: () => []
  },
  height: {
    type: String,
    required: false,
    default: "600px"
  }
})

const messages = reactive(props.modelValue)
const height = ref(props.height)
</script>
<style scoped lang="scss">
.container {
  display: flex;
  align-items: flex-start;
  justify-content: start;
  margin-top: 0;
}

a {
  color: #409eff;
}

.chatBot {
  width: 100%;
  padding: 20px;
  overflow-y: auto;
  font-size: 14px;
  font-weight: normal;
  line-height: 2;
}

.message-user {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
  margin-bottom: 10px;
}

.message-bot {
  display: flex;
  justify-content: flex-start;
  margin-top: 10px;
  margin-bottom: 10px;
}

.user {
  text-align: right;
  background-color: #c0ff90;
  width: fit-content;
  border-radius: 5px;
  border: 1px solid #ccc;
  padding: 5px;
}

.bot {
  text-align: left;
  background-color: #f0f0f0;
  width: fit-content;
  border-radius: 5px;
  border: 1px solid #ccc;
  padding: 5px;
}

.chat-icon {
  margin-top: 10px;
  margin-right: 10px;
}
</style>
