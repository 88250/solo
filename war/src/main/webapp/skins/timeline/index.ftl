<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="nav-abs">
            <#list archiveDates as archiveDate>
            <span data-year="${archiveDate.archiveDateYear}">
                <#if "en" == localeString?substring(0, 2)>
                <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                   title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})</a>
                <#else>
                <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                   title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})</a>
                </#if>
            </span>
            </#list>
        </div>
        <div class="wrapper">
            <div class="articles container">
                <div class="vertical"></div>
                <#list ["17:02", "17:01"] as date>
                <div class="fn-clear">
                    <h2>
                        <span class="article-archive">${date}</span>
                    </h2>
                    <#list articles as article>
                    <#if article.articleCreateDate?string("HH:mm") == date>
                    <article>
                        <div class="module">
                            <div class="dot"></div>
                            <div class="arrow"></div>
                            <time class="article-time">
                                <span>
                                    ${article.articleCreateDate?string("yy-MM-dd HH:mm")}
                                </span>
                            </time>
                            <h3 class="article-title">
                                <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                                    ${article.articleTitle}
                                </a>
                                <#if article.hasUpdated>
                                <sup>
                                    ${updatedLabel}
                                </sup>
                                </#if>
                                <#if article.articlePutTop>
                                <sup>
                                    ${topArticleLabel}
                                </sup>
                                </#if>
                            </h3>
                            <p>
                                ${article.articleAbstract}
                            </p>
                            <span class="ico-tags ico" title="${tagLabel}">
                                <#list article.articleTags?split(",") as articleTag><a rel="category tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></#list>
                            </span>
                            <span class="ico-author ico" title="${authorLabel}">
                                <a rel="author" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
                            </span>
                            <span class="ico-comment ico" title="${commentLabel}">
                                <a rel="nofollow" href="${servePath}${article.articlePermalink}#comments">
                                    <#if article.articleCommentCount == 0>
                                    ${noCommentLabel}
                                    <#else>
                                    ${article.articleCommentCount}
                                    </#if>
                                </a>
                            </span>
                            <span class="ico-view ico" title="${viewLabel}">
                                <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                                    ${article.articleViewCount}
                                </a>
                            </span>
                        </div>
                    </article>
                    <#if paginationCurrentPageNum != paginationPageCount && 0 != paginationPageCount && !article_has_next>
                    <div class="article-more" onclick="timeline.getNextPage(this, '${date}')" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
                    </#if>
                    </#if>
                    </#list>    
                </div>
                </#list>
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>
