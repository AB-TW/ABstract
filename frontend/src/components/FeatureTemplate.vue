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
            prop="name"
            :rules="getNameRules(data.name)"
            label-width="auto"
            v-if="showKey(node, data)"
          >
            <el-input v-model="data.name" @change="updateData" />
          </el-form-item>
          <el-form-item label="描述" prop="description" label-width="auto" v-if="showKey(node, data)">
            <el-input v-model="data.description" @change="updateData" />
          </el-form-item>
          <el-form-item label="数据类型" prop="dataType" label-width="auto">
            <el-select v-model="data.dataType" @change="handleDataTypeChange(node, data)">
              <el-option v-for="item in Object.keys(TemplateDataType)" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <FeatureTemplateChoice
            v-model="data.choice"
            :disabled="isViewModel"
            v-if="data.dataType == TemplateDataType.SELECT"
          />
          <el-form-item prop="file" label-width="auto" v-if="data.dataType == TemplateDataType.FILE">
            <FeatureTemplateFile v-model="data.file" :disabled="isViewModel" parent-prop="file" />
          </el-form-item>
          <el-form-item
            label="控件类型"
            prop="stringType"
            label-width="auto"
            v-if="data.dataType == TemplateDataType.STRING"
          >
            <el-select v-model="data.stringType" :disabled="isViewModel">
              <el-option v-for="item in Object.keys(StringType)" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="默认值" prop="defaultValue" label-width="auto" v-if="showDefaultValue(node, data)">
            <el-input
              v-if="data.dataType == TemplateDataType.GENERATED"
              v-model="data.defaultValue"
              :placeholder="defaultValuePlaceholder(data.dataType)"
              :type="data.dataType == TemplateDataType.NUMBER ? 'number' : 'string'"
              @change="updateData"
            />
            <CommonConfig
              v-else
              :isViewModel="props.isViewModel"
              :default-value="null"
              :max-length="data?.validator?.maxLength || Number.MAX_SAFE_INTEGER"
              :max-value="data?.validator?.max || Number.MAX_SAFE_INTEGER"
              :min-value="data?.validator?.min || 0"
              :data-type="data.dataType"
              :string-type="data.stringType"
              :choice="data.choice"
              :description="data.description"
              :parent="null"
              :file="data.file"
              v-model="data.defaultValue"
              @update:modelValue="updateData"
            />
          </el-form-item>
          <el-form-item label="是否主键字段" prop="keyParam" label-width="auto" v-if="showKeyParam(node, data)">
            <el-switch v-model="data.keyParam" />
          </el-form-item>
          <el-form-item
            label="校验"
            prop="validator"
            label-width="auto"
            v-if="
              data.dataType != TemplateDataType.ARRAY &&
              data.dataType != TemplateDataType.OBJECT &&
              data.dataType != TemplateDataType.BOOLEAN &&
              data.dataType != TemplateDataType.GENERATED
            "
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
              <el-form-item
                label="最大长度"
                prop="maxLength"
                label-width="auto"
                v-if="data.dataType == TemplateDataType.STRING"
              >
                <el-input-number v-model="data.validator['maxLength']" />
              </el-form-item>
              <el-form-item
                label="最小值"
                prop="min"
                label-width="auto"
                v-if="data.dataType == TemplateDataType.NUMBER"
              >
                <el-input-number v-model="data.validator['min']" :precision="4" :step="1" />
              </el-form-item>
              <el-form-item
                label="最大值"
                prop="max"
                label-width="auto"
                v-if="data.dataType == TemplateDataType.NUMBER"
              >
                <el-input-number v-model="data.validator['max']" :precision="4" :step="1" />
              </el-form-item>
              <el-form-item
                label="模式"
                prop="pattern"
                label-width="auto"
                v-if="data.dataType == TemplateDataType.STRING"
              >
                <el-input v-model="data.validator['pattern']" />
              </el-form-item>
            </el-form>
          </el-form-item>
          <div class="operation-container">
            <div class="left">{{ data.children.length > 0 ? "├─" : "" }}</div>
            <div class="right">
              <el-button @click="addChildNode(node, data)" type="primary" icon="plus" v-if="showAddNextBtn(node, data)">
                添加属性(层级:{{ node.level }})
              </el-button>
              <el-button
                style="margin-left: 8px"
                @click="remove(node)"
                type="danger"
                icon="delete"
                v-if="showDelBtn(node)"
              >
                删除当前节点
              </el-button>
            </div>
          </div>
          <el-divider v-if="showDelBtn(node)" />
        </div>
      </template>
    </el-tree>
    <div v-else>this is a UI Test</div>
  </div>
</template>

<script lang="ts" setup>
import { computed, h, ref } from "vue"
import type Node from "element-plus/es/components/tree/src/model/node"
import { ChoiceType, FileType, StringType, TemplateDataType } from "@/domain/Features"
import { ElNotification } from "element-plus"
import FeatureTemplateChoice from "@/components/FeatureTemplateChoice.vue"
import FeatureTemplateFile from "@/components/FeatureTemplateFile.vue"
import CommonConfig from "@/components/CommonConfig.vue"

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
    required: true,
    default: false
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
        return Promise.reject("请输入名称")
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
      required: true,
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
  return node.parent.childNodes.length > 1
}
const shouldShowSubItems = (data: any) => {
  return [TemplateDataType.OBJECT, TemplateDataType.ARRAY].includes(data.dataType)
}
const handleDataTypeChange = (node: any, data: any) => {
  if (shouldShowSubItems(data)) {
    if (data.dataType == TemplateDataType.ARRAY) {
      removeChildren(node, data)
    }

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
    case TemplateDataType.SELECT:
      data.choice = data.choice || { type: ChoiceType.CONTENT, featureKey: "", items: [] }
      break
    case TemplateDataType.FILE:
      data.file = data.file || { type: FileType.IMAGE, sizeLimit: 1024, fileExtensions: [] }
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
    dataType: TemplateDataType.BOOLEAN,
    defaultValue: false,
    choice: { type: ChoiceType.CONTENT, items: [], featureKey: "" },
    file: { type: FileType.IMAGE, sizeLimit: 1024, fileExtensions: [] },
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

const remove = (node: Node) => {
  const children = node.parent?.childNodes || []
  const datas = node.parent?.data?.children || []
  const index = children.findIndex((d) => d.id === node.id)
  datas.splice(index, 1)
  dataSource.value = [...dataSource.value]
}

const removeChildren = (node: any, data: any) => {
  data.children = []
  dataSource.value = [...dataSource.value]
}

const showAddNextBtn = (node, child) => {
  return child.dataType == TemplateDataType.OBJECT
}

const showKey = (node, child) => {
  const parentType = node?.parent?.data.dataType || null
  console.log(parentType, child.dataType)
  return !!parentType && parentType == TemplateDataType.OBJECT
}

const showDefaultValue = (parent, child) => {
  return ![TemplateDataType.ARRAY, TemplateDataType.OBJECT, TemplateDataType.SELECT, TemplateDataType.FILE].includes(
    child.dataType
  )
}

const keyParamCount = computed(() => {
  return (
    dataSource.value?.filter(
      (item: any) => item?.keyParam || (item.children || []).filter((child: any) => child.keyParam).length > 0
    )?.length || 0
  )
})

const showKeyParam = (node, child) => {
  const parentType = node?.parent?.data.dataType || null
  const parentParentType = node?.parent?.parent?.data.dataType || null
  return (
    (!parentType || (parentType == TemplateDataType.OBJECT && !parentParentType)) &&
    child.dataType != TemplateDataType.ARRAY &&
    child.dataType != TemplateDataType.OBJECT &&
    (keyParamCount.value < 1 || child.keyParam)
  )
}

const defaultValuePlaceholder = (dataType: string) => {
  if (dataType == TemplateDataType.GENERATED) {
    return "请输入生成规则，例如：'url={url}',使用{url}表示url对应的值"
  }
  return ""
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
