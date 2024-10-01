import { type RouteRecordRaw, createRouter, createWebHashHistory, createWebHistory } from "vue-router"

const Layout = () => import("@/layout/index.vue")

/** 常驻路由 */
export const constantRoutes: RouteRecordRaw[] = [
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
    path: "/",
    component: Layout,
    redirect: "/main",
    children: [
      {
        path: "main",
        component: () => import("@/views/main/index.vue"),
        name: "Main",
        meta: {
          title: "首页",
          // svgIcon: "main",
          elIcon: "HomeFilled",
          affix: true
        }
      }
    ]
  },
  {
    path: "/features",
    component: Layout,
    name: "Feature",
    redirect: "/feature/list",
    meta: {
      title: "Feature管理",
      elIcon: "Opportunity",
      keepAlive: true
    },
    children: [
      {
        path: "/feature/list",
        component: () => import("@/views/feature/index.vue"),
        name: "Feature",
        meta: {
          title: "Feature管理",
          elIcon: "Opportunity",
          keepAlive: true
        }
      },
      {
        path: "/feature/edit",
        component: () => import("@/views/feature/edit.vue"),
        name: "编辑",
        meta: {
          title: "编辑",
          keepAlive: true,
          hidden: true
        }
      },
      {
        path: "/feature/config/list",
        component: () => import("@/views/feature/config/index.vue"),
        name: "featureConigList",
        meta: {
          title: "配置管理",
          hidden: true,
          keepAlive: true
        }
      },
      {
        path: "/feature/config/edit",
        component: () => import("@/views/feature/config/edit.vue"),
        name: "featureConfigEdit",
        meta: {
          title: "配置管理",
          hidden: true
        }
      }
    ]
  },
  {
    path: "/experiments",
    component: Layout,
    name: "Experiment",
    redirect: "/experiment/list",
    meta: {
      title: "Experiment管理",
      keepAlive: true
    },
    children: [
      {
        path: "/experiment/list",
        component: () => import("@/views/experiment/index.vue"),
        name: "Experiment-List",
        meta: {
          title: "Experiment管理",
          elIcon: "ScaleToOriginal",
          keepAlive: true
        }
      },
      {
        path: "/experiment/edit/:id?",
        component: () => import("@/views/experiment/edit.vue"),
        name: "Edit-Experiment",
        meta: {
          title: "编辑",
          hidden: true
        }
      }
    ]
  },
  {
    path: "/template/filter-condition",
    component: Layout,
    name: "FilterConditionTemplate",
    redirect: "/template/filter-condition/list",
    meta: {
      title: "FilterCondition管理",
      keepAlive: true
    },
    children: [
      {
        path: "/template/filter-condition/list",
        component: () => import("@/views/filter-condition-template/index.vue"),
        name: "Filter-Condition-Template-List",
        meta: {
          title: "FilterCondition管理",
          elIcon: "Document",
          keepAlive: true
        }
      },
      {
        path: "/template/filter-condition/edit/:id?",
        component: () => import("@/views/filter-condition-template/edit.vue"),
        name: "Edit-Filter-Condition-Template",
        meta: {
          title: "编辑",
          hidden: true
        }
      }
    ]
  },
  {
    path: "/demos",
    component: Layout,
    redirect: "/demo",
    name: "Demo",
    meta: {
      keepAlive: true
    },
    children: [
      {
        path: "/demo",
        component: () => import("@/views/demo/index.vue"),
        name: "Demo-index",
        meta: {
          title: "Demo",
          elIcon: "Paperclip",
          keepAlive: true
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
export const asyncRoutes: RouteRecordRaw[] = []

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
