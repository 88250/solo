<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${archiveLabel}"/>
        <meta name="description" content="${metaDescription},${archiveLabel}"/>
        </@head>
    </head>
    <body>
        <div class="container one-column page-home">
            <div class="headband"></div>
            <#include "header.ftl">
            <main id="main" class="main">
                <div class="main-inner">
                    <div id="content" class="content">
                        <article>
                            <header>
                                <h2>
                                    <a rel="archive" href="${servePath}/archives.html">
                                        ${archiveLabel}
                                    </a>
                                </h2>
                            </header>
                            <#if 0 != archiveDates?size>
                            <ul class="tags fn-clear">
                                <#list archiveDates as archiveDate>
                                <li>
                                    <#if "en" == localeString?substring(0, 2)>
                                    <a class="tag" href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                                       title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                                        ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})</a>
                                    <#else>
                                    <a class="tag" href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                                       title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})</a>
                                    </#if>
                                </li>
                                <li>
                                    <#list articles as article>
                                     ${article.articleTitle}
                                    </#list>
                                </li>
                                </#list>
                            </ul>
                            </#if>
                        </article>
                    </div>
                </div>
                <#include "side.ftl">
            </main>
            <#include "footer.ftl">
        </div>
    </body>
</html>
