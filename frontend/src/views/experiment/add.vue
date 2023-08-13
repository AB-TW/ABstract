<template>
  <el-card>
    <el-form
      :model="experimentForm"
      :rules="experimentRules"
      ref="experimentFormRef"
      label-width="140px"
      :disabled="false"
      :scroll-to-error="true"
    >
      <el-form-item label="实验名称" prop="name">
        <el-input
          v-model="experimentForm.name"
          maxlength="20"
          placeholder="请输入实验名称"
          show-word-limit
          type="text"
        />
      </el-form-item>

      <el-form-item label="实验描述" prop="description">
        <el-input v-model="experimentForm.description" placeholder="请输入实验描述" type="text" />
      </el-form-item>

      <el-form-item label="实验分组">
        <el-form-item
          :prop="`buckets.${index}`"
          :key="index"
          :rules="{ validator: validateBucket, trigger: 'blur' }"
          v-for="(bucket, index) in experimentForm.buckets"
        >
          <div class="bucket-item">
            <p class="bucket-label" style="margin: 0">
              分组
              {{ index + 1 }}
              <el-icon class="delete-bucket-icon" @click="() => experimentForm.buckets?.splice(index, 1)">
                <Delete />
              </el-icon>
            </p>
            <el-input v-model="bucket.key" maxlength="10" placeholder="请输入分组键" show-word-limit type="text" />
            <el-input v-model="bucket.name" maxlength="10" placeholder="请输入分组名称" show-word-limit type="text" />
            <el-input
              v-model="bucket.description"
              maxlength="10"
              placeholder="请输入分组描述"
              show-word-limit
              type="text"
            />
            <el-input-number v-model.number="bucket.percentage" style="width: 100%" :min="1" :max="100" :step="10" />
          </div>
        </el-form-item>
        <el-button class="add-bucket-button" @click="addBucket" plain icon="Plus" />
      </el-form-item>
      <FilterConditionComponent ref="filterConditionRef" v-model="experimentForm.filterCondition" />
      <el-form-item>
        <el-button type="default" @click="resetExperimentForm(experimentFormRef, filterConditionRef)">重置</el-button>
        <el-button type="primary" @click="submitExperimentForm(experimentFormRef, filterConditionRef)">提交</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormInstance, FormRules } from "element-plus"
import { FilterCondition } from "@/model/FilterCondition"
import FilterConditionComponent from "@/components/FilterCondition.vue"
import { reactive, ref } from "vue"
import { Delete } from "@element-plus/icons-vue"
import { ExperimentBucket } from "@/model/ExperimentBucket"
import _ from "lodash"

interface AddExperiment {
  name: string
  description: string
  buckets: ExperimentBucket[]
  filterCondition: FilterCondition
}

const experimentFormRef = ref<FormInstance>()
const filterConditionRef = ref<FormInstance>()
const experimentForm = reactive<AddExperiment>({
  buckets: [
    {
      key: "",
      name: "",
      description: "",
      percentage: 1,
      config: {}
    }
  ]
} as AddExperiment)
const addBucket = () => {
  experimentForm.buckets.push({
    key: "",
    name: "",
    description: "",
    percentage: 1,
    config: {}
  })
}
const experimentRules: FormRules = {
  name: [
    {
      required: true,
      message: "请输入实验名称",
      trigger: "blur"
    }
  ],
  description: [
    {
      required: true,
      message: "请输入实验名称",
      trigger: "blur"
    }
  ]
}
const validateBucket = (rule: any, bucket: ExperimentBucket, callback: any) => {
  if (
    _.isEmpty(bucket.key) ||
    !_.isNumber(bucket.percentage) ||
    _.isEmpty(bucket.description) ||
    _.isEmpty(bucket.name)
  ) {
    callback(new Error("请检查所有值是否填写～"))
  } else if (_.size(_.filter(experimentForm.buckets, (b: ExperimentBucket) => b.key === bucket.key)) > 1) {
    callback(new Error("分组键需保持唯一～"))
  } else if (
    !_.inRange(
      _.sumBy(experimentForm.buckets, (b: ExperimentBucket) => b.percentage),
      1,
      100
    )
  ) {
    callback(new Error("所有分组比例需在1-100之间～"))
  } else {
    callback()
  }
}

const resetExperimentForm = (
  experimentFormEl: FormInstance | undefined,
  filterConditionRef: FormInstance | undefined
) => {
  if (!experimentFormEl || !filterConditionRef) return
  experimentFormEl.resetFields()
  filterConditionRef.$refs.filterConditionFormRef.resetFields()
}

const submitExperimentForm = async (
  experimentFormEl: FormInstance | undefined,
  filterConditionRef: FormInstance | undefined
) => {
  if (!experimentFormEl || !filterConditionRef) return
  try {
    await Promise.all([experimentFormEl.validate(), filterConditionRef.$refs.filterConditionFormRef.validate()])
    console.log(experimentForm)
  } catch (e: Error) {
    ElMessage.error("请检查表单项是否填写符合规范～")
  }
}
</script>

<style scoped lang="scss">
.bucket-item {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;

  &:hover {
    background-color: #f5f7fa;
  }

  .bucket-label {
    color: rgba(0, 0, 0, 0.85);
    font-size: 14px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .delete-bucket-icon {
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

.add-bucket-button {
  margin-left: 20px;
  margin-top: 10px;
  border-style: dashed;
}
</style>
