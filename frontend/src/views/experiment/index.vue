<template>
  <el-card>
    <div class="header">
      <span>实验搜索：</span>
      <el-input class="search-input" v-model="pageSearchParams.key" placeholder="请输入实验名称或ID" />
      <div class="operation-btn-group">
        <el-button @click="() => (pageSearchParams.key = '')">重置</el-button>
        <el-button type="primary" @click="() => getExperiments()">查询</el-button>
      </div>
    </div>

    <el-button class="add-experiment-button" plain icon="Plus" @click="() => router.push({ path: '/experiment/edit' })"
      >添加
    </el-button>

    <el-table :data="experiments" border style="width: 100%; margin-top: 30px">
      <el-table-column align="center" prop="name" label="实验名称" />
      <el-table-column align="center" prop="status.staticStatus" label="实验状态">
        <template #default="scope">
          {{
            EXPERIMENT_STATUS_MAPPING[
              scope.row?.status.staticStatus === Status.PUBLISHED
                ? scope.row?.status.status
                : scope.row?.status.staticStatus
            ]
          }}
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createdAt" label="创建时间" />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <div style="display: flex">
            <el-button
              type="success"
              size="small"
              @click="() => router.push({ path: `/experiment/edit/${scope.row.id}`, query: { type: 'view' } })"
              >查看
            </el-button>
            <el-button
              type="primary"
              size="small"
              v-if="scope.row?.status.staticStatus === Status.DRAFT"
              @click="() => router.push({ path: `/experiment/edit/${scope.row.id}` })"
              >编辑
            </el-button>
            <el-button
              type="primary"
              size="small"
              v-if="scope.row?.status.staticStatus === Status.DRAFT"
              @click="publishExperiment(scope.row.id)"
              >发布
            </el-button>
            <el-button type="primary" size="small" v-if="scope.row?.status.status === Status.RUNNING">暂停</el-button>
            <el-button type="danger" size="small" :disabled="scope.row?.status.staticStatus !== Status.DRAFT"
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
import { reactive, ref } from "vue"
import { EXPERIMENT_STATUS_MAPPING } from "@/constants/constants"
import _ from "lodash"
import { Status } from "@/model/Status"
import { Experiment, Experiments } from "@/domain/Experiments"
import { PageResponse } from "@/domain/Features"
import { ElMessage } from "element-plus"
import { ExperimentQueryRequest } from "@/model/request/ExperimentQueryRequest"

// API
const experimentsApi = new Experiments()
// View-Model
const experiments = reactive<Experiment[]>([])
const totalExperiment = ref<number>(0)
const pageSearchParams = reactive<ExperimentQueryRequest>({
  page: 0,
  pageSize: 10,
  key: undefined,
  status: undefined
})
// about experiment function
const getExperiments = () => {
  experimentsApi
    .page(pageSearchParams)
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
  experimentsApi.publish(id).then((resp) => {
    console.log(resp)
    getExperiments()
  })
}
</script>

<style scoped lang="scss">
.header {
  display: flex;
  align-items: center;

  .search-input {
    flex: 1;
  }

  .operation-btn-group {
    display: flex;
    margin-left: 25px;
  }
}

.add-experiment-button {
  width: 100%;
  margin-top: 15px;
  border-style: dashed;
}
</style>
