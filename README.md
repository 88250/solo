# <img src="https://cloud.githubusercontent.com/assets/873584/26024695/4defcb5e-3809-11e7-9755-fa4d22c45718.png"> [Solo](https://github.com/b3log/solo) [![Build Status](https://img.shields.io/travis/b3log/solo.svg?style=flat)](https://travis-ci.org/b3log/solo) [![Coverage Status](https://img.shields.io/coveralls/b3log/solo.svg?style=flat)](https://coveralls.io/github/b3log/solo?branch=master)  [![AGPLv3](http://img.shields.io/badge/license-AGPLv3-orange.svg?style=flat)](https://www.gnu.org/licenses/agpl-3.0.txt) [![Download](http://img.shields.io/badge/download-14K+-blue.svg?style=flat)](https://pan.baidu.com/s/1dzk7SU) 

## 简介

[Solo](https://github.com/b3log/solo) 是**一个命令**就能搭建好的 Java 开源博客系统，并内置了 18+ 套精心制作的皮肤。除此之外，Solo 还有着非常活跃的[社区](https://hacpai.com)，文章分享到社区后可以让很多人看到，产生丰富的交流互动。

## 案例

* [D 的个人博客](https://88250.b3log.org)
* [Jiahao.Zhang's Blog](https://blog.hduzplus.xyz)
* [子兮子兮](https://zixizixi.cn)
* [铅笔的个人博客](https://pencilso.cn)
* [洗澡狂魔的技术阵地](https://blog.washmoretech.com)
* [刘美胜奇](http://www.liumapp.com)
* [水星的随笔](https://note.abeffect.com)
* [1992 社区](https://1992.cool)
* [何遇](http://littleq.cn)
* [Relyn](http://relyn.cn)
* [幸运草](https://www.vseu.com)

## 功能 

* Markdown / Emoji
* [聚合分类](https://github.com/b3log/solo/issues/12256) / 标签
* 自定义导航（页面、链接）
* 草稿夹
* 评论/回复邮件提醒
* 随机文章 / 相关文章 / 置顶 / 更新提醒
* 自定义文章永久链接
* 自定义站点 SEO 参数
* 自定义公告 / 页脚
* 多个签名档
* 代码高亮 / 数学公式 / 流程图
* [多皮肤，多端适配](https://github.com/b3log/solo-skins/tree/master/skin-preview) / [社区皮肤](https://github.com/b3log/solo-third-skins/tree/master/skin-preview)
* 多语言 / 国际化
* 上传本地 / 七牛云
* 友情链接管理
* 多用户写作，团队博客
* [Hexo/Jekyll 导入](https://hacpai.com/article/1498490209748)
* SQL / JSON / Markdown 导出
* 插件系统
* Atom / RSS 订阅
* Sitemap
* MetaWeblog API
* CDN 静态资源分离 

## 界面

### 编辑文章

![5f7258675e0143c79e15ddffabf02147-article.png](https://img.hacpai.com/file/2017/8/5f7258675e0143c79e15ddffabf02147-article.png) 

### 选择皮肤

![ac9a044c18ec4dd4a9356caf698d7fe8-skin.png](https://img.hacpai.com/file/2017/8/ac9a044c18ec4dd4a9356caf698d7fe8-skin.png) 

### 前台界面

* [预览及下载](https://hacpai.com/article/1493814851007#toc_h2_12)

## 安装

[下载](https://pan.baidu.com/s/1dzk7SU)最新的 Solo 包解压，进入解压目录执行：

* Windows: `java -cp "WEB-INF/lib/*;WEB-INF/classes" org.b3log.solo.Starter`
* Unix-like: `java -cp "WEB-INF/lib/*:WEB-INF/classes" org.b3log.solo.Starter`

用 `Docker` 运行？

`docker volume create solo_datas && docker run --privileged --name solo --restart=unless-stopped -p 8080:8080 -v solo_datas:/opt/b3log/backup/ -d 88250/solo`

**如果你不想自己维护服务器，可以购买我们运维的 Solo 直接使用，细节请联系 QQ845765。**

## 文档

* [《提问的智慧》精读注解版](https://hacpai.com/article/1536377163156)
* [用户指南](https://hacpai.com/article/1492881378588)
* [开发指南](https://hacpai.com/article/1493822943172)
* [皮肤开发](https://hacpai.com/article/1493814851007)
* [插件开发](https://docs.google.com/document/pub?id=15H7Q3EBo-44v61Xp_epiYY7vK_gPJLkQaT7T1gkE64w&pli=1)

## 社区

* [讨论区](https://hacpai.com/tag/solo)
* [报告问题](https://github.com/b3log/solo/issues/new/choose)

## 授权

Solo 使用 [GNU Affero General Public License, Version 3](https://www.gnu.org/licenses/agpl-3.0.txt) 作为开源协议，请务必遵循该开源协议相关约定。

## 鸣谢

* [jQuery](https://github.com/jquery/jquery)：前端 JavaScript 工具库
* [CodeMirror](https://github.com/codemirror/CodeMirror)：前端 Markdown 编辑器内核
* [Highlight.js](https://github.com/isagalaev/highlight.js)：前端代码高亮库
* [jsoup](https://github.com/jhy/jsoup)：Java HTML 解析器
* [flexmark](https://github.com/vsch/flexmark-java)：Java Markdown 处理库
* [Apache Commons](http://commons.apache.org)：Java 工具库集
* [Latke](https://github.com/b3log/latke)：简洁高效的 Java Web 框架 

----

<p align = "center">
<strong>小而美博客系统</strong>
<br><br>
<img src="https://cloud.githubusercontent.com/assets/873584/26024667/c031e40a-3808-11e7-9176-f2c9af01bd64.png">
</p>

