<p align = "center">
<img alt="Solo" src="https://user-images.githubusercontent.com/873584/52320401-2593e600-2a0a-11e9-9ba1-db79ee71d1af.png">
<br><br>
小而美的博客系统，为未来而构建
<br><br>
<a title="Build Status" target="_blank" href="https://travis-ci.org/b3log/solo"><img src="https://img.shields.io/travis/b3log/solo.svg?style=flat-square"></a>
<a title="Coverage Status" target="_blank" href="https://coveralls.io/github/b3log/solo"><img src="https://img.shields.io/coveralls/github/b3log/solo.svg?style=flat-square"></a>
<a title="Code Size" target="_blank" href="https://github.com/b3log/solo"><img src="https://img.shields.io/github/languages/code-size/b3log/solo.svg?style=flat-square"></a>
<a title="AGPLv3" target="_blank" href="https://www.gnu.org/licenses/agpl-3.0.txt"><img src="http://img.shields.io/badge/license-AGPLv3-orange.svg?style=flat-square"></a>
<a title="Releases" target="_blank" href="https://github.com/b3log/solo/releases"><img src="https://img.shields.io/github/release/b3log/solo.svg?style=flat-square"></a>
<a title="Downloads" target="_blank" href="https://github.com/b3log/solo/releases"><img src="https://img.shields.io/github/downloads/b3log/solo/total.svg?style=flat-square"></a>
</p>

## 简介

[Solo](https://github.com/b3log/solo) 是一款小而美的开源博客系统，专为程序员设计。Solo 有着非常活跃的[社区](https://hacpai.com)，文章自动推送到社区后可以让很多人看到，产生丰富的交流互动。

## 案例

* [D 的个人博客](https://88250.b3log.org)
* [Jiahao.Zhang's Blog](https://blog.hduzplus.xyz)
* [子兮子兮](https://zixizixi.cn)
* [铅笔的个人博客](https://pencilso.cn)
* [洗澡狂魔的技术阵地](https://blog.washmoretech.com)
* [liumapp 的个人博客](http://www.liumapp.com)
* [水星的随笔](https://note.abeffect.com)
* [1992 社区](https://1992.cool)
* [何遇](http://littleq.cn)
* [Relyn](http://relyn.cn)
* [思干豆](http://sigandou.com)
* [DevHyxo](https://blog.devhyxo.top)

## 功能 

* Markdown / Emoji
* [聚合分类](https://github.com/b3log/solo/issues/12256) / 标签
* 自定义导航页面 / 链接
* 评论 / 回复邮件提醒
* 随机文章 / 相关文章 / 置顶 / 更新提醒
* 自定义文章永久链接 / 签名档
* 配置站点 SEO 参数 / 公告 / 页脚
* 代码高亮 / 数学公式 / 流程图
* [多皮肤，多端适配](https://github.com/b3log/solo-skins/tree/master/skin-preview) / [社区皮肤](https://github.com/b3log/solo-third-skins/tree/master/skin-preview)
* 多语言 / 国际化
* 友情链接管理
* 多用户写作，团队博客
* [Hexo / Jekyll / Markdown 导入](https://hacpai.com/article/1498490209748)
* SQL / JSON / Markdown 导出
* Atom / RSS / Sitemap
* CDN 静态资源分离
* [GitHub 集成](https://github.com/b3log/solo/issues/12514)
* [内置 HTTPS+CDN 文件存储](https://github.com/b3log/solo/issues/12556)

## 界面

### 初始化

![init](https://user-images.githubusercontent.com/873584/52908896-800a2d80-32b9-11e9-9702-43bab360651d.png)

### 后台首页

![console-index](https://user-images.githubusercontent.com/873584/52255442-85788700-294d-11e9-8c8e-38bdcba6736c.png)

### 编辑文章

![article](https://user-images.githubusercontent.com/873584/52255441-85788700-294d-11e9-8fb4-f72e353a76de.png)

### 选择皮肤

![skin](https://user-images.githubusercontent.com/873584/52255444-85788700-294d-11e9-9c21-8758bad2c3b4.png)

### 前台界面

![index](https://user-images.githubusercontent.com/873584/52255333-19961e80-294d-11e9-85c4-92bc508864a4.png)

## 安装

[下载](https://github.com/b3log/solo/releases)最新的 Solo 包解压，进入解压目录执行：

* Windows: `java -cp "WEB-INF/lib/*;WEB-INF/classes" org.b3log.solo.Starter`
* Unix-like: `java -cp "WEB-INF/lib/*:WEB-INF/classes" org.b3log.solo.Starter`

用 `Docker` 运行？

`docker volume create solo_datas && docker run --privileged --name solo --restart=unless-stopped -p 8080:8080 -v solo_datas:/opt/b3log/backup/ -d 88250/solo`

## 文档

* [《提问的智慧》精读注解版](https://hacpai.com/article/1536377163156)
* [用户指南](https://hacpai.com/article/1492881378588)
* [Solo 从设计到实现](https://hacpai.com/article/1537690756242)
* [贡献指南](https://github.com/b3log/solo/blob/master/CONTRIBUTING.md)
* [皮肤开发](https://hacpai.com/article/1493814851007)
* [插件开发](https://docs.google.com/document/pub?id=15H7Q3EBo-44v61Xp_epiYY7vK_gPJLkQaT7T1gkE64w&pli=1)

## 社区

* [讨论区](https://hacpai.com/tag/solo)
* [报告问题](https://github.com/b3log/solo/issues/new/choose)

## 授权

Solo 使用 [GNU Affero General Public License, Version 3](https://www.gnu.org/licenses/agpl-3.0.txt) 开源协议。

## 鸣谢

* [jQuery](https://github.com/jquery/jquery)：前端 JavaScript 工具库
* [Vditor](https://github.com/b3log/vditor)： 浏览器端的 Markdown 编辑器
* [Highlight.js](https://github.com/isagalaev/highlight.js)：前端代码高亮库
* [pjax](https://github.com/defunkt/jquery-pjax)：pushState + ajax = pjax
* [jsoup](https://github.com/jhy/jsoup)：Java HTML 解析器
* [flexmark](https://github.com/vsch/flexmark-java)：Java Markdown 处理库
* [Apache Commons](http://commons.apache.org)：Java 工具库集
* [Latke](https://github.com/b3log/latke)：以 JSON 为主的 Java Web 框架
