# <img src="https://cloud.githubusercontent.com/assets/873584/26024695/4defcb5e-3809-11e7-9755-fa4d22c45718.png"> [Solo](https://github.com/b3log/solo) [![Build Status](https://img.shields.io/travis/b3log/solo.svg?style=flat)](https://travis-ci.org/b3log/solo) [![Coverage Status](https://img.shields.io/coveralls/b3log/solo.svg?style=flat)](https://coveralls.io/github/b3log/solo?branch=master)  [![AGPLv3](http://img.shields.io/badge/license-AGPLv3-orange.svg?style=flat)](https://www.gnu.org/licenses/agpl-3.0.txt) [![Download](http://img.shields.io/badge/download-14K+-blue.svg?style=flat)](https://pan.baidu.com/s/1dzk7SU) 

<p align="center">
<a href="https://github.com/b3log/solo/blob/master/README.md"><strong>English</strong></a> | <a href="https://github.com/b3log/solo/blob/master/README_zh_CN.md"><strong>中文</strong></a>
</p>

* [简介](#简介)
* [特性](#特性)
* [界面](#界面)
* [安装](#安装)
* [文档](#文档)
* [技术](#技术)
* [社区](#社区)
* [开源协议](#开源协议)
* [鸣谢](#鸣谢)

## 简介

[Solo](https://github.com/b3log/solo) 是一款**一个命令**就能搭建好的 Java 开源博客系统，并内置了 15+ 套精心制作的皮肤。除此之外，Solo 还有着非常活跃的[社区](https://hacpai.com)，文章分享到社区后可以让很多人看到，产生丰富的交流互动。

Solo 的第一个版本发布于 2010 年，至今已经非常成熟稳定，请放心使用 :smirk_cat:

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

如果有新版可用，升级过程也是非常简单的，只需要重新部署新版本就可以，不用运行额外的任何脚本。

## 界面

编辑文章

![5f7258675e0143c79e15ddffabf02147-article.png](https://img.hacpai.com/file/2017/8/5f7258675e0143c79e15ddffabf02147-article.png) 

选择皮肤

![ac9a044c18ec4dd4a9356caf698d7fe8-skin.png](https://img.hacpai.com/file/2017/8/ac9a044c18ec4dd4a9356caf698d7fe8-skin.png) 

前台界面

* [预览及下载](https://hacpai.com/article/1493814851007#toc_h2_11)

## 安装

JDK 环境准备好之后[下载](https://pan.baidu.com/s/1dzk7SU)最新的 Solo 包解压，进入解压目录执行：

* Windows: `java -cp "WEB-INF/lib/*;WEB-INF/classes" org.b3log.solo.Starter`
* Unix-like: `java -cp "WEB-INF/lib/*:WEB-INF/classes" org.b3log.solo.Starter`

用 `Docker` 运行？

`docker volume create solo_datas && docker run --privileged --name solo --restart=unless-stopped -p 8080:8080 -v solo_datas:/opt/b3log/backup/ -d 88250/solo`

**更多细节请参考 [Solo 用户指南](https://hacpai.com/article/1492881378588)。另外，如果你想用 Solo 但又不想自己维护服务器，可以购买我们搭建好的 Solo 直接[使用](https://b3log.org/services/#solo)。**

## 文档

* [用户指南](https://hacpai.com/article/1492881378588)：安装、配置、备份以及常见问题
* [开发指南](https://hacpai.com/article/1493822943172)：开发环境、项目结构、框架说明
* [皮肤开发](https://hacpai.com/article/1493814851007)：开发步骤、模版变量
* [插件开发](https://docs.google.com/document/pub?id=15H7Q3EBo-44v61Xp_epiYY7vK_gPJLkQaT7T1gkE64w&pli=1)：插件机制、处理流程

## 技术

* 后端框架：为了尽量降低服务器的内存占用，顺带尝试[一些技术构想](https://hacpai.com/article/1403847528022)，我们开发了 [Latke](https://github.com/b3log/latke) 框架，并在此基础上构建了 [Solo](https://github.com/b3log/solo)、[Sym](https://github.com/b3log/symphony)、[小薇](https://github.com/b3log/xiaov)等产品。这些产品反过来也会对框架提出需求，这是一个相互促进，共同演化的良性发展过程
* 前端框架：Solo 的前端部分为了降低复杂度， 只依赖于 jQuery、编辑器、代码高亮等组件。管理后台的 SPA 框架、皮肤响应式 UI 都是我们自己实现的

**没有最好的轮子，只有最适合的轮子。** BTW，如果你想研究如何制造 Web 轮子，Solo 是一个不错的入口。

另外，为了保证 Solo 的质量，我们也做了很多努力，包括：

* 统一规范的编码风格
* 完善的 javadoc 注释
* 严格的分支、缺陷追踪管理
* 不断完善的测试用例、持续集成

## 社区

大家可以通过以下方式进行项目讨论，帮助项目发展：

* Solo [讨论区](https://hacpai.com/tag/Solo)
* [报告问题](https://github.com/b3log/solo/issues/new)

我们非常期待你加入到项目中，无论是使用反馈还是代码补丁，都是对 Solo 一份满满的爱 :heart:

## 开源协议

Solo 使用 [GNU Affero General Public License, Version 3](https://www.gnu.org/licenses/agpl-3.0.txt) 作为开源协议。

如有疑问，请电邮 d@b3log.org。

## 鸣谢

Solo 的诞生离不开以下开源项目：

* [jQuery](https://github.com/jquery/jquery)：使用最广泛的 JavaScript 工具库
* [CodeMirror](https://github.com/codemirror/CodeMirror)：Markdown 编辑器内核
* [SyntaxHighlighter](https://github.com/syntaxhighlighter/syntaxhighlighter)：一个代码高亮库
* [Highlight.js](https://github.com/isagalaev/highlight.js)：又一个代码高亮库
* [emojify.js](https://github.com/Ranks/emojify.js)：前端 Emoji 处理库
* [jsoup](https://github.com/jhy/jsoup)：Java HTML 解析器
* [flexmark](https://github.com/vsch/flexmark-java)：Java Markdown 处理库
* [marked](https://github.com/chjj/marked)：NodeJS Markdown 处理库
* [Apache Commons](http://commons.apache.org)：Java 工具库集
* [emoji-java](https://github.com/vdurmont/emoji-java)：Java Emoji 处理库
* [FreeMarker](http://freemarker.org)：好用的 Java 模版引擎
* [H2](https://github.com/h2database/h2database)：Java SQL 数据库
* [Jetty](https://github.com/eclipse/jetty.project)：轻量级的 Java Web 容器
* [Latke](https://github.com/b3log/latke)：简洁高效的 Java Web 框架 
* [IntelliJ IDEA](https://www.jetbrains.com/idea)：全宇宙暂时排名第二的 IDE

----

<p align = "center">
<strong>漂亮、简约、稳定、极速的 Java 博客系统</strong>
<br><br>
<img src="https://cloud.githubusercontent.com/assets/873584/26024667/c031e40a-3808-11e7-9176-f2c9af01bd64.png">
</p>

