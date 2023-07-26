<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">{{ title }}</span>
          <el-button size="mini" style="float: right" @click="router.go(-1)">返回</el-button>
        </div>
      </template>
      <el-form :model="featureFlag" :rules="formRules" ref="form" label-width="140px" :disabled="isViewModel">
        <el-form-item label="Feature Key" prop="featureKey">
          <el-input v-model="featureFlag.featureKey" autocomplete="off" :disabled="formType == 'edit'" />
        </el-form-item>
        <el-form-item label="Feature Name" prop="name">
          <el-input v-model="featureFlag.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="featureFlag.description" autocomplete="off" />
        </el-form-item>

        <el-form-item label="Config Template" prop="template">
          <feature-flag-template-item
            :key="featureFlag.dataSource"
            :index="0"
            :isViewModel="isViewModel"
            :dataSource="featureFlag.dataSource"
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
import { FeatureFlag, FeatureFlags, FeatureFlagTemplate, TemplateDataType } from "@/domain/FeatureFlags"
import FeatureFlagTemplateItem from "../../components/FeatureFlagTemplateItemNew.vue"
import _ from "lodash"
import { replaceChildrenWithItems, StatusMaps, translateDataToTemplateItems, transTempToTreeDataSource } from "@/utils"

const route = useRoute()
const router = useRouter()
const featureFlags = new FeatureFlags()
const featureFlag = ref({} as FeatureFlag)

const title = ref("Feature")
const formType = ref("add")
const isViewModel = ref(false)
const originalFeatureFlag = ref({} as FeatureFlag)

onBeforeMount(async () => {
  const model = route.query.type || "add"
  if (model == "view") {
    isViewModel.value = true
  }

  title.value = StatusMaps[model] + "Feature"
  formType.value = route.query.type == "edit" ? "edit" : "add"
  featureFlag.value = {
    status: "PUBLISHED",
    template: {
      dataType: TemplateDataType.BOOLEAN,
      defaultValue: false,
      items: [] as FeatureFlagTemplate[]
    },
    dataSource: [
      transTempToTreeDataSource({
        dataType: TemplateDataType.BOOLEAN,
        defaultValue: false,
        items: [] as FeatureFlagTemplate[]
      })
    ]
  } as FeatureFlag

  if (route.query.featureKey) {
    const res = await featureFlags.fetch(route.query.featureKey.toString())
    featureFlag.value = res
    featureFlag.value.dataSource = [transTempToTreeDataSource(featureFlag.value.template)] as Array<FeatureFlagTemplate>
    originalFeatureFlag.value = res
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
  console.log("featureFlag.value.dataSource =", JSON.stringify(featureFlag.value.dataSource))
}

const submitForm = async () => {
  const copyJsonObj = _.cloneDeep(featureFlag.value)
  const tempResult = translateDataToTemplateItems(copyJsonObj)
  replaceChildrenWithItems(tempResult.template)
  console.log(JSON.stringify(tempResult))

  form.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (formType.value.toLowerCase() == "add") {
          debugger
          await featureFlags.create(tempResult)
        } else {
          await featureFlags.update(tempResult)
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
  featureFlag.value.dataSource,
  (newValue, oldValue) => {
    console.log("值发生了变更", newValue, oldValue)
  },
  { deep: true }
)
const resetForm = () => {
  if (formType.value.toLowerCase() == "add") {
    form.value.resetFields()
  } else {
    featureFlag.value = originalFeatureFlag.value
  }
}
</script>

<style scoped></style>
