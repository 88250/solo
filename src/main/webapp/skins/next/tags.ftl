<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${allTagsLabel} - ${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${allTagsLabel}"/>
        <meta name="description" content="<#list tags as tag>${tag.tagTitle}<#if tag_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        <#include "side.ftl">
        <main class="classify">
            <article>
                <header>
                    <h2>
                        <a rel="archive" href="${servePath}/tags.html">
                            ${tagLabel}
                        </a>
                    </h2>
                </header>
                <ul class="tags fn-clear">
                    <#list tags as tag>
                    <li>
                        <a rel="tag" class="tag" data-count="${tag.tagPublishedRefCount}"
                           href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}" title="${tag.tagTitle}">
                            <span>${tag.tagTitle}</span>
                            (<b>${tag.tagPublishedRefCount}</b>)
                        </a>
                    </li>
                    </#list>
                </ul>
            </article>
            <#include "footer.ftl">
        </main>
    </body>
</html>
