<script setup lang="ts">
import {ref} from 'vue'

defineProps<{ msg: string }>()

const count = ref(0)
</script>

<template>
  <h1>{{ msg || "Vite + Vue"}}</h1>
  <div class="card">
    <button type="button" @click="count++">count is{{ count }}</button>
  </div>
  <router-link to="/test">jump to Test</router-link>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
