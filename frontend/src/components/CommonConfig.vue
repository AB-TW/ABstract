<template>
  <div class="common-container">
    <div>
      <div v-if="dataType == TemplateDataType.STRING">
        <el-input v-model="value" :disabled="props.isViewModel" type="text" :maxlength="maxLength" @blur="onInput" />
      </div>
      <div v-if="dataType == TemplateDataType.GENERATED">
        <el-input v-model="value" :disabled="true" type="text" @blur="onInput" />
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
        <el-select v-model="value" :loading="selectLoading" placeholder="Select" style="width: 240px" @blur="onInput">
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
  modelValue: {
    type: [String, Number, Boolean, null],
    required: true
  },
  dataType: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: false
  },
  defaultValue: {
    type: [String, Number, Boolean, null],
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
  parent: {
    type: [Object, Array, String, Number, Boolean, null],
    required: false
  }
})
const emit = defineEmits(["update:modelValue", "change"])
// 绑定控件的值
const value = ref(props.modelValue)
const defaultValue = ref(props.defaultValue)
// const latestValue = ref(props.modelValue)

const onInput = () => {
  emit("update:modelValue", value.value)
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

if (props.dataType == TemplateDataType.SELECT) {
  if (props.choice?.type === ChoiceType.CONTENT) {
    choiceItems.value = props.choice?.items || []
  } else if (props.choice?.type === ChoiceType.INTERFACE) {
    if (!featureConfigLoaded.value) {
      loadFeatureConfigs(props.choice?.featureKey)
    }
  }
}

const updateValue = (v1) => {
  if (props.dataType != TemplateDataType.GENERATED) {
    value.value = v1
    return
  }

  const pattern = /\{.*?}/
  if (pattern.test((defaultValue.value || "").toString())) {
    const variables = (defaultValue.value || "")
      .toString()
      .match(pattern)
      .map((item) => ({
        key: item,
        value: _.isObject(props.parent) && props.parent ? _.get(props.parent, item.replace(/[{}]/g, "")) : ""
      }))

    console.log("variables:" + JSON.stringify(variables))
    value.value = defaultValue.value
    variables.forEach((item: any) => {
      value.value = (value.value || "").toString().replace(item.key, (item.value as string) || "")
    })
  } else {
    value.value = v1
  }
  console.log("value.value:" + value.value)
}
updateValue(value.value)
// watch(
//   () => props.modelValue,
//   (v1) => {
//     updateValue(v1)
//   }
// )

// watch(
//   () => value.value,
//   (curValue, lastValue) => {
//     console.log("watch value.value")
//     console.log(curValue)
//     console.log(lastValue)
//     if (curValue != lastValue) {
//       emit("update:modelValue", curValue)
//       emit("change", curValue)
//     }
//   }
// )

watch(
  () => props.defaultValue,
  (v1) => {
    defaultValue.value = v1
    updateValue(value.value)
  }
)

watch(
  () => props.parent,
  () => {
    updateValue(value.value)
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
