<script lang="ts" setup>
import { ref } from "vue"
import { useUserStore } from "@/store/modules/user"
import AdminDashboard from "./admin/index.vue"
import EditorDashboard from "./editor/index.vue"

type CurrentRole = "1" | "2"

const userStore = useUserStore()
const currentRole = ref<CurrentRole>("1")
if (!userStore.roles.includes("1")) {
  currentRole.value = "2"
}
</script>

<template>
  <component :is="currentRole === '1' ? AdminDashboard : EditorDashboard" />
</template>
