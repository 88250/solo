<ul>
    <#list pageNavigations as page>
    <li>
        <a href="${page.pagePermalink}" target="${page.pageOpenTarget}">${page.pageTitle}</a>
    </li>
    </#list>
    <li>
        <a href="${servePath}/dynamic.html">${dynamicLabel}</a>&nbsp;&nbsp;
    </li>
    <li>
        <a href="${servePath}/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
    </li>
    <li>
        <a rel="alternate" href="${servePath}/blog-articles-feed.do">${atomLabel}</a><a href="${servePath}/blog-articles-feed.do"></a>
    </li>
</ul>

<div class="right" id="statistic">
    <span>${viewCount1Label}
        <span class='error-msg'>
            ${statistic.statisticBlogViewCount}
        </span>
        &nbsp;&nbsp;
    </span>
    <span>
        ${articleCount1Label}
        <span class='error-msg'>
            ${statistic.statisticPublishedBlogArticleCount}
        </span>
        &nbsp;&nbsp;
    </span>
    <span>
        ${commentCount1Label}
        <span class='error-msg'>
            ${statistic.statisticPublishedBlogCommentCount}
        </span>
        当前在线人数
    </span>
</div>
<div id="sideNavi" class="side-navi">
    <#if "" != noticeBoard>
    ${noticeBoard}
    </#if>
</div>
注册/登录
管理/退出