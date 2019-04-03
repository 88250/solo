<p align = "center">
<img alt="Solo" src="https://user-images.githubusercontent.com/873584/52320401-2593e600-2a0a-11e9-9ba1-db79ee71d1af.png">
<br><br>
小而美的博客系统，为未来而构建
<br><br>
<a title="Build Status" target="_blank" href="https://travis-ci.org/b3log/solo"><img src="https://img.shields.io/travis/b3log/solo.svg?style=flat-square"></a>
<a title="Coverage Status" target="_blank" href="https://coveralls.io/github/b3log/solo"><img src="https://img.shields.io/coveralls/github/b3log/solo.svg?style=flat-square"></a>
<a title="Code Size" target="_blank" href="https://github.com/b3log/solo"><img src="https://img.shields.io/github/languages/code-size/b3log/solo.svg?style=flat-square&color=9cf"></a>
<a title="AGPLv3" target="_blank" href="https://www.gnu.org/licenses/agpl-3.0.txt"><img src="http://img.shields.io/badge/license-AGPLv3-orange.svg?style=flat-square"></a>
<a title="Releases" target="_blank" href="https://github.com/b3log/solo/releases"><img src="https://img.shields.io/github/release/b3log/solo.svg?style=flat-square"></a>
<a title="Downloads" target="_blank" href="https://github.com/b3log/solo/releases"><img src="https://img.shields.io/github/downloads/b3log/solo/total.svg?style=flat-square"></a>
<a title="Docker Pulls" target="_blank" href="https://hub.docker.com/r/b3log/solo"><img src="https://img.shields.io/docker/pulls/b3log/solo.svg?style=flat-square&color=blueviolet"></a>
<a title="Hits" target="_blank" href="http://hits.dwyl.io/b3log/solo"><img src="http://hits.dwyl.io/b3log/solo.svg"></a>
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
* [EchoCow](https://echocow.cn)

## 功能

* Markdown / Emoji
* [聚合分类](https://github.com/b3log/solo/issues/12256) / 标签
* 自定义导航页面 / 链接
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
* [GitHub 仓库展示](https://github.com/b3log/solo/issues/12514) / [自动备份文章到仓库](https://github.com/b3log/solo/issues/12676)
* [内置 HTTPS+CDN 文件存储](https://github.com/b3log/solo/issues/12556)

## 界面

### 开始使用

![start](https://user-images.githubusercontent.com/873584/54970301-9001e500-4fbc-11e9-83de-bf3841adecff.png)

### 后台首页

![console](https://user-images.githubusercontent.com/873584/54970489-4796f700-4fbd-11e9-837b-93394ff80304.png)

### 编辑文章

![post](https://user-images.githubusercontent.com/873584/54970405-ea9b4100-4fbc-11e9-9e05-5f5e4b94d2b8.png)

### 选择皮肤

![skin](https://user-images.githubusercontent.com/873584/54970463-22a28400-4fbd-11e9-953e-6922a12f5f11.png)

### 前台界面

![index](https://user-images.githubusercontent.com/873584/54970236-5a5cfc00-4fbc-11e9-8d04-d7a517f78839.png)

## 安装

### 本地试用

[下载](https://github.com/b3log/solo/releases)最新的 Solo 包解压，进入解压目录执行：

* Windows: `java -cp "WEB-INF/lib/*;WEB-INF/classes" org.b3log.solo.Starter`
* Unix-like: `java -cp "WEB-INF/lib/*:WEB-INF/classes" org.b3log.solo.Starter`

如果你有 Java 开发环境，可参考[这里](https://hacpai.com/article/1493822943172)通过源码构建运行。

**请注意**：我们不建议通过 war 发布包或者源码构建部署，因为这样的部署方式在将来有新版本发布时升级会比较麻烦。
这两种方式请仅用于本地试用，线上生产环境建议通过 Docker 部署。

### Docker 部署

获取最新镜像：

```shell
docker pull b3log/solo
```

* 使用 MySQL

  先手动建库（库名 `solo`，字符集使用 `utf8mb4`，排序规则 `utf8mb4_general_ci`），然后启动容器：
  
  ```shell
  docker run --detach --name solo --network=host \
      --env RUNTIME_DB="MYSQL" \
      --env JDBC_USERNAME="root" \
      --env JDBC_PASSWORD="123456" \
      --env JDBC_DRIVER="com.mysql.cj.jdbc.Driver" \
      --env JDBC_URL="jdbc:mysql://127.0.0.1:3306/solo?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC" \
      b3log/solo --listen_port=8080 --server_scheme=http --server_host=localhost 
  ```
  为了简单，使用了主机网络模式来连接主机上的 MySQL。
  
* 使用 H2 Database

  ```shell
  docker run --detach --name solo --volume ~/solo_h2/:/opt/solo/h2/ --publish 8080:8080 \
      --env RUNTIME_DB="H2" \
      --env JDBC_USERNAME="root" \
      --env JDBC_PASSWORD="123456" \
      --env JDBC_DRIVER="org.h2.Driver" \
      --env JDBC_URL="jdbc:h2:/opt/solo/h2/db;MODE=MYSQL" \
      b3log/solo --listen_port=8080 --server_scheme=http --server_host=localhost 
  ```

启动参数说明：

* `--listen_port`：进程监听端口
* `--server_scheme`：最终访问协议，如果反代服务启用了 HTTPS 这里也需要改为 `https`
* `--server_host`：最终访问域名或公网 IP，不要带端口号

完整启动参数的说明可以使用 `-h` 来查看。

### Docker 升级

1. 拉取最新镜像
2. 重启容器

可参考[这里](https://github.com/b3log/solo/blob/master/scripts/docker-restart.sh)编写一个重启脚本，并通过 crontab 每日凌晨运行来实现自动更新。

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
