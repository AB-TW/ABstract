<template>
  <el-form
    class="filter-condition-container"
    :model="filterCondition"
    :rules="filterConditionRules"
    ref="filterConditionFormRef"
    label-width="140px"
    :disabled="props.disabled"
    :scroll-to-error="true"
  >
    <el-form-item label="灰度发布比例" prop="grayReleaseCondition.releasePercentage">
      <el-input-number
        v-model="filterCondition.grayReleaseCondition.releasePercentage"
        :min="0"
        :max="100"
        :step="10"
        controls-position="right"
      />
    </el-form-item>

    <el-form-item label="自定义字段过滤">
      <el-form-item
        :prop="`attributeConditions.${index}`"
        :key="index"
        :rules="{ validator: validateCustomAttributeConditions, trigger: 'blur' }"
        v-for="(conditionItem, index) in filterCondition.attributeConditions"
      >
        <div class="custom-attribute-condition-item">
          <div class="custom-attribute-label" style="margin: 0">
            <el-popover placement="top-start" :width="200" trigger="click">
              <template #reference>
                <span class="customer-attribute-name-area">
                  {{ conditionItem.name }}
                </span>
              </template>
              <template #default>
                <el-input
                  v-model="conditionItem.name"
                  maxlength="50"
                  placeholder="请输入条件名"
                  show-word-limit
                  type="text"
                />
              </template>
            </el-popover>
            <el-icon
              v-if="!props.disabled"
              class="delete-custom-attribute-icon"
              @click="() => filterCondition.attributeConditions?.splice(index, 1)"
            >
              <Delete />
            </el-icon>
          </div>
          <el-input
            v-model="conditionItem.key"
            maxlength="100"
            placeholder="请输入要过滤的键"
            show-word-limit
            type="text"
          />

          <el-select
            v-model="conditionItem.operator"
            style="width: 100%"
            placeholder="Select One Operator"
            @change="conditionOperatorChanged(conditionItem.operator, conditionItem)"
          >
            <el-option
              v-for="operator in supportOperatorList"
              :key="operator.value"
              :label="operator.label"
              :value="operator.value"
            />
          </el-select>

          <el-input
            v-show="![CompareOperator.IN, CompareOperator.NOT_IN].includes(conditionItem.operator)"
            v-model="conditionItem.value"
            maxlength="10"
            placeholder="请输入要匹配的值"
            show-word-limit
            type="text"
          />

          <div v-show="[CompareOperator.IN, CompareOperator.NOT_IN].includes(conditionItem.operator)">
            <div class="tags" :style="conditionItem.items?.length > 0 ? 'height:35px' : ''">
              <el-tag
                class="tag"
                v-for="(tag, index) in conditionItem.items"
                :key="index"
                closable
                :disable-transitions="false"
                @close="removeTagValue(conditionItem, index)"
              >
                {{ tag }}
              </el-tag>
            </div>
            <el-input
              style="max-width: 100%"
              v-if="tagInputVisible"
              v-model="tagInputValue"
              @keyup.enter="addTagValue(conditionItem)"
              @blur="addTagValue(conditionItem)"
              :validate-event="false"
              placeholder="请输入需要包含的值"
            />
            <div v-else>
              <el-button class="add-tag-value-button" @click="changeInputVisible(true)"> + Value</el-button>
              <el-upload
                class="add-tag-value-button"
                v-if="!props.disabled"
                style="float: right"
                action=""
                :before-upload="(file) => updateWhitelist(file, conditionItem)"
                :show-file-list="false"
                accept=".txt,.csv"
              >
                <el-button>
                  <el-icon>
                    <Upload />
                  </el-icon>
                  Import File
                </el-button>
              </el-upload>
            </div>
          </div>
        </div>
      </el-form-item>
      <el-button class="add-custom-attribute-button" plain icon="Plus" @click="addCustomAttributeItem" />
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import _ from "lodash"
import lodash from "lodash"
import { reactive, ref, watch } from "vue"
import type { PropType } from "vue"
import {
  CompareOperator,
  CustomAttributeCondition,
  defaultFilterCondition,
  FilterCondition,
  isListCompareOperator,
  isValidCustomAttributeCondition
} from "@/domain/FilterConditions"
import { Delete } from "@element-plus/icons-vue"
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormRules } from "element-plus"

defineOptions({
  name: "FilterCondition"
})
const props = defineProps({
  modelValue: {
    type: [Object, null] as PropType<FilterCondition | null>,
    required: true,
    default: () => {
      return defaultFilterCondition()
    }
  },
  disabled: {
    type: Boolean,
    required: false,
    default: false
  }
})
const emit = defineEmits(["update:modelValue"])

// code about filter condition
const normalizeFilterCondition = (filterCondition?: FilterCondition | null): FilterCondition => {
  const defaultValue = defaultFilterCondition()
  return {
    ...defaultValue,
    ...(filterCondition ?? {}),
    grayReleaseCondition: {
      ...defaultValue.grayReleaseCondition,
      ...(filterCondition?.grayReleaseCondition ?? {})
    },
    attributeConditions: filterCondition?.attributeConditions ?? defaultValue.attributeConditions
  }
}
const filterCondition = reactive<FilterCondition>(normalizeFilterCondition(props.modelValue))

// watch reactive object then assign parent bind object
watch<FilterCondition>(filterCondition, (cur, _) => emit("update:modelValue", cur))
const validateCustomAttributeConditions = (rule: any, customAttribute: CustomAttributeCondition, callback: any) => {
  if (!isValidCustomAttributeCondition(customAttribute)) {
    callback(new Error("请检查键或值是否填写～"))
  } else {
    callback()
  }
}
watch(
  () => props.modelValue,
  (cur, _) => lodash.assign(filterCondition, normalizeFilterCondition(cur))
)
const filterConditionRules: FormRules = {
  "grayReleaseCondition.releasePercentage": [{ required: true, message: "灰度发布比例不可为空", trigger: "blur" }]
}
const supportOperatorList = _.map(CompareOperator, (operator: string) => ({
  label: operator,
  value: operator
}))
const addCustomAttributeItem = () => {
  filterCondition.attributeConditions = filterCondition?.attributeConditions || []
  filterCondition.attributeConditions?.push({
    name: "条件" + (filterCondition.attributeConditions.length + 1),
    key: "",
    operator: CompareOperator.EQUALS,
    value: "",
    items: []
  })
}
const conditionOperatorChanged = (val: CompareOperator, customAttribute: CustomAttributeCondition) => {
  if (isListCompareOperator(val)) {
    customAttribute.items = []
  } else {
    customAttribute.value = ""
  }
}

// code about tag when operator is in or not_in
const tagInputVisible = ref<boolean>(false)
const tagInputValue = ref<string>("")
const changeInputVisible = (showOrHidden: boolean) => {
  tagInputVisible.value = showOrHidden
}

const addTagValue = (customAttribute: CustomAttributeCondition) => {
  console.log("addTagValue")
  console.log(customAttribute.items)
  console.log(tagInputValue.value)
  if (_.isEmpty(tagInputValue.value)) {
    changeInputVisible(false)
    return
  }
  customAttribute.items = customAttribute.items || []
  customAttribute.items.push(tagInputValue.value)
  tagInputValue.value = ""
}

const removeTagValue = (customAttribute: CustomAttributeCondition, index: number) => {
  if (_.isArray(customAttribute.items) && customAttribute.items.length >= index + 1) {
    customAttribute.items.splice(index, 1)
  }
}

const updateWhitelist = (file, customAttribute: CustomAttributeCondition) => {
  const reader = new FileReader()
  reader.onload = (event) => {
    try {
      customAttribute.items = (event.target.result as string).split("\n").filter((item) => item)
    } catch (error) {
      ElMessage.error("文件解析失败，请上传正确的文本文件,并保证每行一个值")
    }
  }
  reader.readAsText(file)

  // 阻止文件自动上传
  return false
}
</script>

<style scoped lang="scss">
.filter-condition-container {
  .label {
    color: rgba(0, 0, 0, 0.85);
    font-size: 14px;
  }

  .custom-attribute-condition-item {
    width: 300px;
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 20px;

    &:hover {
      background-color: #f5f7fa;
    }

    .custom-attribute-label {
      color: rgba(0, 0, 0, 0.85);
      font-size: 14px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .customer-attribute-name-area {
        cursor: pointer;
      }

      .delete-custom-attribute-icon {
        cursor: pointer;

        &:hover {
          color: #ef6868;
        }
      }
    }

    :deep(+ .el-form-item__error) {
      margin-left: 20px !important;
    }
  }

  .add-custom-attribute-button {
    margin-left: 20px;
    margin-top: 10px;
    border-style: dashed;
  }

  .add-tag-value-button {
    //border-style: dashed;
    width: 48%;
  }

  .tags {
    max-width: 100%;
    display: flex;
    gap: 5px;
    overflow: scroll;
  }
}
</style>
