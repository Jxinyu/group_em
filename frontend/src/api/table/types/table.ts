export interface CreateTableRequestData {
  username: string
  password: string
}

export interface UpdateTableDocumentRequestData {
  id: string,
  /** 标题 */
  title?: string,
  /** 文件名 */
  filename?: string,
  /** 文件描述 */
  remark?: string
}

/**
 * 批量删除
 * idString: 2&3&
 */
export interface DeleteTableRequestData {
  idString: string
}
export interface GetTableDocumentRequestData {
  /** 当前页码 */
  currentPage: number
  /** 查询条数 */
  size: number
  /** 文件标题 */
  title?: string,
  /** 文件名 */
  filename?: string,
  /** 文件描述 */
  remark?: string,
  /** 文件创建时间 */
  createDate?: string,
  /** 创建者 */
  loginName?: string
}
export interface GetTableDocumentData {
  id: bigint
  title: string
  filename: string
  remark: string
  fileName: string
  createDate: string
  loginName: string
  filePath: string
}

//region 公告
export interface GetTableNoticeRequestData {
  /** 当前页码 */
  currentPage: number
  /** 查询条数 */
  size: number
  /** 公告标题 */
  title?: string,
  /** 公告内容 */
  content?: string,
  /** 公告创建时间 */
  createDate?: string,
  /** 创建者 */
  loginName?: string
}
export interface GetTableNoticeData {
  id: bigint
  /** 公告标题 */
  title: string,
  /** 公告内容 */
  content: string,
  /** 公告创建时间 */
  createDate: string,
  /** 创建者 */
  loginName: string
}
export interface UpdateTableNoticeData {
  id?: string
  /** 公告标题 */
  title?: string,
  /** 公告内容 */
  content?: string,
  /** 公告创建时间 */
  createDate?: string,
  /** 创建者 */
  loginName?: string
}
export interface GetTablePushedNoticeData {
  nid: bigint,
  scope: bigint,
  pushWay: bigint,
  pushDate: string
}

//endregion
//region 用户
export interface GetTableUserRequestData {
  /** 当前页码 */
  currentPage?: number
  /** 查询条数 */
  size?: number
  /** 用户id */
  id?: string
  /** 登录名 */
  loginname?: string,
  /** 权限 */
  status?: string,
  /** 公告创建时间 */
  createdate?: string,
  /** 用户名 */
  username?: string
  /** 密码 */
  password?: string

  email?: string

}
//endregion
//region 职位
export interface GetTableJobRequestData {
  /** 当前页码 */
  currentPage?: number
  /** 查询条数 */
  size?: number
  /** id */
  id?: string
  /** 职位名称 */
  name?: string,
  /** 详细信息 */
  remark?: string,
  dept?: string
  deptId?: string
}
//endregion
//region 部门
export interface GetTableDeptRequestData {
  /** 当前页码 */
  currentPage?: number
  /** 查询条数 */
  size?: number
  /** id */
  id?: string
  /** 部门名称 */
  name?: string,
  /** 部门信息 */
  remark?: string
}
//endregion
//region 员工
export interface GetTableEmployeeRequestData {
  /** 当前页码 */
  currentPage?: number
  /** 查询条数 */
  size?: number
  /** id */
  id?: string
  /** 部门 */
  deptId?: string
  /** 部门 */
  dept?: string
  /** 职位id */
  jobId?: string
  /** 职位 */
  job?: string
  /** 名字 */
  name?: string
  /** 身份证 */
  cardId?: string
  /** 地址 */
  address?: string
  /** 邮编 */
  postCode?: string
  /** 电话 */
  tel?: string
  /** 手机 */
  phone?: string
  /** qq */
  qqNum?: string
  /** 邮箱 */
  email?: string
  /** 性别 */
  sex?: string
  /** 政治面貌 */
  party?: string
  /** 出生日期 */
  birthday?: string
  /** 民族 */
  race?: string
  /** 学历 */
  education?: string
  /** 专业 */
  speciality?: string
  /** 特长 */
  hobby?: string
  /** 备注 */
  remark?: string
  /** 创建日期 */
  createDate?: string
}
//endregion
//region chat
export interface GetChatHistoryRecords{
  key_username: string,
  side_username: string,
  content: string,
  datetime: string
}

//endregion



export type GetTableResponseData = ApiResponseData<{
  data: GetTableDocumentData[]
  total: number
}>

export type GetTableResponseNoticeData = ApiResponseData<{
  data: GetTableNoticeData[]
  total: number
}>
