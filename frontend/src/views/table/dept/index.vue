<script lang="ts" setup>
import {reactive, ref, watch} from "vue"
import {
  deleteTableDataApi,
  insertTableUserDataApi,
  updateTableUserDataApi,
  getTableUserDataApi,
  insertTableJobDataApi,
  updateTableJobDataApi,
  getTableJobDataApi,
  insertTableDeptDataApi, updateTableDeptDataApi, getTableDeptDataApi, getTableEmployeeDataApi
} from "@/api/table"
import {
  GetTableDeptRequestData,
  GetTableJobRequestData,
} from "@/api/table/types/table"
import {type FormInstance, type FormRules, ElMessage, ElMessageBox, ElTable, ElNotification} from "element-plus"
import {Search, Refresh, CirclePlus, Delete, Download, RefreshRight} from "@element-plus/icons-vue"
import {usePagination} from "@/hooks/usePagination"
import excelExport from "@/utils/excelExport";

defineOptions({
  name: "dept"
})

const loading = ref<boolean>(false)
const {paginationData, handleCurrentChange, handleSizeChange} = usePagination()
const deleteBatch = ref<GetTableJobRequestData[]>();


//#region 改1
const dialogVisible = ref<boolean>(false)
const formRef = ref<FormInstance | null>(null)
const formData = reactive({
  id: "",
  name: "",
  remark: "",
})
const formRules: FormRules = reactive({
  name: [{required: true, trigger: "blur", message: "请输入部门名称"}],
  remark: [{required: true, trigger: "blur", message: "请输入部门描述"}],
})
const handleCreate = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (currentUpdateId.value == undefined){ // 新增
        insertTableDeptDataApi({
          name: formData.name,
          remark: formData.remark
        }).then((res) => {
          ElMessage.success("添加成功")
          dialogVisible.value = false
          getTableData()
        });
      }else{  // 更新
        updateTableDeptDataApi({
          id: formData.id,
          name: formData.name,
          remark: formData.remark
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
  formData.name = ""
  formData.remark = ""
}
//#endregion

//#region 删
const handleDelete = (row: GetTableJobRequestData) => {
  ElMessageBox.confirm(`正在删除：${row.name}，确认删除？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteTableDataApi(
      {
        idString: row.id.toString()
      },
      'dept'
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
  ElMessageBox.confirm(`确认删除所有选中的吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteTableDataApi({
        idString: idStrings
      },
      'dept'
    ).then(() => {
      ElMessage.success("删除成功")
      getTableData()
    })
  }).finally(() => {
    deleteBatch.value = []
  })
}
const handleSelectionChange = (val: GetTableJobRequestData[]) => {
  deleteBatch.value = val
}
//#endregion

//#region 更新
const currentUpdateId = ref<undefined | string>(undefined)
const handleUpdate = (row: GetTableDeptRequestData) => {

  currentUpdateId.value = row.id
  formData.id = <string>row.id
  formData.name = <string>row.name
  formData.remark = <string>row.remark
  dialogVisible.value = true
}
//#endregion

//#region 查
const tableData = ref<GetTableDeptRequestData[]>([])
const searchFormRef = ref<FormInstance | null>(null)
const searchData = reactive({
  id: "",
  name: "",
  remark: "",
})
const getTableData = () => {
  loading.value = true
  getTableDeptDataApi({
    currentPage: paginationData.currentPage,
    size: paginationData.pageSize,
    id: searchData.id || undefined,
    name: searchData.name || undefined,
    remark: searchData.remark || undefined,
  })
    .then((res) => {
      //console.log(res)
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

const downLoadExcel = ()=>{
  loading.value = true
  getTableDeptDataApi({
    currentPage: 1,
    size: 10000000,
  })
    .then((res) => {
      //console.log(res.data.data)
      excelExport(res.data.data, 'sheet1', '部门')
    })
    .catch(() => {
      ElMessage.warning('下载失败')
    })
    .finally(() => {
      loading.value = false
    })
}

/** 监听分页参数的变化 */
watch([() => paginationData.currentPage, () => paginationData.pageSize], getTableData, {immediate: true})
</script>

<template>
  <div class="app-container">
    <el-card v-loading="loading" shadow="never" class="search-wrapper">
      <el-form ref="searchFormRef" :inline="true" :model="searchData">
        <el-form-item prop="name" label="部门名称">
          <el-input v-model="searchData.name" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="remark" label="部门描述">
          <el-input v-model="searchData.remark" placeholder="请输入"/>
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
          <el-button type="primary" :icon="CirclePlus" @click="dialogVisible = true">新增部门</el-button>
        </div>
        <div>
          <el-tooltip content="下载">
            <el-button type="primary" :icon="Download" circle @click="downLoadExcel"/>
          </el-tooltip>
          <el-tooltip content="刷新表格">
            <el-button type="primary" :icon="RefreshRight" circle @click="handleRefresh"/>
          </el-tooltip>
        </div>
      </div>
      <div class="table-wrapper">
        <el-table :data="tableData" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column prop="name" label="部门名称" align="center"/>
          <el-table-column prop="remark" label="部门描述" align="center"/>
          <el-table-column fixed="right" label="操作" width="190" align="center">
            <template #default="scope">
              <el-button type="primary"   size="small" @click="handleUpdate(scope.row)">修改</el-button>
              <el-button type="danger"  size="small" @click="handleDelete(scope.row)">删除</el-button>
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
      :title="currentUpdateId === undefined ? '新增部门' : '修改部门'"
      @close="resetForm"
      width="30%"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" label-position="left">
        <el-form-item prop="name" label="职业名称">
          <el-input v-model="formData.name" maxlength="10" minlength="2" show-word-limit placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="remark" label="职业描述">
          <el-input v-model="formData.remark" show-word-limit placeholder="请输入"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确认</el-button>
      </template>
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
