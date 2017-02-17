<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} (${archiveDate.archiveDatePublishedArticleCount}) - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${archiveDate.archiveDateYear}${archiveDate.archiveDateMonth}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        <#include "header.ftl">
        <main class="main wrapper">
            <div class="content page-archive">
                <section class="posts-collapse">
                    <span class="archive-move-on"></span>
                    <span class="archive-page-counter">
                      ${ohLabel}..! 
                        <#if "en" == localeString?substring(0, 2)>
                        ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear} 
                        <#else>
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                        </#if>
                        ${sumLabel} ${archiveDate.archiveDatePublishedArticleCount} ${fightLabel}
                    </span>
                </section>
                <#include "article-list.ftl">
            </div>
            <#include "side.ftl">
        </main>
        <#include "footer.ftl">
    </body>
</html>
