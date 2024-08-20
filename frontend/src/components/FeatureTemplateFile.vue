<template>
  <el-form-item label="文件类型" :prop="parentProp + '.type'" label-width="60">
    <el-select v-model="file.type" :disabled="disabled">
      <el-option v-for="item in Object.keys(FileType)" :key="item" :label="item" :value="item" />
    </el-select>
  </el-form-item>
  <el-form-item label="文件大小（KB）" :prop="parentProp + '.sizeLimit'" label-width="60">
    <el-input-number v-model="file.sizeLimit" :disabled="disabled" />
  </el-form-item>
  <el-form-item label="扩展名" :prop="parentProp + 'file.fileExtensions'" label-width="60">
    <el-select
      v-model="file.fileExtensions"
      multiple
      collapse-tags
      collapse-tags-tooltip
      :max-collapse-tags="3"
      placeholder="Select"
      style="width: 240px"
      :disabled="disabled"
    >
      <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
    </el-select>
  </el-form-item>
</template>

<script setup lang="ts">
import { FileType } from "@/domain/Features"
import { computed, reactive } from "vue"

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  parentProp: {
    type: String,
    required: false,
    default: "template"
  },
  disabled: {
    type: Boolean,
    required: false,
    default: false
  }
})

const file = reactive(props.modelValue)

const options = computed(() => {
  switch (file.type) {
    case FileType.IMAGE:
      return [
        { value: "jpg", label: "jpg" },
        { value: "jpeg", label: "jpeg" },
        { value: "png", label: "png" },
        { value: "gif", label: "gif" },
        { value: "bmp", label: "bmp" },
        { value: "webp", label: "webp" },
        { value: "svg", label: "svg" }
      ]
    case FileType.VIDEO:
      return [
        { value: "mp4", label: "mp4" },
        { value: "avi", label: "avi" },
        { value: "mov", label: "mov" }
      ]
    case FileType.RADIO:
      return [
        { value: "mp3", label: "mp3" },
        { value: "wav", label: "wav" },
        { value: "flac", label: "flac" }
      ]
    case FileType.OTHER:
      return [
        { value: "txt", label: "txt" },
        { value: "pdf", label: "pdf" },
        { value: "zip", label: "zip" },
        { value: "rar", label: "rar" },
        { value: "7z", label: "7z" },
        { value: "tar", label: "tar" },
        { value: "gz", label: "gz" },
        { value: "docx", label: "docx" },
        { value: "xlsx", label: "xlsx" },
        { value: "pptx", label: "pptx" },
        { value: "json", label: "json" },
        { value: "xml", label: "xml" },
        { value: "yml", label: "yml" },
        { value: "yaml", label: "yaml" },
        { value: "md", label: "md" }
      ]
    default:
      return []
  }
})
</script>

<style scoped lang="scss"></style>
