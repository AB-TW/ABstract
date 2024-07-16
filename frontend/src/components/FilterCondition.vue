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
            v-show="_.isString(conditionItem.value)"
            v-model="conditionItem.value"
            maxlength="10"
            placeholder="请输入要匹配的值"
            show-word-limit
            type="text"
          />

          <div v-show="_.isArray(conditionItem.value)">
            <div class="tags" :style="conditionItem.value.length > 0 ? 'height:35px' : ''">
              <el-tag
                class="tag"
                v-for="(tag, index) in conditionItem.value"
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
            <el-button class="add-tag-value-button" v-else @click="changeInputVisible(true)"> + Value</el-button>
          </div>
        </div>
      </el-form-item>
      <el-button class="add-custom-attribute-button" plain icon="Plus" @click="addCustomAttributeItem" />
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import _ from "lodash"
import { reactive, ref, watch } from "vue"
import {
  CompareOperator,
  CustomAttributeCondition,
  defaultFilterCondition,
  FilterCondition
} from "@/domain/FilterConditions"
import { Delete } from "@element-plus/icons-vue"
import { ElForm, ElFormItem, ElInput, FormRules } from "element-plus"
import lodash from "lodash"

defineOptions({
  name: "FilterCondition"
})
const props = defineProps({
  modelValue: {
    type: Object,
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
const filterCondition = reactive<FilterCondition>(props.modelValue)

// watch reactive object then assign parent bind object
watch<FilterCondition>(filterCondition, (cur, _) => emit("update:modelValue", cur))
const validateCustomAttributeConditions = (rule: any, customAttribute: CustomAttributeCondition, callback: any) => {
  if (_.isEmpty(customAttribute.value) || _.isEmpty(customAttribute.key)) {
    callback(new Error("请检查键或值是否填写～"))
  } else {
    callback()
  }
}
watch(
  () => props.modelValue,
  (cur, _) => lodash.assign(filterCondition, cur)
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
    value: ""
  })
}
const conditionOperatorChanged = (val: CompareOperator, customAttribute: CustomAttributeCondition) => {
  if (_.includes([CompareOperator.IN, CompareOperator.NOT_IN], val)) {
    if (!_.isArray(customAttribute.value)) {
      customAttribute.value = []
    }
  } else {
    if (!_.isString(customAttribute.value)) {
      customAttribute.value = ""
    }
  }
}

// code about tag when operator is in or not_in
const tagInputVisible = ref<boolean>(false)
const tagInputValue = ref<string>("")
const changeInputVisible = (showOrHidden: boolean) => {
  tagInputVisible.value = showOrHidden
}
const addTagValue = (customAttribute: CustomAttributeCondition) => {
  if (_.isEmpty(tagInputValue.value)) {
    changeInputVisible(false)
    return
  }
  if (_.isArray(customAttribute.value)) {
    customAttribute.value?.push(tagInputValue.value)
  }
  tagInputValue.value = ""
}
const removeTagValue = (customAttribute: CustomAttributeCondition, index: number) => {
  if (_.isArray(customAttribute.value) && customAttribute.value.length >= index + 1) {
    customAttribute.value.splice(index, 1)
  }
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
    border-style: dashed;
    width: 100%;
  }

  .tags {
    max-width: 100%;
    display: flex;
    gap: 5px;
    overflow: scroll;
  }
}
</style>
