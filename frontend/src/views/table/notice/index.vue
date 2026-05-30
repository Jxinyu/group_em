<script lang="ts" setup>
import {reactive, ref, watch} from "vue"
import {
  deleteTableDataApi,
  getTableNoticeDataApi,
  updateTableNoticeDataApi,
  insertTableNoticeDataApi,
  getTableUserDataApi,
  pushNoticeDataApi,
  getPushedNoticeById
} from "@/api/table"
import {type GetTableNoticeData, GetTablePushedNoticeData} from "@/api/table/types/table"
import {type FormInstance, type FormRules, ElMessage, ElMessageBox, ElTable, ElNotification} from "element-plus"
import {Search, Refresh, CirclePlus, Delete, Download, RefreshRight} from "@element-plus/icons-vue"
import {usePagination} from "@/hooks/usePagination"
import Editor from "@/views/table/notice/editor.vue"
import excelExport from "@/utils/excelExport";
import {log} from "util";
import generatePDF from "@/utils/htmlToPDF";
import axios from "axios";

defineOptions({
  name: "notice"
})

const loading = ref<boolean>(false)
const {paginationData, handleCurrentChange, handleSizeChange} = usePagination()
const deleteBatch = ref<GetTableNoticeData[]>();
const editorRef = ref() // 保存editor子组件

//region  预览公告
const previewNotice = (res) => {
  // console.log(res.content)
  ElNotification({
    customClass: 'previewNotice',
    title: res.title,
    dangerouslyUseHTMLString: true,
    duration: 3000,
    message: res.content,
  })
}
//endregion

//#region 改1
const dialogVisible = ref<boolean>(false)
const formRef = ref<FormInstance | null>(null)
const formData = reactive({
  id: "",
  /** 公告标题 */
  title: "",
  /** 公告内容 */
  content: "",
  /** 负责人*/
  loginName: ""
})
const formRules: FormRules = reactive({
  title: [{required: true, trigger: "blur", message: "请输入标题"}],
  content: [{required: true, trigger: "blur", message: "请输入公告内容"}]
})
const handleCreate = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (currentUpdateId.value == undefined) { // 新增
        insertTableNoticeDataApi({
          title: formData.title,
          content: editorRef.value.acData() // 获取子组件的值
        }).then((res) => {
          ElMessage.success("添加成功")
          dialogVisible.value = false
          getTableData()
        })
      } else {  // 更新
        updateTableNoticeDataApi({
          id: currentUpdateId.value.toString(),
          title: formData.title,
          content: editorRef.value.acData() // 获取子组件的值
        }).then(() => {
          ElMessage.success("修改成功")
          dialogVisible.value = false
          getTableData()
        })
      }
    } else {
      return false
    }
  })
}
const resetForm = () => {
  currentUpdateId.value = undefined
  formData.title = ""
  formData.content = ""
}
//#endregion

//#region 删
const handleDelete = (row: GetTableNoticeData) => {
  // console.log(row)
  ElMessageBox.confirm(`正在删除文件：${row.title}，确认删除？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteTableDataApi(
      {
        idString: row.id.toString()
      },
      'notice'
    ).then(() => {
      ElMessage.success("删除成功")
      getTableData()
    })
  }).catch(() => {
    // 错误捕获

  })
}
// 批量删除
const batchDelete = () => {
  let idStrings = '';
  deleteBatch.value?.forEach(value => {
    idStrings += value.id + '&'
  })
  if (idStrings === '') {
    ElMessage.info('选择为空')
    return
  }
  ElMessageBox.confirm(`确认删除所有选中文件吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteTableDataApi({
        idString: idStrings
      },
      'notice'
    ).then(() => {
      ElMessage.success("删除成功")
      getTableData()
    })
  }).catch(() => {
    idStrings = ''
  }).finally(() => {
    deleteBatch.value = []
  })
}
const handleSelectionChange = (val: GetTableNoticeData[]) => {
  deleteBatch.value = val
}
//#endregion

//#region 更新
const currentUpdateId = ref<undefined | bigint>(undefined)
const handleUpdate = (row: GetTableNoticeData) => {

  currentUpdateId.value = row.id
  formData.title = row.title
  formData.content = row.content
  dialogVisible.value = true


}
//#endregion

//#region 查
const tableData = ref<GetTableNoticeData[]>([])
const searchFormRef = ref<FormInstance | null>(null)
const searchData = reactive({
  title: "",
  content: "",
  createDate: "",
  loginName: ""
})
const getTableData = () => {
  loading.value = true
  getTableNoticeDataApi({
    currentPage: paginationData.currentPage,
    size: paginationData.pageSize,
    title: searchData.title || undefined,
    content: searchData.content || undefined,
    createDate: searchData.createDate || undefined,
    loginName: searchData.loginName || undefined,
  })
    .then((res) => {
      // console.log(res)
      paginationData.total = res.data.total
      tableData.value = res.data.data
    })
    .catch(() => {
      tableData.value = []
    })
    .finally(() => {
      loading.value = false
    })
}
const handleSearch = () => {
  if (paginationData.currentPage === 1) {
    getTableData()
  }
  paginationData.currentPage = 1
}
const resetSearch = () => {
  searchFormRef.value?.resetFields()
  if (paginationData.currentPage === 1) {
    getTableData()
  }
  paginationData.currentPage = 1
}
const handleRefresh = () => {
  getTableData()
}
//#endregion

//region 下载
const handleDownload = (row) => {
  //console.log(row)
  generatePDF(row.content, row.title)
}

//endregion

//region  公告推送
const dialogVisiblePush = ref<boolean>(false)
const dialogVisibleAgainPush = ref<boolean>(false)
const pushItems = [
  {
    value: 1,
    label: '管理员'
  },
  {
    value: 2,
    label: '普通用户'
  },
  {
    value: 3,
    label: '全部用户'
  },
  {
    value: 4,
    label: '全部员工'
  },
  {
    value: 5,
    label: '全部人员'
  },
]
const pushWays = [
  {
    value: 1,
    label: '普通方式'
  },
  {
    value: 2,
    label: '邮件方式'
  }
]
const pushStatus = [
  {
    value: 1,
    label: '普通'
  },
  {
    value: 2,
    label: '警告'
  },
  {
    value: 3,
    label: '紧急'
  }
]
const scope = ref(null)
const noticeId = ref(null)
const pushWay = ref(null)
const noticePush = (res) => {
  noticeId.value = res.id
  dialogVisiblePush.value = true
}

const handlePush = (type) => {
  if (scope.value === null) {
    ElMessage.warning('请选择要推送的范围')
    return
  }
  if (noticeId.value === null) {
    ElMessage.warning('请选择要推送的公告')
    return
  }
  if (pushWay.value === null) {
    ElMessage.warning('请选择推送的方式')
    return
  }
  pushNoticeDataApi(type, scope.value, noticeId.value, pushWay.value).then((res) => {
    // console.log(res)
    if (res.code === 200) {
      ElMessage.success('推送成功')
      dialogVisiblePush.value = false
      dialogVisibleAgainPush.value = false
      noticeId.value = null
      scope.value = null
      pushWay.value = null
      getTableData()
      return
    } else if (res.code === 201) {
      ElMessage.warning(res.message)
      dialogVisibleAgainPush.value = true
      return;
    }
    noticeId.value = null
    ElMessage.error('推送失败')
  })
}

const pushedTableData = ref<GetTablePushedNoticeData[]>()
const dialogVisiblePushedTable = ref<boolean>(false)
const getPushedById = (val) => {
  dialogVisiblePushedTable.value = true
  getPushedNoticeById(val.id)
    .then((res) => {
      pushedTableData.value = res.data.data
    })
}

//endregion

/** 监听分页参数的变化 */
watch([() => paginationData.currentPage, () => paginationData.pageSize], getTableData, {immediate: true})
</script>

<template>
  <div class="app-container">
    <el-card v-loading="loading" shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="searchData">
        <el-form-item prop="filename" label="公告标题">
          <el-input v-model="searchData.title" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="title" label="公告内容">
          <el-input v-model="searchData.content" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="loginName" label="负责人">
          <el-input v-model="searchData.loginName" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="searchData.createDate"
            type="date"
            prop="createDate"
            placeholder="选择时间"
            format="YYYY/MM/DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card v-loading="loading" shadow="never">
      <div class="toolbar-wrapper">
        <div>
          <el-button type="danger" :icon="Delete" @click="batchDelete">批量删除</el-button>
          <el-button type="primary" :icon="CirclePlus" @click="dialogVisible = true">新增公告</el-button>
        </div>
        <div>
          <el-tooltip content="刷新表格">
            <el-button type="primary" :icon="RefreshRight" circle @click="handleRefresh"/>
          </el-tooltip>
        </div>
      </div>
      <div class="table-wrapper">
        <el-table :data="tableData" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column prop="title" label="公告标题" align="center"/>
          <el-table-column prop="content" label="公告内容" align="center">
            <template #default="scope">
              <el-button type="primary" @click="previewNotice(scope.row)">点击预览</el-button>
            </template>
          </el-table-column>
          <el-table-column prop="createDate" sortable="true" label="创建时间" align="center"/>
          <el-table-column prop="loginName" label="负责人" align="center"/>
          <el-table-column prop="isPush" label="推送" align="center">
            <template #default="scope">
              <el-button
                type="success"
                @click="noticePush(scope.row)"
              > 点击推送
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="" label="已推送" align="center">
            <template #default="scope">
              <el-button type="primary" @click="getPushedById(scope.row)">查看已推送</el-button>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="190" align="center">
            <template #default="scope">
              <el-button type="primary" text bg size="small" @click="handleDownload(scope.row)">下载</el-button>
              <el-button type="primary" bg size="small" @click="handleUpdate(scope.row)">修改</el-button>
              <el-button type="danger" bg size="small" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pager-wrapper">
        <el-pagination
          background
          :layout="paginationData.layout"
          :page-sizes="paginationData.pageSizes"
          :total="paginationData.total"
          :page-size="paginationData.pageSize"
          :currentPage="paginationData.currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    <!-- 修改 -->
    <el-dialog
      v-model="dialogVisible"
      :title="currentUpdateId === undefined ? '新增公告' : '修改公告'"
      @close="resetForm"
      width="60%"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" label-position="left">
        <el-form-item prop="title" label="标题">
          <el-input v-model="formData.title" maxlength="40" show-word-limit placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="remark" label="公告内容">
          <Editor ref="editorRef" :content="formData.content"></Editor>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确认</el-button>
      </template>
    </el-dialog>

    <!--推送公告-->
    <el-dialog
      v-model="dialogVisiblePush"
      title="推送公告"
      width="30%"
    >
      <el-select v-model="scope" class="m-2" placeholder="请选择推送的人群" size="large">
        <el-option
          v-for="item in pushItems"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="pushWay" class="m-2" placeholder="请选择推送的方式" size="large">
        <el-option
          v-for="item in pushWays"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>

      <template #footer>
        <el-button @click="dialogVisiblePush = false">取消</el-button>
        <el-button type="primary" @click="handlePush(false)">确认</el-button>
      </template>
    </el-dialog>
    <!--再次确认推送-->
    <el-dialog
      v-model="dialogVisibleAgainPush"
      title="是否再次推送"
      width="30%"
    >
      <template #footer>
        <el-button @click="dialogVisibleAgainPush = false">取消</el-button>
        <el-button type="primary" @click="handlePush(true)">确认</el-button>
      </template>
    </el-dialog>
    <!--相关已推送-->
    <el-dialog
      v-model="dialogVisiblePushedTable"
      title="相关已推送"
      width="60%"
    >
      <div class="table-wrapper">
        <el-table :data="pushedTableData">
          <el-table-column prop="scope" label="推送的人群" align="center">
            <template #default="scope">
              <div v-for="items in pushItems">
                <el-text v-if="items.value === scope.row.scope">{{ items.label }}</el-text>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="pushWay" label="推送的方式" align="center">
            <template #default="scope">
              <div v-for="items in pushWays">
                <el-text v-if="items.value === scope.row.pushWay">{{ items.label }}</el-text>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="pushDate" label="推送的时间" align="center"/>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="dialogVisiblePushedTable = false">关闭</el-button>
      </template>
    </el-dialog>

  </div>
</template>
<style>
@media screen and (max-width: 1700px) {
  .previewNotice {
    width: auto;
  }
}
</style>
<style lang="scss" scoped>
.search-wrapper {
  margin-bottom: 20px;

  :deep(.el-card__body) {
    padding-bottom: 2px;
  }
}

.toolbar-wrapper {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.table-wrapper {
  margin-bottom: 20px;
}

.pager-wrapper {
  display: flex;
  justify-content: flex-end;
}


</style>
