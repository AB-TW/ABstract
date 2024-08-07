<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header" style="padding: 10px">
          <span class="title">{{ title }}</span>
          <el-button size="small" style="float: right" @click="router.go(-1)">返回</el-button>
        </div>
      </template>
      <el-form :model="featureConfig" :rules="formRules" ref="form" label-width="140px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="featureConfig.title" autocomplete="off" :disabled="formType == 'view'" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="featureConfig.description" autocomplete="off" :disabled="formType == 'view'" />
        </el-form-item>
        <el-form-item label="配置内容" prop="data">
          <FeatureConfigData
            :key="feature?.dataSource?.toString()"
            :isViewModel="isViewModel"
            :dataSource="feature.dataSource"
          />
        </el-form-item>
        <el-form-item label="生效时间" prop="startTime">
          <el-date-picker
            v-model="featureConfig.startTime"
            type="datetime"
            placeholder="生效时间"
            :disabled="formType == 'view'"
          />
        </el-form-item>
        <el-form-item label="截止时间" prop="endTime">
          <el-date-picker
            v-model="featureConfig.endTime"
            type="datetime"
            placeholder="截止时间"
            :disabled="formType == 'view'"
          />
        </el-form-item>
        <el-form-item label="埋点数据" prop="trackingData">
          <div class="track-cls">
            <tracking-data-item
              v-for="(item, index) in saData"
              :key="item.index"
              :item="item"
              :index="index"
              :disabled="formType == 'view'"
              @deleteItem="deleteTrackingData(index)"
            />
            <div>
              <el-button type="primary" @click="addTrackingData" v-if="formType !== 'view'">
                <el-icon>
                  <Plus />
                </el-icon>
              </el-button>
            </div>
          </div>
        </el-form-item>
        <FilterConditionComponent
          ref="filterConditionRef"
          :key="featureConfig.filterCondition"
          v-model="featureConfig.filterCondition"
          :disabled="formType == 'view'"
        />
        <el-form-item v-if="formType !== 'view'">
          <el-button type="default" @click="resetForm()">重置</el-button>
          <el-button type="primary" @click="submitForm()">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage, FormInstance } from "element-plus"
import { FeatureConfig, FeatureConfigs } from "@/domain/FeatureConfigs"
import { Feature, Features, getDefaultValue, TemplateDataType } from "@/domain/Features"
import { defaultFilterCondition } from "@/domain/FilterConditions"
import TrackingDataItem from "@/components/TrackingDataItem.vue"
import FeatureConfigData from "@/components/FeatureConfigData.vue"
import FilterConditionComponent from "@/components/FilterCondition.vue"
import { convertConfigObject, mapDataToTemplate, transTempToTreeDataSource } from "@/utils"
import { StaticStatus } from "@/constants/common"
import _ from "lodash"

const route = useRoute()
const router = useRouter()
const featureConfigs = new FeatureConfigs()
const featureConfig = ref({} as FeatureConfig)
const feature = ref({ dataSource: [] } as Feature)
const features = new Features()
const title = ref("Feature Config")
const formType = ref("add")
const originalFeatureConfig = ref({} as FeatureConfig)
const featureKey = ref("")
const id = ref("")
const saData = ref([] as any[])
const isViewModel = ref(false)
let trackingDataIndex = 0
onBeforeMount(async () => {
  title.value =
    (route.query.type == "edit" ? "编辑" : route.query.type == "view" ? "查看" : "新增") +
    (route.query.featureName || "Feature Config")
  isViewModel.value = route.query.type == "view"
  formType.value = route.query.type == "edit" ? "edit" : route.query.type == "view" ? "view" : "add"
  featureKey.value = "" + route.query.featureKey
  // featureKey 是点击feature 模块跳转过来的
  if (route.query.featureKey) {
    feature.value = await features.fetch(route.query.featureKey.toString())
    feature.value.dataSource = [transTempToTreeDataSource(feature.value.template)]
  }

  if (route.query.id) {
    id.value = route.query.id as string
    featureConfig.value = await featureConfigs.fetchById(featureKey.value, route.query.id as string)
    if (!featureConfig.value.filterCondition) {
      featureConfig.value.filterCondition = defaultFilterCondition()
    }
    if (!_.isNil(featureConfig.value.data)) {
      // 如果返回的数据是一个对象：

      const templateInfo = mapDataToTemplate("", featureConfig.value.data, feature.value.template)

      feature.value.dataSource = [templateInfo]
    }
  } else {
    featureConfig.value = {
      data: getDefaultValue(feature.value),
      filterCondition: defaultFilterCondition()
    } as FeatureConfig
  }

  saData.value = featureConfig.value.saData
    ? Object.keys(featureConfig.value.saData).map((key) => {
        return {
          index: trackingDataIndex++,
          key: key,
          value: featureConfig.value.saData[key]
        }
      })
    : []
})
// 防止只有一个Boolean 值为false 或者NUMBER，并且数字为 0
const isSimpleBooleanOrNumber = () => {
  return [TemplateDataType.NUMBER, TemplateDataType.BOOLEAN].includes(_.get(feature.value, "template.dataType", null))
}
nextTick(() => {
  if (form.value) {
    form.value.clearValidate()
  }
})
const formRules = {
  title: [{ required: true, message: "Please enter title", trigger: "submit" }],
  data: [{ required: isSimpleBooleanOrNumber(), message: "Please enter data", trigger: "submit" }],
  endTime: [
    {
      trigger: "blur",
      validator: (rule: any, value: any, callback: Function) => {
        if (!featureConfig.value.startTime) {
          callback()
        } else {
          if (!value) {
            callback()
            return
          }

          if (new Date(value).getTime() > new Date(featureConfig.value.startTime).getTime()) {
            callback()
          } else {
            callback(new Error("结束日期不能在起始日期前，请重新选择"))
          }
        }
      }
    }
  ]
}
const form = ref<FormInstance>(null)
const filterConditionRef = ref<FormInstance>(null)

const deleteTrackingData = (index: number) => {
  saData.value.splice(index, 1)
}
const addTrackingData = () => {
  saData.value.push({
    index: trackingDataIndex++,
    key: "",
    value: ""
  })
}

const getKey = (): string => {
  const keyParam = feature.value.template.keyParam
    ? "data"
    : (() => {
        const keyItem = (feature.value.template.items || []).find((item: any) => item.keyParam)
        return keyItem ? "data." + keyItem.key : ""
      })()
  return keyParam ? _.get(featureConfig.value, keyParam) || "" : featureConfig.value.key || ""
}

const submitForm = async () => {
  if (!featureKey.value) {
    ElMessage.error("featureKey 不能为空！")
    return
  }
  form?.value?.validate(async (valid: boolean) => {
    if (valid) {
      await filterConditionRef?.value?.$refs?.filterConditionFormRef?.validate(async (valid: boolean) => {
        if (valid) {
          try {
            // featureConfig.value.templateData = JSON.stringify(feature.value.dataSource)
            // debugger
            // if (_.isObject(tempRet)) {
            //   featureConfig.value.data = JSON.stringify(tempRet)
            // } else {
            featureConfig.value.data = convertConfigObject(feature.value.dataSource[0])
            // }
            featureConfig.value.featureKey = featureKey.value
            featureConfig.value.status = StaticStatus.DRAFT
            featureConfig.value.key = getKey()
            featureConfig.value.saData = saData.value.reduce((acc, cur) => {
              acc[cur.key] = cur.value
              return acc
            }, {} as any)
            if (formType.value.toLowerCase() == "add") {
              const response = await featureConfigs.create(featureKey.value, featureConfig.value)
              console.log("response = ", response)
              // const ret = await features.updateConfig(featureKey.value, response.id, response)
              // console.log(ret)
            } else {
              await featureConfigs.update(featureKey.value, id.value, featureConfig.value)
            }
            ElMessage.success("操作成功！")
            setTimeout(() => {
              router.push({
                path: "/feature/config/list",
                query: { featureKey: featureKey.value }
              })
            }, 1000)
          } catch (error: any) {
            ElMessage.error("操作失败，请稍后重试！")
          }
        } else {
          return false
        }
      })
    } else {
      return false
    }
  })
}

const resetForm = () => {
  if (formType.value.toLowerCase() == "add") {
    form.value.resetFields()
    filterConditionRef.value.resetFields()
  } else {
    featureConfig.value = originalFeatureConfig.value
  }
}
</script>

<style scoped>
.track-cls {
  display: flex;
  flex-direction: column;
}

span.title {
  float: left;
  font-size: 14px;
  font-weight: bold;
}
</style>
