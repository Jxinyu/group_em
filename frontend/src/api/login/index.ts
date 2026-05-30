import { request } from "@/utils/service"
import type * as Login from "./types/login"
import {UserInfoChange, UserInfoChangeRequestData} from "./types/login";

/** 获取登录验证码 */
export function getLoginCodeApi() {
  return request<Login.LoginCodeResponseData>({
    url: "login/code",
    method: "get"
  })
}

/** 登录并返回 Token */
export function loginApi(data: Login.LoginRequestData) {
  return request<Login.LoginResponseData>({
    url: "users/login",
    method: "post",
    data
  })
}

/** 人脸登录并返回 Token */
export function loginApiFace(data: Login.LoginFaceRequestData) {
  return request<Login.LoginResponseFaceData>({
    url: "users/login/face",
    method: "post",
    data
  })
}

/** 获取用户详情 */
export function getUserInfoApi() {
  return request<Login.UserInfoResponseData>({
    url: "users/info",
    method: "get"
  })
}

/** 用户注册脸部信息 */
export function registerFace(data: Login.LoginFaceRequestData) {
  return request<Login.RegisterFace>({
    url: "user/register/face",
    method: "post",
    data
  })
}

/** 用户信息更改 */
export function userInfoChange(data: Login.UserInfoChangeRequestData) {
  return request<Login.UserInfoChange>({
    url: "user/info/change",
    method: "post",
    data
  })
}
