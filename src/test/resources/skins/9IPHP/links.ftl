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
        <div class="wrapper">
            <div class="main-wrap">
                <main class="other">
                    <div class="title">
                         <h2><i class="icon-link"></i>
                             &nbsp;${linkLabel}</h2>
                    </div>
                    <#if 0 != links?size>
                        <ul class="list">
                            <#list links as link>
                                <li>
                                    <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">
                                        ${link.linkTitle}
                                    </a>
                                </li>
                            </#list>
                        </ul>
                    </#if>
                </main>
                <#include "side.ftl">
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>
