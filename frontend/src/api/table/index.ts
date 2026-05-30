import {request} from "@/utils/service"
import type * as Table from "./types/table"
import {GetTableResponseNoticeData} from "./types/table";

/** 删
 * url: "table/" + url
 */
export function deleteTableDataApi(data: Table.DeleteTableRequestData, url: string) {
  return request({
    url: `table/` + url,
    method: "delete",
    data
  })
}


//#region document
/** 改 文件
 * url: "table/" + url
 */
export function updateTableDocumentDataApi(data: Table.UpdateTableDocumentRequestData, url: string) {
  return request({
    url: "table/" + url,
    method: "put",
    data
  })
}

/** 查 文件
 * url: "table/" + url
 */
export function getTableDocumentDataApi(params: Table.GetTableDocumentRequestData, url: string) {
  return request<Table.GetTableResponseData>({
    url: "table/" + url,
    method: "get",
    params
  })
}
/** 下载 */
export function downloadDocument(id: bigint) {
  return request({
    url: "table/download/" + id,
    method: "get",
  })
}
//#endregion

//#region notice
/** 查 公告
 */
export function getTableNoticeDataApi(params: Table.GetTableNoticeRequestData) {
  return request<Table.GetTableResponseNoticeData>({
    url: "table/notice",
    method: "get",
    params
  })
}
/** 改 公告
 */
export function updateTableNoticeDataApi(data: Table.UpdateTableNoticeData) {
  return request({
    url: "table/notice",
    method: "put",
    data
  })
}
/** 增 公告
 */
export function insertTableNoticeDataApi(data: Table.UpdateTableNoticeData) {
  return request({
    url: "table/notice",
    method: "post",
    data
  })
}
/** 推送 公告
 */
export function pushNoticeDataApi(type: boolean, pushValue: null, noticeId: null, pushWay: null) {
  return request({
    url: "table/notice/push/"+type,
    method: "post",
    data: {
      nid: noticeId,
      scope: pushValue,
      pushWay: pushWay
    }
  })
}

/** 根据公告id查看已经推送的公告的。。
 */
export function getPushedNoticeById(noticeId: null) {
  return request({
    url: "table/notice/pushed/"+noticeId,
    method: "get",
  })
}

//#endregion

//#region user
/** 查
 */
export function getTableUserDataApi(params: Table.GetTableUserRequestData) {
  return request<Table.GetTableResponseData>({
    url: "table/user",
    method: "get",
    params
  })
}
/** 改
 */
export function updateTableUserDataApi(data: Table.GetTableUserRequestData) {
  return request({
    url: "table/user",
    method: "put",
    data
  })
}
/** 增
 */
export function insertTableUserDataApi(data: Table.GetTableUserRequestData) {
  return request({
    url: "table/user",
    method: "post",
    data
  })
}
//#endregion

//#region job
/** 查
 */
export function getTableJobDataApi(params: Table.GetTableJobRequestData) {
  return request<Table.GetTableResponseData>({
    url: "table/job",
    method: "get",
    params
  })
}
/** 改
 */
export function updateTableJobDataApi(data: Table.GetTableJobRequestData) {
  return request({
    url: "table/job",
    method: "put",
    data
  })
}
/** 增
 */
export function insertTableJobDataApi(data: Table.GetTableJobRequestData) {
  return request({
    url: "table/job",
    method: "post",
    data
  })
}
//#endregion

//#region dept
/** 查
 */
export function getTableDeptDataApi(params: Table.GetTableDeptRequestData) {
  return request<Table.GetTableResponseData>({
    url: "table/dept",
    method: "get",
    params
  })
}
/** 改
 */
export function updateTableDeptDataApi(data: Table.GetTableDeptRequestData) {
  return request({
    url: "table/dept",
    method: "put",
    data
  })
}
/** 增
 */
export function insertTableDeptDataApi(data: Table.GetTableDeptRequestData) {
  return request({
    url: "table/dept",
    method: "post",
    data
  })
}
//#endregion

//#region employee
/** 查
 */
export function getTableEmployeeDataApi(params: Table.GetTableEmployeeRequestData) {
  return request<Table.GetTableResponseData>({
    url: "table/employee",
    method: "get",
    params
  })
}
/** 改
 */
export function updateTableEmployeeDataApi(data: Table.GetTableEmployeeRequestData) {
  return request({
    url: "table/employee",
    method: "put",
    data
  })
}
/** 增
 */
export function insertTableEmployeeDataApi(data: Table.GetTableEmployeeRequestData) {
  return request({
    url: "table/employee",
    method: "post",
    data
  })
}
//#endregion
