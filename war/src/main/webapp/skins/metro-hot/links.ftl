<#include "macro-head.ftl">
<#include "macro-side.ftl">
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
        <div class="wrapper">
            <#include "header.ftl" />
            <div class="sub-nav fn-clear">
                <h2>${linkLabel}</h2>
            </div>
            <div class="fn-clear">
                <div class="main">
                    <#if 0 != links?size>
                    <ul class="archives fn-clear">
                        <#list links as link>
                        <li>
                            <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">
                                <img src="http://www.google.com/s2/u/0/favicons?domain=<#list link.linkAddress?split('/') as x><#if x_index=2>${x}<#break></#if></#list>" />
                                ${link.linkTitle}
                            </a>
                        </li>
                        </#list>
                    </ul>
                    </#if>
                    <#include "copyright.ftl"/>
                </div>
                <@side isArticle=false />
            </div>
        </div>
        <span id="goTop" onclick="Util.goTop()" data-ico="&#xe042;" class="side-tile"></span>
        <#include "footer.ftl"/>
    </body>
</html>
