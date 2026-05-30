<template>
  <div>
    <!-- 此处注意写法v-model:content -->
    <QuillEditor ref="myQuillEditor"
                 theme="snow"
                 v-model:content="content"
                 :options="data.editorOption"
                 contentType="html"
                 @update:content="setValue()"
    />
    <!-- 使用自定义图片上传 -->
    <input type="file" hidden accept=".jpg,.png,.jpeg" ref="fileBtn" @change="handleUpload"/>
  </div>
</template>

<script setup>
import {QuillEditor} from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {reactive, onMounted, ref, toRaw, watch, defineExpose, toRefs} from 'vue'
import axios from "axios";
import {ElMessage} from "element-plus";
import {getToken} from "@/utils/cache/cookies";

const props = defineProps(['content'])  // 接收父组件传过来的值
console.log(props.content)

const content = ref('')
const myQuillEditor = ref()
// 通过watch监听回显，笔者这边使用v-model:content 不能正常回显
watch(() => props.content, (val) => {
  toRaw(myQuillEditor.value).setHTML(val)
}, { deep: true })

const fileBtn = ref()
const data = reactive({
  content: '',
  editorOption: {
    modules: {
      toolbar: [
        ['bold', 'italic', 'underline', 'strike'],
        [{'size': ['small', false, 'large', 'huge']}],
        [{'font': []}],
        [{'align': []}],
        [{'list': 'ordered'}, {'list': 'bullet'}],
        [{'indent': '-1'}, {'indent': '+1'}],
        [{'header': 1}, {'header': 2}],
        ['image'],
        [{'direction': 'rtl'}],
        [{'color': []}, {'background': []}]
      ]
    },
    placeholder: '请输入内容...'
  }
})
// 初始化编辑器
onMounted(() => {
  const quill = toRaw(myQuillEditor.value).getQuill()
  if (myQuillEditor.value) {
    quill.getModule('toolbar').addHandler('image', imgHandler)
  }
})

//#region 处理图片
const imgHandler = (state) => {
  if (state) {
    fileBtn.value.click()
  }
}
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
  //console.log(formData.get('file'))

  axios.post('/api/v1/table-chat/notice/img', formData, {
    headers: {
      "AUTH-TOKEN": getToken(),
    }
  })
    .then((response) => {
      // 文件上传成功后的处理
      //console.log(response)
      // 这个 Code 是和后端约定的业务 Code
      const code = response.data.code
      // 如果没有 Code, 代表这不是项目后端开发的 API
      if (code === undefined) {
        ElMessage.error("非本系统的接口")
      } else {
        switch (code) {
          case 200:
            // code === 200 代表没有错误, 可以放行
            ElMessage.success(response.data.message)
            break
          default:
            // 不是正确的 Code 也就是用户不在服务范围内
            ElMessage.error(response.data.message || "Error")
        }
      }
      if (response.data.data) {
        const quill = toRaw(myQuillEditor.value).getQuill()
        const length = quill.getSelection().index
        quill.insertEmbed(length, 'image', response.data.data)
        quill.setSelection(length + 1)
      }

    })
    .catch((error) => {
      // 文件上传失败后的处理
      //console.log(error)
      ElMessage.error("文件上传失败，请稍后重试")
    });

  // uploadFile(formData, 'table/notice/img')
  //   .then(res => {
  //     console.log(res)

  // })
}
//endregion

// 抛出更改内容，此处避免出错直接使用文档提供的getHTML方法
const setValue = () => {
  const text = toRaw(myQuillEditor.value).getHTML()
}
// 获取编辑器内容
const acData = () => {
  return myQuillEditor.value.content
}
// 暴露方法，父组件能够调用
defineExpose({
  acData
})
</script>
<style scoped lang="scss">
// 调整样式
:deep(.ql-editor) {
  min-height: 180px;
  max-height: 180px;
}

:deep(.ql-formats) {
  height: 21px;
  line-height: 21px;
}

:deep(.ql-container) {
  background-color: white;
  font-size: 15px;
  border: #a3fffc solid 2px;
  margin-bottom: 10px;
  margin-left: 5px;
  margin-right: 5px;
}

:deep(.ql-toolbar) {
  background-color: white;
  margin-top: 10px;
  margin-left: 5px;
  margin-right: 5px;
}
</style>

