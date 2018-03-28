# <img src="https://cloud.githubusercontent.com/assets/873584/26024695/4defcb5e-3809-11e7-9755-fa4d22c45718.png"> [Solo](https://github.com/b3log/solo) [![Build Status](https://img.shields.io/travis/b3log/solo.svg?style=flat)](https://travis-ci.org/b3log/solo) [![Coverage Status](https://img.shields.io/coveralls/b3log/solo.svg?style=flat)](https://coveralls.io/github/b3log/solo?branch=master)  [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](http://img.shields.io/badge/download-14K+-blue.svg?style=flat)](https://pan.baidu.com/s/1dzk7SU) 

<p align="center">
<a href="https://github.com/b3log/symphony/blob/master/README.md"><strong>English</strong></a> | <a href="https://github.com/b3log/symphony/blob/master/README_zh_CN.md"><strong>中文</strong></a>
</p>

* [Introduction](#introduction)
* [Features](#features)
* [Screenshots](#screenshots)
* [Installation](#installation)
* [Documents](#documents)
* [Stack](#stack)
* [Contributions](#contributions)
* [Terms](#terms)
* [Credits](#credits)

## Introduction

[Solo](https://github.com/b3log/solo) is a Java open-source blogging system that can be set up with **one command** and has 15+ sets of elaborate skins. In addition, Solo has a very active [community](https://hacpai.com). After the article is shared with the community, it can be seen by many people and generates rich interaction.

The first version of Solo was released in 2010 and has been very mature so far. Please feel free to use :smirk_cat:

## Features

You should use every function Solo has deposited so far. We will not add features that only "20%" users use. Only in this way can we keep the blog system pure and light enough to bring a simple user experience.

* Markdown / Emoji
* [Polymeric classification](https://github.com/b3log/solo/issues/12256) / 标签
* Custom navigation (pages, links)
* Drafts
* Comment/Reply Email Notification
* Random Articles / Related Articles / Sticky / Update Notification
* Custom article permanent link
* Custom site SEO parameters
* Custom announcement / footer
* Multiple signature columns
* Code block highlighting
* [Multi-skin, multi-end fitting](https://github.com/b3log/solo-skins/tree/master/skin-preview) / [Community skin](https://github.com/b3log/solo-third-skins/tree/master/skin-preview)
* Multilingual / International
* Upload local / Qiniu
* Friendship link management
* Multi-user writing, team blog
* [Hexo/Jekyll Import](https://hacpai.com/article/1498490209748)
* SQL / JSON / Markdown Export
* Plugins
* Atom / RSS Feed
* Sitemap
* MetaWeblog API
* CDN Static resource separation 

If there is a new version available, the upgrade process is also very simple. You just need to redeploy the new version without running any additional scripts.

## Screenshots

Edit

![5f7258675e0143c79e15ddffabf02147-article.png](https://img.hacpai.com/file/2017/8/5f7258675e0143c79e15ddffabf02147-article.png) 

Skin

![ac9a044c18ec4dd4a9356caf698d7fe8-skin.png](https://img.hacpai.com/file/2017/8/ac9a044c18ec4dd4a9356caf698d7fe8-skin.png) 

Front

* [Preview and Download](https://hacpai.com/article/1493814851007#toc_h2_11)

## Installation

After the JDK environment is ready, [download](https://pan.baidu.com/s/1dzk7SU) the latest Solo package decompression and enter the decompression directory to execute:

* Windows: `java -cp "WEB-INF/lib/*;WEB-INF/classes" org.b3log.solo.Starter`
* Unix-like: `java -cp "WEB-INF/lib/*:WEB-INF/classes" org.b3log.solo.Starter`

**See the [Solo User Guide](https://hacpai.com/article/1492881378588) for more details. In addition, if you want to use Solo but do not want to maintain the server yourself, you can purchase our built Solo and [use it directly](https://b3log.org/services/#solo).**

## Documents

* [User Guide](https://hacpai.com/article/1492881378588): Installation, configuration, backup, and frequently asked questions
* [Dev Guide](https://hacpai.com/article/1493822943172): Development environment, project structure, framework description
* [Skin Dev Guide](https://hacpai.com/article/1493814851007): Development steps, template variables
* [Plugin Dev Guide](https://docs.google.com/document/pub?id=15H7Q3EBo-44v61Xp_epiYY7vK_gPJLkQaT7T1gkE64w&pli=1): Plugin mechanism, process flow

## Stack

* 后端框架：为了尽量降低服务器的内存占用，顺带尝试[一些技术构想](https://hacpai.com/article/1403847528022)，我们开发了 [Latke](https://github.com/b3log/latke) 框架，并在此基础上构建了 [Solo](https://github.com/b3log/solo)、[Sym](https://github.com/b3log/symphony)、[小薇](https://github.com/b3log/xiaov)等产品。这些产品反过来也会对框架提出需求，这是一个相互促进，共同演化的良性发展过程
* 前端框架：Solo 的前端部分为了降低复杂度， 只依赖于 jQuery、编辑器、代码高亮等组件。管理后台的 SPA 框架、皮肤响应式 UI 都是我们自己实现的

**没有最好的轮子，只有最适合的轮子。** BTW，如果你想研究如何制造 Web 轮子，Solo 是一个不错的入口。

另外，为了保证 Solo 的质量，我们也做了很多努力，包括：

* 统一规范的编码风格
* 完善的 javadoc 注释
* 严格的分支、缺陷追踪管理
* 不断完善的测试用例、持续集成

## Contributions

### Authors

Solo 的主要作者是 [Daniel](https://github.com/88250) 与 [Vanessa](https://github.com/Vanessa219)，所有贡献者可以在[这里](https://github.com/b3log/solo/graphs/contributors)看到。

我们非常期待你加入到这个项目中，无论是使用反馈还是代码补丁，都是对 Solo 一份满满的爱 :heart:

### Discussion

* 到 Solo 官方[讨论区](https://hacpai.com/tag/Solo)发帖（推荐做法）
* 来一发 [issue](https://github.com/b3log/solo/issues/new)
* 加入 Solo 开发支持 Q 群 242561391

## Terms

* This software is open sourced under the Apache License 2.0
* You can not get rid of the "Powered by [B3log 开源](https://b3log.org)" from any page, even which you made
* If you want to use this software for commercial purpose, please mail to support@liuyun.io for a commercial license request
* Copyright &copy; b3log.org, all rights reserved

## Credits

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
<strong>专业、简约、稳定、极速的 Java 博客</strong>
<br><br>
<img src="https://cloud.githubusercontent.com/assets/873584/26024667/c031e40a-3808-11e7-9176-f2c9af01bd64.png">
</p>
