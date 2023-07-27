<template>
  <div class="custom-tree-container">
    <el-tree
      v-if="!debugMode"
      :data="dataSource"
      node-key="id"
      default-expand-all
      :expand-on-click-node="false"
      :highlight-current="true"
      indent="50"
    >
      <template #default="{ node, data }">
        <div class="custom-tree-node">
          <el-form-item :label="name" :prop="name" label-width="auto">
            <CommonConfig
              :default-value="getDataValue(data)"
              :max-length="data?.validator?.maxLength || Number.MAX_SAFE_INTEGER"
              :max-value="data?.validator?.max || Number.MAX_SAFE_INTEGER"
              :min-value="data?.validator?.min || 0"
              :data-type="data.dataType"
              :description="data.description"
              v-model="data.value"
            />
          </el-form-item>
        </div>
        <div class="operation-container">
          <div class="right">
            <el-button @click="addChildNode(node, data)" type="primary" icon="plus" v-if="showAddNextBtn(node, data)">
              新增 {{ node.dataType }}--{{ node.children }}-{{ data.dataType }}
            </el-button>
            <el-button
              style="margin-left: 8px"
              @click="remove(node, data)"
              type="danger"
              icon="delete"
              v-if="showDelBtn(node, data)"
            >
              删除{{ data.id }}
            </el-button>
          </div>
        </div>
      </template>
    </el-tree>
  </div>
</template>

<script lang="ts" setup>
import { h, ref } from "vue"
import type Node from "element-plus/es/components/tree/src/model/node"
import { ElNotification } from "element-plus"
import CommonConfig from "@/components/CommonConfig.vue"
import _ from "lodash"
import { getValue } from "@/utils"
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
const count = ref(1)
const getDataValue = (value) => {
  return getValue(value)
}
const dataSource = ref(props.dataSource)
const emit = defineEmits(["change"])

// 这个不起作用，用于查看数据是否正确的发送消息
const showDelBtn = (node: any, data) => {
  const parentType = node?.parent?.data.dataType || null
  if (parentType != "OBJECT" && ["NUMBER", "BOOLEAN", "STRING"].includes(data.dataType)) {
    return true
  } else if (
    parentType &&
    data.dataType == "ARRAY" &&
    data.children.every((item) => ["NUMBER", "BOOLEAN", "STRING"].includes(item.dataType))
  ) {
    return true
  }
  return false
}
const showAddNextBtn = (node, child) => {
  // const parentType = node?.parent?.data.dataType || null
  return ["ARRAY"].includes(child.dataType)
}
interface Tree {
  id: number
  children?: Tree[]
}

const id = 1000

const addChildNode = (node: any, data: Tree) => {
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
  data.children.push(tempCloneObj)
  dataSource.value = [...dataSource.value]
}

const remove = (node: Node, data: Tree) => {
  const parent = node.parent
  const children: Tree[] = parent.data.children || parent.data
  const index = children.findIndex((d) => d.id === data.id)
  children.splice(index, 1)
  dataSource.value = [...dataSource.value]
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
