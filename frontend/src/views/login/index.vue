<script lang="ts" setup>
import {reactive, Ref, ref} from "vue"
import {useRouter} from "vue-router"
import {useUserStore} from "@/store/modules/user"
import {User, Lock, Key, Picture, Loading} from "@element-plus/icons-vue"
import ThemeSwitch from "@/components/ThemeSwitch/index.vue"
import {ElLoading, ElMessage, type FormInstance, FormRules} from "element-plus"
import {getLoginCodeApi} from "@/api/login"
import {type LoginRequestData} from "@/api/login/types/login"

const router = useRouter()
const loginFormRef = ref<FormInstance | null>(null)
//region  登录
/** 登录按钮 Loading */
const loading = ref(false)
/** 验证码图片 URL */
const codeUrl = ref("")
/** 登录表单数据 */
const loginForm: LoginRequestData = reactive({
  loginName: "",
  password: "",
  code: ""
})
/** 登录表单校验规则 */
const loginFormRules: FormRules = {
  loginName: [{required: true, message: "请输入用户名", trigger: "blur"}],
  password: [
    {required: true, message: "请输入密码", trigger: "blur"},
    {min: 6, max: 28, message: "长度在 6 到 28 个字符", trigger: "blur"}
  ],
  code: [{required: true, message: "请输入验证码", trigger: "blur"}]
}
/** 登录逻辑 */
const handleLogin = () => {
  loginFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      // console.log(loginForm)
      useUserStore()
        .login({
          loginName: loginForm.loginName,
          password: loginForm.password,
          code: loginForm.code
        })
        .then(() => {
          router.push({path: "/"})
        })
        .catch(() => {
          createCode()
        })
        .finally(() => {
          loading.value = false
        })
    } else {
      return false
    }
  })
}
//endregion
//region 刷脸登录逻辑
/** 刷脸登录逻辑 */
const dialogVisible = ref(false)
const videoElements = ref()
const videoStop: Ref<any> = ref()
const openDialog = () => {
  const videoElement = ref(videoElements)
  navigator.mediaDevices.getUserMedia({ video: true })
    .then(stream => {
      videoElement.value.srcObject = stream
      videoElement.value.play()
      videoStop.value = stream.getTracks()
    })
    .catch(error => {
      console.error('Error accessing camera: ', error)
    })
}
const openFullScreen2 = () => {
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  return loading;
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

  // 从canvas获取图像信息
  // const imageData = context.getImageData(
  //   0,
  //   0,
  //   canvasElement.width,
  //   canvasElement.height
  // )

  // 处理图像信息，例如将图像数据发送到后端或进行其他操作
  //console.log('Captured Image Data:', imageData)

  // 获取图像的Base64编码
  const imageDataURI = canvasElement.toDataURL('image/jpeg')

  // 处理Base64编码，例如将其发送到后端或进行其他操作
  //console.log('Captured Image Base64:', imageDataURI)
  videoElement.value.pause();  // 暂停录制

  useUserStore()
    .loginFace({
      base: imageDataURI
    })
    .then(() => {
      router.push({path: "/"})
    })
    .catch(() => {
      ElMessage.error('登录失败，请使用账号密码登录')
    })
    .finally(() => {
      dialogVisible.value = false
      closeVideo()
      openFullScreen.close()  // 关闭加载层
      loading.value = false
    })

}
//endregion
const closeVideo = async ()=>{
  videoStop.value.forEach(track=>{
    if (track.readyState == 'live' && track.kind === 'video'){
      track.stop()
    }
  })
}

//region 创建验证码
/** 创建验证码 */
const createCode = () => {
  // 先清空验证码的输入
  loginForm.code = ""
  // 获取验证码
  codeUrl.value = ""
  getLoginCodeApi().then((res) => {
    codeUrl.value = 'data:image/jpeg;base64,' + res.data;
  })
}

/** 初始化验证码 */
createCode()
//endregion
</script>

<template>
  <div class="login-container" style="background-image: linear-gradient(90deg, #000000 0%, #313133 100%);">
    <ThemeSwitch class="theme-switch"/>
    <div class="animation" style="">
      <!--外层立方体-->
      <div id="parent">
        <div><img src="/api/v1/static/1.jpg" style="width: 100%; height: 100%;" alt=""></div>
        <div><img src="/api/v1/static/2.jpg" style="width: 100%; height: 100%;" alt=""></div>
        <div><img src="/api/v1/static/3.jpg" style="width: 100%; height: 100%;" alt=""></div>
        <div><img src="/api/v1/static/4.jpg" style="width: 100%; height: 100%;" alt=""></div>
        <div><img src="/api/v1/static/5.jpg" style="width: 100%; height: 100%;" alt=""></div>
        <div><img src="/api/v1/static/6.jpg" style="width: 100%; height: 100%;" alt=""></div>

      </div>
<!--      &lt;!&ndash;内层立方体&ndash;&gt;-->
<!--      <div id="parent_new">-->
<!--        <div><img src="/api/v1/static/1.jpg" style="width: 100%; height: 100%;" alt=""></div>-->
<!--        <div><img src="/api/v1/static/2.jpg" style="width: 100%; height: 100%;" alt=""></div>-->
<!--        <div><img src="/api/v1/static/3.jpg" style="width: 100%; height: 100%;" alt=""></div>-->
<!--        <div><img src="/api/v1/static/4.jpg" style="width: 100%; height: 100%;" alt=""></div>-->
<!--        <div><img src="/api/v1/static/5.jpg" style="width: 100%; height: 100%;" alt=""></div>-->
<!--        <div><img src="/api/v1/static/6.jpg" style="width: 100%; height: 100%;" alt=""></div>-->
<!--      </div>-->
    </div>
    <div class="login-card">
      <div class="title">
        <img src="@/assets/layout/logo-text-2.png"/>
      </div>
      <div class="content">
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginFormRules" @keyup.enter="handleLogin">
          <el-form-item prop="username">
            <el-input
              v-model.trim="loginForm.loginName"
              placeholder="用户名"
              type="text"
              tabindex="1"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model.trim="loginForm.password"
              placeholder="密码"
              type="password"
              tabindex="2"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item prop="code">
            <el-input
              v-model.trim="loginForm.code"
              placeholder="验证码"
              type="text"
              tabindex="3"
              :prefix-icon="Key"
              maxlength="7"
              size="large"
            >
              <template #append>
                <el-image :src="codeUrl" @click="createCode" draggable="false">
                  <template #placeholder>
                    <el-icon>
                      <Picture/>
                    </el-icon>
                  </template>
                  <template #error>
                    <el-icon>
                      <Loading/>
                    </el-icon>
                  </template>
                </el-image>
              </template>
            </el-input>
          </el-form-item>
          <el-button :loading="loading" type="primary" size="large" @click.prevent="handleLogin"> 登 录</el-button>
        </el-form>
        <el-button type="primary" size="large" @click="dialogVisible = true"
                   > 刷 脸 登 录
        </el-button>

        <el-dialog
          v-model="dialogVisible"
          title="人脸登录"
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
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.animation{
  display: flex;
  justify-content: left;
  align-items: center;
  width: 60%;
  min-height: 100%;
}

.login-container {
  display: flex;
  justify-content: right;
  align-items: center;
  width: 100%;
  min-height: 100%;

  .theme-switch {
    position: fixed;
    top: 5%;
    right: 5%;
    cursor: pointer;
  }

  .login-card {
    width: 380px;
    border-radius: 20px;
    box-shadow: 0 0 10px #dcdfe6;
    background-color: #fff;
    margin-right: 10%;
    overflow: hidden;

    .title {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 150px;

      img {
        height: 100%;
      }
    }

    .content {
      padding: 20px 50px 50px 50px;

      :deep(.el-input-group__append) {
        padding: 0;
        overflow: hidden;

        .el-image {
          width: 100px;
          height: 40px;
          border-left: 0px;
          user-select: none;
          cursor: pointer;
          text-align: center;
        }
      }

      .el-button {
        width: 100%;
        margin-top: 10px;
      }
    }
  }
}
</style>
<style scoped>
#parent{
  margin: 40px auto;
  width: 300px;
  height: 300px;
  position: relative;
  transform: rotate(45deg);
  transform-style: preserve-3d;
  animation: 10s spin linear infinite;
  transition: transform 1s linear;
}
#parent_new{
  left: calc(50% - 75px);
  width: 150px;
  height: 150px;
  position: absolute;
  transform: rotate(45deg) ;
  transform-style: preserve-3d;
  animation: 3s spin_new linear infinite;
  transition: transform 0.5s linear;
}
#parent>div{
  width: 300px;
  height: 300px;
  position: absolute;
  border: 1px solid #ccc;
  opacity: 0.5;

}
#parent_new>div{
  width: 150px;
  height: 150px;
  position: absolute;
  border: 1px solid #ccc;
  background: black;
  opacity: 0.5;
}
#parent>div:nth-child(1){
  transform: translateZ(-150px);
  transition: transform 1s linear;
}
#parent>div:nth-child(2){
  transform:rotateY(-90deg) translateZ(150px) ;
  transition: transform 1s linear;
}
#parent>div:nth-child(3){
  transform:rotateY(-90deg) translateZ(-150px) ;
  transition: transform 1s linear;

}
#parent>div:nth-child(4){
  transform:rotateX(90deg) translateZ(150px) ;
  transition: transform 1s linear;

}
#parent>div:nth-child(5){
  transform:rotateX(90deg) translateZ(-150px) ;
  transition: transform 1s linear;

}
#parent>div:nth-child(6){
  transform: translateZ(150px);
}

#parent_new>div:nth-child(1){
  transform: translateZ(-75px);
  transition: transform 1s linear;

}
#parent_new>div:nth-child(2){
  transform:rotateY(-90deg) translateZ(75px) ;
  transition: transform 1s linear;
}
#parent_new>div:nth-child(3){
  transform:rotateY(-90deg) translateZ(-75px) ;
  transition: transform 1s linear;

}
#parent_new>div:nth-child(4){
  transform:rotateX(90deg) translateZ(75px) ;
  transition: transform 1s linear;

}
#parent_new>div:nth-child(5){
  transform:rotateX(90deg) translateZ(-75px) ;
  transition: transform 1s linear;

}
#parent_new>div:nth-child(6){
  transform: translateZ(75px);
}
@keyframes spin{
  0%{
    transform: rotateX(0deg) rotateY(0deg);
  }
  100%{
    transform: rotateX(360deg) rotateY(360deg);
  }

}
@keyframes spin_new{
  0%{
    transform: rotateX(0deg) rotateY(0deg);
  }
  100%{
    transform: rotateX(360deg) rotateY(360deg);
  }

}

</style>
