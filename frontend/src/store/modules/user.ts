import {ref} from "vue"
import store from "@/store"
import {defineStore} from "pinia"
import {usePermissionStore} from "./permission"
import {useTagsViewStore} from "./tags-view"
import {getToken, removeToken, setToken} from "@/utils/cache/cookies"
import router, {resetRouter} from "@/router"
import {loginApi, getUserInfoApi, loginApiFace} from "@/api/login"
import {LoginFaceRequestData, type LoginRequestData, LoginResponseFaceData} from "@/api/login/types/login"
import {type RouteRecordRaw} from "vue-router"
import asyncRouteSettings from "@/config/async-route"

export const useUserStore = defineStore("user", () => {
  const token = ref<string>(getToken() || "")
  const roles = ref<string[]>([])
  const username = ref<string>("")

  const permissionStore = usePermissionStore()
  const tagsViewStore = useTagsViewStore()

  /** 设置角色数组 */
  const setRoles = (value: string[]) => {
    roles.value = value
  }
  /** 登录 */
  const login = async ({loginName, password, code}: LoginRequestData) => {
    const {data} = await loginApi(
      {
        loginName: loginName,
        password: password,
        code: code
      }
    )
    setToken(data.token)
    token.value = data.token
  }

  /** 人脸登录 */
  const loginFace = async ({base}: LoginFaceRequestData) => {
    const {data} = await loginApiFace(
      {base}
    )
    setToken(data.token)
    token.value = data.token
  }

  /** 获取用户详情 权限 */
  const getInfo = async () => {
    const {data} = await getUserInfoApi()
    username.value = data.loginName
    // 验证返回的 roles 是否为一个非空数组，否则塞入一个没有任何作用的默认角色，防止路由守卫逻辑进入无限循环
    roles.value = data.roles?.length > 0 ? data.roles : asyncRouteSettings.defaultRoles
  }

  /** 切换角色 */
  const changeRoles = async (role: string) => {
    const newToken = "token-" + role
    token.value = newToken
    setToken(newToken)
    await getInfo()
    permissionStore.setRoutes(roles.value)
    resetRouter()
    permissionStore.dynamicRoutes.forEach((item: RouteRecordRaw) => {
      router.addRoute(item)
    })
    _resetTagsView()
  }
  /** 登出 */
  const logout = () => {
    removeToken()
    token.value = ""
    roles.value = []
    username.value = ''
    resetRouter()
    _resetTagsView()
  }
  /** 重置 Token */
  const resetToken = () => {
    removeToken()
    token.value = ""
    roles.value = []
  }
  /** 重置 Visited Views 和 Cached Views */
  const _resetTagsView = () => {
    tagsViewStore.delAllVisitedViews()
    tagsViewStore.delAllCachedViews()
  }

  return {token, roles, username, setRoles, login, getInfo, changeRoles, logout, resetToken, loginFace}
})

/** 在 setup 外使用 */
export function useUserStoreHook() {
  return useUserStore(store)
}
