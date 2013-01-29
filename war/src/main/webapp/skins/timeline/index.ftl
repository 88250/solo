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
        <ul class="nav-abs" style="padding: 0;">
            <#list archiveDates as archiveDate>
            <li data-year="${archiveDate.archiveDateYear}"
                onclick="timeline.getArchive('${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}')">
                <#if "en" == localeString?substring(0, 2)>
                ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})
                <#else>
                ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})
                </#if>
            </li>
            </#list>
        </ul>
        <div class="wrapper">
            <div class="articles container">
                <div class="vertical"></div>
                <#list archiveDates as archiveDate>
                <div class="fn-clear" id="${archiveDate.archiveDateYear}${archiveDate.archiveDateMonth}">
                    <h2>
                        <span class="article-archive">
                            <#if "en" == localeString?substring(0, 2)>
                            ${archiveDate.monthName} ${archiveDate.archiveDateYear}
                            <#else>   
                            ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                            </#if>
                        </span>
                    </h2>
                    <#list articles as article>
                    <#if article.articleCreateDate?string("yyyy/MM") == "${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
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
                    <div class="article-more" onclick="timeline.getNextPage(this, '${article.articleCreateDate?string("yyyy/MM")}')" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
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
