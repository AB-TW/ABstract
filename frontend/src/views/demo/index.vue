<template>
  <div>
    <el-card>
      <div class="header">
        <el-input v-model="clientId" placeholder="请输入ClientId" class="search-input">
          <template #append>
            <el-button icon="Search" @click="fetchFeatureConfig" />
          </template>
        </el-input>
      </div>

      <el-card>
        <el-form>
          <el-form-item label="Response">
            <el-input type="textarea" placeholder="" v-model="response" />
            <el-button :type="buttonType">查询</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { Customers } from "@/domain/Customers"

const clientId = ref<string>("123456")
const buttonType = ref<string>("primary")
const response = ref<string>("")
buttonType.value = "success"
const customers = new Customers()

const fetchFeatureConfig = () => {
  customers
    .getCustomerFeatureConfigs(
      {
        customerId: "test",
        clientId: clientId.value
      },
      "demoFeature"
    )
    .then((res) => {
      if (res.length > 0 && res[0]?.data === true) {
        buttonType.value = "success"
      } else {
        buttonType.value = "danger"
      }
      response.value = JSON.stringify(res, null, 2)
      console.log(res)
    })
    .catch((err) => {
      console.log(err)
    })
}
</script>

<style scoped lang="scss">
.header {
  display: flex;
  align-items: center;
  gap: 20px;

  .search-input {
    width: 400px;
  }
}
</style>
