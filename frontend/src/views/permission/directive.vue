<script lang="ts" setup>
import { ref } from "vue"
import { checkPermission } from "@/utils/permission" // checkPermission 权限判断函数
import SwitchRoles from "./components/SwitchRoles.vue"

/** key 是为了能每次切换权限的时候重新初始化指令 */
const key = ref(1)
const handleRolesChange = () => {
  key.value++
}
</script>

<template>
  <div class="app-container">
    <SwitchRoles @change="handleRolesChange" />
    <div :key="key" class="margin-top">
      <div>
        <span v-permission="['1']" class="permission-alert">
          只有
          <el-tag>1</el-tag>可以看见这个
        </span>
        <el-tag v-permission="['1']" class="permission-code" type="info" size="large">
          v-permission="['1']"
        </el-tag>
      </div>
      <div>
        <span v-permission="['2']" class="permission-alert">
          只有
          <el-tag>2</el-tag>可以看见这个
        </span>
        <el-tag v-permission="['2']" class="permission-code" type="info" size="large">
          v-permission="['2']"
        </el-tag>
      </div>
      <div>
        <span v-permission="['1', '2']" class="permission-alert">
          两者
          <el-tag>1</el-tag>和 <el-tag>2</el-tag>都可以看见这个
        </span>
        <el-tag v-permission="['1', '2']" class="permission-code" type="info" size="large">
          v-permission="['1', '2']"
        </el-tag>
      </div>
    </div>
    <div :key="'checkPermission' + key" class="margin-top">
      <el-tag type="info" size="large">
        在某些情况下，不适合使用 v-permission。例如: Element Plus 的 el-tab-pane 或 el-table-column 以及其它动态渲染 Dom
        的场景。你只能通过手动设置 v-if 来实现。
      </el-tag>
      <el-tabs type="border-card" class="margin-top">
        <el-tab-pane v-if="checkPermission(['1'])" label="1">
          1 可以看见这个
          <el-tag class="permission-code" type="info"> v-if="checkPermission(['1'])" </el-tag>
        </el-tab-pane>
        <el-tab-pane v-if="checkPermission(['2'])" label="1">
          2 可以看见这个
          <el-tag class="permission-code" type="info"> v-if="checkPermission(['2'])" </el-tag>
        </el-tab-pane>
        <el-tab-pane v-if="checkPermission(['1', '2'])" label="1 和 2">
          两者 1 和 2 都可以看见这个
          <el-tag class="permission-code" type="info"> v-if="checkPermission(['1', '2'])" </el-tag>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.permission-alert {
  width: 320px;
  margin-top: 15px;
  background-color: #f0f9eb;
  color: #67c23a;
  padding: 8px 16px;
  border-radius: 4px;
  display: inline-block;
}

.permission-code {
  margin-left: 15px;
}

.margin-top {
  margin-top: 30px;
}
</style>
