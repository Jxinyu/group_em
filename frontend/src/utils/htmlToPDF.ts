import html2pdf from "html2pdf.js";
import {ElMessage} from "element-plus";

export default async function generatePDF(html, filename) {
  if (!html) {
    ElMessage.error('HTML element not found.');
    return;
  }

  try {
    const opt = {
      margin: [0, 0, 0, 0], // 设置页面边距
      filename: filename + '.pdf', // 设置PDF文件名
      image: {type: 'jpeg', quality: 0.98}, // 设置图像质量
      html2canvas: {scale: 2}, // 设置html2canvas选项
      jsPDF: {unit: 'mm', format: 'a4', orientation: 'portrait'} // 设置jsPDF选项
    };

    await html2pdf().from(html).set(opt).save(); // 生成PDF并下载
  } catch (error) {
    console.error('Error generating PDF:', error);
  }
}
