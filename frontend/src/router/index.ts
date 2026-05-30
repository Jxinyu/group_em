import { type RouteRecordRaw, createRouter, createWebHashHistory, createWebHistory } from "vue-router"

const Layout = () => import("@/layout/index.vue")

/** 常驻路由 */
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/redirect",
    component: Layout,
    meta: {
      hidden: true
    },
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue")
      }
    ]
  },
  {
    path: "/403",
    component: () => import("@/views/error-page/403.vue"),
    meta: {
      hidden: true
    }
  },
  {
    path: "/404",
    component: () => import("@/views/error-page/404.vue"),
    meta: {
      hidden: true
    },
    alias: "/:pathMatch(.*)*"
  },
  {
    path: "/login",
    component: () => import("@/views/login/index.vue"),
    meta: {
      hidden: true
    }
  },
  {
    path: "/",
    component: Layout,
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        component: () => import("@/views/dashboard/index.vue"),
        name: "Dashboard",
        meta: {
          title: "首页",
          svgIcon: "dashboard",
          affix: true
        }
      }
    ]
  },
  {
    path: "/unocss",
    component: Layout,
    redirect: "/unocss/index",
    children: [
      {
        path: "index",
        component: () => import("@/views/unocss/index.vue"),
        name: "UnoCSS",
        meta: {
          title: "unocss",
          svgIcon: "unocss"
        }
      }
    ]
  },

  //region 个人中心
  {
    path: "/personal",
    component: Layout,
    children: [
      {
        path: "center",
        component: () => import("@/views/personal/index.vue"),
        name: "个人中心",
        meta: {
          title: '个人中心'
        }
      }
    ],
    meta: {
      hidden: true
    }
  },
  //endregion

  //region 用户管理
  {
    path: "/user-management",
    component: Layout,
    redirect: "/user/user",
    children: [
      {
        path: "user",
        component: () => import("@/views/table/user/index.vue"),
        name: "user",
        meta: {
          title: "用户管理",
          elIcon: "Avatar"
        }
      },
    ]
  },
  //endregion

  //region 员工管理
  {
    path: "/employee-management",
    component: Layout,
    redirect: "/employee/employee",
    children: [
      {
        path: "employee",
        component: () => import("@/views/table/employee/index.vue"),
        name: "employee",
        meta: {
          title: "员工管理",
          elIcon: "UserFilled"
        }
      },
    ]
  },
  //endregion

  //region 部门管理
  {
    path: "/dept-management",
    component: Layout,
    redirect: "/dept/dept",
    children: [
      {
        path: "dept",
        component: () => import("@/views/table/dept/index.vue"),
        name: "dept",
        meta: {
          title: "部门管理",
          elIcon: "HomeFilled"
        }
      },
    ]
  },
  //endregion

  //region 职位管理
  {
    path: "/job-management",
    component: Layout,
    redirect: "/job/job",
    children: [
      {
        path: "job",
        component: () => import("@/views/table/job/index.vue"),
        name: "job",
        meta: {
          title: "职位管理",
          elIcon: "List"
        }
      },
    ]
  },
  //endregion

  //region 文件管理
  {
    path: "/table",
    component: Layout,
    redirect: "/table/document",
    name: "文件",
    meta: {
      title: "文件管理",
      elIcon: "Management"
    },
    children: [
      {
        path: "document",
        component: () => import("@/views/table/document/index.vue"),
        name: "document",
        meta: {
          title: "文件管理",
          elIcon: 'Document',
          keepAlive: true
        }
      },
      {
        path: "document_upload",
        component: () => import("@/views/table/document/uploadFile.vue"),
        name: "document_upload",
        meta: {
          title: "文件上传",
          elIcon: 'DocumentAdd',
          keepAlive: true
        }
      }
    ]
  },
  //endregion

  //region 公告管理
  {
    path: "/notice-management",
    component: Layout,
    redirect: "/notice/management",
    children: [
      {
        path: "notice",
        component: () => import("@/views/table/notice/index.vue"),
        name: "notice",
        meta: {
          title: "公告管理",
          elIcon: "Histogram"
        }
      },
    ]
  },
  //endregion

  //region chat
  {
    path: "/chatting",
    component: Layout,
    redirect: "/chatting",
    children: [
      {
        path: "chatting",
        component: () => import("@/views/table/privacyChat.vue"),
        name: "私人聊天",
        meta: {
          title: "私人聊天",
          elIcon: "ChatDotSquare"
        }
      },
    ]
  },
  //test
  {
    path: "/chatting2",
    component: Layout,
    redirect: "/chatting2",
    children: [
      {
        path: "chatting2",
        component: () => import("@/views/table/groupChat.vue"),
        name: "群聊",
        meta: {
          title: "群聊",
          elIcon: "ChatDotSquare"
        }
      },
    ]
  },
  //endregion

  {
    path: "/menu",
    component: Layout,
    redirect: "/menu/menu1",
    name: "Menu",
    meta: {
      title: "多级菜单",
      svgIcon: "menu"
    },
    children: [
      {
        path: "menu1",
        component: () => import("@/views/menu/menu1/index.vue"),
        redirect: "/menu/menu1/menu1-1",
        name: "Menu1",
        meta: {
          title: "menu1"
        },
        children: [
          {
            path: "menu1-1",
            component: () => import("@/views/table/document/index.vue"),
            name: "Menu1-1",
            meta: {
              title: "menu1-1"
            }
          },
          {
            path: "menu1-2",
            component: () => import("@/views/menu/menu1/menu1-2/index.vue"),
            redirect: "/menu/menu1/menu1-2/menu1-2-1",
            name: "Menu1-2",
            meta: {
              title: "menu1-2"
            },
            children: [
              {
                path: "menu1-2-1",
                component: () => import("@/views/menu/menu1/menu1-2/menu1-2-1/index.vue"),
                name: "Menu1-2-1",
                meta: {
                  title: "menu1-2-1"
                }
              },
              {
                path: "menu1-2-2",
                component: () => import("@/views/menu/menu1/menu1-2/menu1-2-2/index.vue"),
                name: "Menu1-2-2",
                meta: {
                  title: "menu1-2-2"
                }
              }
            ]
          },
          {
            path: "menu1-3",
            component: () => import("@/views/table/document/uploadFile.vue"),
            name: "Menu1-3",
            meta: {
              title: "menu1-3"
            }
          }
        ]
      },
      {
        path: "menu2",
        component: () => import("@/views/menu/menu2/index.vue"),
        name: "Menu2",
        meta: {
          title: "menu2"
        }
      }
    ]
  },
  {
    path: "/hook-demo",
    component: Layout,
    redirect: "/hook-demo/use-fetch-select",
    name: "HookDemo",
    meta: {
      title: "hook 示例",
      elIcon: "Menu",
      alwaysShow: true
    },
    children: [
      {
        path: "use-fetch-select",
        component: () => import("@/views/hook-demo/use-fetch-select.vue"),
        name: "UseFetchSelect",
        meta: {
          title: "useFetchSelect"
        }
      },
      {
        path: "use-fullscreen-loading",
        component: () => import("@/views/hook-demo/use-fullscreen-loading.vue"),
        name: "UseFullscreenLoading",
        meta: {
          title: "useFullscreenLoading"
        }
      }
    ]
  }
]

/**
 * 动态路由
 * 用来放置有权限 (Roles 属性) 的路由
 * 必须带有 Name 属性
 */
export const asyncRoutes: RouteRecordRaw[] = [
  {
    path: "/permission",
    component: Layout,
    redirect: "/permission/page",
    name: "Permission",
    meta: {
      title: "权限管理",
      svgIcon: "lock",
      roles: ["1", "2"], // 可以在根路由中设置角色
      alwaysShow: true // 将始终显示根菜单
    },
    children: [
      {
        path: "page",
        component: () => import("@/views/permission/page.vue"),
        name: "PagePermission",
        meta: {
          title: "页面权限",
          roles: ["1"] // 或者在子导航中设置角色
        }
      },
      {
        path: "directive",
        component: () => import("@/views/permission/directive.vue"),
        name: "DirectivePermission",
        meta: {
          title: "指令权限" // 如果未设置角色，则表示：该页面不需要权限，但会继承根路由的角色
        }
      }
    ]
  },
  {
    path: "/:pathMatch(.*)*", // Must put the 'ErrorPage' route at the end, 必须将 'ErrorPage' 路由放在最后
    redirect: "/404",
    name: "ErrorPage",
    meta: {
      hidden: true
    }
  }
]

const router = createRouter({
  history:
    import.meta.env.VITE_ROUTER_HISTORY === "hash"
      ? createWebHashHistory(import.meta.env.VITE_PUBLIC_PATH)
      : createWebHistory(import.meta.env.VITE_PUBLIC_PATH),
  routes: constantRoutes
})

/** 重置路由 */
export function resetRouter() {
  // 注意：所有动态路由路由必须带有 Name 属性，否则可能会不能完全重置干净
  try {
    router.getRoutes().forEach((route) => {
      const { name, meta } = route
      if (name && meta.roles?.length) {
        router.hasRoute(name) && router.removeRoute(name)
      }
    })
  } catch {
    // 强制刷新浏览器也行，只是交互体验不是很好
    window.location.reload()
  }
}

export default router
