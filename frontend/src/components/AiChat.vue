<template>
  <div>
    <div class="container">
      <div class="chatBot">
        <div v-for="(message, index) in messages" :key="index">
          <div class="message-user" v-if="message.sender === 'user'">
            <div class="user">{{ message.text }}</div>
          </div>
          <div class="message-bot" v-else>
            <div class="bot">{{ message.text }}</div>
          </div>
        </div>
      </div>
    </div>
    <form @submit.prevent="sendMessage">
      <input v-model="newMessage" placeholder="Type a message..." />
      <button type="submit">Send</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue"
import { Ai } from "@/domain/Ai"

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

<style scoped lang="scss">
.chatBot {
  width: 100%;
  height: 600px;
  border: 1px solid #ccc;
  padding: 20px;
  overflow-y: auto;
}

.container {
  display: flex;
  align-items: flex-start;
  justify-content: start;
  margin-top: 0;
}

.message-user {
  margin-bottom: 10px;
  display: flex;
  justify-content: flex-end;
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
</style>
