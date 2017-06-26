# <img src="https://cloud.githubusercontent.com/assets/873584/26024695/4defcb5e-3809-11e7-9755-fa4d22c45718.png"> [Solo](https://github.com/b3log/solo) [![Build Status](https://img.shields.io/travis/b3log/solo.svg?style=flat)](https://travis-ci.org/b3log/solo) [![Coverage Status](https://img.shields.io/coveralls/b3log/solo.svg?style=flat)](https://coveralls.io/github/b3log/solo?branch=master)  [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](http://img.shields.io/badge/download-11K+-blue.svg?style=flat)](http://pan.baidu.com/share/link?shareid=541735&uk=3255126224) 

* [简介](#%E7%AE%80%E4%BB%8B)
* [功能](#%E5%8A%9F%E8%83%BD)
* [安装](#%E5%AE%89%E8%A3%85)
* [文档](#%E6%96%87%E6%A1%A3)
* [技术](#%E6%8A%80%E6%9C%AF)
* [Terms](#terms)
* [鸣谢](#%E9%B8%A3%E8%B0%A2)

[![Activities](https://graphs.waffle.io/b3log/solo/throughput.svg)](https://waffle.io/b3log/solo/metrics/throughput)

## 简介

[Solo](https://github.com/b3log/solo) 是一款**一个命令**就能搭建好的 Java 开源博客系统，并内置了 15+ 套精心制作的皮肤。除此之外，Solo 还有着非常活跃的[社区](https://hacpai.com/b3log)，文章分享到社区后可以让很多人看到，产生丰富的交流互动。

## 功能 

Solo 沉淀至今的**每一个功能你应该都会用到**。我们不会将只有“20%”用户使用的功能添加进来，只有这样才能保持博客系统本该有的纯净，足够轻量才能带来简约的使用体验。

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
* 代码高亮
* [多皮肤，多端适配](https://github.com/b3log/solo-skins/tree/master/skin-preview) / [社区皮肤](https://github.com/b3log/solo-third-skins/tree/master/skin-preview)
* 多语言 / 国际化
* 上传七牛云
* 友情链接管理
* 多用户写作，团队博客
* SQL 文件导出
* [Hexo/Jekyll 导入](https://hacpai.com/article/1498490209748)
* 插件系统
* Atom / RSS 订阅
* Sitemap
* MetaWeblog API
* CDN 静态资源分离 

如果有新版可用，升级过程也是非常简单的，只需要重新部署新版本就可以，不用运行额外的任何脚本。

## 安装

JDK 环境准备好之后[下载](http://pan.baidu.com/share/link?shareid=541735&uk=3255126224)最新的 Solo 包解压，进入解压目录执行：

* Windows: `java -cp WEB-INF/lib/*;WEB-INF/classes org.b3log.solo.Starter`
* Unix-like: `java -cp WEB-INF/lib/*:WEB-INF/classes org.b3log.solo.Starter`

**更多细节请参考 [Solo 用户指南](https://hacpai.com/article/1492881378588)。**

另外，如果你想用 Solo 但又不想自己维护服务器，那可以尝试购买我们搭建好的 Solo 直接[使用](http://b3log.org/services/#solo)。

## 文档

* [用户指南](https://hacpai.com/article/1492881378588)：安装、配置、备份以及常见问题
* [开发指南](https://hacpai.com/article/1493822943172)：开发环境、项目结构、框架说明
* [皮肤开发](https://hacpai.com/article/1493814851007)：开发步骤、模版变量
* [插件开发](https://docs.google.com/document/pub?id=15H7Q3EBo-44v61Xp_epiYY7vK_gPJLkQaT7T1gkE64w&pli=1)：插件机制、处理流程

## 技术

* 后端框架：为了尽量降低服务器的内存占用，顺带尝试[一些技术构想](https://hacpai.com/article/1403847528022)，我们开发了 [Latke](https://github.com/b3log/latke) 框架，并在此基础上构建了 Solo、Sym、XiaoV 等产品。这些产品反过来也会对框架提出需求，这是一个相互促进，共同演化的良性发展过程
* 前端框架：Solo 的前端部分为了降低复杂度， 只依赖于 jQuery、编辑器、代码高亮等组件。管理后台的 SPA 框架、皮肤响应式 UI 都是我们自己实现的

**没有最好的轮子，只有最适合的轮子。** BTW，如果你想研究如何制造 Web 轮子，Solo 是一个不错的入口。

另外，为了保证 Solo 的质量，我们也做了很多努力，包括：

* 统一规范的编码风格
* 完善的 javadoc 注释
* 严格的分支、缺陷追踪管理
* 不断完善的测试用例、持续集成

## Terms

* This software is open sourced under the Apache License 2.0
* You can not get rid of the "Powered by [B3log 开源](http://b3log.org)" from any page, even which you made
* If you want to use this software for commercial purpose, please mail to support@liuyun.io for a commercial license request
* Copyright &copy; b3log.org, all rights reserved

## 鸣谢

Solo 的诞生离不开以下开源项目：

* [jQuery](https://github.com/jquery/jquery)：使用最广泛的 JavaScript 工具库
* [CodeMirror](https://github.com/codemirror/CodeMirror)：Markdown 编辑器内核
* [SyntaxHighlighter](https://github.com/syntaxhighlighter/syntaxhighlighter)：一个代码高亮库
* [Highlight.js](https://github.com/isagalaev/highlight.js)：又一个代码高亮库
* [emojify.js](https://github.com/Ranks/emojify.js)：前端 Emoji 处理库
* [jsoup](https://github.com/jhy/jsoup)：Java HTML 解析器
* [flexmark](https://github.com/vsch/flexmark-java)：Java Markdown 处理库
* [Apache Commons](http://commons.apache.org)：Java 工具库集
* [emoji-java](https://github.com/vdurmont/emoji-java)：Java Emoji 处理库
* [FreeMarker](http://freemarker.org)：好用的 Java 模版引擎
* [H2](https://github.com/h2database/h2database)：Java SQL 数据库
* [Jetty](https://github.com/eclipse/jetty.project)：轻量级的 Java Web 容器
* [Latke](https://github.com/b3log/latke)：简洁高效的 Java Web 框架 
* [IntelliJ IDEA](https://www.jetbrains.com/idea)：全宇宙暂时排名第二的 IDE

----

<p align = "center">
<strong>专业、简约、稳定、极速的 Java 博客</strong>
<br><br>
<img src="https://cloud.githubusercontent.com/assets/873584/26024667/c031e40a-3808-11e7-9176-f2c9af01bd64.png">
</p>