<script lang="ts" setup>
import {onMounted, type PropType} from "vue"
import { type ListItem } from "./data"
import {ElMessage, ElNotification} from "element-plus";
import {request} from "@/utils/service";

const props = defineProps({
  list: {
    type: Object as PropType<ListItem[]>,
    required: true
  }
})

//region  预览公告
const previewNotice = (res) => {
  // console.log(res.content)
  ElNotification({
    customClass: 'previewNotice',
    title: res.title,
    dangerouslyUseHTMLString: true,
    duration: 0,
    message: res.content,
  })

  // 标记已经阅读过
  request({url: "/user/readed/pushed/noticed/"+res.id, method: "post"})
}
//endregion
</script>

<template>
  <el-empty v-if="props.list.length === 0" />
  <el-card v-else v-for="(item, index) in props.list" :key="index" shadow="never" class="card-container">
    <template #header>
      <div class="card-header">
        <div>
          <span>
            <span class="card-title">{{ item.title }}</span>
            <el-tag v-if="item" @click="" effect="plain" size="small">{{ index+1 }}</el-tag>
          </span>
          <div class="card-time">{{ item.createDate }}</div>
        </div>

      </div>
    </template>
    <div class="card-body">
      <el-text truncated @click="previewNotice(item)">点击查看具体内容</el-text>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.card-container {
  margin-bottom: 10px;
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .card-title {
      font-weight: bold;
      margin-right: 10px;
    }
    .card-time {
      font-size: 12px;
      color: grey;
    }
    .card-avatar {
      display: flex;
      align-items: center;
    }
  }
  .card-body {
    font-size: 12px;
  }
}
</style>
