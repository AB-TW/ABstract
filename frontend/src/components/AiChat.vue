<template>
  <div>
    <chat-box v-model="messages" @questionClicked="questionSelected" />
    <form @submit.prevent="sendMessage" style="margin-top: 10px">
      <div>
        <el-input v-model="newMessage" placeholder="Type a message..." :disabled="loading">
          <template #append>
            <el-button icon="Top" @click="sendMessage" :disabled="loading" />
          </template>
          <template #prepend>
            <el-popconfirm title="确定清除历史聊天记录吗?" @confirm="clearHistory">
              <template #reference>
                <el-button icon="RefreshRight" type="warning" :disabled="loading" title="清除历史记录" />
              </template>
            </el-popconfirm>
          </template>
        </el-input>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { Ai } from "@/domain/Ai"
import ChatBox, { Message } from "@/components/ChatBox.vue"
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

const newMessage = ref("")
const messages = reactive([
  {
    sender: "bot",
    text: "您好，我是ABstract智能助理，有什么可以帮您的吗?",
    questions: ["请简单介绍一下ABstract系统", "请问ABstract系统有哪些基本概念？", "当前系统有多少特性"]
  }
] as Message[])

const loading = ref(false)

function getLinkValue(href: string, text: string) {
  const aColor = "#409eff"
  return `<a style='color: ${aColor}' target='_blank' href='${href}'>${text}</a>`
}

function injectLink(resultText: string) {
  return resultText
    .replaceAll(/feature|Feature/g, getLinkValue("/#/feature/list", "Feature"))
    .replaceAll(/特性/g, getLinkValue("/#/feature/list", "特性"))
    .replaceAll(/experiment|Experiment/g, getLinkValue("/#/experiment/list", "Experiment"))
    .replaceAll(/实验/g, getLinkValue("/#/experiment/list", "实验"))
}

const sendMessage = async () => {
  messages.push({ sender: "user", text: newMessage.value })
  loading.value = true
  const response = await aiApi.ai(newMessage.value, clientId.value)
  newMessage.value = ""
  const message = { sender: "bot", text: "", sources: [] }
  messages.push(message)
  if (!response.ok) {
    message.text = "Sorry, some thing went wrong."
    loading.value = false
    return
  }
  const reader = response.body.getReader()
  const textDecoder = new TextDecoder()
  let result = true
  let sources = false
  let sourcesStr = ""
  while (result) {
    const { done, value } = await reader.read()

    if (done) {
      console.log("Stream ended")
      result = false
      break
    }
    const chunkText = textDecoder.decode(value)
    const resultText = chunkText.replaceAll("data:", "")
    if (!sources && resultText.includes("#Sources:")) {
      sources = true
      const splitResult = resultText.split("#Sources:")
      message.text += splitResult[0]
      sourcesStr += splitResult[1]
      continue
    }
    if (!sources) {
      message.text += injectLink(resultText)
    } else {
      sourcesStr += resultText
    }
    messages.pop()
    messages.push(message)
  }

  console.log(sourcesStr)
  if (sourcesStr) {
    message.sources = JSON.parse(sourcesStr)
  }
  messages.pop()
  messages.push(message)
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

const questionSelected = (question) => {
  newMessage.value = question
  // newMessage 获取焦点
  const input = document.querySelector(".el-input__inner")
  if (input) {
    input.focus()
  }
}
onMounted(() => {
  console.log(messages)
  console.log(clientId.value)
  aiApi.history(clientId.value).then((response) => {
    if (response.status !== 200) {
      return
    }
    response.data.forEach((item: any) => {
      messages.push({
        ...item,
        text: injectLink(item.text)
      })
    })
  })
})
</script>

<style scoped lang="scss"></style>
