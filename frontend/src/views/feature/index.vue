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

      <el-radio-group v-model="searchForm.enabled" @change="searchData"
                      class="status-check-group">
        <el-radio-button label="null">全部</el-radio-button>
        <el-radio-button label="true">启用</el-radio-button>
        <el-radio-button label="false">禁用</el-radio-button>
      </el-radio-group>
      <el-button
        type="primary"
        @click="() => router.push({ path: '/feature/edit', query: { type: 'add' } })"
        class="add-btn"
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
                  path: '/feature/config/list',
                  query: { featureKey: scope.row.featureKey, featureName: scope.row.name }
                })
              "
            >
              {{ scope.row.featureKey }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="200" />
        <el-table-column prop="description" label="描述" width="300" />
        <el-table-column label="是否启用" width="240" >
          <template #default="scope">
            <el-tag v-if="scope.row?.enabled == true" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="goToWatch(scope.row.featureKey, 'view')">查看</el-button>
            <el-button type="primary" size="small" @click="goToWatch(scope.row.featureKey, 'edit')">编辑</el-button>
            <el-button
              v-if="scope.row?.enabled == true"
              type="info"
              size="small"
              @click="disable(scope.row.featureKey)"
              >禁用
            </el-button>
            <el-button v-else type="success" size="small" @click="enable(scope.row.featureKey)"
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
import { Feature, Features } from "../../domain/Features"
import { ElMessage } from "element-plus"

const router = useRouter()

const features = new Features()
const total = ref(0)

const searchForm = reactive({
  page: 1,
  size: 10,
  featureKey: "",
  enabled: "null"
})

const tableData = ref([] as Feature[])
const goToWatch = (key:string, type: string) => {
  router.push({
    path: "/feature/edit",
    query: {
      type,
      featureKey: key
    }
  })
}

const searchData = () => {
  features
    .fetchAll(searchForm.page > 1 ? searchForm.page - 1 : 0, searchForm.size, searchForm.featureKey,
       (searchForm.enabled == "null" ? null : searchForm.enabled == "true"))
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

const disable = (featureKey: string) => {
  features
    .disable(featureKey)
    .then(() => {
      ElMessage.success("操作成功")
      searchData()
    })
    .catch((err) => {
      console.log(err)
      ElMessage.error("操作失败，请重试")
    })
}

const enable = (featureKey: string) => {
  features
      .enable(featureKey)
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

.add-btn {
  margin-left: 20px;
}
.status-check-group {
  margin-left: 20px;
}
</style>
