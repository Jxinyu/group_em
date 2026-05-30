<script lang="ts" setup>
import {reactive, ref} from "vue"
import {ElLoading, ElMessage, FormInstance, FormRules} from "element-plus";
import {registerFace, userInfoChange} from "@/api/login";

defineOptions({
  name: "personal"
})
const loading = ref(false)
//region 人脸注册逻辑
/** 刷脸登录逻辑 */
const dialogVisible = ref(false)
const dialogVisible2 = ref(false)
const videoElements = ref()
const openDialog = () => {
  const videoElement = ref(videoElements)
  navigator.mediaDevices.getUserMedia({ video: true })
    .then(stream => {
      videoElement.value.srcObject = stream
      videoElement.value.play()
    })
    .catch(error => {
      console.error('Error accessing camera: ', error)
    })
}

const openFullScreen2 = () => {
  return ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  });
}
const capture = () => {
  let openFullScreen = openFullScreen2();  // 打开加载层

  const videoElement = ref(videoElements)
  // 进行拍照或录像操作，根据需求进行处理
  const canvasElement = document.createElement('canvas')
  const context = canvasElement.getContext('2d')

  // 将canvas的尺寸设置为视频的尺寸
  canvasElement.width = videoElement.value.videoWidth
  canvasElement.height = videoElement.value.videoHeight

  // 在canvas上绘制视频帧
  context.drawImage(
    videoElement.value,
    0,
    0,
    canvasElement.width,
    canvasElement.height
  )

  // 获取图像的Base64编码
  const imageDataURI = canvasElement.toDataURL('image/jpeg')

  // 处理Base64编码，例如将其发送到后端或进行其他操作
  //console.log('Captured Image Base64:', imageDataURI)
  videoElement.value.pause();  // 暂停录制

    registerFace({  // 上传图片进行注册
      base: imageDataURI
    })
    .then((res) => {
      ElMessage.success(res.message)
    })
    .catch((err) => {
      //ElMessage.error(err)
    })
    .finally(() => {
      dialogVisible.value = false  // 关闭弹出层
      openFullScreen.close()  // 关闭加载层
    })

}
//endregion

//region  用户信息更改
const ruleFormRef = ref<FormInstance>()

const validatePass = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('Please input the password'))
  } else {
    if (ruleForm.checkPass !== '') {
      if (!ruleFormRef.value) return
      ruleFormRef.value?.validateField('checkPass', () => null)
    }
    callback()
  }
}
const validatePass2 = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('Please input the password again'))
  } else if (value !== ruleForm.password) {
    callback(new Error("Two inputs don't match!"))
  } else {
    callback()
  }
}

const ruleForm = reactive({
  password: '',
  checkPass: '',
  username: '',
})

const rules = reactive<FormRules>({
  password: [{ validator: validatePass, trigger: 'blur' }],
  checkPass: [{ validator: validatePass2, trigger: 'blur' }],
})

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      console.log('submit!')
      userInfoChange({ // 信息上传
        password: ruleForm.password,
        username: ruleForm.username
      }).then((res)=>{
        ElMessage.success(res.message)
      }).catch((err)=>{
        console.log(err)
      }).finally(()=>{
        dialogVisible2.value = false
      })
    } else {
      console.log('error submit!')
      return false
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}

//endregion

</script>

<template>
  <div class="app-container">
    <el-card style="text-align: center">
      <h1>个人中心</h1>
    </el-card>
    <el-card style="margin-top: 20px">
      <el-button @click="dialogVisible = true" type="success">点击 注册/更新 刷脸信息</el-button>
      <el-button @click="dialogVisible2 = true" type="success">点击 修改 个人信息</el-button>
    </el-card>


    <el-dialog
      v-model="dialogVisible"
      title="人脸注册/修改"
      width="35%"
      @opened="openDialog">
      <video id="video" ref="videoElements" autoplay style="width: 100%; height: 300px"></video>
      <template #footer>
            <span class="dialog-footer">
              <el-button @click="capture" size="large" type="primary"> 确  认 </el-button>
              <el-button @click="openDialog" size="large" type="info" style="margin-left: 0"> 刷  新 </el-button>
            </span>
      </template>
    </el-dialog>

    <el-dialog
      v-model="dialogVisible2"
      title="个人信息修改"
      width="35%"
      @opened="openDialog"
    >
      <el-form
        ref="ruleFormRef"
        :model="ruleForm"
        status-icon
        :rules="rules"
        label-width="120px"
        class="demo-ruleForm"
      >
        <el-form-item label="新密码" prop="password">
          <el-input v-model="ruleForm.password" type="password" autocomplete="off" />
        </el-form-item>
        <el-form-item label="再次输入" prop="checkPass">
          <el-input
            v-model="ruleForm.checkPass"
            type="password"
            autocomplete="off"
          />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="ruleForm.username" min="2" max="10" autocomplete="off"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm(ruleFormRef)"
          >Submit</el-button
          >
          <el-button @click="resetForm(ruleFormRef)">Reset</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>


</style>
