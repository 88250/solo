## v4.4.0 / 2022-01-22

### 改进皮肤

* [9IPHP 皮肤优化](https://github.com/88250/solo/issues/177)

### 改进功能

* [关闭看板娘后关闭数据请求](https://github.com/88250/solo/issues/157)
* [升级编辑器](https://github.com/88250/solo/issues/191)
* [友链图标统一使用 `linkIcon` 字段](https://github.com/88250/solo/issues/203)

### 开发重构

* [Tip Msg 样式优化](https://github.com/88250/solo/issues/153)
* [使用 TLS v1.2](https://github.com/88250/solo/issues/230)
* [升级到 JDK 11](https://github.com/88250/solo/issues/231)

### 修复缺陷

* [后台评论数/访问数偶尔为 0](https://github.com/88250/solo/issues/183)
* [Solo 文章推送到社区报错](https://github.com/88250/solo/issues/199)
* [升级 H2Database](https://github.com/88250/solo/issues/233)

## v4.3.1 / 2020-09-09

### 引入特性

* [集成 Gitalk 评论系统](https://github.com/88250/solo/issues/167)

### 改进功能

* [分类下文章计数统计展现改进](https://github.com/88250/solo/issues/169)
* [社区端域名变更](https://github.com/88250/solo/issues/180)

### 修复缺陷

* [H2Database 升级 v4.3.0 问题](https://github.com/88250/solo/issues/170)
* [SV 模式粘贴时自动拉取图片问题](https://github.com/88250/solo/issues/171)
* [metro-hot 文章中字的颜色看不清](https://github.com/88250/solo/issues/174)
* [编辑器在预览状态下发布文章会丢失代码块](https://github.com/88250/solo/issues/182)

## v4.3.0 / 2020-07-17

### 引入特性

* [复制粘贴时如果包含图片自动替换为社区图床](https://github.com/88250/solo/issues/114)

### 改进功能

* [优化随机文章算法](https://github.com/88250/solo/issues/156)
* [开发模式下不走页面静态缓存](https://github.com/88250/solo/issues/159)
* [彻底移除本地评论系统](https://github.com/88250/solo/issues/161)
* [重写编辑器分屏预览模式](https://github.com/88250/solo/issues/163)

### 开发重构

* [清理后台无用的配置项](https://github.com/88250/solo/issues/160)
* [删除 v2.9.9~v3.6.2 升级脚本](https://github.com/88250/solo/issues/164)
* [移除默认的社区导航](https://github.com/88250/solo/issues/165)

## v4.2.0 / 2020-06-28

### 引入特性

* [同步 GitHub solo-blog 仓库功能](https://github.com/88250/solo/issues/125)
* [支持 Unix domain socket](https://github.com/88250/solo/issues/127)
* [新增 Markdown zip 导入方式](https://github.com/88250/solo/issues/128)

### 改进功能

* [推送社区需弹框确认](https://github.com/88250/solo/issues/120)
* [支持 flac 音频格式解析](https://github.com/88250/solo/issues/129)
* [增加取消选中文本朗读功能开关](https://github.com/88250/solo/issues/141)
* [Markdown 中文排版段首缩进配置开关](https://github.com/88250/solo/issues/146)
* [自动生成的文章链接重复问题优化](https://github.com/88250/solo/issues/147)
* [伺服 /favicon.ico](https://github.com/88250/solo/issues/154)
* [静态博客页脚的相关阅读、随机阅读不显示文章列表](https://github.com/88250/solo/issues/155)

### 开发重构

* [Repository 查询结果 rslts 使用 List 类型](https://github.com/88250/solo/issues/133)
* [社区交互接口返回码重构](https://github.com/88250/solo/issues/150)
* [sc 调整为 code](https://github.com/88250/solo/issues/151)

### 修复缺陷

* [大纲 bug](https://github.com/88250/solo/issues/130)
* [导航管理自定义链接中的空格替换为短横线](https://github.com/88250/solo/issues/132)
* [添加导航和文章链接重复](https://github.com/88250/solo/issues/135)
* [PJAX + 页面缓存导致样式丢失的问题](https://github.com/88250/solo/issues/137)
* [修复登录验证安全漏洞](https://github.com/88250/solo/issues/138)

## v4.1.0 / 2020-05-19

### 引入特性

* [支持类似 Typora 的即时渲染模式（保留 Markdown 标记符）](https://github.com/88250/solo/issues/81)
* [后台增加服务端日志浏览](https://github.com/88250/solo/issues/91)
* [页面静态化](https://github.com/88250/solo/issues/107)

### 改进皮肤

* [皮肤页脚去掉版权信息](https://github.com/88250/solo/issues/111)

### 改进功能

* [社区图床更换新域名](https://github.com/88250/solo/issues/21)
* [使用 jsDelivr 时自动加入版本号](https://github.com/88250/solo/issues/83)
* [不显示发布文章计数为零的标签](https://github.com/88250/solo/issues/88)
* [废弃评论推送社区接口](https://github.com/88250/solo/issues/89)
* [看板娘背景优化](https://github.com/88250/solo/issues/93)
* [支持配置编辑器模式](https://github.com/88250/solo/issues/95)
* [调整前台动态皮肤预览逻辑](https://github.com/88250/solo/issues/116)

### 文档相关

* [Add README in English](https://github.com/88250/solo/issues/104)

### 开发重构

* [Docker 镜像加入 Git 提交哈希值环境变量](https://github.com/88250/solo/issues/82)
* [更换开源协议为 木兰宽松许可证, 第2版](https://github.com/88250/solo/issues/99)
* [彻底移除浏览数统计相关代码](https://github.com/88250/solo/issues/110)

### 修复缺陷

* [生成静态站点的分类分页 404](https://github.com/88250/solo/issues/86)
* [采用 cdn.jsdelivr.net 加速后访问文章详情页面 js 报错](https://github.com/88250/solo/issues/100)
* [图床上传相同图片后第二幅显示不出来](https://github.com/88250/solo/issues/101)
* [更新文章编辑器加载报错](https://github.com/88250/solo/issues/106)
* [后台 footer 遮挡 菜单栏](https://github.com/88250/solo/issues/108)
* [casper 文章页面开始使用被分享遮挡](https://github.com/88250/solo/issues/109)
* [导入 Markdown 文件存档时间问题](https://github.com/88250/solo/issues/112)
* [nijigen 皮肤修改](https://github.com/88250/solo/issues/115)
* [自定义模板变量解析规则问题](https://github.com/88250/solo/issues/123)

## v4.0.0 / 2020-03-24

### 引入特性

* [优化社区集成](https://github.com/88250/solo/issues/20)
* [Markdown 支持改进](https://github.com/88250/solo/issues/54)

### 改进功能

* [允许在 localhost 上推送文章到社区](https://github.com/88250/solo/issues/39)
* [优化“Latke 配置错误”问题](https://github.com/88250/solo/issues/58)
* [允许 base64 图片](https://github.com/88250/solo/issues/59)
* [图片 alt 属性优化](https://github.com/88250/solo/issues/64)
* [编辑器常用表情使用社区端的设置](https://github.com/88250/solo/issues/67)
* [分类 URI 必须指定为非中文路径](https://github.com/88250/solo/issues/68)
* [看板娘妹子被分割了](https://github.com/88250/solo/issues/75)
* [通过 jsDelivr 实现静态资源 CDN 加速](https://github.com/88250/solo/issues/77)
* [命令行参数去掉单横线短格式支持](https://github.com/88250/solo/issues/78)

### 开发重构

* [重构请求路由](https://github.com/88250/solo/issues/61)

### 修复缺陷

* [生成静态站点链接问题](https://github.com/88250/solo/issues/70)
* [引用非社区图床图片不应该加图片处理样式](https://github.com/88250/solo/issues/71)
* [密码文章输入正确密码后也不能访问](https://github.com/88250/solo/issues/79)

## v3.9.0 / 2020-02-21

### 引入特性

* [HTML 静态站点生成](https://github.com/88250/solo/issues/19)

### 改进皮肤

* [0 评论应该显示为浏览数](https://github.com/88250/solo/issues/46)
* [Pinghsu 皮肤 footer.ftl 存在错误 script 标签](https://github.com/88250/solo/issues/50)
* [next 皮肤目录样式优化](https://github.com/88250/solo/issues/55)

### 改进功能

* [支持代码块行号显示](https://github.com/88250/solo/issues/4)

### 开发重构

* [日志组件迁移到 log4j2](https://github.com/88250/solo/issues/44)
* [支持 ES6 Module](https://github.com/88250/solo/issues/47)

### 修复缺陷

* [评论插件点击之后自动关闭](https://github.com/88250/solo/issues/48)
* [解决目录插件偶尔重复加载的问题](https://github.com/88250/solo/issues/53)

## v3.8.0 / 2020-01-16

### 引入特性

* [评论社区组件化](https://github.com/88250/solo/issues/18)

### 改进皮肤

* [皮肤 Casper 标签页标题显示异常](https://github.com/88250/solo/issues/43)

### 改进功能

* [移除计数相关数据填充和功能](https://github.com/88250/solo/issues/45)

### 修复缺陷

* [Jar 包运行时皮肤目录名问题](https://github.com/88250/solo/issues/41)
* [自定义表前缀后存档 404](https://github.com/88250/solo/issues/42)

## v3.7.0 / 2020-01-13

### 引入特性

* [浏览计数插件化](https://github.com/88250/solo/issues/17)
* [加入社区 IP 黑名单防护](https://github.com/88250/solo/issues/36)

### 改进功能

* [看板娘资源移至社区](https://github.com/88250/solo/issues/34)
* [密码文章后台不允许其他用户查看](https://github.com/88250/solo/issues/35)
* [社区备份全部文章](https://github.com/88250/solo/issues/37)

### 修复缺陷

* [pjax 到文章页面，访问数不会 +1](https://github.com/88250/solo/issues/38)
* [在未开启“允许通过链接访问草稿”时访问草稿报错](https://github.com/88250/solo/issues/40)

## v3.6.8 / 2020-01-03

### 引入特性

* [编辑器 Vditor 所见即所得](https://github.com/88250/solo/issues/22)

### 改进皮肤

* [浏览器回退上一页，标签页标题未更新或显示异常](https://github.com/88250/solo/issues/27)

### 改进功能

* [更新项目地址相关链接](https://github.com/88250/solo/issues/3)
* [使用社区登录](https://github.com/88250/solo/issues/12)
* [备份功能从 GitHub 迁移到社区](https://github.com/88250/solo/issues/13)
* [浏览器调整窗口位置后，目录位置错位](https://github.com/88250/solo/issues/24)
* [关闭评论功能后不显示评论相关模块](https://github.com/88250/solo/issues/29)
* [移动端所见即所得编辑两侧留白过多问题](https://github.com/88250/solo/issues/31)

### 修复缺陷

* [删除文章页面问题](https://github.com/88250/solo/issues/10)