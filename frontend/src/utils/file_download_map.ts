// globalStore.ts

import { reactive } from 'vue';

interface KeyValueMap {
  [key: string]: any;
}

/**
 * 全局变量  存储用户下载时的键值对，用于判断请求是否放行
 */
const globalDownloadFile: KeyValueMap = reactive({
  map: new Map<string, any>(),
  addItem(key: string, value: any) {
    this.map.set(key, value);
  },
  removeItem(key: string) {
    this.map.delete(key);
  },
  getItem(key: string) {
    return this.map.get(key);
  },
  getAllItems() {
    return this.map;
  },
});

export default globalDownloadFile;
