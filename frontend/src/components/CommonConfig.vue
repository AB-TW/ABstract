<template>
  <div class="common-container">
    <div class="label">{{ description || "设置值" }} :</div>
    <div>
      <div v-if="dataType == 'STRING'">
        <el-input v-model="value" type="string" :maxlength="maxLength" @change="onInput" />
      </div>
      <div v-else-if="dataType == 'NUMBER'">
        <el-input-number v-model="value" :placeholder="description" :max="maxValue" :min="minValue" @change="onInput" />
      </div>
      <div v-else-if="dataType == 'BOOLEAN'">
        <el-switch v-model="value" @change="onInput" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue"

const props = defineProps({
  dataType: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: false
  },
  defaultValue: {
    type: Object,
    required: true
  },
  maxLength: {
    type: String,
    required: false
  },
  maxValue: {
    type: Number,
    required: false
  },
  minValue: {
    type: Number,
    required: false
  }
})
const emit = defineEmits(["update:modelValue"])
// 绑定控件的值
const value = ref(props.defaultValue)
const onInput = (e) => {
  emit("update:modelValue", e)
}
watch(
  () => props.defaultValue,
  (v1, v2) => {
    value.value = v1
  },
  {
    immediate: true // 必须要深度监听，否则初始值赋不进去
  }
)
</script>

<style lang="scss" scoped>
.common-container {
  display: flex;
  justify-content: center;
  align-content: center;
}
.label {
  width: 80px;
  text-align: center;
}
</style>
