<template>
  <div class="custom-tree-container">
    <div class="custom-tree-node">
      <el-form-item
        :key="key"
        :label-width="props.parent ? '80px' : '140px'"
        :label="props.label || template.name"
        :prop="template.key ? props.parentProp + '.' + template.key : props.parentProp"
        :rules="dataItemRule(template)"
      >
        <div v-if="template?.dataType == TemplateDataType.OBJECT && featureConfigData != undefined">
          <div v-for="item in template.items" :key="item.key">
            <FeatureConfigData
              v-model="featureConfigData[item.key]"
              :template="item"
              :parent="featureConfigData"
              :parent-prop="props.parentProp"
              :index="0"
              :isViewModel="props.isViewModel"
              :debug-mode="props.debugMode"
              @update:modelValue="updateData"
            />
          </div>
        </div>
        <div v-else-if="template?.dataType == TemplateDataType.ARRAY">
          <div v-for="(item, index) in featureConfigData" :key="template.key + index">
            <FeatureConfigData
              :key="count++"
              v-model="featureConfigData[index]"
              :template="template.items[0]"
              :index="index"
              :parent-prop="(props.parentProp || 'data') + '.' + template.key + '.' + index"
              :parent="item"
              :isViewModel="props.isViewModel"
              :debug-mode="props.debugMode"
              @update:modelValue="updateData"
            />
            <div class="operation-container">
              <div class="right">
                <el-button
                  style="margin-left: 8px"
                  @click="remove(index)"
                  type="warning"
                  icon="delete"
                  v-if="(featureConfigData as any[])?.length > 1 "
                  :disabled="props.isViewModel"
                />
              </div>
            </div>
          </div>
          <div class="operation-container">
            <div class="right">
              <el-button :disabled="props.isViewModel" @click="addChildNode()" type="primary" icon="plus" />
            </div>
          </div>
        </div>
        <div v-else>
          <CommonConfig
            :isViewModel="props.isViewModel"
            :default-value="getDefaultValue(template)"
            :max-length="template?.validator?.maxLength || Number.MAX_SAFE_INTEGER"
            :max-value="template?.validator?.max || Number.MAX_SAFE_INTEGER"
            :min-value="template?.validator?.min || 0"
            :data-type="template.dataType"
            :string-type="template.stringType"
            :choice="template.choice"
            :description="template.description"
            :parent="props.parent"
            :file="template.file"
            v-model="featureConfigData"
            @update:modelValue="updateData"
          />
        </div>
      </el-form-item>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch } from "vue"
import CommonConfig from "@/components/CommonConfig.vue"
import _ from "lodash"
import { TemplateDataType, FeatureTemplate, getTemplateDefaultValue, StringType } from "@/domain/Features"

const props = defineProps({
  modelValue: {
    type: [Object, Array, String, Number, Boolean, null],
    required: true,
    defaultValue: null
  },
  parent: {
    type: [Object, Array, String, Number, Boolean, null],
    required: true,
    defaultValue: null
  },
  template: {
    type: Object,
    required: true
  },
  label: {
    type: String,
    required: false
  },
  parentProp: {
    type: String,
    required: false,
    defaultValue: "data"
  },
  index: {
    type: Number,
    required: false
  },
  isViewModel: {
    type: Boolean,
    required: true
  },
  debugMode: {
    type: Boolean,
    required: false
  }
})

const count = ref(1)
const emit = defineEmits(["update:modelValue", "change"])
const featureConfigData = ref(props.modelValue)
const template = ref(props.template as FeatureTemplate)

console.log("featureConfigData.value:")
console.log(template.value)
console.log(featureConfigData.value)

watch(
  () => featureConfigData.value,
  (curVal) => {
    emit("update:modelValue", curVal)
    console.log('emit("update:modelValue", curVal)')
    console.log(curVal)
  },
  {
    // immediate: true
  }
)

watch(
  () => props.modelValue,
  (cur, _) => (featureConfigData.value = cur)
)

const key = computed(() => {
  if (template.value.dataType == TemplateDataType.OBJECT) {
    return (template.value.items || [])
      .map((item) => {
        if (item.dataType != TemplateDataType.GENERATED) {
          return _.get(featureConfigData.value, item.key, "") || ""
        }
        return ""
      })
      .join(",")
  }
  return template.value.key || ""
})

const getDefaultValue = (template) => {
  return template.defaultValue
}

const addChildNode = () => {
  if (!featureConfigData.value || !_.isArray(featureConfigData.value)) {
    featureConfigData.value = []
  }
  featureConfigData.value.push(getTemplateDefaultValue(props.template.items[0]))
}

const remove = (index: number) => {
  console.log("index:" + index)
  console.log(featureConfigData.value)
  ;(featureConfigData.value as any[]).splice(index, 1)
}

const updateData = (v1) => {
  console.log("updateData")
  console.log(v1)
  emit("update:modelValue", featureConfigData.value)
}

watch(
  () => props.template,
  (v1) => {
    template.value = v1 as FeatureTemplate
  },
  {
    immediate: true // 必须要深度监听，否则初始值赋不进去
  }
)

const dataItemRule = (data: any) => {
  const rules = [
    {
      required: data?.validator?.required || false,
      message: "该字段为必填字段",
      trigger: "submit"
    }
  ]
  if (data.dataType == TemplateDataType.STRING) {
    switch (data.stringType) {
      case StringType.URL:
        rules.push({ type: "url", message: "请输入正确的URL地址", trigger: "submit" })
        break
      case StringType.EMAIL:
        rules.push({ type: "email", message: "请输入正确的邮箱地址", trigger: "submit" })
        break
      case StringType.PHONE:
        rules.push({ pattern: /^1[3456789]\d{9}$/, message: "请输入正确的手机号码", trigger: "submit" })
        break
    }
  }

  return rules
}
</script>
<style lang="scss">
.custom-tree-container {
  width: 100% !important;

  .el-tree-node__content {
    height: initial !important;
    padding-top: 10px !important;
    width: 100%;
  }

  .el-form-item {
    width: 100% !important;
  }

  .el-form-item__error {
    z-index: 100 !important;
  }

  .validator-container {
    width: 100% !important;

    .el-form-item__label {
      justify-content: flex-start !important;
      min-width: 80px !important;
    }
  }
}
</style>
<style lang="scss" scoped>
.custom-tree-node {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: flex-start;
  font-size: 14px;
}

.operation-container {
  display: flex;
  width: 100%;
  justify-content: space-between;

  .left {
    flex: 1;
  }
}

.el-form-item {
  margin-bottom: 10px !important;
}

.el-form-item__label-wrap {
  margin-left: 0 !important;
}

.el-select {
  width: 100% !important;
}
</style>
