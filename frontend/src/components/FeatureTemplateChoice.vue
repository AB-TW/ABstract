<template>
  <el-form-item label="选项类型" prop="choice.type" label-width="60">
    <el-select v-model="choice.type" @change="typeChanged">
      <el-option v-for="item in Object.keys(ChoiceType)" :key="item" :label="item" :value="item" />
    </el-select>
  </el-form-item>
  <div v-if="choice.type == ChoiceType.INTERFACE">
    <el-form-item label="Feature" prop="choice.featureKey" label-width="60">
      <el-select v-model="choice.featureKey" @change="featureChanged">
        <el-option v-for="item in allFeatures" :key="item.id" :label="item.name" :value="item.featureKey" />
      </el-select>
    </el-form-item>
    <el-form-item label="值字段" prop="choice.textField" label-width="60">
      <el-select v-model="choice.valueField">
        <el-option v-for="item in featureFields" :key="item.value" :value="item.value" :label="item.label" />
      </el-select>
    </el-form-item>
    <el-form-item label="文本字段" prop="choice.textField" label-width="60">
      <el-select v-model="choice.labelField">
        <el-option v-for="item in featureFields" :key="item.value" :value="item.value" :label="item.label" />
      </el-select>
    </el-form-item>
  </div>
  <div v-if="choice.type == ChoiceType.CONTENT">
    <el-button @click="add()" type="warning" icon="Plus"> 添加选项</el-button>
    <el-form
      v-for="item in choice.items"
      :model="item"
      :key="item.value"
      :rules="choiceItemRules"
      ref="formValidator"
      label-width="140px"
      :disabled="props.disabled"
      class="validator-container"
    >
      <el-form-item label="选项值" prop="value" label-width="100">
        <el-input v-model="item.value" />
      </el-form-item>
      <el-form-item label="选项文本" prop="text" label-width="100">
        <el-input v-model="item.label" />
      </el-form-item>
      <el-button @click="remove(item)" type="danger" icon="delete" />
      <el-divider />
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ChoiceItem, ChoiceType, Features, TemplateDataType } from "@/domain/Features"
import { reactive, ref } from "vue"
import { ElMessage, FormRules } from "element-plus"
import { Feature } from "@/domain/Features"

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  disabled: {
    type: Boolean,
    required: false,
    default: false
  }
})

const choice = reactive(props.modelValue)
const choiceItemRules = reactive<FormRules>({
  value: [{ required: true, trigger: "blur" }],
  label: [{ required: true, trigger: "blur" }]
})

const add = () => {
  if (!choice.items) {
    choice.items = []
  }
  choice.items.push({ value: "", label: "" })
}

const remove = (item) => {
  choice.items = choice.items.filter((it) => it.value !== item.value)
}

const typeChanged = (v) => {
  if (v === ChoiceType.INTERFACE && !featureLoaded.value) {
    loadFeatureConfigs()
  }
}

const allFeatures = ref([] as Feature[])
const featureLoaded = ref(false)
const selectLoading = ref(false)
const features = new Features()

const loadFeatureConfigs = async () => {
  if (featureLoaded.value) {
    return
  }

  try {
    selectLoading.value = true
    const response = await features.fetchAll(0, 100, null, true)
    allFeatures.value = response.content || []
    featureLoaded.value = true
    selectLoading.value = false
    featureChanged(choice.featureKey)
  } catch (error) {
    ElMessage.error("加载选项失败！")
    selectLoading.value = false
  }
}

const featureFields = ref([] as ChoiceItem[])
const featureChanged = (featureKey: string) => {
  const feature = allFeatures.value.find((item) => item.featureKey === featureKey)
  featureFields.value = [] as ChoiceItem[]
  const template = feature?.template
  if (!template) {
    return
  }

  if (template?.dataType != TemplateDataType.OBJECT && template?.dataType != TemplateDataType.ARRAY) {
    featureFields.value = [{ value: "data", label: "配置数据" }]
  }

  if (template?.dataType == TemplateDataType.OBJECT) {
    featureFields.value = [
      { value: "data", label: "配置数据" },
      ...(template.items || []).map((item) => ({ value: "data." + item.key, label: item.name }))
    ] as ChoiceItem[]
  }
}

typeChanged(choice?.type)
</script>

<style scoped lang="scss"></style>
