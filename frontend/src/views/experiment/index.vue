<template>
  <el-card>
    <div class="header">
      <el-select v-model="pageSearchParams.featureKey" @change="getExperiments" placeholder="特性" style="width: 240px">
        <el-option key="000" label="全部特性" style="font-weight: bold" :value="null" />
        <el-option v-for="item in featureList" :key="item.featureKey" :label="item.name" :value="item.featureKey" />
      </el-select>
      <el-input
        v-model="pageSearchParams.key"
        clearable
        placeholder="请输入实验名称"
        class="search-input"
        @clear="getExperiments"
      >
        <template #append>
          <el-button icon="Search" @click="() => getExperiments()" />
        </template>
      </el-input>
      <div class="operation-btn-group">
        <el-radio-group v-model="status" @change="() => getExperiments()">
          <el-radio-button :label="undefined">全部</el-radio-button>
          <el-radio-button v-for="(value, key) in EXPERIMENT_STATUS_TEXT_MAPPING" :label="key">
            {{ value }}
          </el-radio-button>
        </el-radio-group>
      </div>
      <el-button type="primary" icon="Plus" @click="() => router.push({ path: '/experiment/edit' })">新增</el-button>
    </div>

    <el-table :data="experiments" border style="width: 100%; margin-top: 30px">
      <el-table-column prop="name" label="实验名称" />
      <el-table-column prop="featureKey" label="特性">
        <template #default="scope">
          {{ getFeatureName(scope.row?.featureKey) }}
        </template>
      </el-table-column>
      <el-table-column prop="status.staticStatus" label="实验状态">
        <template #default="scope">
          <el-tag :type="EXPERIMENT_STATUS_TAG_STATUS_MAPPING[scope.row?.status?.status]">
            {{ EXPERIMENT_STATUS_TEXT_MAPPING[scope.row?.status?.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="有效期" width="350">
        <template #default="scope">
          <span v-if="!scope.row?.status.startTime && !scope.row?.status.endTime">长期有效</span>
          <span v-else-if="scope.row?.status.startTime && !scope.row?.status.endTime"
            >{{ scope.row?.startTime }} ～ 不限</span
          >
          <span v-else-if="!scope.row?.status.startTime && scope.row?.status.endTime">
            不限 ～ {{ scope.row?.endTime }}</span
          >
          <span v-else>{{ scope.row?.status.startTime }} ～ {{ scope.row?.status.endTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <div style="display: flex">
            <el-button
              size="small"
              @click="
                () =>
                  router.push({
                    path: `/experiment/edit/${scope.row.id}`,
                    query: scope.row?.status?.status === Status.STOPPED ? { type: 'view' } : undefined
                  })
              "
              >{{ scope.row?.status?.status === Status.STOPPED ? "查看" : "编辑" }}
            </el-button>
            <el-button
              type="primary"
              size="small"
              @click="
                () =>
                  router.push({
                    path: `/experiment/edit/${scope.row.id}`,
                    query: { type: 'copy' }
                  })
              "
            >
              复制
            </el-button>
            <el-button
              type="warning"
              size="small"
              v-if="[Status.DRAFT, Status.PAUSED].includes(scope.row?.status?.status)"
              @click="publishExperiment(scope.row.id)"
              >{{ scope.row?.status?.status === Status.DRAFT ? "发布" : "恢复" }}
            </el-button>
            <el-button
              type="warning"
              size="small"
              v-if="[Status.NOT_BEGIN, Status.COLLECTING].includes(scope.row?.status?.status)"
              @click="pausedExperiment(scope.row.id)"
              >暂停
            </el-button>
            <el-button
              type="danger"
              size="small"
              v-if="![Status.STOPPED, Status.DRAFT].includes(scope.row?.status?.status)"
              @click="stopExperiment(scope.row.id)"
              >停止
            </el-button>
            <el-button
              type="danger"
              size="small"
              v-if="scope.row?.status?.status === Status.DRAFT"
              @click="deleteExperiment(scope.row.id)"
              >删除
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 20px"
      :current-page="pageSearchParams.page + 1"
      :page-size="pageSearchParams.pageSize"
      :page-sizes="[10, 30, 50, 100]"
      layout="->,total, sizes, prev, pager, next, jumper"
      :total="totalExperiment"
      @size-change="(currentSize: number) => pageSearchParamsChange('pageSize', currentSize)"
      @current-change="(currentPage: number) => pageSearchParamsChange('page', currentPage - 1)"
    />
  </el-card>
</template>

<script setup lang="ts">
import router from "@/router"
import { onBeforeMount, reactive, ref } from "vue"
import { EXPERIMENT_STATUS_TAG_STATUS_MAPPING, EXPERIMENT_STATUS_TEXT_MAPPING } from "@/constants/constants"
import _ from "lodash"
import { Status } from "@/model/Status"
import { Experiment, Experiments } from "@/domain/Experiments"
import { Features, PageResponse } from "@/domain/Features"
import { ElMessage } from "element-plus"
import { ExperimentQueryRequest } from "@/model/request/ExperimentQueryRequest"

// API
const experimentsApi = new Experiments()
const features = new Features()
const featureList = ref([] as any[])
const featureNameMap = ref({})

onBeforeMount(async () => {
  featureList.value.push(...((await features.fetchAll(0, 100))?.content || []))
  featureNameMap.value = featureList.value.reduce((acc, cur) => {
    acc[cur.featureKey] = cur.name
    return acc
  }, {} as any)
})
// View-Model
const experiments = reactive<Experiment[]>([])
const totalExperiment = ref<number>(0)
const pageSearchParams = reactive<ExperimentQueryRequest>({
  page: 0,
  pageSize: 10,
  key: undefined,
  featureKey: null
})

const getFeatureName = (featureKey) => {
  return featureNameMap.value[featureKey] || ""
}
const status = ref<string | undefined>(undefined)
// about experiment function
const getExperiments = () => {
  experimentsApi
    .page({ ...pageSearchParams, status: [status.value as Status] })
    .then((res: PageResponse<Experiment>) => {
      experiments.splice(0, experiments.length)
      experiments.push(...res.content)
      totalExperiment.value = res.total
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("获取数据失败，请重试")
    })
}
const setup = () => {
  getExperiments()
}
setup()

const pageSearchParamsChange = (field: string, value: number | string | boolean): void => {
  if (_.has(pageSearchParams, field)) {
    _.set(pageSearchParams, field, value)
    getExperiments()
  }
}

const publishExperiment = (id: string) => {
  experimentsApi.publish(id).then((_) => {
    getExperiments()
  })
}
const pausedExperiment = (id: string) => {
  experimentsApi.paused(id).then((_) => {
    getExperiments()
  })
}
const stopExperiment = (id: string) => {
  experimentsApi.stop(id).then((_) => {
    getExperiments()
  })
}
const deleteExperiment = (id: string) => {
  experimentsApi.deleted(id).then((_) => {
    getExperiments()
  })
}
</script>

<style scoped lang="scss">
.header {
  display: flex;
  align-items: center;
  gap: 20px;

  .search-input {
    width: 240px;
  }

  .operation-btn-group {
    display: flex;
  }
}
</style>
