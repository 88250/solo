<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${linkLabel}"/>
        <meta name="description" content="${metaDescription},${linkLabel}"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="main">
            <div class="wrapper">
                <#if 0 != links?size>
                <ul class="other-main links">
                    <#list links as link>
                    <li>
                        <a rel="friend" href="${link.linkAddress}" alt="${link.linkTitle}" target="_blank">
                            <img alt="${link.linkTitle}"
                                 src="http://www.google.com/s2/u/0/favicons?domain=<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" /></a>
                        <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">${link.linkTitle}
                        </a>
                    </li>
                    </#list>
                </ul>
                </#if>
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>
