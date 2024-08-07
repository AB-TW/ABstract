<template>
  <div class="custom-tree-container">
    <el-tree
      v-if="!debugMode"
      :data="dataSource"
      :key="treeKey"
      node-key="id"
      default-expand-all
      :expand-on-click-node="false"
      :highlight-current="true"
      :indent="50"
    >
      <template #default="{ node, data }">
        <div class="custom-tree-node">
          <el-form-item
            label-width="auto"
            :label="node.level == 1 ? props.label || '配置' : data.name"
            :rules="dataItemRule(data)"
          >
            <CommonConfig
              :isViewModel="props.isViewModel"
              :default-value="getDefaultValue(data)"
              :max-length="data?.validator?.maxLength || Number.MAX_SAFE_INTEGER"
              :max-value="data?.validator?.max || Number.MAX_SAFE_INTEGER"
              :min-value="data?.validator?.min || 0"
              :data-type="data.dataType"
              :name="data.name"
              :choice="data.choice"
              :description="data.description"
              :brothers="node.parent?.data?.children"
              v-model="data.value"
              @change="(value) => updateTree(value, data)"
            />
          </el-form-item>
        </div>
        <div class="operation-container">
          <div class="right">
            <el-button
              :disabled="props.isViewModel"
              @click="addChildNode(node, data)"
              type="primary"
              icon="plus"
              v-if="showAddNextBtn(node, data)"
            >
              新增
            </el-button>
            <el-button
              style="margin-left: 8px"
              @click="remove(node)"
              type="warning"
              icon="delete"
              v-if="showDelBtn(node)"
              :disabled="props.isViewModel"
            />
          </div>
        </div>
      </template>
    </el-tree>
  </div>
</template>

<script lang="ts" setup>
import { computed, h, ref, watch } from "vue"
import type Node from "element-plus/es/components/tree/src/model/node"
import { ElNotification } from "element-plus"
import CommonConfig from "@/components/CommonConfig.vue"
import _ from "lodash"
import { convertConfigObject, getValue, mapDataToTemplate, transTempToTreeDataSource } from "@/utils"
import { Feature, TemplateDataType } from "@/domain/Features"

const props = defineProps({
  modelValue: {
    type: [Object, Array, String, Number, Boolean, null],
    required: true,
    defaultValue: null
  },
  feature: {
    type: Object,
    required: true
  },
  label: {
    type: String,
    required: false
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

const emit = defineEmits(["update:modelValue", "change"])

const featureConfigData = ref(props.modelValue)
featureConfigData.value = featureConfigData.value || null
const feature = ref(props.feature as Feature)

const dataSource = computed({
  get() {
    const result = [transTempToTreeDataSource(feature.value?.template)]
    if (!_.isNil(featureConfigData.value)) {
      // 如果返回的数据是一个对象：
      const templateInfo = mapDataToTemplate("", featureConfigData.value, feature.value?.template)
      return [templateInfo]
    }
    return result
  },
  set(dataSource) {
    featureConfigData.value = convertConfigObject(dataSource[0])
    emit("update:modelValue", featureConfigData.value)
  }
})
const count = ref(1)
const getDefaultValue = (value) => {
  if (value.dataType == TemplateDataType.GENERATED) {
    return value.defaultValue
  }

  return getValue(value)
}

const lastDataSource = ref(JSON.stringify(featureConfigData.value))

const updateTree = (value, data) => {
  console.log("updateTree")
  console.log(value)
  console.log(data)
  // data.value = value || null
  dataSource.value = [...dataSource.value]
  // featureConfigData.value = convertConfigObject(dataSource.value[0])
  console.log("JSON.stringify(featureConfigData.value)")
  console.log(JSON.stringify(featureConfigData.value))
  if (lastDataSource.value !== JSON.stringify(featureConfigData.value)) {
    treeKey.value += 1
    lastDataSource.value = JSON.stringify(featureConfigData.value)
  }
  console.log("lastDataSource.value")
  console.log(lastDataSource.value)
  console.log(treeKey.value)
}

// 这个不起作用，用于查看数据是否正确的发送消息
const showDelBtn = (node: any) => {
  const parentType = node?.parent?.data.dataType || null
  return parentType && parentType == TemplateDataType.ARRAY && node?.parent?.data.children?.length > 1
}
const showAddNextBtn = (node, child) => {
  return [TemplateDataType.ARRAY].includes(child.dataType)
}

interface Tree {
  id: number
  children?: Tree[]
}

const addChildNode = (node: any, data: Tree) => {
  console.log(node)
  console.log(data)
  count.value++
  if (node.level >= 5) {
    ElNotification({
      title: "提示",
      message: h("i", { style: "color: red" }, "层级最大支持5级")
    })
    return
  }
  const tempCloneObj = _.cloneDeep(node.data.children[0])
  tempCloneObj.id = count.value
  tempCloneObj.index = node.data.children.length - 1
  data.children.push(tempCloneObj)
  console.log(data)
}

const remove = (node: Node) => {
  console.log("node")
  console.log(node)
  console.log("node.parent?.childNodes")
  console.log(node.parent?.childNodes)
  const children = node.parent?.childNodes || []
  const datas = node.parent?.data?.children || []
  const index = children.findIndex((d) => d.id === node.id)
  datas.splice(index, 1)
}

const treeKey = ref(0)
const featureKey = ref("")

// watch(
//   () => treeKey.value,
//   () => {
//     featureConfigData.value = convertConfigObject(dataSource.value[0])
//     emit("update:modelValue", featureConfigData.value)
//   },
//   {
//     immediate: true // 必须要深度监听，否则初始值赋不进去
//   }
// )

watch(
  () => props.feature,
  (v1) => {
    feature.value = v1 as Feature
    featureKey.value = feature.value?.featureKey
  },
  {
    immediate: true // 必须要深度监听，否则初始值赋不进去
  }
)

const dataItemRule = (data: any) => {
  return [
    {
      required: data?.validator?.required || false,
      validator: () => {
        if (data?.validator?.required && !data.value) {
          return Promise.reject("该字段为必填字段")
        }
      },
      trigger: "submit"
    }
  ]
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
  margin-bottom: 5px !important;
}

.el-form-item__label-wrap {
  margin-left: 0 !important;
}

.el-select {
  width: 100% !important;
}
</style>
