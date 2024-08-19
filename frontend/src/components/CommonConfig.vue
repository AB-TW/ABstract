<template>
  <div class="common-container">
    <div>
      <div v-if="dataType == TemplateDataType.STRING">
        <el-input
          v-if="
            [
              StringType.TEXTAREA,
              StringType.TEXT,
              StringType.PHONE,
              StringType.URL,
              StringType.EMAIL,
              StringType.PASSWORD
            ].includes(stringType as StringType)
          "
          v-model="value"
          :disabled="props.isViewModel"
          :type="textType"
          :maxlength="maxLength"
          @change="onInput"
        />
        <el-color-picker v-else-if="stringType == StringType.COLOR" v-model="value" @blur="onInput" />
        <el-date-picker
          v-else-if="stringType == StringType.DATE"
          v-model="value"
          :disabled="props.isViewModel"
          type="date"
          placeholder="请选择日期"
          @blur="onInput"
        />
        <el-date-picker
          v-else-if="stringType == StringType.DATE_TIME"
          v-model="value"
          :disabled="props.isViewModel"
          type="datetime"
          placeholder="请选择日期时间"
          @blur="onInput"
        />
        <el-time-picker
          v-else-if="stringType == StringType.TIME"
          :disabled="props.isViewModel"
          v-model="value"
          placeholder="请选择时间"
          @blur="onInput"
        />
        <div v-else-if="stringType == StringType.RICH_TEXT" style="border: 1px solid #ccc">
          <Toolbar
            style="border-bottom: 1px solid #ccc"
            :editor="editorRef"
            :defaultConfig="toolbarConfig"
            mode="simple"
            :disabled="props.isViewModel"
          />
          <Editor
            style="height: 500px; overflow-y: hidden"
            v-model="value"
            :defaultConfig="editorConfig"
            mode="simple"
            @onCreated="handleCreated"
            :disabled="props.isViewModel"
            @onBlur="onInput"
          />
        </div>
        <el-input
          v-else
          v-model="value"
          :disabled="props.isViewModel"
          type="text"
          :maxlength="maxLength"
          @blur="onInput"
        />
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
      <div v-else-if="dataType == TemplateDataType.FILE">
        <el-tooltip
          effect="dark"
          :content="
            '可选文件类型：' +
            (file?.fileExtensions || []).join(',') +
            '；文件大小不超过' +
            (file?.sizeLimit || 2048) +
            'KB'
          "
          placement="top"
        >
          <el-icon style="margin-left: 4px" :size="12">
            <Warning />
          </el-icon>
        </el-tooltip>
        <el-upload
          v-model:file-list="fileList"
          :accept="file.fileExtensions?.join(',') || '*'"
          :before-upload="beforeFileUpload"
          v-loading="fileUploadLoading"
          :list-type="file.type != FileType.IMAGE ? 'text' : 'picture-card'"
          @remove="
            () => {
              value = ''
              onInput()
            }
          "
        >
          <el-icon v-if="file.type == FileType.IMAGE">
            <Plus />
          </el-icon>
          <el-button v-else size="small" type="primary">点击上传</el-button>
        </el-upload>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import "@wangeditor/editor/dist/css/style.css" // 引入 css
import { Editor, Toolbar } from "@wangeditor/editor-for-vue"
import { computed, ref, shallowRef, watch } from "vue"
import { ChoiceItem, ChoiceType, FileType, StringType, TemplateDataType } from "@/domain/Features"
import { FeatureConfig, FeatureConfigs } from "@/domain/FeatureConfigs"
import { ElMessage, UploadProps } from "element-plus"
import _ from "lodash"
import { Files } from "@/domain/Files"

const files = new Files()

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
  file: {
    type: Object,
    required: false
  },
  stringType: {
    type: String,
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

const fileUploadLoading = ref(false)
const fileList = computed(() => {
  return value.value ? [{ name: (value.value as string).split("/").pop(), url: value.value as string }] : []
})
const beforeFileUpload: UploadProps["beforeUpload"] = async (rawFile) => {
  if (
    props.file?.fileExtensions &&
    !props.file?.fileExtensions.includes(rawFile.name.split(".").pop().toLowerCase() || "")
  ) {
    ElMessage.error(`文件类型不支持！`)
    return false
  }
  if (rawFile.size / 1024 > (props.file?.sizeLimit || 2048)) {
    ElMessage.error(`文件大小超过${props.file?.sizeLimit || 2048}KB！`)
    return false
  }

  fileUploadLoading.value = true
  try {
    const response = await files.uploadFile(rawFile)
    console.log(response)
    value.value = response.url
    onInput()
    console.log("value.value:" + value.value)
  } catch (error) {
    ElMessage.error("上传文件失败！")
    return false
  } finally {
    fileUploadLoading.value = false
  }

  return true
}

const textType = computed(() => {
  switch (props.stringType) {
    case StringType.PASSWORD:
      return "password"
    case StringType.EMAIL:
      return "email"
    case StringType.URL:
      return "url"
    case StringType.TEXTAREA:
      return "textarea"
    default:
      return "text"
  }
})

const editorRef = shallowRef()
const toolbarConfig = {}
const editorConfig = {
  placeholder: "请输入内容...",
  MENU_CONF: {
    uploadImage: {
      async customUpload(file: File, insertFn) {
        if (!file.type?.startsWith("image")) {
          ElMessage.error(`请选择图片`)
          return
        }
        if (file.size / 1024 / 1024 > 2) {
          ElMessage.error(`文件大小超过2MB！`)
          return
        }

        fileUploadLoading.value = true
        try {
          const response = await files.uploadFile(file)
          insertFn(response.url, "", "")
        } catch (error) {
          ElMessage.error("上传文件失败！")
        }
      }
    }
  }
}
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}
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
