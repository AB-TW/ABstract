<template>
  <div class="track-container">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-form-item label="Key" :rules="itemRules" :prop="getProp('key')">
          <el-input v-model="item.key" autocomplete="off" :disabled="disabled" />
        </el-form-item>
      </el-col>
      <el-col :span="10">
        <el-form-item label="Value" :rules="itemRules" :prop="getProp('value')">
          <el-input v-model="item.value" autocomplete="off" :disabled="disabled" />
        </el-form-item>
      </el-col>
      <el-col :span="4">
        <el-form-item v-if="!disabled">
          <el-button type="warning" @click="deleteItem()">
            <el-icon>
              <Delete />
            </el-icon>
          </el-button>
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
  parentProp: {
    type: String,
    required: false,
    defaultValue: "trackingData"
  },
  enableValidation: {
    type: Boolean,
    required: false,
    defaultValue: true
  },
  disabled: {
    type: Boolean,
    required: false
  }
})

const getProp = (prop) => {
  const parentPro = props.parentProp ? props.parentProp + "." : ""
  const indexPro = props.index != null ? props.index + "." : ""
  return parentPro + indexPro + prop
}

const emit = defineEmits(["deleteItem"])
const deleteItem = () => {
  emit("deleteItem", props.index)
}

const item = ref(props.item)
const itemRules = props.enableValidation
  ? [
      {
        required: true,
        message: "该字段为必填字段"
      }
    ]
  : []
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
