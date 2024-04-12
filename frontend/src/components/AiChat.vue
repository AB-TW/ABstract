<template>
  <div>
    <chat-box v-model="messages" />
    <form @submit.prevent="sendMessage" style="margin-top: 10px">
      <div>
        <el-input v-model="newMessage" placeholder="Type a message..." :disabled="loading">
          <template #append>
            <el-button icon="Top" @click="sendMessage" :disabled="loading" />
          </template>
        </el-input>
        <div style="margin-top: 10px">
          <el-popconfirm title="确定清除历史聊天记录吗?" @confirm="clearHistory">
            <template #reference>
              <el-button icon="RefreshRight" type="warning" :disabled="loading"> 清除历史记录</el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { Ai } from "@/domain/Ai"
import ChatBox from "@/components/ChatBox.vue"
import FingerprintJS from "@fingerprintjs/fingerprintjs"

const aiApi = new Ai()
const clientId = ref(localStorage.getItem("browserId") || "")
const getDeviceFingerprint = async () => {
  FingerprintJS.load().then((fp) => {
    fp.get().then((result) => {
      const fingerprintId = result.visitorId
      localStorage.setItem("browserId", fingerprintId)
      clientId.value = fingerprintId
    })
  })
}
getDeviceFingerprint()

const newMessage = ref("请简单介绍一下ABstract系统")
const messages = reactive([
  {
    sender: "bot",
    text: "您好，我是ABstract智能助理，有什么可以帮您的吗?"
  }
])

const loading = ref(false)
const sendMessage = async () => {
  messages.push({ sender: "user", text: newMessage.value })
  loading.value = true
  const response = await aiApi.ai(newMessage.value, clientId.value)
  newMessage.value = ""
  const message = { sender: "bot", text: "" }
  messages.push(message)
  if (!response.ok) {
    message.text = "Sorry, some thing went wrong."
    return
  }
  const reader = response.body.getReader()
  const textDecoder = new TextDecoder()
  let result = true

  while (result) {
    const { done, value } = await reader.read()

    if (done) {
      console.log("Stream ended")
      result = false
      break
    }
    const chunkText = textDecoder.decode(value)
    message.text =
      message.text +
      chunkText
        .replaceAll("data:", "")
        .replaceAll("\n", "")
        .replaceAll(
          /feature|Feature|特性/g,
          "<a style='color: #409eff' target='_blank' href='/#/feature/list'>特性</a>"
        )
    messages.pop()
    messages.push(message)
  }

  loading.value = false
}

const clearHistory = () => {
  aiApi.clearHistory(clientId.value).then((response) => {
    if (response.status !== 200) {
      return
    }
    messages.splice(1, messages.length)
  })
}

onMounted(() => {
  console.log(messages)
  console.log(clientId.value)
  aiApi.history(clientId.value).then((response) => {
    if (response.status !== 200) {
      return
    }
    response.data.forEach((item) => {
      messages.push(item)
    })
  })
})
</script>

<style scoped lang="scss"></style>
