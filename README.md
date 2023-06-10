# YA自动记账

[![GitHub license](https://img.shields.io/github/license/duzhaokun123/YAQianjiAuto?style=flat-square)](https://github.com/duzhaokun123/YAQianjiAuto/blob/main/LICENSE)
![Android SDK min 33](https://img.shields.io/badge/Android%20SDK-%3E%3D%2033-brightgreen?style=flat-square&logo=android)
![Android SDK target 33](https://img.shields.io/badge/Android%20SDK-target%2033-brightgreen?style=flat-square&logo=android)
![Xposed Module](https://img.shields.io/badge/Xposed-Module-blue?style=flat-square)
![Jetpack Compose](https://img.shields.io/badge/Jetpack-Compose-blue?style=flat-square&logo=jetpackcompose)

实现类似[自动记账](https://github.com/Auto-Accounting/Qianji_auto)的功能 只支持 Xposed 且应用支持有限

### 自用优先 没有的功能就是我目前用不上

## 特色

### 相比自动记账的优势

- 使用 js 解析应用数据而不是**扭曲的正则匹配好好的 json 被移除`"`和`\`成的__** (但凡它复杂解析人能写出来这个项目也不会产生)
- 解析配置做文件导入导出
- 账单详情显示解析路径
- md3 动态取色

### 不足
- 只对支付宝应用数据支持好
- 不支持读取钱迹数据
- 不支持通知
- 不支持短信
- 界面随意 (真的很随意 不会常看到界面就是能显示信息就行)

## Thanks

### 工具

[jadx](https://github.com/skylot/jadx)

### 库

[AOSP](https://source.android.com/)

[EzXHelper](https://github.com/KyuubiRan/EzXHelper)

[Jetpack Compose](https://developer.android.com/jetpack/compose)

[XmlToJson](https://github.com/smart-fun/XmlToJson)

[compose-code-editor](https://github.com/Qawaz/compose-code-editor)

[gson](https://github.com/google/gson)

[js-evaluator-for-android](https://github.com/evgenyneu/js-evaluator-for-android)

[snakeyaml-engine](https://bitbucket.org/snakeyaml/snakeyaml-engine)

[xposed](https://forum.xda-developers.com/xposed)