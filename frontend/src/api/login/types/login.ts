export interface LoginRequestData {
  /** admin 或 editor */
  loginName: string
  /** 密码 */
  password: string
  /** 验证码 */
  code: string
}

export interface LoginFaceRequestData {
  /** 图片的base64编码*/
  base: string
}

export interface UserInfoChangeRequestData {
  password: string
  username: string
}

//  图片验证码请求响应
export type LoginCodeResponseData = ApiResponseData<string>

// token 响应
export type LoginResponseData = ApiResponseData<{ token: string }>

// 人脸登录响应
export type LoginResponseFaceData = ApiResponseData<{ token: string }>

// 用户登录响应
export type UserInfoResponseData = ApiResponseData<{ username: string; roles: string[] }>

// 用户注册脸部信息
export type RegisterFace = ApiResponseData<{ base: string }>

// 用户信息更改
export type UserInfoChange = ApiResponseData<{ password: string; username: string }>
