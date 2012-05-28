<div class="marginBottom12">
    <h1 class="title">
        <a href="${servePath}" id="logoTitle" >
            ${blogTitle}
        </a>
        <span class="sub-title">${blogSubtitle}</span>
    </h1>
</div>
<div class="left">
    <#list pageNavigations as page>
    <span>
        <a href="${page.pagePermalink}" target="${page.pageOpenTarget}">${page.pageTitle}</a>&nbsp;&nbsp;
    </span>
    </#list>
    <a href="${servePath}/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
    <a rel="alternate" href="${servePath}/blog-articles-feed.do">${atomLabel}</a><a href="${servePath}/blog-articles-feed.do"><img src="${staticServePath}/images/feed.png" alt="Atom"/></a>
</div>
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
    </span>
</div>
<div class="clear"></div>