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
        <div class="container one-column page-archive">
            <div class="headband"></div>
            <#include "header.ftl">
            <main id="main" class="main">
                <div class="main-inner">
                    <div id="content" class="content">
                        <section id="posts" class="posts-collapse">
                            <span class="archive-move-on"></span>
                            <span class="archive-page-counter">
                                嗯..! 目前共计 ${statistic.statisticPublishedBlogArticleCount} 篇日志。 继续努力。
                            </span>
                            <#if 0 != archiveDates?size>
                            <#list archiveDates as archiveDate>
                            <div class="collection-title">
                                <#if "en" == localeString?substring(0, 2)>
                                <h2 class="archive-year motion-element">
                                    <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                        ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})
                                    </a>
                                </h2>
                                <#else>
                                <h2 class="archive-year motion-element">
                                    <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})
                                    </a>
                                </h2>
                                </#if>
                            </div>
                            </#list>
                            </#if>
                        </section>
                    </div>
                </div>
                <#include "side.ftl">
            </main>
            <#include "footer.ftl">
        </div>
    </body>
</html>
