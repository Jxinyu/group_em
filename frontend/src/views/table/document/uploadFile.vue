<template>
  <div class="app-container">
    <el-form ref="form" :model="form" label-width="100px">
      <el-form-item label="选择文件">
        <el-upload
          class="upload-demo"
          :action="uploadUrl"
          :multiple="true"
          :before-upload="handleBeforeUpload"
          :on-success="handleSuccess"
          :on-remove="handleRemove"
          :drag="true"
        >
          <el-icon class="el-icon--upload"><upload-filled/></el-icon>
          <div class="el-upload__text">
            文件拖拽到此 或者 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              xlsx/doc/pdf/txt/docx/sql/pptx/rar/zip/xls/jpg/gif/png 支持最大5MB
            </div>
          </template>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="uploadFiles">确认上传</el-button>
      </el-form-item>
      <div v-for="(file, index) in fileList" :key="index">
        <el-form-item :label="`标题 ${index + 1}`" required>
          <el-input v-model="file.title"></el-input>
        </el-form-item>
        <el-form-item :label="`文件描述 ${index + 1}`">
          <el-input v-model="file.remark"></el-input>
        </el-form-item>
      </div>
    </el-form>

    <el-table v-if="fileList.length > 0" :data="fileList" style="margin-top: 20px;">
      <el-table-column label="文件名" prop="file.name"></el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="removeFile(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import axios from 'axios';
import {getToken} from "@/utils/cache/cookies";
import {ElMessage} from "element-plus";

const fileList = ref([]);  // 存储文件列表
const uploadUrl = '/api/v1/user/upload/docu'; // 替换为实际的上传接口地址
const form = ref({
  title: '',
  remark: ''
});

const handleBeforeUpload = (file) => {
  const fileData = {
    file,
    title: '',
    remark: ''
  };
  fileList.value.push(fileData);
  return false; // 阻止自动上传
};

/**
 * 文件列表移除文件时的钩子
 * @param file
 * @param fileList
 */
const handleRemove = (file, fileList) => {

};

/**
 * 	文件上传成功时的钩子
 * @param file
 * @param fileList
 */
const handleSuccess = (file, fileList) => {

}


/**
 * 上传文件
 */
const uploadFiles = () => {
  fileList.value.forEach((file) => {
    const formData = new FormData();
    formData.append('file', file.file);
    formData.append('title', file.title);
    formData.append('remark', file.remark);

    axios.post(uploadUrl, formData, {
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
              // code === 200 代表没有错误, 可以放行
              ElMessage.success(response.data.message)
              break
            default:
              // 不是正确的 Code 也就是用户不在服务范围内
              ElMessage.error(response.data.message || "Error")
          }
        }

      })
      .catch((error) => {
        // 文件上传失败后的处理
        console.log(error)
        ElMessage.error("文件上传失败，请稍后重试")
      });
  });

  fileList.value = []; // 清空文件列表
  form.value.title = ''; // 清空标题
  form.value.remark = ''; // 清空文件描述
};

/**
 * 删除已经添加的文件
 * @param file
 */
const removeFile = (file) => {
  const index = fileList.value.indexOf(file);
  if (index !== -1) {
    fileList.value.splice(index, 1);
  }
};


</script>
