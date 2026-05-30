<template>
  <el-card>
    <div class="container">
      <div class="user-list">
        <h2>在线用户</h2>
        <ul>
          <li v-for="(user, index) in onlineUsers" :key="user">
            {{ user }}
          </li>
        </ul>
      </div>
      <div class="chat-container">
        <span>
          <el-link style="color: #34a3ff; font-size: 10px; user-select: none"
                   @click="loadChatHistory">点击加载历史聊天记录</el-link>
        </span>
        <div id="chat-window" ref="chatWindow">
          <div v-for="message in chatHistory" :key="message.createDate"
               :class="{ 'message-left': message.username!==currentUser, 'message-right': message.username===currentUser}">
            <div class="message-content">

              <el-row :gutter="25">
                <el-col :lg="25">{{ message.username }}
                  <el-text size="small">{{ message.createDate }}</el-text>
                </el-col>
              </el-row>
              <el-row :gutter="10">
                <el-col :lg="10" v-if="message.contentType===0">{{ message.content }}</el-col>
                <el-col :lg="10" v-if="message.contentType===1"><img style="width: 100%" :src="message.content"/>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
        <div class="input-container">
          <el-input type="textarea" :rows="3" v-model="messageInput" placeholder="输入消息..."/>
          <div>
            <el-button type="primary" @click="sendMessage(null)">发送</el-button>
            <div class="custom-file-upload">
              <input type="file" ref="fileBtn" id="fileInput" style="display: none" accept=".jpg,.png,.jpeg"
                     @change="handleUpload"/>
              <label for="fileInput" class="el-button el-button--danger" style="width: 60px">选择文件</label>
            </div>

          </div>

        </div>
      </div>
    </div>
  </el-card>

</template>

<script setup lang="ts">
import {ref, onUnmounted, nextTick, watch, toRaw, onMounted, onBeforeMount} from 'vue';
import {ElMessage, ElMessageBox} from "element-plus";
import {useUserStoreHook} from "@/store/modules/user";
import {toJSONString, toStringJSON} from "xe-utils";
import {request} from "@/utils/service";
import axios from "axios";
import {getToken} from "@/utils/cache/cookies";

//region
// 在线用户
const onlineUsers = ref([]);
//正在聊天的用户
const chattingUser = ref('');
//消息输入
const messageInput = ref('');
// 当前用户
const currentUser = useUserStoreHook().username

//聊天历史记录
const chatHistory = ref([]);

const wsUrl = 'ws://localhost:8080/chat/group'

let ws = new WebSocket(wsUrl)

function reconnectWs() {  // 创建连接
  ws = new WebSocket(wsUrl)
}

//建立连接
//const ws = new WebSocket('ws://localhost:8080/chat/group');

//region加载历史聊天记录
const isloadChatHistory = ref(false)
const loadChatHistory = () => {
  if (isloadChatHistory.value) {
    ElMessage.warning('已加载过')
    return
  }
  request({url: '/chat/group/jigechat/his', method: 'get'})
    .then((res) => {
      chatHistory.value.splice(0, chatHistory.value.length)  // 清空

      chatHistory.value = chatHistory.value.concat(res.data.data)

      // 使用 sort 方法和比较函数对数组进行排序
      chatHistory.value.sort((a, b) => {
        const dateA = new Date(a.datetime);
        const dateB = new Date(b.datetime);
        return dateA - dateB; // 升序排序
      });

      isloadChatHistory.value = true
    })
}
//endregion

//region接收服务端推送的消息后触发
ws.onmessage = (res) => {
  // 获取服务器端推送过来的消息
  let re = toStringJSON(res.data)
  if (re.system) { // 系统消息
    console.log(re)
    if (re.contentType === 5) { // 是心跳
      console.log('接收到心跳')
    } else if (re.contentType === 6) {
      ElMessage.success('群聊连接成功')
      startInterval() // 开启心跳
    }else{
      onlineUsers.value = re.message
    }
  } else {// 不是系统消息
    // 存储对方的聊天记录
    if (re.toName === currentUser) {
      return
    }
    chatHistory.value = chatHistory.value.concat([{
      username: re.toName,
      content: re.message,
      contentType: re.contentType,
      createDate: currentDate()
    }])
  }
}
//endregion

//region 发送消息
function sendMessage(val) {
  const message = messageInput.value.trim();
  if (message !== '') {
    // 封装数据，并发送数据给后端
    const data = {toName: null, message: message, contentType: 0}
    // // 发送数据给服务端
    ws.send(toJSONString(data))
    // 存储历史聊天记录
    chatHistory.value = chatHistory.value.concat([{
      username: currentUser,
      content: message,
      contentType: 0,
      createDate: currentDate()
    }])
    messageInput.value = '';
  }
  if (val !== null && val !== 'heartBeat') {
    // 封装数据，并发送数据给后端
    const data = {toName: null, message: val, contentType: 1}
    // // 发送数据给服务端
    ws.send(toJSONString(data))
    // 存储历史聊天记录
    chatHistory.value = chatHistory.value.concat([{
      username: currentUser,
      content: val,
      contentType: 1,
      createDate: currentDate()
    }])
  }
  if (val === 'heartBeat') {  // 心跳检测
    // 封装数据，并发送数据给后端
    const data = {toName: null, message: 'heartBeat', contentType: 5}
    // // 发送数据给服务端
    ws.send(toJSONString(data))
  }
}

//endregion

//region 上传文件
const fileBtn = ref()
// 上传图片
const handleUpload = (e) => {
  const files = Array.prototype.slice.call(e.target.files)
  //console.log(files, "files")
  if (!files) {
    return
  }
  const formData = new FormData()
  formData.append('file', files[0])
  //此处使用服务端提供上传接口
  axios.post('/api/v1/table-chat/notice/img', formData, {
    headers: {
      "AUTH-TOKEN": getToken(),
    }
  })
    .then((response) => {
      // 文件上传成功后的处理
      // 这个 Code 是和后端约定的业务 Code
      const code = response.data.code
      // 如果没有 Code, 代表这不是项目后端开发的 API
      if (code === undefined) {
        ElMessage.error("非本系统的接口")
      } else {
        switch (code) {
          case 200:
            break
          default:
            // 不是正确的 Code 也就是用户不在服务范围内
            ElMessage.error(response.data.message || "Error")
        }
      }
      sendMessage(response.data.data)

    })
    .catch((error) => {
      ElMessage.error("文件上传失败，请稍后重试")
    })
    .finally(() => {
      fileBtn.value = null
      formData.delete('file')
    })
}
//endregion


ws.onopen = () => {
 // ElMessage.success('连接成功')
}

//关闭时、、、
ws.onclose = () => {
  // 显示离线信息
  ElMessage.warning('群聊连接断开')
  if (intervalId !== null) { //暂停定时器
    pauseInterval()
  }
}

//region 心跳检测
function heartBeat() {
  sendMessage('heartBeat')
}

const isStartHeartBeat = ref(false)

// 初始化定时器
let intervalId = null;

// 启动定时器
function startInterval() {
  if (!intervalId) {
    intervalId = setInterval(heartBeat, 2000); // 间隔1秒执行一次
  }
}

// 暂停定时器
function pauseInterval() {
  if (intervalId) {
    clearInterval(intervalId);
    intervalId = null;
  }
}

// 开启定时器
function resumeInterval() {
  if (!intervalId) {
    startInterval();
  }
}

const open = () => {
  ElMessageBox({
    title: '连接断开',
    message: '连接已断开，是否重连？',
    showCancelButton: true,
    confirmButtonText: '确认重连',
    cancelButtonText: '取消',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true
        instance.confirmButtonText = '重连中...'
        setTimeout(() => {
          done()
          setTimeout(() => {
            instance.confirmButtonLoading = false
          }, 300)
        }, 2000)
      } else {
        done()
      }
    },
  }).then((action) => {
    reconnectWs()
  })
}


//endregion


//获取日期时间
function currentDate() {
  const today = new Date();
  const year = today.getFullYear();
  const month = (today.getMonth() + 1).toString().padStart(2, '0');
  const day = today.getDate().toString().padStart(2, '0');
  const hours = today.getHours().toString().padStart(2, '0');
  const minutes = today.getMinutes().toString().padStart(2, '0');
  const seconds = today.getSeconds().toString().padStart(2, '0');

  // 格式化日期和时间为 "YYYY-MM-DD hh:mm:ss"
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

//region 在数据变动时，保持scroll滚动到最后
// 创建一个 ref 引用到 chatWindow
const chatWindow = ref(null);

// 数据更新后滚动到底部的方法
const scrollToBottom = () => {
  nextTick(() => {
    const element = chatWindow.value;
    if (element) {
      element.scrollTop = element.scrollHeight;
    }
  });
};

// 当数据变动时，调用 scrollToBottom 方法来滚动到底部
watch(chatHistory, () => {
  scrollToBottom();
});
//endregion


//关闭时触发
onUnmounted(() => {
  // console.log('onUnmounted')
  ws.close()

})


</script>

<style scoped>
.container {
  display: flex;
  height: 80vh;
}

.user-list {
  flex: 1;
  background-color: #f5f5f5;
  padding: 20px;
}

.user-list h2 {
  margin-bottom: 10px;
}

.user-list ul {
  list-style-type: none;
  padding: 0;
}

.user-list li {
  cursor: pointer;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 5px;
  border: white solid 2px;
}

.chat-container {
  flex: 3;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

#chat-window {
  flex: 1;
  overflow-y: scroll;
  border: #c4c1c1 solid 2px;
}

.message-left {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
}

.message-left .message-content {
  padding: 10px;
  border-radius: 12px;
  background-color: #71e171;
  color: #333;
  max-width: 70%;
}

.message-left .message-content p {
  margin: 0;
}

.message-right {
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.message-right .message-content {
  padding: 10px;
  border-radius: 12px;
  background-color: #34a3ff;
  color: #333;
  max-width: 70%;
}

.message-right .message-content p {
  margin: 0;
}

.input-container {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.input-container input {
  flex: 1;
  padding: 10px;
  border-radius: 5px;
  margin-right: 10px;
  border: gray solid 2px;
}

</style>
