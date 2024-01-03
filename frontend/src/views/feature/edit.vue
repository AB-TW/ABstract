<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">{{ title }}</span>
          <el-button size="small" style="float: right" @click="router.go(-1)">返回</el-button>
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
          <feature-template-item
            :key="feature.dataSource"
            :index="0"
            :isViewModel="isViewModel"
            :dataSource="feature.dataSource"
            @change="getDataSource"
          />
        </el-form-item>

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
import { Feature, Features, FeatureTemplate, TemplateDataType } from "@/domain/Features"
import FeatureTemplateItem from "../../components/FeatureTemplateItemNew.vue"
import _ from "lodash"
import { replaceChildrenWithItems, StatusMaps, translateDataToTemplateItems, transTempToTreeDataSource } from "@/utils"

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
    status: "PUBLISHED",
    template: {
      dataType: TemplateDataType.BOOLEAN,
      defaultValue: false,
      items: [] as FeatureTemplate[]
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
  console.log(JSON.stringify(tempResult))

  form.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (formType.value.toLowerCase() == "add") {
          debugger
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
</script>

<style scoped></style>
