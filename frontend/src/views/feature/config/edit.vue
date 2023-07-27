<template>
  <div>
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">{{ title }}</span>
          <el-button size="mini" style="float: right" @click="router.go(-1)">返回</el-button>
        </div>
      </template>
      <el-form :model="featureConfig" :rules="formRules" ref="form" label-width="140px">
        <el-form-item label="配置标题" prop="title">
          <el-input v-model="featureConfig.title" autocomplete="off" />
        </el-form-item>
        <el-form-item label="配置描述" prop="description">
          <el-input v-model="featureConfig.description" autocomplete="off" />
        </el-form-item>
        <el-form-item label="配置内容" prop="data">
          <FeatureTemplateConfig
            :key="featureFlag.dataSource"
            :isViewModel="true"
            :dataSource="featureFlag.dataSource"
          />
        </el-form-item>
        <el-form-item label="生效时间" prop="timeRange">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="To"
            @calendar-change="calendarChange"
            start-placeholder="Start date"
            end-placeholder="End date"
          />
        </el-form-item>
        <el-form-item label="埋点数据" prop="trackingData">
          <div class="track-cls">
            <tracking-data-item
              v-for="(item, index) in saData"
              :key="item.index"
              :item="item"
              :index="index"
              @deleteItem="deleteTrackingData(index)"
            />
            <div>
              <el-button type="primary" @click="addTrackingData">
                <el-icon>
                  <Plus />
                </el-icon>
              </el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="default" @click="resetForm()">重置</el-button>
          <el-button type="primary" @click="submitForm()">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage } from "element-plus"
import { FeatureConfig, FeatureConfigs } from "../../../domain/FeatureConfigs"
import {
  FeatureFlag,
  FeatureFlags,
  FeatureFlagTemplate,
  getDefaultValue,
  TemplateDataType
} from "../../../domain/FeatureFlags"
import TrackingDataItem from "@/components/TrackingDataItem.vue"
// import FeatureTemplateConfig from "@/components/FeatureFlagTemplateItemNew.vue"
import FeatureTemplateConfig from "@/components/FeatureTemplateItemConfig.vue"
import { convertConfigObject, transTempToTreeDataSource } from "@/utils"
import { StaticStatus } from "@/constants/common"
import _ from "lodash"

const route = useRoute()
const router = useRouter()
const featureConfigs = new FeatureConfigs()
const featureConfig = ref({} as FeatureConfig)
const featureFlag = ref({} as FeatureFlag)
const featureFlags = new FeatureFlags()

const title = ref("Feature Config")
const formType = ref("add")
const originalFeatureConfig = ref({} as FeatureConfig)
const featureKey = ref("")
const id = ref("")
const saData = ref([] as any[])
const timeRange = ref([])
let trackingDataIndex = 0
onBeforeMount(async () => {
  title.value = (route.query.type == "edit" ? "编辑" : "新增") + "Feature Config"
  formType.value = route.query.type == "edit" ? "edit" : "add"
  featureKey.value = "" + route.query.featureKey
  // featureKey 是点击feature 模块跳转过来的
  if (formType.value == "add") {
    if (route.query.featureKey) {
      const res = await featureFlags.fetch(route.query.featureKey.toString())
      featureFlag.value = res
      featureFlag.value.dataSource = [
        transTempToTreeDataSource(featureFlag.value.template)
      ] as Array<FeatureFlagTemplate>
    } else {
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
    }
  }

  if (route.query.id) {
    id.value = route.query.id
    featureConfig.value = await featureConfigs.fetchById(featureKey.value, route.query.id)
    timeRange.value[0] = new Date(featureConfig.value.startTime)
    timeRange.value[1] = new Date(featureConfig.value.endTime)
    if (featureConfig.value.templateData) {
      featureFlag.value.dataSource = JSON.parse(featureConfig.value.templateData)
      console.log("###", featureFlag.value.dataSource)
    }
  } else {
    featureConfig.value = {
      timeRange: {
        startDate: "",
        endDate: ""
      },
      trackingData: [],
      data: getDefaultValue(featureFlag.value)
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

const formRules = {
  title: [{ required: true, message: "Please enter title", trigger: "submit" }],
  data: [{ required: true, message: "Please enter data", trigger: "submit" }]
}

const form = ref<ElForm>(null)

const deleteTrackingData = (index: number) => {
  saData.value.splice(index, 1)
}

const calendarChange = (values) => {
  featureConfig.value.startTime = values[0]
  featureConfig.value.endTime = values[1]
}
const addTrackingData = () => {
  saData.value.push({
    index: trackingDataIndex++,
    key: "",
    value: ""
  })
}

const submitForm = async () => {
  if (!featureKey.value) {
    ElMessage.error("featureKey 不能为空！")
    return
  }
  form?.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        featureConfig.value.templateData = JSON.stringify(featureFlag.value.dataSource)
        const tempRet = convertConfigObject(featureFlag.value.dataSource[0])
        if (_.isObject(tempRet)) {
          featureConfig.value.data = JSON.stringify(tempRet)
        } else {
          featureConfig.value.data = tempRet
        }
        debugger
        featureConfig.value.featureKey = featureKey.value
        featureConfig.value.status = StaticStatus.DRAFT
        featureConfig.value.saData = saData.value.reduce((acc, cur) => {
          acc[cur.key] = cur.value
          return acc
        }, {} as any)
        if (formType.value.toLowerCase() == "add") {
          const response = await featureConfigs.create(featureKey.value, featureConfig.value)
          console.log("response = ", response)
          // const ret = await featureFlags.updateConfig(featureKey.value, response.id, response)
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
}

const resetForm = () => {
  if (formType.value.toLowerCase() == "add") {
    form.value.resetFields()
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
</style>
