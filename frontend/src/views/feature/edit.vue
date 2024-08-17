<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header" style="padding: 10px">
          <span class="title">{{ title }}</span>
          <el-button size="small" style="float: right; margin-left: 5px" @click="router.go(-1)">返回</el-button>
          <el-button
            size="small"
            style="float: right; margin-right: 5px"
            v-if="isViewModel || formType != 'add'"
            @click="
              router.push({
                path: '/feature/config/list',
                query: { featureKey: feature.featureKey, featureName: feature.name }
              })
            "
            >配置管理
          </el-button>
          <el-button
            size="small"
            style="float: right; margin-right: 5px"
            v-if="isViewModel || formType != 'add'"
            @click="
              router.push({
                path: '/experiment/list',
                query: { featureKey: feature.featureKey }
              })
            "
            >实验管理
          </el-button>
          <el-button
            v-if="isViewModel || formType !== 'add'"
            size="small"
            style="float: right; margin-right: 5px"
            @click="exportTemplate()"
          >
            <el-icon>
              <Download />
            </el-icon>
            导出
          </el-button>
          <el-upload
            v-if="!isViewModel && formType == 'add'"
            style="float: right; margin-right: 10px"
            action=""
            :before-upload="importTemplate"
            :show-file-list="false"
            accept=".json"
          >
            <el-button size="small">
              <el-icon>
                <Upload />
              </el-icon>
              导入
            </el-button>
          </el-upload>
        </div>
      </template>
      <el-form :model="feature" :rules="formRules" ref="form" label-width="140px" :disabled="isViewModel">
        <el-form-item label="Feature Key" prop="featureKey">
          <el-input v-model="feature.featureKey" autocomplete="off" :disabled="formType == 'edit'" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="feature.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="feature.description" autocomplete="off" />
        </el-form-item>

        <el-form-item label="配置模版" prop="template">
          <feature-template-component
            :key="feature.dataSource"
            :index="0"
            :isViewModel="isViewModel"
            :dataSource="feature.dataSource"
            @change="getDataSource"
          />
        </el-form-item>
        <el-collapse v-if="isViewModel">
          <el-collapse-item title="配置时间线" name="1">
            <feature-time-line :featureKey="route.query.featureKey.toString()" />
          </el-collapse-item>
        </el-collapse>
        <el-form-item v-if="!isViewModel">
          <el-button type="default" @click="resetForm()">重置</el-button>
          <el-button type="primary" @click="submitForm()">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount, ref, watch } from "vue"
import { useRoute, useRouter } from "vue-router"
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from "element-plus"
import { ChoiceType, Feature, Features, FeatureTemplate, FileType, TemplateDataType } from "@/domain/Features"
import FeatureTemplateComponent from "../../components/FeatureTemplate.vue"
import _ from "lodash"
import { replaceChildrenWithItems, StatusMaps, translateDataToTemplateItems, transTempToTreeDataSource } from "@/utils"
import FeatureTimeLine from "@/components/FeatureTimeLine.vue"

const route = useRoute()
const router = useRouter()
const features = new Features()
const feature = ref({} as Feature)

const title = ref("Feature")
const formType = ref("add")
const isViewModel = ref(false)
const originalFeature = ref({} as Feature)

onBeforeMount(async () => {
  const model = route.query.type || "add"
  isViewModel.value = model == "view"

  title.value = StatusMaps[model] + "Feature"
  formType.value = route.query.type == "edit" ? "edit" : "add"
  feature.value = {
    enabled: true,
    featureKey: "",
    name: "",
    template: {
      dataType: TemplateDataType.BOOLEAN,
      defaultValue: false,
      items: [] as FeatureTemplate[],
      choice: { type: ChoiceType.CONTENT, items: [], featureKey: "" },
      file: { type: FileType.IMAGE, sizeLimit: 1024, fileExtensions: [] }
    },
    dataSource: [
      transTempToTreeDataSource({
        dataType: TemplateDataType.BOOLEAN,
        defaultValue: false,
        items: [] as FeatureTemplate[]
      })
    ]
  } as Feature

  if (route.query.featureKey) {
    const res = await features.fetch(route.query.featureKey.toString())
    feature.value = res
    feature.value.dataSource = [transTempToTreeDataSource(feature.value.template)] as Array<FeatureTemplate>
    originalFeature.value = res
  }
})

const formRules = {
  featureKey: [
    { required: true, message: "Please enter feature key", trigger: "submit" },
    // 由字母、数字、下划线组成，且以字母开头
    {
      pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/,
      message: "由字母、数字、下划线组成，且以字母开头",
      trigger: "submit"
    }
  ],
  name: [{ required: true, message: "Please enter feature name", trigger: "submit" }]
} as any

const form = ref<ElForm>(null)
const getDataSource = () => {
  console.log("feature.value.dataSource =", JSON.stringify(feature.value.dataSource))
}

const submitForm = async () => {
  const copyJsonObj = _.cloneDeep(feature.value)
  const tempResult = translateDataToTemplateItems(copyJsonObj)
  replaceChildrenWithItems(tempResult.template)
  console.log("JSON.stringify(tempResult)")
  console.log(JSON.stringify(tempResult))

  form.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (formType.value.toLowerCase() == "add") {
          await features.create(tempResult)
        } else {
          await features.update(tempResult)
        }
        ElMessage.success("操作成功！")
        setTimeout(() => {
          router.push({ path: "/feature/list" })
        }, 1000)
      } catch (error: any) {
        ElMessage.error("操作失败，请稍后重试！")
      }
    } else {
      return false
    }
  })
}

watch(
  feature.value.dataSource,
  (newValue, oldValue) => {
    console.log("值发生了变更", newValue, oldValue)
  },
  { deep: true }
)
const resetForm = () => {
  if (formType.value.toLowerCase() == "add") {
    form.value.resetFields()
  } else {
    feature.value = originalFeature.value
  }
}

const exportTemplate = () => {
  // 将数据对象转换为 JSON 字符串
  const jsonData = JSON.stringify(_.omit(feature.value, "dataSource"), null, 2)

  // 创建一个 Blob 对象
  const blob = new Blob([jsonData], { type: "application/json" })

  // 创建一个链接元素
  const link = document.createElement("a")
  link.href = URL.createObjectURL(blob)
  link.download = feature.value?.featureKey + ".json"

  // 将链接元素添加到文档中
  document.body.appendChild(link)

  // 触发下载操作
  link.click()

  // 移除链接元素
  document.body.removeChild(link)
}

const importTemplate = (file) => {
  const reader = new FileReader()
  reader.onload = (event) => {
    try {
      const result = JSON.parse(event.target.result as string) as Feature
      feature.value = {
        ...result,
        dataSource: [
          transTempToTreeDataSource(
            result.template || {
              dataType: TemplateDataType.BOOLEAN,
              defaultValue: false,
              items: [] as FeatureTemplate[]
            }
          )
        ]
      }
    } catch (error) {
      ElMessage.error("文件解析失败，请上传正确的 JSON 文件")
    }
  }
  reader.readAsText(file)

  // 阻止文件自动上传
  return false
}
</script>

<style scoped>
span.title {
  float: left;
  font-size: 14px;
  font-weight: bold;
}
</style>
