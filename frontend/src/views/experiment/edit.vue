<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span class="title">{{ title }}</span>
        <el-button size="small" style="float: right" @click="router.go(-1)">返回</el-button>
      </div>
    </template>
    <el-form
      :model="experimentForm"
      ref="experimentFormRef"
      label-width="140px"
      :disabled="isViewMode"
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

      <el-form-item
        label="关联Feature"
        prop="featureKey"
        :rules="[{ required: true, message: '请选择一个关联的Feature', trigger: 'blur' }]"
      >
        <el-select
          v-model="experimentForm.featureKey"
          @change="selectFeature"
          placeholder="请选择实验项"
          :disabled="isViewMode || (isEditMode && experimentForm?.status?.status !== Status.DRAFT)"
        >
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
              <el-icon
                class="delete-bucket-icon"
                v-if="isAddMode || (isEditMode && experimentForm?.status?.status === Status.DRAFT)"
                @click="() => experimentForm.buckets?.splice(index, 1)"
              >
                <Delete />
              </el-icon>
            </p>
            <el-form-item
              label="分组键"
              :prop="`buckets.${index}.key`"
              :rules="[
                { required: true, message: '请输入分组键', trigger: 'blur' },
                { validator: () => validBucketKey(bucket), message: '分组键不唯一', trigger: 'blur' }
              ]"
              class="level-2-form-item"
            >
              <el-input
                :disabled="isViewMode || (isEditMode && experimentForm?.status?.status !== Status.DRAFT)"
                v-model="bucket.key"
                maxlength="10"
                placeholder="请输入分组键"
                show-word-limit
                type="text"
              />
            </el-form-item>
            <el-form-item
              label="分组名称"
              :prop="`buckets.${index}.name`"
              class="level-2-form-item"
              validate-status="success"
            >
              <el-input
                :disabled="isViewMode || (isEditMode && experimentForm?.status?.status !== Status.DRAFT)"
                v-model="bucket.name"
                maxlength="10"
                placeholder="请输入分组名称"
                show-word-limit
                type="text"
              />
            </el-form-item>
            <el-form-item
              label="分组描述"
              :prop="`buckets.${index}.description`"
              class="level-2-form-item"
              validate-status="success"
            >
              <el-input
                v-model="bucket.description"
                maxlength="10"
                :disabled="isViewMode || (isEditMode && experimentForm?.status?.status !== Status.DRAFT)"
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
              :rules="[
                { required: true, message: '请输入分组比例', trigger: 'blur' },
                { validator: () => validBucketPercentage(), message: '所有的分组比例和需为100', trigger: 'blur' }
              ]"
            >
              <el-input-number v-model.number="bucket.percentage" style="width: 100%" :min="0" :max="100" :step="10" />
            </el-form-item>
            <el-form-item
              class="level-2-form-item"
              label="分组配置"
              :prop="`buckets.${index}.config`"
              v-if="!_.isEmpty(selectedFeature)"
            >
              <FeatureTemplateConfig :isViewModel="isViewMode" :dataSource="bucket.datasource" />
            </el-form-item>
          </div>
        </el-form-item>
        <el-button
          class="add-bucket-button"
          v-if="isAddMode || (isEditMode && experimentForm?.status?.status === Status.DRAFT)"
          @click="addBucket"
          plain
          icon="Plus"
        />
      </el-form-item>

      <el-form-item
        label="实验开始时间"
        :rules="[
          { required: true, message: '请选择选择实验开始的时间', trigger: 'blur' },
          { validator: () => validStartTime(), message: '开始时间需早于结束/收集时间', trigger: 'blur' }
        ]"
        prop="startTime"
      >
        <el-date-picker
          v-model="experimentForm.startTime"
          type="datetime"
          placeholder="请选择选择实验开始的时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
          :disabled-date="(time) => validateSelectDateLowerThanToday(time) || !validStartTime(time)"
        />
      </el-form-item>

      <el-form-item
        label="停止收集数据时间"
        :rules="[
          { required: true, message: '请选择选择停止收集数据的时间', trigger: 'blur' },
          { validator: () => validEndCollectTime(), message: '收集时间需晚于开始/收集时间', trigger: 'blur' }
        ]"
        prop="endCollectTime"
      >
        <el-date-picker
          v-model="experimentForm.endCollectTime"
          type="datetime"
          placeholder="选择停止收集数据时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
          :disabled-date="(time) => validateSelectDateLowerThanToday(time) || !validEndCollectTime(time)"
        />
      </el-form-item>

      <el-form-item
        label="实验结束时间"
        :rules="[
          { required: true, message: '请选择选择实验结束的时间', trigger: 'blur' },
          { validator: () => validEndTime(), message: '结束时间需晚于开始/停止收据时间且早于结束时间', trigger: 'blur' }
        ]"
        prop="endTime"
      >
        <el-date-picker
          v-model="experimentForm.endTime"
          type="datetime"
          placeholder="请选择选择实验结束的时间"
          value-format="YYYY-MM-DDTHH:mm:ss"
          :disabled-date="(time) => validateSelectDateLowerThanToday(time) || !validEndTime(time)"
        />
      </el-form-item>

      <FilterConditionComponent
        ref="filterConditionRef"
        v-model="experimentForm.filterCondition"
        :disabled="isViewMode"
      />
      <el-form-item v-if="!isViewMode">
        <el-button type="default" @click="resetExperimentForm(experimentFormRef, filterConditionRef)">重置</el-button>
        <el-button type="primary" @click="submitExperimentForm(experimentFormRef, filterConditionRef)">提交</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormInstance } from "element-plus"
import FilterConditionComponent from "@/components/FilterCondition.vue"
import { computed, reactive, ref } from "vue"
import { Delete } from "@element-plus/icons-vue"
import { ExperimentBucket } from "@/model/ExperimentBucket"
import _ from "lodash"
import {
  convertConfigObject,
  mapDataToTemplate,
  StatusMaps,
  transTempToTreeDataSource,
  validateSelectDateLowerThanToday
} from "@/utils"
import { Feature, Features, FeatureTemplate, PageResponse } from "@/domain/Features"
import FeatureTemplateConfig from "@/components/FeatureTemplateItemConfig.vue"
import moment from "moment/moment"
import { Experiment, Experiments } from "@/domain/Experiments"
import { Status } from "@/model/Status"
import { CompareOperator, CustomAttributeCondition, FilterCondition } from "@/domain/FilterConditions"
import { useRoute, useRouter } from "vue-router"

interface AddExperiment {
  id?: string
  name: string
  description: string
  featureKey: string
  buckets: ExperimentBucket[]
  filterCondition: FilterCondition
  startTime: string
  endTime: string
  endCollectTime: string
  status?: {
    status: Status
    staticStatus: Status
    startTime: string
    endCollectTime: string
    endTime: string
  }
}

// Vue Component
const route = useRoute()
const router = useRouter()
// API
const features = new Features()
const experiments = new Experiments()

const title = ref("Experiment")

// View-Model
const allFeatures = reactive<Feature[]>([])
const selectedFeature = reactive<Feature>({} as Feature)
const experimentFormRef = ref<FormInstance>()
const filterConditionRef = ref<FormInstance>()
const experimentForm = reactive<AddExperiment>({
  buckets: [
    {
      key: "",
      name: "",
      description: "",
      percentage: 100
    }
  ]
} as AddExperiment)
const experimentId = route.params.id
const isEditMode = ref<boolean>(!_.isEmpty(experimentId) && route.query.type === undefined)
const isCopyMode = ref<boolean>(!_.isEmpty(experimentId) && route.query.type === "copy")
const isViewMode = ref<boolean>(route.query.type === "view")
const isAddMode = computed(() => !isViewMode.value && !isEditMode.value)

const model = isEditMode.value ? "edit" : isViewMode.value ? "view" : "add"
title.value = StatusMaps[model] + "Experiment"

// setup page
const setup = async () => {
  const needQueryExperiment = isViewMode.value || isEditMode.value || isCopyMode.value
  const [featureResp, experimentResp]: [PageResponse<Feature>, Experiment] = await Promise.all<
    PageResponse<Feature> | Experiment
  >([
    features.fetchAll(0, 999, undefined, true),
    needQueryExperiment ? experiments.getById(experimentId as string) : Promise.resolve(undefined)
  ])
  allFeatures.push(...featureResp.content)
  if (needQueryExperiment) {
    _.assign(experimentForm, {
      startTime: experimentResp.status?.startTime,
      endTime: experimentResp.status?.endTime,
      endCollectTime: experimentResp.status?.endCollectTime,
      ..._.omit(experimentResp, ["filterCondition", "status.startTime", "status.endTime", "status.endCollectTime"]),
      filterCondition: {
        ...experimentResp.filterCondition,
        attributeConditions: _.map(
          experimentResp.filterCondition?.attributeConditions ?? [],
          (attributeCondition: CustomAttributeCondition) => ({
            ...attributeCondition,
            value: _.includes([CompareOperator.IN, CompareOperator.NOT_IN], attributeCondition.operator)
              ? _.split(attributeCondition.value as string, ",")
              : attributeCondition.value
          })
        )
      }
    })
    if (isCopyMode.value) {
      experimentForm.name += "-复制"
      experimentForm.id = undefined
    }
    if (!_.isEmpty(experimentResp.featureKey)) {
      selectFeature(experimentResp.featureKey)
    }
  }
  console.log(experimentForm)
}
setup()

const selectFeature = (featureKey: string) => {
  const currentSelectedFeature = _.find(allFeatures, (feature: Feature) => feature.featureKey === featureKey)
  _.assign(selectedFeature, currentSelectedFeature)
  // 设置bucket的config为datasource
  setBucketsConfigDataSource(currentSelectedFeature.template)
}
const setBucketsConfigDataSource = (template: FeatureTemplate) => {
  if (template) {
    _.each(experimentForm.buckets, (bucket: ExperimentBucket) => {
      bucket.datasource =
        isViewMode.value || isEditMode.value || isCopyMode.value
          ? [mapDataToTemplate("", bucket.config, template)]
          : [transTempToTreeDataSource(template)]
    })
  }
}

const addBucket = () => {
  const currentRemainingPercentage = 100 - _.sumBy(experimentForm.buckets, (bucket) => bucket.percentage)
  experimentForm.buckets.push({
    key: "",
    name: "",
    description: "",
    percentage: currentRemainingPercentage
  })
  setBucketsConfigDataSource(selectedFeature.template)
}

// form validator
const validBucketPercentage = () => {
  return _.sumBy(experimentForm.buckets, "percentage") === 100
}
const validStartTime = (time = experimentForm.startTime) => {
  const startTime = moment(time)
  const nextDay = moment(time).add(1, "day")
  return (
    startTime.isBefore(experimentForm.endCollectTime ?? nextDay) &&
    startTime.isBefore(experimentForm.endTime ?? nextDay)
  )
}
const validEndCollectTime = (time = experimentForm.endCollectTime) => {
  const endCollectTime = moment(time)
  const prevDay = moment(time).subtract(1, "day")
  const nextDay = moment(time).add(1, "day")
  return endCollectTime.isBetween(
    experimentForm.startTime ?? prevDay,
    experimentForm.endTime ?? nextDay,
    "second",
    "[]"
  )
}
const validEndTime = (time = experimentForm.endTime) => {
  const endTime = moment(time)
  const prevDay = moment(time).subtract(1, "day")
  return (
    endTime.isAfter(experimentForm.startTime ?? prevDay) && endTime.isAfter(experimentForm.endCollectTime ?? prevDay)
  )
}
const validBucketKey = (bucket: ExperimentBucket) => {
  const sameBucketKey = _.chain(experimentForm.buckets ?? [])
    .filter((experimentBucket: ExperimentBucket) => bucket.key === experimentBucket.key)
    .value()
  return sameBucketKey.length < 2
}

// form operation
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
    const requestBody = {
      ..._.omit(experimentForm, ["filterCondition", "startTime", "endCollectTime", "endTime"]),
      buckets: _.map(experimentForm.buckets, (bucket: ExperimentBucket) => ({
        ..._.omit(bucket, ["datasource"]),
        config: convertConfigObject(bucket.datasource[0])
      })),
      status: {
        staticStatus: isEditMode.value ? experimentForm.status?.staticStatus : Status.DRAFT,
        startTime: experimentForm.startTime,
        endCollectTime: experimentForm.endCollectTime,
        endTime: experimentForm.endTime
      },
      filterCondition: {
        ...experimentForm.filterCondition,
        attributeConditions: _.map(
          experimentForm.filterCondition?.attributeConditions ?? [],
          (attributeCondition: CustomAttributeCondition) => ({
            ...attributeCondition,
            value: _.isArray(attributeCondition.value)
              ? _.join(attributeCondition.value, ",")
              : attributeCondition.value
          })
        )
      }
    }
    const operationFunc = () =>
      isEditMode.value ? experiments.update(requestBody as Experiment) : experiments.create(requestBody as Experiment)
    await operationFunc().then(() => {
      ElMessage.success("操作成功！")
      router.push("/experiment/list")
    })
  } catch (e: Error) {
    console.error(e)
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
