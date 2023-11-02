<template>
  <el-card>
    <div class="header">
      <span>实验搜索：</span>
      <el-input class="search-input" v-model="pageSearchParams.keyword" placeholder="请输入实验名称或ID" />
      <div class="operation-btn-group">
        <el-button @click="() => (pageSearchParams.keyword = '')">重置</el-button>
        <el-button type="primary">查询</el-button>
      </div>
    </div>

    <el-button class="add-experiment-button" plain icon="Plus" @click="() => router.push({ path: '/experiment/add' })"
      >添加
    </el-button>

    <el-table :data="experiments" border style="width: 100%; margin-top: 30px">
      <el-table-column align="center" prop="name" label="实验名称" />
      <el-table-column align="center" prop="status" label="实验状态">
        <template #default="scope">
          {{ EXPERIMENT_STATUS_MAPPING[scope.row?.status] }}
        </template>
      </el-table-column>
      <el-table-column align="center" prop="createdAt" label="创建时间" />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <div style="display: flex">
            <el-button type="success" size="small">查看</el-button>
            <el-button type="primary" size="small" v-if="scope.row?.status === Status.DRAFT">编辑</el-button>
            <el-button type="primary" size="small" v-if="scope.row?.status === Status.DRAFT">发布</el-button>
            <el-button type="primary" size="small" v-if="scope.row?.status === Status.RUNNING">暂停</el-button>
            <el-button type="danger" size="small" :disabled="scope.row?.status === Status.DRAFT">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 20px"
      :current-page="pageSearchParams.page"
      :page-size="pageSearchParams.size"
      :page-sizes="[10, 30, 50, 100]"
      layout="->,total, sizes, prev, pager, next, jumper"
      :total="totalExperiment"
      @size-change="(currentSize: number) => pageSearchParamsChange('size', currentSize)"
      @current-change="(currentPage: number) => pageSearchParamsChange('page', currentPage)"
    />
  </el-card>
</template>

<script setup lang="ts">
import router from "@/router"
import { reactive, ref } from "vue"
import { EXPERIMENT_STATUS_MAPPING } from "@/constants/constants"
import _ from "lodash"
import { Status } from "@/model/Status"

const totalExperiment = ref<number>(0)
const pageSearchParams = reactive<{
  page: number
  size: number
  keyword: string
  [key: string]: number | string | boolean
}>({
  page: 1,
  size: 10,
  keyword: ""
})
const experiments = reactive<any[]>([
  {
    id: "afe2566a-5998-492b-9eea-8a5a1e5ee4ee",
    name: "早餐换购弹窗排序推荐",
    description: "此实验测试了这些两种不同弹窗UI对于用户下单的提升率",
    featureKey: "feature-key",
    samplePercentage: 100,
    buckets: [],
    status: Status.PUBLISHED,
    createdAt: "2023-07-23 17:26:00"
  },
  {
    id: "afe2566a-5998-492b-9eea-8a5a1e5ee4ee",
    name: "菜单UI推荐",
    description: "此实验测试了这些两种UI对于用户下单的提升率",
    featureKey: "feature-key",
    samplePercentage: 100,
    buckets: [],
    status: Status.DRAFT,
    createdAt: "2023-07-23 17:26:00"
  },
  {
    id: "afe2566a-5998-492b-9eea-8a5a1e5ee4ee",
    name: "运行中实验",
    description: "运行哦",
    featureKey: "feature-key",
    samplePercentage: 100,
    buckets: [],
    status: Status.RUNNING,
    createdAt: "2023-07-23 17:26:00"
  }
])

const pageSearchParamsChange = (field: string, value: number | string | boolean): void => {
  if (_.has(pageSearchParams, field)) {
    _.set(pageSearchParams, field, value)
  }
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
