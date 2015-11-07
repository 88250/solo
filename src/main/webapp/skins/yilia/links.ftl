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
        <#include "side.ftl">
        <main class="classify">
            <article>
                <header>
                    <h2>
                        <a rel="archive" href="${servePath}/links.html">
                            ${linkLabel}
                        </a>
                    </h2>
                </header>
                <#if 0 != links?size>
                <ul class="tags fn-clear">
                    <#list links as link>
                    <li>
                        <a rel="friend" href="${link.linkAddress}" class="tag"
                           title="${link.linkDescription}" target="_blank">
                            <img src="${faviconAPI}<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" width="16" height="16" />
                            ${link.linkTitle}
                        </a>
                    </li>
                    </#list>
                </ul>
                </#if>
            </article>
            <#include "footer.ftl">
        </main>
    </body>
</html>
