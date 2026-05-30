import * as XLSX from "xlsx";

export default function excelExport(tableData, sheet, fileName){
  const data = XLSX.utils.json_to_sheet(tableData)//此处tableData.value为表格的数据
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, data, sheet)//test-data为自定义的sheet表名
  XLSX.writeFile(wb, fileName + '.xlsx')//test.xlsx为自定义的文件名
}
