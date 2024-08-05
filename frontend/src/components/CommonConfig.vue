<template>
  <div class="common-container">
    <div class="label">{{ name || "" }}</div>
    <div>
      <div v-if="dataType == TemplateDataType.STRING">
        <el-input v-model="value" :disabled="props.isViewModel" type="text" :maxlength="maxLength" @change="onInput" />
      </div>
      <div v-if="dataType == TemplateDataType.GENERATED">
        <el-input v-model="value" :readonly="true" type="text" />
      </div>
      <div v-else-if="dataType == TemplateDataType.NUMBER">
        <el-input-number
          v-model="value"
          :disabled="props.isViewModel"
          :placeholder="description"
          :precision="4"
          :step="1"
          :max="maxValue"
          :min="minValue"
          @change="onInput"
        />
      </div>
      <div v-else-if="dataType == TemplateDataType.BOOLEAN">
        <el-switch v-model="value" :disabled="props.isViewModel" @change="onInput" />
      </div>
      <div v-else-if="dataType == TemplateDataType.SELECT">
        <el-select v-model="value" :loading="selectLoading" placeholder="Select" style="width: 240px" @change="onInput">
          <el-option
            v-for="item in choiceItems"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            :disabled="props.isViewModel"
          />
        </el-select>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue"
import { ChoiceItem, ChoiceType, TemplateDataType } from "@/domain/Features"
import { FeatureConfig, FeatureConfigs } from "@/domain/FeatureConfigs"
import { ElMessage } from "element-plus"
import _ from "lodash"

const props = defineProps({
  dataType: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: false
  },
  name: {
    type: String,
    required: true
  },
  defaultValue: {
    type: [String, Number, Boolean],
    required: true
  },
  maxLength: {
    type: Number,
    required: false
  },
  maxValue: {
    type: Number,
    required: false
  },
  minValue: {
    type: Number,
    required: false
  },
  isViewModel: {
    type: Boolean,
    required: true
  },
  choice: {
    type: Object,
    required: false
  },
  brothers: {
    type: Array,
    required: false
  }
})
const emit = defineEmits(["update:modelValue"])
// 绑定控件的值
const value = ref(props.defaultValue)
const onInput = (e) => {
  let tempValue = e
  if (props.dataType == TemplateDataType.NUMBER) {
    tempValue = Number(e)
  } else if (props.dataType == TemplateDataType.BOOLEAN) {
    tempValue = Boolean(e)
  }
  emit("update:modelValue", tempValue)
}

const allFeatureConfigs = ref([] as FeatureConfig[])
const featureConfigLoaded = ref(false)
const selectLoading = ref(false)
const featureConfigs = new FeatureConfigs()
const choiceItems = ref([] as ChoiceItem[])

const loadFeatureConfigs = async (featureKey: string) => {
  if (featureConfigLoaded.value) {
    return
  }

  try {
    selectLoading.value = true
    const response = await featureConfigs.fetchAll("", 0, 100, featureKey, ["RUNNING"])
    allFeatureConfigs.value = response.content || []
    choiceItems.value = allFeatureConfigs.value.map((item) => ({
      value: _.get(item, props.choice.valueField || "data") || "",
      label: _.get(item, props.choice.labelField || "data") || ""
    }))
    featureConfigLoaded.value = true
    selectLoading.value = false
  } catch (error) {
    ElMessage.error("加载选项失败！")
    selectLoading.value = false
  }
}

if (props.choice?.type === ChoiceType.CONTENT) {
  choiceItems.value = props.choice?.items || []
} else if (props.choice?.type === ChoiceType.INTERFACE) {
  if (!featureConfigLoaded.value) {
    loadFeatureConfigs(props.choice?.featureKey)
  }
}

const updateValue = () => {
  const v1 = props.defaultValue
  if (props.dataType != TemplateDataType.GENERATED) {
    value.value = v1
    return
  }

  const pattern = /\{.*?}/
  console.log("v1:" + v1)
  console.log(props.brothers)
  if (pattern.test((v1 || "").toString())) {
    const variables = (v1 || "")
      .toString()
      .match(pattern)
      .map((item) => ({
        key: item,
        value: (props.brothers || []).find((bro: any) => bro.key === item.replace(/[{}]/g, ""))?.value || ""
      }))

    console.log("variables:" + JSON.stringify(variables))
    value.value = v1
    variables.forEach((item: any) => {
      value.value = (value.value || "").toString().replace(item.key, (item.value as string) || "")
    })
    console.log("value.value:" + value.value)
  } else {
    value.value = v1
  }
}

watch(
  () => props.defaultValue,
  () => {
    updateValue()
  },
  {
    immediate: true // 必须要深度监听，否则初始值赋不进去
  }
)

watch(
  () => props.brothers,
  () => {
    updateValue()
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
