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
        <div class="wrapper">
            <div class="main-wrap">
                <main>
                    <div class="title">
                        <h2 class="tip">
                            <i class="icon-inbox"></i>
                            &nbsp;
                            <#if "en" == localeString?substring(0, 2)>
                            ${archiveDate.archiveDateMonth} ${archiveDate.archiveDateYear}
                            <#else>
                                ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}
                            </#if>
                            - ${archiveDate.archiveDatePublishedArticleCount} ${articleLabel}
                        </h2>
                    </div>
                    <#include "article-list.ftl">
                </main>
                <#include "side.ftl">
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>
