<script lang="ts" setup>
import {reactive, ref, watch} from "vue"
import {
  deleteTableDataApi,
  insertTableUserDataApi,
  updateTableUserDataApi, getTableUserDataApi
} from "@/api/table"
import {
  GetTableUserRequestData
} from "@/api/table/types/table"
import {type FormInstance, type FormRules, ElMessage, ElMessageBox, ElTable, ElNotification} from "element-plus"
import {Search, Refresh, CirclePlus, Delete, Download, RefreshRight} from "@element-plus/icons-vue"
import {usePagination} from "@/hooks/usePagination"
import excelExport from "@/utils/excelExport"

defineOptions({
  name: "user"
})

const loading = ref<boolean>(false)
const {paginationData, handleCurrentChange, handleSizeChange} = usePagination()
const deleteBatch = ref<GetTableUserRequestData[]>();


//#region 改1
const dialogVisible = ref<boolean>(false)
const formRef = ref<FormInstance | null>(null)
const formData = reactive({
  id: "",
  /** 登录名 */
  loginname: "",
  /** 用户名 */
  username: "",
  /** 密码 */
  password: "",
  /** 权限 */
  status: "",
  /** 公告创建时间 */
  createdate: "",
  email: ""
})
const formRules: FormRules = reactive({
  loginname: [{required: true, trigger: "blur", message: "请输入登录名"}],
  username: [{required: true, trigger: "blur", message: "请输入用户名"}],
  password: [{required: true, trigger: "blur", message: "请输入密码"}],
  status: [{required: true, trigger: "blur", message: "选择权限"}],
  email: [{required: true, trigger: "blur", message: "请输入邮箱"}],
})
const handleCreate = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (currentUpdateId.value == undefined){ // 新增
        insertTableUserDataApi({
          loginname: formData.loginname,
          status: formData.status,
          username: formData.username,
          password: formData.password,
          email: formData.email,
        }).then((res)=>{
          ElMessage.success("添加成功")
          dialogVisible.value = false
          getTableData()
        })
      }else{  // 更新
        console.log(formData)
        updateTableUserDataApi({
          id: formData.id,
          loginname: formData.loginname,
          status: formData.status,
          username: formData.username,
          password: formData.password,
          email: formData.email,
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
  formData.loginname = ""
  formData.status = ""
  formData.username = ""
  formData.email = ""
}
//#endregion

//#region 删
const handleDelete = (row: GetTableUserRequestData) => {
  ElMessageBox.confirm(`正在删除：${row.loginname}，确认删除？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    deleteTableDataApi(
      {
        idString: row.id.toString()
      },
      'user'
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
      'user'
    ).then(() => {
      ElMessage.success("删除成功")
      getTableData()
    })
  }).finally(() => {
    deleteBatch.value = []
  })
}
const handleSelectionChange = (val: GetTableUserRequestData[]) => {
  deleteBatch.value = val
}
//#endregion

//#region 更新
const currentUpdateId = ref<undefined | string>(undefined)
const handleUpdate = (row: GetTableUserRequestData) => {

  currentUpdateId.value = row.id
  formData.id = <string>row.id
  formData.loginname = <string>row.loginname
  formData.status = <string>row.status
  formData.username = <string>row.username
  formData.email = <string>row.email
  dialogVisible.value = true
}
//#endregion

//#region 查
const tableData = ref<GetTableUserRequestData[]>([])
const searchFormRef = ref<FormInstance | null>(null)
const searchData = reactive({
  id: "",
  loginname: "",
  status: "",
  createdate: "",
  username: "",
  email: ""
})
const getTableData = () => {
  loading.value = true
  getTableUserDataApi({
    currentPage: paginationData.currentPage,
    size: paginationData.pageSize,
    id: searchData.id || undefined,
    loginname: searchData.loginname || undefined,
    status: searchData.status || undefined,
    createdate: searchData.createdate || undefined,
    username: searchData.username || undefined,
    email: searchData.email || undefined
  })
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

const statusOptions = [
  {
    label: '管理员',
    value: 1
  },
  {
    label: '普通用户',
    value: 2
  }
]

const downLoadExcel = ()=>{
  loading.value = true
  getTableUserDataApi({
    currentPage: 1,
    size: 10000000,
  })
    .then((res) => {
      //console.log(res.data.data)
      excelExport(res.data.data, 'sheet1', 'user')
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
        <el-form-item prop="loginname" label="登陆名">
          <el-input v-model="searchData.loginname" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="status" label="权限">
          <el-select v-model="searchData.status" placeholder="Select" size="default">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="username" label="用户名">
          <el-input v-model="searchData.username" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="searchData.email" placeholder="请输入"/>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="searchData.createdate"
            type="date"
            prop="createdate"
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
          <el-button type="primary" :icon="CirclePlus" @click="dialogVisible = true">新增用户</el-button>
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
          <el-table-column prop="loginname" label="登录名" align="center"/>
          <el-table-column prop="username" label="用户名" align="center"/>
          <el-table-column prop="email" label="邮箱" align="center"/>
          <el-table-column prop="status" label="权限" align="center"/>
          <el-table-column prop="createdate" sortable label="创建时间" align="center"/>
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
      :title="currentUpdateId === undefined ? '新增用户' : '修改用户'"
      @close="resetForm"
      width="30%"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px" label-position="left">
        <el-form-item prop="loginname" label="登录名">
          <el-input v-model.number="formData.loginname" maxlength="20" minlength="10" show-word-limit placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="username" label="用户名">
          <el-input v-model="formData.username" maxlength="20" minlength="10" show-word-limit placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="password" label="密码">
          <el-input v-model="formData.password" type="password" show-word-limit placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="formData.email" show-word-limit placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="status" label="权限">
          <el-select v-model="formData.status" placeholder="Select" size="default">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
