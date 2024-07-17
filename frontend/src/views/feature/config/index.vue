<template>
  <div>
    <el-card>
      <el-card-header>
        <div class="card-header" style="padding: 5px">
          <span class="title"> Feature: {{ featureName }}</span>
          <el-button size="small" style="float: right" @click="router.push({ path: '/feature/list' })">返回</el-button>
        </div>
      </el-card-header>
      <el-divider />
      <div class="header">
        <el-input
          v-model="searchForm.key"
          clearable
          placeholder="请输入关键字"
          class="search-input"
          @clear="searchData"
        >
          <template #append>
            <el-button icon="Search" @click="searchData" />
          </template>
        </el-input>
        <div class="operation-btn-group">
          <el-radio-group v-model="searchForm.status" @change="searchData" class="status-check-group">
            <el-radio-button label="ALL">全部</el-radio-button>
            <el-radio-button label="DRAFT">草稿</el-radio-button>
            <el-radio-button label="NOT_BEGIN">未生效</el-radio-button>
            <el-radio-button label="RUNNING">生效中</el-radio-button>
            <el-radio-button label="EXPIRED">已过期</el-radio-button>
            <el-radio-button label="DISABLED">已撤销</el-radio-button>
          </el-radio-group>
        </div>
        <el-button
          type="primary"
          class="add-btn"
          @click="
            () =>
              router.push({
                path: '/feature/config/edit',
                query: {
                  type: 'add',
                  featureKey: featureKey,
                  featureName,
                  feature: encodeURI(JSON.stringify(feature))
                }
              })
          "
        >
          <el-icon>
            <Plus />
          </el-icon>
          新增
        </el-button>
      </div>
      <el-table :data="tableData" border style="width: 100%; margin-top: 30px">
        <!--        <el-table-column label="id" prop="id" width="320" />-->
        <!--        <el-table-column prop="featureKey" label="Feature Key" width="180" />-->
        <el-table-column prop="title" label="标题" width="230" />
        <el-table-column label="有效期" width="350">
          <template #default="scope">
            <span v-if="!scope.row?.startTime && !scope.row?.endTime">长期有效</span>
            <span v-else-if="scope.row?.startTime && !scope.row?.endTime">{{ scope.row?.startTime }} ～ 不限</span>
            <span v-else-if="!scope.row?.startTime && scope.row?.endTime"> 不限 ～ {{ scope.row?.endTime }}</span>
            <span v-else>{{ scope.row?.startTime }} ～ {{ scope.row?.endTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="200">
          <template #default="scope">
            <el-tag v-if="scope.row?.status == 'DRAFT'" type="">草稿</el-tag>
            <el-tag v-else-if="scope.row?.status == 'NOT_BEGIN'" type="warning">未生效</el-tag>
            <el-tag v-else-if="scope.row?.status == 'RUNNING'" type="success">生效中</el-tag>
            <el-tag v-else-if="scope.row?.status == 'EXPIRED'" type="info">已过期</el-tag>
            <el-tag v-else-if="scope.row?.status == 'DISABLED'" type="danger">已撤销</el-tag>
            <el-tag v-else type="danger">已删除</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="200" />
        <el-table-column prop="updatedAt" label="更新时间" width="200" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button v-if="scope.row?.status !== 'RUNNING'" type="warning" size="small" @click="publish(scope.row.id)"
              >发布
            </el-button>
            <el-button v-if="scope.row?.status === 'RUNNING'" type="warning" size="small" @click="disable(scope.row.id)"
              >撤销
            </el-button>
            <el-button type="danger" size="small" @click="deleteData(scope.row.id)">删除</el-button>
            <el-button
              v-if="scope.row?.status !== 'RUNNING'"
              size="small"
              @click="
                () =>
                  router.push({
                    path: '/feature/config/edit',
                    query: {
                      featureKey: featureKey,
                      featureName,
                      type: 'edit',
                      id: scope.row.id,
                      feature: encodeURI(JSON.stringify(feature))
                    }
                  })
              "
            >
              编辑
            </el-button>
            <el-button
              v-if="scope.row?.status === 'RUNNING'"
              size="small"
              @click="
                () =>
                  router.push({
                    path: '/feature/config/edit',
                    query: {
                      featureKey: featureKey,
                      featureName,
                      type: 'view',
                      id: scope.row.id,
                      feature: encodeURI(JSON.stringify(feature))
                    }
                  })
              "
            >
              查看
            </el-button>
            <el-button v-if="scope.row?.status !== 'DISABLED'" type="primary" size="small" @click="top(scope.row.id)"
              >置顶
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        style="margin-top: 20px"
        :current-page="searchForm.page"
        :page-size="searchForm.size"
        :page-sizes="[10, 20, 30, 40]"
        layout="->,total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onBeforeMount, onMounted, reactive, ref } from "vue"
import { useRoute, useRouter } from "vue-router"

import { FeatureConfig, FeatureConfigs } from "@/domain/FeatureConfigs"
import { ElMessage } from "element-plus"
import { Features } from "@/domain/Features"

const route = useRoute()
const feature = ref()
const features = new Features()

const featureKey = ref()
const featureName = ref("")

onBeforeMount(() => {
  featureKey.value = route.query.featureKey
  featureName.value = <string>route.query.featureName
  feature.value = await features.fetch(featureKey.value)
  featureName.value = featureName.value || feature.value.name
})

const router = useRouter()

const featureConfigs = new FeatureConfigs()
const total = ref(0)

const searchForm = reactive({
  page: 1,
  size: 10,
  key: "",
  status: "ALL"
})

const tableData = ref([] as FeatureConfig[])

const deleteData = (id: string) => {
  featureConfigs
    .delete(featureKey.value, id)
    .then(() => {
      ElMessage.success("删除成功")
      searchData()
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("删除失败，请重试")
    })
}
const searchData = () => {
  featureConfigs
    .fetchAll(
      searchForm.key,
      searchForm.page > 1 ? searchForm.page - 1 : 0,
      searchForm.size,
      featureKey.value,
      searchForm.status === "ALL" ? [] : [searchForm.status]
    )
    .then((res) => {
      tableData.value = res.content
      total.value = res.total
      searchForm.page = res.page + 1
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("获取数据失败，请重试")
    })
}

const publish = (id: string) => {
  featureConfigs
    .publish(featureKey.value, id)
    .then(() => {
      ElMessage.success("发布成功")
      searchData()
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("发布失败，请重试")
    })
}

const disable = (id: string) => {
  featureConfigs
    .disable(featureKey.value, id)
    .then(() => {
      ElMessage.success("操作成功")
      searchData()
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("操作失败，请重试")
    })
}

const handleSizeChange = () => {
  searchData()
}

const handleCurrentChange = (value: number) => {
  searchForm.page = value
  searchData()
}

const top = (id: string) => {
  featureConfigs
    .top(featureKey.value, id)
    .then(() => {
      ElMessage.success("操作成功")
      searchData()
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("操作失败，请重试")
    })
}

onMounted(() => {
  searchData()
})
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  gap: 20px;

  .search-input {
    width: 400px;
  }

  .operation-btn-group {
    display: flex;
  }
}

span.title {
  float: left;
  font-size: 14px;
  font-weight: bold;
}

.add-btn {
  margin-left: 20px;
}

.status-check-group {
  margin-left: 20px;
}
</style>
