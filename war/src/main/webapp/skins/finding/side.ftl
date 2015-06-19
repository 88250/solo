<div class="fn-clear">
    <span class="fn-right">
        <#if isLoggedIn>
        <a href="${servePath}/admin-index.do#main" title="${adminLabel}" class="icon-setting"></a>
        &nbsp; 
        <a title="${logoutLabel}" class="icon-logout" href="${logoutURL}"></a>
        <#else>
        <a title="${loginLabel}" href="${loginURL}" class="icon-login"></a>
        &nbsp; 
        <a href="${servePath}/register" title="${registerLabel}" class="icon-register"></a>
        </#if>
    </span>
</div>
<ul>
    <#list pageNavigations as page>
    <li>
        <a href="${page.pagePermalink}" target="${page.pageOpenTarget}">${page.pageTitle}</a>
    </li>
    </#list>
    <li>
        <a href="${servePath}/dynamic.html">${dynamicLabel}</a>
    </li>
    <li>
        <a href="${servePath}/tags.html">${allTagsLabel}</a>
    </li>
    <li>
        <a href="${servePath}/archives.html">${archiveLabel}</a>
    </li>
    <li>
        <a href="${servePath}/links.html">${linkLabel}</a>
    </li>
    <li>
        <a rel="alternate" href="${servePath}/blog-articles-rss.do">${subscribeLabel}</a>
    </li>
</ul>

<div class="count">
    <span>
        ${viewCount1Label}
        ${statistic.statisticBlogViewCount}
    </span> &nbsp; &nbsp;
    <span>
        ${articleCount1Label}
        ${statistic.statisticPublishedBlogArticleCount}
    </span><br/>
    <span>
        ${commentCount1Label}
        ${statistic.statisticPublishedBlogCommentCount}
    </span> &nbsp; &nbsp;
    <span>
        ${onlineVisitor1Label}
        ${onlineVisitorCnt}
    </span>
</div>
