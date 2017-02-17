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
        <#include "header.ftl">
        <div class="wrapper">
            <div class="main-wrap">
                <main class="other">
                    <div class="title">
                        <h2><i class="icon-tags"></i>
                            &nbsp; ${sumLabel} ${tags?size} ${tagLabel}
                    </div>
                    <div class="tags">
                        <#list tags as tag>
                            <a rel="tag" data-count="${tag.tagPublishedRefCount}" class="tag"
                               href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                                <span>${tag.tagTitle}</span>
                                (<b>${tag.tagPublishedRefCount}</b>)
                            </a>
                        </#list>
                    </div>
                </main>
                <#include "side.ftl">
            </div>
        </div>
        <#include "footer.ftl">
            <script type="text/javascript" src="${staticServePath}/skins/${skinDirName}/js/isotope.pkgd.min.js" charset="utf-8"></script>
            <script>
            $('.tags').isotope({
                transitionDuration: '1.5s',
                filter: 'a',
                layoutMode: 'fitRows'
            });
            $('.tags').isotope({
                sortBy: 'random'
            });
            </script>
    </body>
</html>
