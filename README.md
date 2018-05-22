# <img src="https://cloud.githubusercontent.com/assets/873584/26024695/4defcb5e-3809-11e7-9755-fa4d22c45718.png"> [Solo](https://github.com/b3log/solo) [![Build Status](https://img.shields.io/travis/b3log/solo.svg?style=flat)](https://travis-ci.org/b3log/solo) [![Coverage Status](https://img.shields.io/coveralls/b3log/solo.svg?style=flat)](https://coveralls.io/github/b3log/solo?branch=master)  [![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](http://img.shields.io/badge/download-14K+-blue.svg?style=flat)](https://pan.baidu.com/s/1dzk7SU) 

<p align="center">
<a href="https://github.com/b3log/solo/blob/master/README.md"><strong>English</strong></a> | <a href="https://github.com/b3log/solo/blob/master/README_zh_CN.md"><strong>中文</strong></a>
</p>

* [Introduction](#introduction)
* [Features](#features)
* [Screenshots](#screenshots)
* [Installation](#installation)
* [Documents](#documents)
* [Stack](#stack)
* [Community](#community)
* [Terms](#terms)
* [Credits](#credits)

## Introduction

[Solo](https://github.com/b3log/solo) is a Java open-source blogging system that can be set up with **one command** and has 15+ sets of elaborate skins. In addition, Solo has a very active [community](https://hacpai.com). After the article is shared with the community, it can be seen by many people and generates rich interaction.

The first version of Solo was released in 2010 and has been very mature so far. Please feel free to use :smirk_cat:

## Features

You should use every function Solo has deposited so far. We will not add features that only "20%" users use. Only in this way can we keep the blog system pure and light enough to bring a simple user experience.

* Markdown / Emoji
* [Polymeric classification](https://github.com/b3log/solo/issues/12256) / Tags
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

Run with docker?

`docker volume create solo_datas && docker run --privileged --name solo --restart=unless-stopped -p 8080:8080 -v solo_datas:/opt/b3log/backup/ -d 88250/solo`

**See the [Solo User Guide](https://hacpai.com/article/1492881378588) for more details. In addition, if you want to use Solo but do not want to maintain the server yourself, you can purchase our built Solo and [use it directly](https://b3log.org/services/#solo).**

## Documents

* [User Guide](https://hacpai.com/article/1492881378588): Installation, configuration, backup, and frequently asked questions
* [Dev Guide](https://hacpai.com/article/1493822943172): Development environment, project structure, framework description
* [Skin Dev Guide](https://hacpai.com/article/1493814851007): Development steps, template variables
* [Plugin Dev Guide](https://docs.google.com/document/pub?id=15H7Q3EBo-44v61Xp_epiYY7vK_gPJLkQaT7T1gkE64w&pli=1): Plugin mechanism, process flow

## Stack

* Backend framework: In order to reduce the memory footprint of the server as much as possible and try some technical [ideas](https://hacpai.com/article/1403847528022) along the way, we have developed the [Latke](https://github.com/b3log/latke) framework and built on this basis [Solo](https://github.com/b3log/solo), [Sym](https://github.com/b3log/symphony), [XiaoV](https://github.com/b3log/xiaov) and other products. These products will in turn put demands on the framework. This is a mutually beneficial and co-evolutionary benign development process
* Frontend framework: In order to reduce the complexity, Solo's front-end part only depends on components such as jQuery, editor, and code highlighting. The SPA framework and skin responsive UI of the management background are all realized by us.

**There is no best wheel, only the most suitable wheel.** BTW, if you want to study how to make web wheels, Solo is a good entry.

In addition, in order to ensure the quality of Solo, we have also made a lot of efforts, including:

* Uniform code style
* Complete javadoc comments
* Strict branch and defect tracking management
* Continuously improved test cases, continuous integration

## Contributions

### Authors

Solo's main authors are [Daniel](https://github.com/88250) and [Vanessa](https://github.com/Vanessa219). All contributors can be seen [here](https://github.com/b3log/solo/graphs/contributors).

We are very much looking forward to your joining the project. Whether it is using feedback or code patches, it is a complete love for Solo :heart:

### Community

* Post to Solo's official [forum](https://hacpai.com/tag/Solo)
* New [issues](https://github.com/b3log/solo/issues/new)

## Terms

* This software is open sourced under the Apache License 2.0
* You can not get rid of the "Powered by [B3log 开源](https://b3log.org)" from any page, even which you made
* If you want to use this software for commercial purpose, please mail to d@b3log.org for a commercial license request
* Copyright &copy; b3log.org, all rights reserved

## Credits

The birth of Solo was inseparable from the following open source projects:

* [jQuery](https://github.com/jquery/jquery): The most widely used JavaScript tool library
* [CodeMirror](https://github.com/codemirror/CodeMirror): Markdown editor kernel
* [SyntaxHighlighter](https://github.com/syntaxhighlighter/syntaxhighlighter): A code highlighting library
* [Highlight.js](https://github.com/isagalaev/highlight.js): Another code highlighted library
* [emojify.js](https://github.com/Ranks/emojify.js): Frontend emoji processing library
* [jsoup](https://github.com/jhy/jsoup): Java HTML parser
* [flexmark](https://github.com/vsch/flexmark-java): Java markdown processor
* [marked](https://github.com/chjj/marked): NodeJS markdown processor
* [Apache Commons](http://commons.apache.org): Java tool library
* [emoji-java](https://github.com/vdurmont/emoji-java): Java emoji library
* [FreeMarker](http://freemarker.org): Java template engine
* [H2](https://github.com/h2database/h2database): Java SQL database
* [Jetty](https://github.com/eclipse/jetty.project): Lightweight Java Web container
* [Latke](https://github.com/b3log/latke): Simple and efficient Java Web framework
* [IntelliJ IDEA](https://www.jetbrains.com/idea): wonderful IDE

----

<p align = "center">
<strong>Beautiful, simple, stable, fast Java blog</strong>
<br><br>
<img src="https://cloud.githubusercontent.com/assets/873584/26024667/c031e40a-3808-11e7-9176-f2c9af01bd64.png">
</p>
