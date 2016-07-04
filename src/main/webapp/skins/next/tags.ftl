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
        <div class="container one-column">
            <div class="headband"></div>
            <#include "header.ftl">
            <main id="main" class="main">
                <div class="main-inner">
                    <div id="content" class="content">
                        <div id="posts" class="posts-expand">


                            <div class="tag-cloud">
                                <div class="tag-cloud-title">
                                    目前共计 ${tags?size} 个标签
                                </div>
                                <ul class="tag-cloud-tags fn-clear" id="tags">
                                    <#list tags as tag>
                                    <li>
                                        <a rel="tag" data-count="${tag.tagPublishedRefCount}"
                                           href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">
                                            <span>${tag.tagTitle}</span>
                                            (<b>${tag.tagPublishedRefCount}</b>)
                                        </a>
                                    </li>
                                    </#list>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <#include "side.ftl">
            </main>
            <#include "footer.ftl">
            <script>
                Util.buildTags();
            </script>
        </div>
    </body>
</html>
