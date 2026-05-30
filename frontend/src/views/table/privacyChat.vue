<template>
  <el-card>
    <div class="container">
      <div class="user-list">
        <h2>在线用户</h2>
        <ul>
          <li v-for="(user, index) in onlineUsers" :key="user" :class="{ 'selected': selectedItem === index }"
              @click="startChat(user, index)">{{ user }}
          </li>
        </ul>
      </div>
      <div class="chat-container">
        <span>你正在与 {{ chattingUser }} 进行聊天
          <el-link style="color: #34a3ff; font-size: 10px; user-select: none" @click="loadChatHistory">点击加载历史聊天记录</el-link>
        </span>
        <div id="chat-window" ref="chatWindow">
          <div v-for="message in chatHistory" :key="message.datetime" v-if="!isChatting()"
               :class="{ 'message-left': message.keyUsername===chattingUser, 'message-right': message.keyUsername!==chattingUser}">
            <div class="message-content" v-if="chattingUser===message.sideUsername || chattingUser===message.keyUsername">
              <strong>{{ message.keyUsername }}:</strong>
              <p>{{ message.content }}</p>
            </div>
          </div>
        </div>
        <div class="input-container">
          <el-input type="textarea" :rows="3" v-model="messageInput" :disabled="isChatting()" placeholder="输入消息..."/>
          <el-button type="primary" @click="sendMessage" :disabled="isChatting()">发送</el-button>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import {ref, onMounted, onUnmounted, nextTick, watch} from 'vue';
import {ElMessage} from "element-plus";
import {useUserStoreHook} from "@/store/modules/user";
import {toJSONString, toStringJSON} from "xe-utils";
import {request} from "@/utils/service";

// 在线用户
const onlineUsers = ref([]);
//正在聊天的用户
const chattingUser = ref('');
//消息输入
const messageInput = ref('');
// 当前用户
const currentUser = useUserStoreHook().username
//标记选中的用户
const selectedItem = ref(null);

//聊天历史记录
const chatHistory = ref([]);

//建立连接
const ws = new WebSocket('ws://localhost:8080/chat/privacy');

// 连接聊天室
function startChat(user: string, index: any) {
  selectedItem.value = index;  // 用于改变样式

  ws.onopen = () => {
    ElMessage.success('已连接聊天室')
  }
  chattingUser.value = user;
  isloadChatHistory.value = false
  chatHistory.value.splice(0, chatHistory.value.length)
}

//加载历史聊天记录
const isloadChatHistory = ref(false)
const loadChatHistory = ()=> {
  if (isloadChatHistory.value){
    ElMessage.warning('已加载过')
    return
  }
  if (isChatting()){
    ElMessage.warning('没有选择用户！！！')
    return;
  }
  request({url: '/chat/' + currentUser + '/' + chattingUser.value, method: 'get'})
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

//接收服务端推送的消息后触发
ws.onmessage = (res) => {
  // 获取服务器端推送过来的消息
  let re = toStringJSON(res.data)
  if (re.system) { // 系统消息
    onlineUsers.value = re.message.filter((value) => value !== currentUser)

  } else {// 不是系统消息
    // 存储对方的聊天记录

    chatHistory.value = chatHistory.value.concat([{
      keyUsername: re.toName,
      sideUsername: currentUser,
      content: re.message,
      datetime: currentDate
    }])
  }
}

ws.onopen = ()=>{
  ElMessage.success('私聊连接成功')
}
//关闭时、、、
ws.onclose = () => {
  // 显示离线信息
  ElMessage.warning('私聊断开连接')
}

// 发送消息
function sendMessage() {
  const message = messageInput.value.trim();
  if (message !== '') {
    // 封装数据，并发送数据给后端
    const data = {toName: chattingUser.value, message: message, contentType: 0}
    // // 发送数据给服务端
    ws.send(toJSONString(data))

    // 存储历史聊天记录
    chatHistory.value = chatHistory.value.concat([{
      keyUsername: currentUser,
      sideUsername: chattingUser,
      content: message,
      datetime: currentDate
    }])
    // 以当前用户名+对方用户名作为key
    messageInput.value = '';
  }
}

function isChatting() {
  if (chattingUser.value === '') {
    return true;
  }
  return false;
}

//获取日期时间
const currentDate = () => {
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
    console.log(element)
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

//打开时，触发
onMounted(() => {
  // console.log('onMounted')


})
//关闭时触发
onUnmounted(() => {
  // console.log('onUnmounted')
  ws.close()

})


</script>

<style scoped>

.selected {
  color: #ffffff;
  background-color: #a4a4a4;
}

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
