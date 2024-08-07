<template>
  <div class="track-container">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-form-item label="Key" required prop="key">
          <el-input v-model="item.key" autocomplete="off" :disabled="disabled" />
        </el-form-item>
      </el-col>
      <el-col :span="10">
        <el-form-item label="Value" required prop="key">
          <el-input v-model="item.value" autocomplete="off" :disabled="disabled" />
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <el-form-item v-if="!disabled">
          <el-button type="warning" @click="deleteItem()"
            ><el-icon> <Delete /> </el-icon
          ></el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"

const props = defineProps({
  index: {
    type: Number,
    required: true
  },
  item: {
    type: Object,
    required: true
  },
  disabled: {
    type: Boolean,
    required: false
  }
})

const emit = defineEmits(["deleteItem"])
const deleteItem = () => {
  emit("deleteItem", props.index)
}

const item = ref(props.item)
</script>

<style lang="scss">
.track-container {
  display: block;
  margin-bottom: 10px;
  .el-form-item__label {
    width: 40px !important;
  }
}
</style>
