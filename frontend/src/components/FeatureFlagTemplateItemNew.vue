<template>
  <div class="custom-tree-container">
    <el-tree
      v-if="!debugMode"
      :data="dataSource"
      node-key="id"
      default-expand-all
      :expand-on-click-node="false"
      :highlight-current="true"
      :indent="50"
    >
      <template #default="{ node, data }">
        <div class="custom-tree-node">
          <el-form-item
            label="Key"
            prop="key"
            :rules="getKeyRules(data.key)"
            label-width="auto"
            v-if="showKey(node, data)"
          >
            <el-input ref="keyInput" v-model="data.key" @change="updateData" />
          </el-form-item>
          <el-form-item
            label="名称"
            :prop="name"
            :rules="getNameRules(data.name)"
            label-width="auto"
            v-if="showKey(node, data)"
          >
            <el-input v-model="data.name" @change="updateData" />
          </el-form-item>
          <el-form-item label="描述" prop="description" label-width="auto" v-if="showKey(node, data)">
            <el-input v-model="data.description" @change="updateData" />
          </el-form-item>
          <el-form-item label="类型" prop="dataType" label-width="auto">
            <el-select v-model="data.dataType" @change="handleDataTypeChange(node, data)">
              <el-option v-for="item in Object.keys(TemplateDataType)" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="初值" prop="defaultValue" label-width="auto" v-if="showDefaultValue(node, data)">
            <el-input v-model="data.defaultValue" :type="data.dataType == 'NUMBER' ? 'number':'string'" @change="updateData"/>
          </el-form-item>
          <el-form-item
            label="校验"
            prop="validator"
            label-width="auto"
            v-if="data.dataType != 'ARRAY' && data.dataType != 'OBJECT' && data.dataType != 'BOOLEAN'"
          >
            <el-form
              :model="data.validator"
              :rules="formValidatorRules"
              ref="formValidator"
              label-width="140px"
              :disabled="isViewModel"
              class="validator-container"
            >
              <el-form-item label="必选" prop="required" label-width="auto">
                <el-switch v-model="data.validator['required']" />
              </el-form-item>
              <el-form-item label="最大长度" prop="maxLength" label-width="auto" v-if="data.dataType == 'STRING'">
                <el-input v-model="data.validator['maxLength']" type="number" />
              </el-form-item>
              <el-form-item label="最小值" prop="min" label-width="auto" v-if="data.dataType == 'NUMBER'">
                <el-input v-model="data.validator['min']" type="number" />
              </el-form-item>
              <el-form-item label="最大值" prop="max" label-width="auto" v-if="data.dataType == 'NUMBER'">
                <el-input v-model="data.validator['max']" type="number" />
              </el-form-item>
              <el-form-item label="模式" prop="pattern" label-width="auto" v-if="data.dataType == 'STRING'">
                <el-input v-model="data.validator['pattern']" />
              </el-form-item>
            </el-form>
          </el-form-item>
          <div class="operation-container">
            <div class="left">{{ data.children.length > 0 ? "├─" : "" }}</div>
            <div class="right" v-if="showAddNextBtn(node, data)">
              <el-button @click="addChildNode(node, data)" type="primary" icon="plus">
                新增节点(层级:{{ node.level }})
              </el-button>
              <el-button
                style="margin-left: 8px"
                @click="remove(node, data)"
                type="danger"
                icon="delete"
                v-if="showDelBtn(node)"
              >
                删除当前节点
              </el-button>
            </div>
          </div>
        </div>
      </template>
    </el-tree>
    <div v-else>this is a UI Test</div>
  </div>
</template>

<script lang="ts" setup>
import { computed, h, ref } from "vue"
import type Node from "element-plus/es/components/tree/src/model/node"
import { TemplateDataType } from "@/domain/FeatureFlags"
import { ElNotification } from "element-plus"

const props = defineProps({
  index: {
    type: Number,
    required: true
  },
  dataSource: {
    type: Array,
    required: true
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
const formValidatorRules = [] as any
const getNameRules = (content: string) => [
  {
    validator: () => {
      if (!content) {
        return Promise.reject("请输入Key")
      }
      if (content.length < 1 || content.length > 50) {
        return Promise.reject("长度在 1 到 50 个字符")
      }

      return Promise.resolve()
    },
    trigger: "blur"
  }
]
const getKeyRules = (content: any) => {
  return [
    {
      validator: () => {
        if (!content) {
          return Promise.reject("请输入Key")
        }

        if (!/^[a-zA-Z][a-zA-Z0-9_]*$/g.test(content)) {
          return Promise.reject("由字母、数字、下划线组成，且以字母开头")
        }

        if (content.length < 1 || content.length > 50) {
          return Promise.reject("长度在 1 到 50 个字符")
        }
        return Promise.resolve()
      },
      trigger: "blur"
    }
  ]
}
const dataSource = ref(props.dataSource)
const isViewModel = ref(props.isViewModel)
const emit = defineEmits(["change"])
// 这个不起作用，用于查看数据是否正确的发送消息
const updateData = () => {
  emit("change", dataSource.value)
}
const showDelBtn = (node: any) => {
  return node.level > 1 || (node.level == 1 && node.parent.childNodes.length > 1)
}
const shouldShowSubItems = (data: any) => {
  return [TemplateDataType.OBJECT, TemplateDataType.ARRAY].includes(data.dataType)
}
const handleDataTypeChange = (node: any, data: any) => {
  if (shouldShowSubItems(data)) {
    if (!data.children || data.children.length == 0) {
      addChildNode(node, data)
    }
  } else {
    removeChildren(node, data)
  }
  switch (data.dataType) {
    case TemplateDataType.NUMBER:
      data.defaultValue = 0
      break
    case TemplateDataType.BOOLEAN:
      data.defaultValue = false
      break
    default:
      data.defaultValue = ""
      break
  }
  dataSource.value = [...dataSource.value]
  updateData()
}

interface Tree {
  id: number
  children?: Tree[]
}

let id = 1000

const addChildNode = (node: any, data: Tree) => {
  if (node.level >= 5) {
    ElNotification({
      title: "提示",
      message: h("i", { style: "color: red" }, "层级最大支持5级")
    })
    return
  }

  const newChild = {
    id: id++,
    name: "",
    description: "",
    key: "",
    dataType: "BOOLEAN",
    defaultValue: false,
    children: [],
    validator: {
      min: 0,
      max: 0,
      maxLength: 0,
      required: false,
      pattern: ""
    }
  }
  if (!data.children) {
    data.children = []
  }
  data.children.push(newChild)
  dataSource.value = [...dataSource.value]
}

const remove = (node: Node, data: Tree) => {
  const parent = node.parent
  const children: Tree[] = parent.data.children || parent.data
  const index = children.findIndex((d) => d.id === data.id)
  children.splice(index, 1)
  dataSource.value = [...dataSource.value]
}

const removeChildren = (node: any, data: any) => {
  data.children = []
  dataSource.value = [...dataSource.value]
}

const showAddNextBtn = (node, child) => {
  return child.dataType == "OBJECT"
}

const showKey = (node, child) => {
  const parentType = node?.parent?.data.dataType || null
  console.log(parentType, child.dataType)
  return (!!parentType && ["ARRAY", "OBJECT"].includes(child.dataType)) || parentType == "OBJECT"
}
const showDefaultValue = (parent, child) => {
  return !["ARRAY", "OBJECT"].includes(child.dataType)
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
  margin-left: 0px !important;
}

.el-select {
  width: 100% !important;
}
</style>
