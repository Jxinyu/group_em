<script lang="ts" setup>
import {onMounted, onUpdated, reactive, ref, watch} from "vue"
import {
  deleteTableDataApi,
  insertTableJobDataApi,
  updateTableJobDataApi,
  getTableJobDataApi,
  getTableDeptDataApi,
  insertTableEmployeeDataApi, updateTableEmployeeDataApi, getTableEmployeeDataApi
} from "@/api/table"
import {
  GetTableDeptRequestData, GetTableEmployeeRequestData,
  GetTableJobRequestData,
} from "@/api/table/types/table"
import {type FormInstance, type FormRules, ElMessage, ElMessageBox, ElTable, ElNotification} from "element-plus"
import {Search, Refresh, CirclePlus, Delete, Download, RefreshRight} from "@element-plus/icons-vue"
import {usePagination} from "@/hooks/usePagination"
import excelExport from "@/utils/excelExport";

defineOptions({
  name: "employee"
})

const loading = ref<boolean>(false)
const {paginationData, handleCurrentChange, handleSizeChange} = usePagination()
const deleteBatch = ref<GetTableJobRequestData[]>();
const optionsSex = [
  {
    value: 1,
    label: "男"
  },
  {
    value: 2,
    label: "女"
  }
]

//#region 改1
const dialogVisible = ref<boolean>(false)
const formRef = ref<FormInstance | null>(null)
const formData = reactive({
  id: "",
  /** 部门Id */
  deptId: "",
  /** 部门 */
  dept: "",
  /** 职位id */
  jobId: "",
  /** 职位 */
  job: "",
  /** 名字 */
  name: "",
  /** 身份证 */
  cardId: "",
  /** 地址 */
  address: "",
  /** 邮编 */
  postCode: "",
  /** 电话 */
  tel: "",
  /** 手机 */
  phone: "",
  /** qq */
  qqNum: "",
  /** 邮箱 */
  email: "",
  /** 性别 */
  sex: "",
  /** 政治面貌 */
  party: "",
  /** 出生日期 */
  birthday: "",
  /** 民族 */
  race: "",
  /** 学历 */
  education: "",
  /** 专业 */
  speciality: "",
  /** 特长 */
  hobby: "",
  /** 备注 */
  remark: "",
  /** 创建日期 */
  createDate: "",
})
const formRules: FormRules = reactive({
  /** 部门 */
  dept: [{required: false, trigger: "blur", message: "请输入部门"}],
  /** 职位 */
  job: [{required: false, trigger: "blur", message: "请输入职位"}],
  /** 名字 */
  name: [{required: true, trigger: "blur", message: "请输入名字"}],
  /** 身份证 */
  cardId: [{required: true, trigger: "blur", message: "请输入身份证"}],
  /** 地址 */
  address: [{required: false, trigger: "blur", message: "请输入地址"}],
  /** 邮编 */
  postCode: [{required: false, trigger: "blur", message: "请输入邮编"}],
  /** 电话 */
  tel: [{required: false, trigger: "blur", message: "请输入电话"}],
  /** 手机 */
  phone: [{required: true, trigger: "blur", message: "请输入手机"}],
  /** qq */
  qqNum: [{required: false, trigger: "blur", message: "请输入qq"}],
  /** 邮箱 */
  email: [{required: false, trigger: "blur", message: "请输入邮箱"}],
  /** 性别 */
  sex: [{required: true, trigger: "blur", message: "请输入性别"}],
  /** 政治面貌 */
  party: [{required: false, trigger: "blur", message: "请输入政治面貌"}],
  /** 出生日期 */
  birthday: [{required: true, trigger: "blur", message: "请输入出生日期"}],
  /** 民族 */
  race: [{required: true, trigger: "blur", message: "请输入民族"}],
  /** 学历 */
  education: [{required: true, trigger: "blur", message: "请输入学历"}],
  /** 专业 */
  speciality: [{required: true, trigger: "blur", message: "请输入专业"}],
  /** 特长 */
  hobby: [{required: false, trigger: "blur", message: "请输入特长"}],
  /** 备注 */
  remark: [{required: true, trigger: "blur", message: "请输入备注"}],
})
const handleCreate = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (currentUpdateId.value == undefined) { // 新增
        insertTableEmployeeDataApi({
          dept: formData.dept,
          deptId: formData.deptId,
          job: formData.job,
          jobId: formData.jobId,
          name: formData.name,
          cardId: formData.cardId,
          address: formData.address,
          postCode: formData.postCode,
          tel: formData.tel,
          phone: formData.phone,
          qqNum: formData.qqNum,
          email: formData.email,
          sex: formData.sex,
          party: formData.party,
          birthday: formData.birthday,
          race: formData.race,
          education: formData.education,
          speciality: formData.speciality,
          hobby: formData.hobby,
          remark: formData.remark
        }).then((res) => {
          ElMessage.success("添加成功")
          dialogVisible.value = false
          getTableData()
        })
      } else {  // 更新
        updateTableEmployeeDataApi({
          id: formData.id,
          dept: formData.dept,
          deptId: formData.deptId,
          job: formData.job,
          jobId: formData.jobId,
          name: formData.name,
          cardId: formData.cardId,
          address: formData.address,
          postCode: formData.postCode,
          tel: formData.tel,
          phone: formData.phone,
          qqNum: formData.qqNum,
          email: formData.email,
          sex: formData.sex,
          party: formData.party,
          birthday: formData.birthday,
          race: formData.race,
          education: formData.education,
          speciality: formData.speciality,
          hobby: formData.hobby,
          remark: formData.remark,
          createDate: formData.createDate
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
  formData.dept = ""
  formData.deptId = ""
  formData.job = ""
  formData.jobId = ""
  formData.name = ""
  formData.cardId = ""
  formData.address = ""
  formData.postCode = ""
  formData.tel = ""
  formData.phone = ""
  formData.qqNum = ""
  formData.email = ""
  formData.sex = ""
  formData.party = ""
  formData.birthday = ""
  formData.race = ""
  formData.education = ""
  formData.createDate = ""
  formData.hobby = ""
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
      'employee'
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
      'employee'
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
const handleUpdate = (row: GetTableEmployeeRequestData) => {
  currentUpdateId.value = row.id
  formData.id = <string>row.id
  formData.deptId = <string>row.deptId
  formData.dept = <string>row.dept
  formData.jobId = <string>row.jobId
  formData.job = <string>row.job
  formData.name = <string>row.name
  formData.cardId = <string>row.cardId
  formData.address = <string>row.address
  formData.postCode = <string>row.postCode
  formData.tel = <string>row.tel
  formData.phone = <string>row.phone
  formData.qqNum = <string>row.qqNum
  formData.email = <string>row.email
  formData.sex = <string>row.sex
  formData.party = <string>row.party
  formData.birthday = <string>row.birthday
  formData.race = <string>row.race
  formData.education = <string>row.education
  formData.hobby = <string>row.hobby
  formData.speciality = <string>row.speciality
  formData.remark = <string>row.remark
  formData.createDate = <string>row.createDate
  dialogVisible.value = true
}
//#endregion

//#region 查
const tableData = ref<GetTableEmployeeRequestData[]>([])
const searchFormRef = ref<FormInstance | null>(null)
const searchData = reactive({
  dept: "",
  deptId: "",
  job: "",
  jobId: "",
  name: "",
  cardId: "",
  address: "",
  postCode: "",
  tel: "",
  phone: "",
  qqNum: "",
  email: "",
  sex: "",
  party: "",
  birthday: "",
  race: "",
  education: "",
  speciality: "",
  hobby: "",
  remark: "",
  createDate: "",
})
const getTableData = () => {
  loading.value = true
  getTableEmployeeDataApi({
    currentPage: paginationData.currentPage,
    size: paginationData.pageSize,
    deptId: searchData.deptId || undefined,
    dept: searchData.dept || undefined,
    jobId: searchData.jobId || undefined,
    job: searchData.job || undefined,
    name: searchData.name || undefined,
    cardId: searchData.cardId || undefined,
    address: searchData.address || undefined,
    postCode: searchData.postCode || undefined,
    tel: searchData.tel || undefined,
    phone: searchData.phone || undefined,
    qqNum: searchData.qqNum || undefined,
    email: searchData.email || undefined,
    sex: searchData.sex || undefined,
    party: searchData.party || undefined,
    birthday: searchData.birthday || undefined,
    race: searchData.race || undefined,
    education: searchData.education || undefined,
    speciality: searchData.speciality || undefined,
    hobby: searchData.hobby || undefined,
    remark: searchData.remark || undefined,
    createDate: searchData.createDate || undefined
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

//region 查所有的部门
const signQueryDept = ref(false)  // 标记是否已经查询过
const optionsDept = ref<GetTableDeptRequestData[]>([])
const queryDept = () => {
  // if (signQueryDept.value && optionsDept.value.length != 0) {
  //   return
  // }
  getTableDeptDataApi({
    currentPage: 1,
    size: 1000,
  })
    .then((res) => {
      optionsDept.value = res.data.data
      queryJob()
    })
    .catch(() => {
    })
    .finally(() => {
    })
  //signQueryDept.value = true
}
//endregion
//region 查所有的职位
const optionsJob = ref<GetTableJobRequestData[]>([])
const queryJob = () => {
  const dept_ids = ref()
  if (formData.deptId != '' && searchData.deptId == ''){
    dept_ids.value = formData.deptId
    a.value = false
  }else{
    dept_ids.value = searchData.deptId
  }
  if (dept_ids.value == ''){
    return
  }
  getTableJobDataApi({
    currentPage: 1,
    size: 1000,
    deptId: dept_ids.value
  })
    .then((res) => {
      optionsJob.value = res.data.data
    })
    .catch(() => {
    })
    .finally(() => {
    })
}
//endregion

const a = ref(true)


const downLoadExcel = ()=>{
  loading.value = true
  getTableEmployeeDataApi({
    currentPage: 1,
    size: 10000000,
  })
    .then((res) => {
      //console.log(res.data.data)
      excelExport(res.data.data, 'sheet1', '员工')
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
        <el-form-item prop="name" label="姓名">
          <el-input v-model="searchData.name" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="cardId" label="身&nbsp&nbsp份&nbsp证">
          <el-input v-model="searchData.cardId" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="address" label="地址">
          <el-input v-model="searchData.address" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="postCode" label="邮编">
          <el-input v-model="searchData.postCode" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="tel" label="电话">
          <el-input v-model="searchData.tel" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="phone" label="手&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp机">
          <el-input v-model="searchData.phone" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="qqNum" label="Q Q">
          <el-input v-model="searchData.qqNum" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="searchData.email" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="sex" label="性别">
          <el-select v-model="searchData.sex" class="m-1" placeholder="选择性别" size="default">
            <el-option
              v-for="item in optionsSex"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="party" label="政治面貌">
          <el-input v-model="searchData.party" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="race" label="民族">
          <el-input v-model="searchData.race" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="education" label="学历">
          <el-input v-model="searchData.education" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="speciality" label="专业">
          <el-input v-model="searchData.speciality" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="hobby" label="特&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp长">
          <el-input v-model="searchData.hobby" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="deptId" label="所属部门">
          <el-select
            v-model="searchData.deptId"
            filterable
            remote
            placeholder="请选择所属部门"
            remote-show-suffix
            :remote-method="queryDept"
            :loading="loading"
            loading-text="加载中..."
          >
            <el-option
              v-for="item in optionsDept"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="jobId" label="所属职位">
          <el-select
            v-model="searchData.jobId"
            filterable
            placeholder="请选择所属职位"
          >
            <el-option
              v-for="item in optionsJob"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="searchData.birthday"
            type="date"
            prop="birthday"
            placeholder="选择时间"
            format="YYYY/MM/DD"
            value-format="YYYY-MM-DD"
          />
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
          <el-button type="primary" :icon="CirclePlus" @click="dialogVisible = true">新增员工</el-button>
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
          <el-table-column prop="dept" label="部门" align="center"/>
          <el-table-column prop="job" label="职位" align="center"/>
          <el-table-column prop="name" label="姓名" align="center"/>
          <el-table-column prop="cardId" label="身份证" align="center"/>
          <el-table-column prop="address" label="地址" align="center"/>
          <el-table-column prop="postCode" label="邮编" align="center"/>
          <el-table-column prop="tel" label="电话" align="center"/>
          <el-table-column prop="phone" label="手机" align="center"/>
          <el-table-column prop="qqNum" label="QQ" align="center"/>
          <el-table-column prop="email" label="邮箱" align="center"/>
          <el-table-column prop="sex" label="性别" align="center"/>
          <el-table-column prop="party" label="政治面貌" align="center"/>
          <el-table-column prop="birthday" label="出生日期" align="center"/>
          <el-table-column prop="race" label="民族" align="center"/>
          <el-table-column prop="education" label="学历" align="center"/>
          <el-table-column prop="speciality" label="专业" align="center"/>
          <el-table-column prop="hobby" label="特长" align="center"/>
          <el-table-column prop="remark" label="备注" align="center"/>
          <el-table-column prop="createDate" label="创建时间" align="center"/>
          <el-table-column fixed="right" label="操作" width="190" align="center">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleUpdate(scope.row)">修改</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
      :title="currentUpdateId === undefined ? '新增员工' : '修改员工'"
      @close="resetForm"
      width="60%"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px" label-position="left">
        <el-form-item prop="name" label="姓名">
          <el-input v-model="formData.name" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="cardId" label="身&nbsp&nbsp份&nbsp证">
          <el-input v-model="formData.cardId" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="address" label="地址">
          <el-input v-model="formData.address" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="postCode" label="邮编">
          <el-input v-model="formData.postCode" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="tel" label="电话">
          <el-input v-model="formData.tel" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="phone" label="手&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp机">
          <el-input v-model="formData.phone" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="qqNum" label="Q Q">
          <el-input v-model="formData.qqNum" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="sex" label="性别">
          <el-select v-model="formData.sex" class="m-1" placeholder="选择性别" size="default">
            <el-option
              v-for="item in optionsSex"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="party" label="政治面貌">
          <el-input v-model="formData.party" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="race" label="民族">
          <el-input v-model="formData.race" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="education" label="学历">
          <el-input v-model="formData.education" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="speciality" label="专业">
          <el-input v-model="formData.speciality" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="hobby" label="特&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp长">
          <el-input v-model="formData.hobby" placeholder="请输入"/>
        </el-form-item>
        <el-form-item prop="dept" label="所属部门">
          <el-select
            v-model="formData.deptId"
            filterable
            remote
            placeholder="请选择所属部门"
            :remote-method="queryDept"
            remote-show-suffix
            :loading="loading"
          >
            <el-option
              v-for="item in optionsDept"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="job" label="所属职位">
          <el-select
            v-model="formData.jobId"
            filterable
            :disabled="a"
            placeholder="请选择所属职位"
          >
            <el-option
              v-for="item in optionsJob"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="formData.birthday"
            type="date"
            prop="birthday"
            placeholder="选择时间"
            format="YYYY/MM/DD"
            value-format="YYYY-MM-DD"
          />
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
