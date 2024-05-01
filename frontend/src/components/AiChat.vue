<template>
  <div>
    <chat-box v-model="messages" ref="chatBoxRef" @send-message="sendMessage" @clear-history="clearHistory" />
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

const chatBoxRef = ref(null)
const messages = reactive([
  {
    sender: "bot",
    text: "您好，我是ABstract智能助理，有什么可以帮您的吗?",
    questions: ["请简单介绍一下ABstract系统", "请问ABstract系统有哪些基本概念？", "当前系统有多少特性"]
  }
] as Message[])

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

const sendMessage = async (newMesssage: string) => {
  const response = await aiApi.ai(newMesssage, clientId.value)
  const message = { sender: "bot", text: "", sources: [] }
  messages.push(message)
  if (!response.ok) {
    message.text = "Sorry, some thing went wrong."
    chatBoxRef.value.stopLoading()
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
  chatBoxRef.value.stopLoading()
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
