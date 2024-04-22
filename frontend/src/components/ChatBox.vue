<template>
  <el-card>
    <div class="chatBot" :style="{ height: height }" ref="chatContainer">
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
            <div v-html="markdown.render(message.text)" />
            <div v-if="message.sources?.length > 0">
              <H3 style="color: #505050">
                <el-icon class="chat-icon">
                  <DocumentCopy />
                </el-icon>
                Sources:
              </H3>
              <div class="source-container">
                <el-popover
                  v-for="(source, index) in message.sources"
                  :key="index"
                  placement="top-start"
                  :width="700"
                  trigger="hover"
                >
                  <template #reference>
                    <div class="source">
                      <div class="source-item">
                        <el-icon class="chat-icon">
                          <Document />
                        </el-icon>
                        {{ source }}
                      </div>
                    </div>
                  </template>
                  <div v-html="markdown.render(source)" />
                </el-popover>
              </div>
            </div>
            <div class="question-container" v-if="message.questions?.length > 0">
              <div class="question" v-for="(question, index) in message.questions" :key="index">
                <div class="question-item" @click="emit('questionClicked', question)">
                  <el-icon class="chat-icon">
                    <Edit />
                  </el-icon>
                  {{ question }}
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
import { reactive, ref, watch } from "vue"
import { Cpu, UserFilled } from "@element-plus/icons-vue"
import MarkdownIt from "markdown-it"

const markdown = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true
})

export interface Message {
  sender: string
  text: string
  sources?: string[]
  questions?: string[]
}

const emit = defineEmits(["questionClicked"])

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

const chatContainer = ref(null)

// 监测messages prop的变化
watch(
  () => props.modelValue.length,
  () => {
    console.log(chatContainer.value.scrollHeight)
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight + 100
  }
)

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
  width: fit-content;
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
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
}

.source {
  margin-top: 0.5rem;
  margin-bottom: 0.5rem;
  padding: 0.5rem;
  border-radius: 5px;
  background-color: #ddd;
  border: 1px solid #ccc;
}

.source:hover {
  background-color: #b7c5d5;
}

.source-item {
  width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.question {
  border-radius: 5px;
  border: 1px solid #ccc;
  margin-top: 10px;
  background-color: #ddd;
}

.question:hover {
  background-color: #b7c5d5;
}
</style>
