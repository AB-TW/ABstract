<template>
  <el-card>
    <el-form
      :model="experimentForm"
      ref="experimentFormRef"
      label-width="140px"
      :disabled="false"
      :scroll-to-error="true"
    >
      <el-form-item
        label="实验名称"
        :rules="[{ required: true, message: '请输入实验名称', trigger: 'blur' }]"
        prop="name"
      >
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

      <el-form-item label="关联Feature" prop="feature">
        <el-select v-model="experimentForm.featureKey" @change="selectFeature" placeholder="请选择实验项">
          <el-option v-for="feature in allFeatures" :key="feature.id" :label="feature.name" :value="feature.featureKey">
            <span style="float: left">{{ feature.name }}</span>
            <span style="float: right; color: var(--el-text-color-secondary); font-size: 13px">
              {{ feature.featureKey }}
            </span>
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="实验分组">
        <el-form-item style="width: 100%" :key="index" v-for="(bucket, index) in experimentForm.buckets">
          <div class="bucket-item">
            <p class="bucket-label" style="margin: 0">
              分组
              {{ index + 1 }}
              <el-icon class="delete-bucket-icon" @click="() => experimentForm.buckets?.splice(index, 1)">
                <Delete />
              </el-icon>
            </p>
            <el-form-item
              label="分组键"
              :prop="`buckets.${index}.key`"
              :rules="[{ required: true, message: '请输入分组键', trigger: 'blur' }]"
              class="level-2-form-item"
            >
              <el-input v-model="bucket.key" maxlength="10" placeholder="请输入分组键" show-word-limit type="text" />
            </el-form-item>
            <el-form-item
              label="分组名称"
              :prop="`buckets.${index}.name`"
              class="level-2-form-item"
              :validate-status="'success'"
            >
              <el-input v-model="bucket.name" maxlength="10" placeholder="请输入分组名称" show-word-limit type="text" />
            </el-form-item>
            <el-form-item
              label="分组描述"
              :prop="`buckets.${index}.description`"
              class="level-2-form-item"
              :validate-status="'success'"
            >
              <el-input
                v-model="bucket.description"
                maxlength="10"
                placeholder="请输入分组描述"
                show-word-limit
                type="text"
              />
            </el-form-item>
            <el-form-item
              label="分组比例"
              required
              class="level-2-form-item"
              style="width: 300px"
              :prop="`buckets.${index}.percentage`"
              :rules="[{ required: true, message: '请输入分组比例', trigger: 'blur' }]"
            >
              <el-input-number v-model.number="bucket.percentage" style="width: 100%" :min="0" :max="100" :step="10" />
            </el-form-item>

            <el-form-item class="level-2-form-item" label="分组配置" prop="template" v-if="!_.isEmpty(selectedFeature)">
              <FeatureTemplateConfig :isViewModel="false" :dataSource="bucket.config" />
            </el-form-item>
          </div>
        </el-form-item>
        <el-button class="add-bucket-button" @click="addBucket" plain icon="Plus" />
      </el-form-item>

      <el-form-item
        label="实验开始时间"
        :rules="[
          { required: true, message: '请选择选择实验开始的时间', trigger: 'blur' },
          { validator: validStartTime, message: '开始时间需早于结束/收集时间', trigger: 'blur' }
        ]"
        prop="startTime"
      >
        <el-date-picker
          v-model="experimentForm.startTime"
          type="datetime"
          placeholder="请选择选择实验开始的时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="validateSelectDateLowerThanToday"
        />
      </el-form-item>

      <el-form-item
        label="停止收集数据时间"
        :rules="[
          { required: true, message: '请选择选择停止收集数据的时间', trigger: 'blur' },
          { validator: validEndCollectTime, message: '收集时间需晚于开始/收集时间', trigger: 'blur' }
        ]"
        prop="endCollectTime"
      >
        <el-date-picker
          v-model="experimentForm.endCollectTime"
          type="datetime"
          placeholder="选择停止收集数据时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="validateSelectDateLowerThanToday"
        />
      </el-form-item>

      <el-form-item
        label="实验结束时间"
        :rules="[
          { required: true, message: '请选择选择实验结束的时间', trigger: 'blur' },
          { validator: validEndTime, message: '结束时间需晚于开始/停止收据时间且早于结束时间', trigger: 'blur' }
        ]"
        prop="endTime"
      >
        <el-date-picker
          v-model="experimentForm.endTime"
          type="datetime"
          placeholder="请选择选择实验结束的时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          :disabled-date="validateSelectDateLowerThanToday"
        />
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
import FilterConditionComponent from "@/components/FilterCondition.vue"
import { reactive, ref } from "vue"
import { Delete } from "@element-plus/icons-vue"
import { ExperimentBucket } from "@/model/ExperimentBucket"
import _ from "lodash"
import { transTempToTreeDataSource, validateSelectDateLowerThanToday } from "@/utils"
import { Feature, Features, FeatureTemplate, PageResponse } from "@/domain/Features"
import FeatureTemplateConfig from "@/components/FeatureTemplateItemConfig.vue"
import { FilterCondition } from "@/domain/FilterConditions"
import moment from "moment/moment"

interface AddExperiment {
  name: string
  description: string
  featureKey: string
  buckets: ExperimentBucket[]
  filterCondition: FilterCondition
  startTime: string
  endTime: string
  endCollectTime: string
}

const allFeatures = reactive<Feature[]>([])
const selectedFeature = reactive<Feature>({} as Feature)
const features = new Features()

const setup = () => {
  features
    .fetchAll(0, 999, undefined, true)
    .then((res: PageResponse<Feature>) => {
      allFeatures.push(...res.content)
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("获取数据失败，请重试")
    })
}
setup()

const selectFeature = (featureKey: string) => {
  console.log(featureKey)
  const currentSelectedFeature = _.find(allFeatures, (feature: Feature) => feature.featureKey === featureKey)
  _.assign(selectedFeature, currentSelectedFeature, {
    dataSource: [transTempToTreeDataSource(currentSelectedFeature.template)]
  })
  // 设置bucket的config为datasource
  setBucketsConfig(selectedFeature.dataSource)
  console.log(selectedFeature)
}
const setBucketsConfig = (dataSource: FeatureTemplate[]) => {
  _.each(experimentForm.buckets, (bucket: ExperimentBucket) => {
    bucket.config = _.cloneDeep(dataSource)
  })
}

const experimentFormRef = ref<FormInstance>()
const filterConditionRef = ref<FormInstance>()
const experimentForm = reactive<AddExperiment>({
  buckets: [
    {
      key: "",
      name: "",
      description: "",
      percentage: 100,
      config: []
    }
  ]
} as AddExperiment)
const addBucket = () => {
  const currentRemainingPercentage = 100 - _.sumBy(experimentForm.buckets, (bucket) => bucket.percentage)
  experimentForm.buckets.push({
    key: "",
    name: "",
    description: "",
    percentage: currentRemainingPercentage,
    config: {}
  })
  setBucketsConfig(selectedFeature.dataSource)
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
const validStartTime = () => {
  const startTime = moment(experimentForm.startTime)
  const nextDay = moment(experimentForm.startTime).add(1, "day")
  return (
    startTime.isBefore(experimentForm.endCollectTime ?? nextDay) &&
    startTime.isBefore(experimentForm.endTime ?? nextDay)
  )
}
const validEndCollectTime = () => {
  const endCollectTime = moment(experimentForm.endCollectTime)
  const prevDay = moment(experimentForm.endCollectTime).subtract(1, "day")
  const nextDay = moment(experimentForm.endCollectTime).add(1, "day")
  return endCollectTime.isBetween(
    experimentForm.startTime ?? prevDay,
    experimentForm.endTime ?? nextDay,
    "second",
    "[]"
  )
}
const validEndTime = () => {
  const endTime = moment(experimentForm.endTime)
  const prevDay = moment(experimentForm.endTime)
  return (
    endTime.isAfter(experimentForm.startTime ?? prevDay) && endTime.isAfter(experimentForm.endCollectTime ?? prevDay)
  )
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
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
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
  margin-top: 20px;
  border-style: dashed;
  width: 100%;
}

.level-2-form-item {
  :deep(.el-form-item__label) {
    justify-content: center !important;
    width: 120px !important;
  }
}
</style>
