# ILikeAcFun - AcFunDown
![语言java](https://img.shields.io/badge/Require-java-green.svg)
![支持系统 Win/Linux/Mac](https://img.shields.io/badge/Platform-%20win%20|%20linux%20|%20mac-lightgrey.svg)
![测试版本64位Win10系统, jre 1.8.0_101](https://img.shields.io/badge/TestPass-Win10%20x64__java__1.8.0__101-green.svg)
![开源协议GPL3.0](https://img.shields.io/badge/license-gpl--3.0-green.svg)  
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

## :smile:免责声明    
+ 本项目为基于浏览器行为的个性化定制工具，其功能是**为A站用户提供其可接触权限内的内容的离线保存**，涉及到的多媒体内容版权归其所有者所有。  
+ 用户对多媒体资源的剪辑、再发布等任何行为，均应确保获得所有者授权。  
+ 作者对使用此工具或基于此工具的二次开发所产生的任何行为概不负责。  

## :smile:第三方库使用声明  
* 使用[JSON.org](https://github.com/stleary/JSON-java)库做简单的Json解析[![](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/stleary/JSON-java/blob/master/LICENSE)
* 使用[zxing](https://github.com/zxing/zxing)库生成链接二维码图片[![](https://img.shields.io/badge/license-Apache%202-green.svg)](https://raw.githubusercontent.com/zxing/zxing/master/LICENSE)  
* 使用[ffmpeg](http://www.ffmpeg.org)进行转码(ts片段转换为mp4)[![](https://img.shields.io/badge/license-LGPL%20(%3E%3D%202.1)%2FGPL%20(%3E%3D%202)-green.svg)](http://www.ffmpeg.org/legal.html)  

## :smile:Win32/Linux/Mac用户请看过来
+ 自带的```ffmpeg.exe```为WIN 64位，32位系统或其它平台请自行[官网](http://www.ffmpeg.org/download.html)下载，替换源程序；  
+ 对于非WIN用户，请直接使用命令行调用该程序  
```javaw -Dfile.encoding=utf-8 -jar ILikeAcFun.jar```
+ 对于非WIN用户，使用程序的一键更新功能后，请人工`update/ILikeAcFun.update.jar`替换掉`ILikeAcFun.jar`

## :smile:其它  
* **下载地址**: [https://github.com/nICEnnnnnnnLee/AcFunDown/releases](https://github.com/nICEnnnnnnnLee/AcFunDown/releases)
* **GitHub**: [https://github.com/nICEnnnnnnnLee/AcFunDown](https://github.com/nICEnnnnnnnLee/AcFunDown)  
* [**更新日志**](https://github.com/nICEnnnnnnnLee/AcFunDown/blob/master/UPDATE.md)

<details>
<summary>LICENSE</summary>


[第三方LICENSE](https://github.com/nICEnnnnnnnLee/AcFunDown/tree/master/release/LICENSE/third-party)
GPL 3.0
</details>
