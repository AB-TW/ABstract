<template>
  <div>
    <chat-box v-model="messages" />
    <form @submit.prevent="sendMessage" style="margin-top: 10px">
      <input v-model="newMessage" placeholder="Type a message..." />
      <button type="submit">Send</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue"
import { Ai } from "@/domain/Ai"
import ChatBox from "@/components/ChatBox.vue"

const aiApi = new Ai()

const newMessage = ref("AB测试是什么")
const messages = reactive([{ sender: "bot", text: "您好，我是ABstract智能助理，有什么可以帮您的吗?" }])

const sendMessage = async () => {
  messages.push({ sender: "user", text: newMessage.value })
  const response = await aiApi.ai(newMessage.value)
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
    message.text = message.text + chunkText.replaceAll("data:", "").replaceAll("\n", "")
    messages.pop()
    messages.push(message)
    console.log("message.text:" + message.text)
  }
}
</script>

<style scoped lang="scss"></style>
