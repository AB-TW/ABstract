<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header" style="padding: 10px">
          <span class="title">{{ title }}</span>
          <el-button size="small" style="float: right" @click="router.go(-1)">返回</el-button>
        </div>
      </template>
      <el-form
        :model="filterConditionTemplateForm"
        ref="filterConditionTemplateRef"
        label-width="140px"
        :disabled="isViewMode"
        :scroll-to-error="true"
      >
        <el-form-item
          label="模板名称"
          :rules="[{ required: true, message: '请输入模板名称', trigger: 'blur' }]"
          prop="templateName"
        >
          <el-input
            v-model="filterConditionTemplateForm.templateName"
            maxlength="20"
            placeholder="请输入模板名称"
            show-word-limit
            type="text"
          />
        </el-form-item>

        <FilterConditionComponent
          ref="filterConditionRef"
          v-model="filterConditionTemplateForm"
          :disabled="isViewMode"
        />
        <el-form-item v-if="!isViewMode">
          <el-button
            type="default"
            @click="resetFilterConditionTemplateForm(filterConditionTemplateRef, filterConditionRef)"
            >重置
          </el-button>
          <el-button type="primary" @click="submitFilterConditionForm(filterConditionTemplateRef, filterConditionRef)"
            >提交
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormInstance } from "element-plus"
import FilterConditionComponent from "@/components/FilterCondition.vue"
import { computed, onBeforeMount, reactive, ref } from "vue"
import _ from "lodash"
import { FilterConditions, FilterConditionTemplate } from "@/domain/FilterConditions"
import { FilterConditionAddRequest } from "@/model/request/FilterConditionAddRequest"
import { useRoute, useRouter } from "vue-router"

// Vue Component
const route = useRoute()
const router = useRouter()

// API
const filterConditionsApi = new FilterConditions()

// View-Model
const title = ref("FilterConditionTemplate")
const templateId = route.params.id
const isEditMode = ref<boolean>(!_.isEmpty(templateId) && route.query.type === undefined)
const isViewMode = ref<boolean>(route.query.type === "view")
const isAddMode = computed(() => !isEditMode.value)

const filterConditionTemplateRef = ref<FormInstance>()
const filterConditionRef = ref<FormInstance>()
const filterConditionTemplateForm = reactive<FilterConditionAddRequest>({
  templateName: "",
  attributeConditions: [],
  grayReleaseCondition: {
    releasePercentage: 100
  }
})

// Vue Life Cycle Hooks
onBeforeMount(() => {
  if (isEditMode.value) {
    filterConditionsApi.getById(templateId).then((res: FilterConditionTemplate) => {
      filterConditionTemplateForm.attributeConditions = res.attributeConditions
      filterConditionTemplateForm.templateName = res.templateName
    })
  }
})

// Form Operation
const resetFilterConditionTemplateForm = (
  filterConditionTemplateRef: FormInstance | undefined,
  filterConditionRef: FormInstance | undefined
) => {
  if (!filterConditionTemplateRef || !filterConditionRef) return
  filterConditionTemplateRef.resetFields()
  filterConditionRef.$refs.filterConditionFormRef.resetFields()
}

const submitFilterConditionForm = async (
  filterConditionTemplateRef: FormInstance | undefined,
  filterConditionRef: FormInstance | undefined
) => {
  if (!filterConditionTemplateRef || !filterConditionRef) return
  try {
    await Promise.all([
      filterConditionTemplateRef.validate(),
      filterConditionRef.$refs.filterConditionFormRef.validate()
    ])
    console.log(filterConditionTemplateForm)
    const operationFunc = () =>
      isEditMode.value
        ? filterConditionsApi.update({ templateId, ...filterConditionTemplateForm })
        : filterConditionsApi.add(filterConditionTemplateForm)
    await operationFunc().then(() => {
      ElMessage.success("操作成功！")
      router.push("/template/filter-condition/list")
    })
  } catch (e: any) {
    console.error(e)
    ElMessage.error("请检查表单项是否填写符合规范～")
  }
}
</script>

<style scoped lang="scss"></style>
