<script lang="ts" setup>
import {ref, computed, onMounted, onUnmounted} from "vue"
import { ElMessage } from "element-plus"
import { Bell } from "@element-plus/icons-vue"
import NotifyList from "./NotifyList.vue"
import { type ListItem, notifyData, messageData, todoData } from "./data"
import {request} from "@/utils/service";
import {useUserStore} from "@/store/modules/user";

type TabName = "通知" | "消息" | "待办"

interface DataItem {
  name: TabName
  type: "primary" | "success" | "warning" | "danger" | "info"
  list: ListItem[]
}

/** 角标当前值 */
// const badgeValue = computed(() => {
//   return data.value.reduce((sum, item) => sum + item.list.length, 0)
// })
const badgeValue = ref(0)
const signSpooling = ref(false)

/** 角标最大值 */
const badgeMax = 99
/** 面板宽度 */
const popoverWidth = 350
/** 当前 Tab */
const activeName = ref<TabName>("通知")

//region 发起长轮询请求
// 判断是否已经存在
const judgeId = (id)=>{
  for (let i = 0; i < notifyData.length; i++) {
    if (id === notifyData[i].id){
      return true
    }
  }
  return false
}
function longPolling() {
  if (!signSpooling.value){ // 判断是否开启轮询
    return
  }
  request({
    url: "/long-polling",
    method: 'GET'
  },
  15000).then((res)=>{
    for (let i = 0; i < res.data.data.length; i++) {
      if (judgeId(res.data.data[i].id)){
        continue
      }
      badgeValue.value += 1
      notifyData.push(res.data.data[i])
    }
    // console.log(notifyData)
    try {
      setTimeout(() => {
        notifyData.splice(0, notifyData.length)
        badgeValue.value = 0
        longPolling();
      }, 1000*60*10)
    } catch (e) {
    }

  })
    .catch((res)=>{
      // console.log(res)
      try {
        setTimeout(() => {
          notifyData.splice(0, notifyData.length)
          badgeValue.value = 0
          longPolling();
        }, 1000*60*10)
      } catch (e) {
      }
    })
}

// 启动长轮询
onMounted(()=>{
  notifyData.splice(0, notifyData.length)
  badgeValue.value = 0
  signSpooling.value = true
  longPolling();
})
onUnmounted(()=>{
  signSpooling.value = true
  notifyData.splice(0, notifyData.length)
  badgeValue.value = 0
})
//endregion


/** 所有数据 */
const data = ref<DataItem[]>([
  // 通知数据
  {
    name: "通知",
    type: "primary",
    list: notifyData
  },
  // 消息数据
  {
    name: "消息",
    type: "danger",
    list: messageData
  },
  // 待办数据
  {
    name: "待办",
    type: "warning",
    list: todoData
  }
])

const handleHistory = () => {
  ElMessage.success(`跳转到${activeName.value}历史页面`)
}
</script>

<template>
  <div class="notify">
    <el-popover placement="bottom" :width="popoverWidth" trigger="click">
      <template #reference>
        <el-badge :value="badgeValue" :max="badgeMax" :hidden="badgeValue === 0">
          <el-tooltip effect="dark" content="消息通知" placement="bottom">
            <el-icon :size="20">
              <Bell />
            </el-icon>
          </el-tooltip>
        </el-badge>
      </template>
      <template #default>
        <el-tabs v-model="activeName" class="demo-tabs" stretch>
          <el-tab-pane v-for="(item, index) in data" :name="item.name" :key="index">
            <template #label>
              {{ item.name }}
              <el-badge :value="item.list.length" :max="badgeMax" :type="item.type" />
            </template>
            <el-scrollbar height="400px">
              <NotifyList v-model:list="item.list" />
            </el-scrollbar>
          </el-tab-pane>
        </el-tabs>
        <div class="notify-history">
          <el-button link @click="handleHistory">暂定查看{{ activeName }}历史</el-button>
        </div>
      </template>
    </el-popover>
  </div>
</template>

<style lang="scss" scoped>
.notify {
  margin-right: 10px;
  color: var(--el-text-color-regular);
}
.notify-history {
  text-align: center;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color);
}
</style>
