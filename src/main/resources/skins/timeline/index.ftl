<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

-->
<#include "../../common-template/macro-common_head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
            <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <#include "header.ftl">
        <ul class="nav-abs index-nav-abs">
            <#list archiveDates as archiveDate>
            <li data-year="${archiveDate.archiveDateYear}"  title="${archiveDate.archiveDatePublishedArticleCount}"
                onclick="timeline.getArchive('${archiveDate.archiveDateYear}', '${archiveDate.archiveDateMonth}'<#if "en" == localeString?substring(0, 2)>, '${archiveDate.monthName}'</#if>)">
                <#if "en" == localeString?substring(0, 2)>
                ${archiveDate.monthName}
                <#else>
                ${archiveDate.archiveDateMonth}
                </#if>
        </li>
        </#list>
    </ul>
    <div class="wrapper">
        <div class="articles container" style="margin-top: 0">
            <div class="vertical"></div>
            <#list archiveDates as archiveDate>
            <div class="fn-clear" id="${archiveDate.archiveDateYear}${archiveDate.archiveDateMonth}" data-count="${archiveDate.archiveDatePublishedArticleCount}">
                <h2>
                    <span class="article-archive">
                        <#if "en" == localeString?substring(0, 2)>
                        ${archiveDate.monthName} ${archiveDate.archiveDateYear}
                        <#else>
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                        </#if>
                    </span>
                </h2>
                <#list articles1 as article>
                <#if article.articleCreateDate?string("yyyy/MM") == "${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                <article>
                    <div class="module">
                        <div class="dot"></div>
                        <div class="arrow"></div>
                        <time class="article-time">
                            <span>
                                ${article.articleUpdateDate?string("yy-MM-dd HH:mm")}
                            </span>
                        </time>
                        <h3 class="article-title">
                            <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                                ${article.articleTitle}
                            </a>
                            <#if article.articlePutTop>
                            <sup>
                                ${topArticleLabel}
                            </sup>
                            </#if>
                            <#if article.hasUpdated>
                                <sup>
                                    <a href="${servePath}${article.articlePermalink}">
                                    ${updatedLabel}
                                    </a>
                                </sup>
                            </#if>
                        </h3>
                        <div class="vditor-reset">
                            ${article.articleAbstract}
                        </div>
                        <span class="ico-tags ico" title="${tagLabel}">
                            <#list article.articleTags?split(",") as articleTag><a rel="category tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a><#if articleTag_has_next>,</#if></#list>
                        </span>
                        <span class="ico-author ico" title="${authorLabel}">
                            <a rel="author" href="${servePath}/authors/${article.authorId}">${article.authorName}</a>
                        </span>
                        <span class="ico-comment ico" title="${commentLabel}">
                            <a rel="nofollow" data-uvstatcmt="${article.oId} href="${servePath}${article.articlePermalink}#b3logsolocomments">0</a>
                        </span>
                        <span class="ico-view ico" title="${viewLabel}">
                            <a rel="nofollow" href="${servePath}${article.articlePermalink}">
                                <span data-uvstaturl="${servePath}${article.articlePermalink}">0</span>
                            </a>
                        </span>
                    </div>
                </article>
                <#if paginationCurrentPageNum != paginationPageCount && 0 != paginationPageCount && !article_has_next>
                <div class="article-more" onclick="timeline.getNextPage(this, '${article.articleUpdateDate?string("yyyy/MM")}')" data-page="${paginationCurrentPageNum}">${moreLabel}</div>
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
