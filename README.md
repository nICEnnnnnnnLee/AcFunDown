# ILikeAcFun - AcFunDown
![语言java](https://img.shields.io/badge/Require-java-green.svg)
![支持系统 Win/Linux/Mac](https://img.shields.io/badge/Platform-%20win%20|%20linux%20|%20mac-lightgrey.svg)
![测试版本64位Win10系统, jre 1.8.0_101](https://img.shields.io/badge/TestPass-Win10%20x64__java__1.8.0__101-green.svg)
![开源协议Apache2.0](https://img.shields.io/badge/license-apache--2.0-green.svg)  
![当前版本](https://img.shields.io/github/release/nICEnnnnnnnLee/AcFunDown.svg?style=flat-square)
![Release 下载总量](https://img.shields.io/github/downloads/nICEnnnnnnnLee/AcFunDown/total.svg?style=flat-square)

AcFun 视频下载器，用于下载A站视频。  
===============================
由[B站视频下载器](https://github.com/nICEnnnnnnnLee/BilibiliDown)改编而来  
**以下多图警告**

## :smile:特性  
+ 支持UI界面(自认为是傻瓜式操作)  
+ 支持扫码登录(能看=能下，反过来也一样)  
+ 支持各种链接解析(直接输入各种网页链接或 ac号等)
+ 支持多p下载! 
+ 支持收藏夹下载!!  
+ 支持UP主视频下载!!!  
+ 支持断点续传下载!!!!!(因异常原因退出后, 只要下载目录不变, 直接在上次基础上继续下载)


## :smile:第三方库使用声明  
* 使用[JSON.org](https://github.com/stleary/JSON-java)库做简单的Json解析[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/stleary/JSON-java/blob/master/LICENSE)
* 使用[zxing](https://github.com/zxing/zxing)库生成链接二维码图片[![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://raw.githubusercontent.com/zxing/zxing/master/LICENSE)  
* 使用[ffmpeg](http://www.ffmpeg.org)进行转码(短片段flv未使用ffmpeg，仅多flv合并及m4s转换mp4格式需要用到)[![](https://img.shields.io/badge/license-LGPL%20(%3E%3D%202.1)%2FGPL%20(%3E%3D%202)-green.svg)](http://www.ffmpeg.org/legal.html)  

## :smile:媒体素材使用声明             
* [主页背景图](https://github.com/nICEnnnnnnnLee/AcFunDown/blob/master/src/resources/background.jpg?raw=true)取自[画师桂七 - 【第三波ac娘壁纸温暖向】](http://mobile.app.acfun.cn/a/ac3369030)  

## :smile:Win32/Linux/Mac用户请看过来
+ 自带的```ffmpeg.exe```为WIN 64位，32位系统或其它平台请自行[官网](http://www.ffmpeg.org/download.html)下载，替换源程序；  
+ 对于非WIN用户，请直接使用命令行调用该程序  
```javaw -Dfile.encoding=utf-8 -jar ILikeAcFun.jar```
+ 对于非WIN用户，使用程序的一键更新功能后，请人工`update/ILikeAcFun.update.jar`替换掉`ILikeAcFun.jar`

## :smile:其它  
* **下载地址**: [https://github.com/nICEnnnnnnnLee/AcFunDown/releases](https://github.com/nICEnnnnnnnLee/AcFunDown/releases)
* **GitHub**: [https://github.com/nICEnnnnnnnLee/AcFunDown](https://github.com/nICEnnnnnnnLee/AcFunDown)  
* **Gitee码云**: [https://gitee.com/NiceLeee/AcFunDown](https://gitee.com/NiceLeee/AcFunDown)  
* [**更新日志**](https://github.com/nICEnnnnnnnLee/AcFunDown/blob/master/UPDATE.md)

<details>
<summary>LICENSE</summary>


[第三方LICENSE](https://github.com/nICEnnnnnnnLee/AcFunDown/tree/master/release/LICENSE/third-party)
```
Copyright (C) 2019 NiceLee. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
</details>
