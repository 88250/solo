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
        <#include "header.ftl">
        <main class="main wrapper">
            <div class="content page-archive">
                <section class="posts-collapse">
                    <span class="archive-move-on"></span>
                    <span class="archive-page-counter">
                        ${ohLabel}..!  ${sumLabel} ${statistic.statisticPublishedBlogArticleCount} ${fightLabel}
                    </span>
                    <#if 0 != archiveDates?size>
                    <#list archiveDates as archiveDate>
                    <article>
                        <header class="post-header">
                            <h1>
                                <#if "en" == localeString?substring(0, 2)>
                                <a class="post-title" href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                    ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})
                                </a>
                                <#else>
                                <a class="post-title" href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                    ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})
                                </a>
                                </#if>
                            </h1>
                        </header>
                    </article>
                    </#list>
                    </#if>
                </section>
            </div>
            <#include "side.ftl">
        </main>
        <#include "footer.ftl">
    </body>
</html>
