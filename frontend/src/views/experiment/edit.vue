<template>
  <el-card>
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
            <el-form-item
              class="level-2-form-item"
              label="分组配置"
              :prop="`buckets.${index}.config`"
              v-if="!_.isEmpty(selectedFeature)"
            >
              <FeatureTemplateConfig :isViewModel="true" :dataSource="bucket.datasource" />
            </el-form-item>
          </div>
        </el-form-item>
        <el-button class="add-bucket-button" @click="addBucket" plain icon="Plus" />
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
      <el-form-item>
        <el-button type="default" @click="resetExperimentForm(experimentFormRef, filterConditionRef)">重置</el-button>
        <el-button type="primary" @click="submitExperimentForm(experimentFormRef, filterConditionRef)">提交</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormInstance } from "element-plus"
import FilterConditionComponent from "@/components/FilterCondition.vue"
import { reactive, ref } from "vue"
import { Delete } from "@element-plus/icons-vue"
import { ExperimentBucket } from "@/model/ExperimentBucket"
import _ from "lodash"
import {
  convertConfigObject,
  mapDataToTemplate,
  transTempToTreeDataSource,
  validateSelectDateLowerThanToday
} from "@/utils"
import { Feature, Features, FeatureTemplate, PageResponse } from "@/domain/Features"
import FeatureTemplateConfig from "@/components/FeatureTemplateItemConfig.vue"
import moment from "moment/moment"
import { Experiment, Experiments } from "@/domain/Experiments"
import { Status } from "@/model/Status"
import { FilterCondition } from "@/domain/FilterConditions"
import { useRoute, useRouter } from "vue-router"

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

// Vue Component
const route = useRoute()
const router = useRouter()
// API
const features = new Features()
const experiments = new Experiments()
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
const isEditMode = ref<boolean>(!_.isEmpty(experimentId))
const isViewMode = ref<boolean>(route.query.type === "view")

// setup page
const setup = async () => {
  const [featureResp, experimentResp]: [PageResponse<Feature>, Experiment] = await Promise.all([
    features.fetchAll(0, 999, undefined, true),
    isEditMode.value ? experiments.getById(experimentId as string) : Promise.resolve(undefined)
  ])
  allFeatures.push(...featureResp.content)
  _.assign(experimentForm, {
    startTime: experimentResp.status?.startTime,
    endTime: experimentResp.status?.endTime,
    endCollectTime: experimentResp.status?.endCollectTime,
    ..._.omit(experimentResp, ["status"])
  })
  console.log(experimentForm)
  if (!_.isEmpty(experimentResp.featureKey)) {
    selectFeature(experimentResp.featureKey)
  }
}
setup()

const selectFeature = (featureKey: string) => {
  const currentSelectedFeature = _.find(allFeatures, (feature: Feature) => feature.featureKey === featureKey)
  _.assign(selectedFeature, currentSelectedFeature)
  // 设置bucket的config为datasource
  setBucketsConfigDataSource(currentSelectedFeature.template)
}
const setBucketsConfigDataSource = (template: FeatureTemplate) => {
  _.each(experimentForm.buckets, (bucket: ExperimentBucket) => {
    bucket.datasource = isEditMode.value
      ? [mapDataToTemplate("", bucket.config, template)]
      : [transTempToTreeDataSource(template)]
  })
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
      ..._.omit(experimentForm, ["startTime", "endCollectTime", "endTime"]),
      buckets: _.map(experimentForm.buckets, (bucket: ExperimentBucket) => ({
        ..._.omit(bucket, ["datasource"]),
        config: convertConfigObject(bucket.datasource[0])
      })),
      status: {
        staticStatus: Status.DRAFT,
        startTime: experimentForm.startTime,
        endCollectTime: experimentForm.endCollectTime,
        endTime: experimentForm.endTime
      }
    }
    const operationFunc = () =>
      isEditMode.value ? experiments.update(requestBody as Experiment) : experiments.create(requestBody as Experiment)
    await operationFunc().then(() => {
      ElMessage.success("操作成功！")
      router.push("/experiment/list")
    })
  } catch (e: Error) {
    console.log(e)
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
