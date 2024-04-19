<template>
  <el-card>
    <div class="chatBot" :style="{ height: height }">
      <div v-for="(message, index) in messages || []" :key="index">
        <div class="message-user" v-if="message.sender === 'user'">
          <div class="user" v-html="message.text" />
          <el-icon class="chat-icon">
            <UserFilled />
          </el-icon>
        </div>
        <div class="message-bot" v-else>
          <el-icon class="chat-icon">
            <Cpu />
          </el-icon>
          <div class="bot">
            <div v-html="message.text" />
            <div v-if="message.sources?.length > 0">
              <H3 style="color: #505050">
                <el-icon class="chat-icon">
                  <DocumentCopy />
                </el-icon>
                Sources:
              </H3>
              <div class="source-container">
                <div class="source" v-for="(source, index) in message.sources" :key="index">
                  <div class="source-item" :title="source">
                    <el-icon class="chat-icon">
                      <Document />
                    </el-icon>
                    {{ source }}
                  </div>
                </div>
              </div>
            </div>
          </div>
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
  sources?: string[]
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
a {
  color: #409eff;
}

.chatBot {
  margin-top: 0;
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
  //width: fit-content;
  border-radius: 5px;
  border: 1px solid #ccc;
  padding: 5px;
}

.chat-icon {
  margin-top: 12px;
  margin-right: 10px;
  margin-left: 10px;
}

.source-container {
  margin-left: 2rem;
  margin-right: 2rem;
  display: flex;
  //flex-wrap: wrap;
  //flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.source {
  padding: 0.5rem;
  border-radius: 5px;
  background-color: #eee;
  border: 1px solid #ccc;
}

.source-item {
  width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
