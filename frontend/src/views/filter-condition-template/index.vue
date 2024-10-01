<template>
  <el-card>
    <div class="header">
      <el-input
        v-model="pageSearchParams.key"
        clearable
        placeholder="请输入模板id/名称"
        class="search-input"
        @clear="getFilterConditionTemplates"
        @keyup.enter="getFilterConditionTemplates"
      >
        <template #append>
          <el-button icon="Search" @click="() => getFilterConditionTemplates()" />
        </template>
      </el-input>
      <el-button
        type="primary"
        icon="Plus"
        @click="
          () =>
            router.push({ path: '/template/filter-condition/edit', query: { featureKey: pageSearchParams.featureKey } })
        "
      >
        新增
      </el-button>
    </div>

    <el-table :data="filterConditionTemplates" border style="width: 100%; margin-top: 30px">
      <el-table-column prop="templateName" label="模板名称" />
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column prop="updatedAt" label="更新时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <div style="display: flex">
            <el-button
              size="small"
              @click="
                () =>
                  router.push({
                    path: `/template/filter-condition/edit/${scope.row.templateId}`
                  })
              "
              >编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteFilterConditionTemplate(scope.row.templateId)"
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
      :total="totalFilterConditionTemplate"
      @size-change="(currentSize: number) => pageSearchParamsChange('pageSize', currentSize)"
      @current-change="(currentPage: number) => pageSearchParamsChange('page', currentPage - 1)"
    />
  </el-card>
</template>
<script setup lang="ts">
import router from "@/router"
import { FilterConditions, FilterConditionTemplate } from "@/domain/FilterConditions"
import { onBeforeMount, reactive, ref } from "vue"
import { PageResponse } from "@/domain/Features"
import { ElMessage } from "element-plus"
import { FilterConditionQueryRequest } from "@/model/request/FilterConditionQueryRequest"
import _ from "lodash"

// API
const filterConditionsApi = new FilterConditions()

// View-Model
const filterConditionTemplates = reactive<FilterConditionTemplate[]>([])
const totalFilterConditionTemplate = ref<number>(0)
const pageSearchParams = reactive<FilterConditionQueryRequest>({
  page: 0,
  pageSize: 10,
  key: ""
})

// Vue Life Cycle Hooks
onBeforeMount(() => {
  getFilterConditionTemplates()
})

const getFilterConditionTemplates = () => {
  filterConditionsApi
    .getByPage(pageSearchParams)
    .then((res: PageResponse<FilterConditionTemplate>) => {
      filterConditionTemplates.splice(0, filterConditionTemplates.length)
      filterConditionTemplates.push(...res.content)
      totalFilterConditionTemplate.value = res.total
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("获取数据失败，请重试")
    })
}

const pageSearchParamsChange = (field: string, value: number | string | boolean): void => {
  if (_.has(pageSearchParams, field)) {
    _.set(pageSearchParams, field, value)
    getFilterConditionTemplates()
  }
}
const deleteFilterConditionTemplate = async (templateId: string) => {
  await filterConditionsApi.deleteById(templateId)
  getFilterConditionTemplates()
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
