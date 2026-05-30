<script lang="ts" setup>
import {reactive, ref, watch} from "vue"
import {
  deleteTableDataApi,
  updateTableDocumentDataApi,
  getTableDocumentDataApi,
  downloadDocument
} from "@/api/table"
import {type GetTableDocumentData} from "@/api/table/types/table"
import {type FormInstance, type FormRules, ElMessage, ElMessageBox, ElTable, ElLoading} from "element-plus"
import {Search, Refresh, CirclePlus, Delete, Download, RefreshRight} from "@element-plus/icons-vue"
import {usePagination} from "@/hooks/usePagination"
import axios from "axios";
import {getToken} from "@/utils/cache/cookies";
import globalDownloadFile from "@/utils/file_download_map";

defineOptions({
  name: "document"
})

const loading = ref<boolean>(false)
const {paginationData, handleCurrentChange, handleSizeChange} = usePagination()
const deleteBatch = ref<GetTableDocumentData[]>();

const dialogVisible = ref<boolean>(false)
const downloadProgress = ref<boolean>(false)  // 下载文件的dialog
const progress = ref<number>()  // 下载文件的dialog
const formRef = ref<FormInstance | null>(null)
//#region 改1
const formData = reactive({
  id: "",
  /** 标题 */
  title: "",
  /** 文件名 */
  filename: "",
  /** 文件描述 */
  remark: "",
  loginName: ""

})
const formRules: FormRules = reactive({
  title: [{required: true, trigger: "blur", message: "请输入标题"}],
  filename: [{required: true, trigger: "blur", message: "请输入文件名"}],
  remark: [{required: true, trigger: "blur", message: "请输入文件描述"}]
})
const handleCreate = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      updateTableDocumentDataApi({
        id: currentUpdateId.value.toString(),
        title: formData.title,
        filename: formData.filename,
        remark: formData.remark
      }, 'document').then(() => {
        ElMessage.success("修改成功")
        dialogVisible.value = false
        getTableData()
      })
    } else {
      return false
    }
  })
}
const resetForm = () => {
  currentUpdateId.value = undefined
  formData.title = ""
  formData.filename = ""
  formData.remark = ""
  formData.loginName = ""
}
//#endregion

//#region 删
const handleDelete = (row: GetTableDocumentData) => {
  ElMessageBox.confirm(`正在删除文件：${row.filename}，确认删除？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteTableDataApi(
      {
        idString: row.id.toString()
      },
      'document'
    ).then(() => {
      ElMessage.success("删除成功")
      getTableData()
    })
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
      'document'
    ).then(() => {
      ElMessage.success("删除成功")
      getTableData()
    })
  }).finally(() => {
    deleteBatch.value = []
  })
}
const handleSelectionChange = (val: GetTableDocumentData[]) => {
  deleteBatch.value = val
}


//#endregion

//#region 改
const currentUpdateId = ref<undefined | bigint>(undefined)
const handleUpdate = (row: GetTableDocumentData) => {
  currentUpdateId.value = row.id
  formData.title = row.title
  formData.filename = row.filename
  formData.remark = row.remark
  dialogVisible.value = true
}
//#endregion

//#region 查
const tableData = ref<GetTableDocumentData[]>([])
const searchFormRef = ref<FormInstance | null>(null)
const searchData = reactive({
  title: "",
  filename: "",
  remark: "",
  createDate: "",
  loginName: ""
})
const getTableData = () => {
  loading.value = true
  getTableDocumentDataApi({
    currentPage: paginationData.currentPage,
    size: paginationData.pageSize,
    title: searchData.title || undefined,
    filename: searchData.filename || undefined,
    remark: searchData.remark || undefined,
    createDate: searchData.createDate || undefined,
    loginName: searchData.loginName || undefined
  }, 'document')
    .then((res) => {
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

//#region 加载层
const openFullScreen = () => {
  const loading = ElLoading.service({
    lock: true,
    text: '下载中...',
    background: 'rgba(17,16,16,0.7)',
  })
  return loading;
}
//endregion


//#region 下载
const downLoadDocument = (row) => {
  let open = openFullScreen()
  downloadDocument(row.id)
    .then((res) => {
      if (getToken() === undefined) {
        ElMessage.error("请登录后，下载")
        return
      }
      downloadProgress.value = true  // 打开下载的进度条
      // 存储用户的键值对
      globalDownloadFile.addItem(getToken(), res.message)
      // 使用文件URL路径进行文件下载
      const url = '/api/v1' + res.message;
      axios.get(url, {
        headers: {
          'Content-Type': 'application/json',
          "AUTH-TOKEN": getToken()
        },
        responseType: 'blob', // 设置响应类型为 blob
        onDownloadProgress: progressEvent => {
          //console.log(progressEvent)
          // 计算下载进度 更新进度条
          progress.value = progressEvent.loaded / progressEvent.total;
          // 关闭
          if (progress.value == 1){
            downloadProgress.value = false
            progress.value = 0
          }
        }
      }).then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', row.filename + '.' + row.fileType); // 设置下载的文件名
        document.body.appendChild(link);
        link.click();
        open.close() // 关闭加载层
      }).catch((error) => {
        // console.log(error)
      }).finally(() => {
        // 删除k
        globalDownloadFile.removeItem(getToken())
      });
    })
    .catch((error) => {
      // console.log(error)
    })

}

//#endregion

/** 监听分页参数的变化 */
watch([() => paginationData.currentPage, () => paginationData.pageSize], getTableData, {immediate: true})
</script>

<template>
  <div class="app-container">
    <el-card v-loading="loading" shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="searchData">
        <el-form-item prop="filename" label="文件名">
          <el-input v-model="searchData.filename" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="title" label="文件标题">
          <el-input v-model="searchData.title" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="remark" label="文件描述">
          <el-input v-model="searchData.remark" placeholder="请输入"/>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="searchData.createDate"
            type="date"
            prop="createDate"
            placeholder="选择时间"
            format="YYYY/MM/DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item prop="loginName" label="创建者">
          <el-input v-model="searchData.loginName" placeholder="请输入"/>
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
          <el-button type="danger" :icon="Delete" @click="batchDelete">批量删除
          </el-button>
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
          <el-table-column prop="filename" label="文件名" align="center"/>
          <el-table-column prop="title" label="文件标题" align="center"/>
          <el-table-column prop="remark" label="文件描述" align="center"/>
          <el-table-column prop="fileType" label="文件类型" align="center"/>
          <el-table-column prop="createDate" label="创建时间" align="center"/>
          <el-table-column prop="loginName" label="创建者" align="center"/>
          <el-table-column fixed="right" label="操作" width="190" align="center">
            <template #default="scope">
              <el-button type="primary" text bg size="small" @click="downLoadDocument(scope.row)">下载</el-button>
              <el-button type="primary" text bg size="small" @click="handleUpdate(scope.row)">修改</el-button>
              <el-button type="danger" text bg size="small" @click="handleDelete(scope.row)">删除</el-button>
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
      title="修改文件"
      @close="resetForm"
      width="30%"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" label-position="left">
        <el-form-item prop="filename" label="文件名">
          <el-input v-model="formData.filename" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="title" label="标题">
          <el-input v-model="formData.title" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="remark" label="文件描述">
          <el-input v-model="formData.remark" placeholder="请输入"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确认</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="downloadProgress" width="40%">
      <div class="slider-demo-block">
        <span class="demonstration">下载进行中...</span>
        <el-slider v-model="progress"  />
      </div>
    </el-dialog>

  </div>
</template>

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
