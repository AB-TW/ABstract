<template>
  <div>
    <el-card>
      <el-input
        v-model="searchForm.featureKey"
        clearable
        placeholder="请输入featureKey或名称"
        class="input-with-select"
        @clear="searchData"
      >
        <template #append>
          <el-button icon="Search" @click="searchData" />
        </template>
      </el-input>
      <el-button
        type="primary"
        @click="() => router.push({ path: '/feature/edit', query: { type: 'add' } })"
        class="search-btn"
      >
        <el-icon>
          <Plus />
        </el-icon>
        新增
      </el-button>
      <el-table :data="tableData" border style="width: 100%; margin-top: 50px">
        <el-table-column label="Feature Key" width="200">
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="
                router.push({
                  path: '/feature-config',
                  query: { featureKey: scope.row.featureKey }
                })
              "
            >
              {{ scope.row.featureKey }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="200" />
        <el-table-column prop="description" label="描述" width="300" />
        <el-table-column prop="enabled" label="使能" width="240" />
        <el-table-column prop="status" label="状态" width="240" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="goToWatch(scope.row.featureKey, 'view')">查看</el-button>
            <el-button type="primary" size="small" @click="goToWatch(scope.row.featureKey, 'edit')">编辑</el-button>
            <el-button
              v-if="scope.row?.enabled == true"
              type="info"
              size="small"
              @click="disable(scope.row.featureKey, 'disable')"
              >禁用
            </el-button>
            <el-button v-else type="success" size="small" @click="disable(scope.row.featureKey, 'enable')"
              >启用
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
import { onMounted, reactive, ref } from "vue"
import { useRouter } from "vue-router"
import { FeatureFlag, FeatureFlags } from "../../domain/FeatureFlags"
import { ElMessage } from "element-plus"

const router = useRouter()

const featureFlags = new FeatureFlags()
const total = ref(0)

const searchForm = reactive({
  page: 1,
  size: 10,
  featureKey: ""
})

const tableData = ref([] as FeatureFlag[])
const goToWatch = (key:string, type: string) => {
  router.push({
    path: "/feature/edit",
    query: {
      type,
      featureKey: key
    }
  })
}

const deleteData = (id: string) => {
  featureFlags
    .delete(id)
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
  featureFlags
    .fetchAll(searchForm.page > 1 ? searchForm.page - 1 : 0, searchForm.size, searchForm.featureKey)
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

const disable = (featureKey: string, type: string) => {
  featureFlags
    .disable(featureKey, type)
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

const handleCurrentChange = () => {
  searchData()
}

onMounted(() => {
  searchData()
})
</script>

<style scoped>
.input-with-select {
  width: 400px;
  float: left;
}

.search-btn {
  margin-left: 20px;
}
</style>
