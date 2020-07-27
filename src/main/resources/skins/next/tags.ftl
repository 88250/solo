<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

-->
<#include "../../common-template/macro-common_head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${allTagsLabel} - ${blogTitle}">
            <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
        </@head>
    </head>
    <body>
        <#include "header.ftl">
        <main class="main">
            <div class="wrapper">
            <#if mostUsedCategories?size != 0>
            <div class="content page-archive">
                <section class="posts-collapse">
                    <div class="tag-cloud">
                        ${sumLabel} ${mostUsedCategories?size} ${categoryLabel}
                    </div>
                    <#list mostUsedCategories as category>
                        <article>
                            <header class="post-header">
                                <h2>
                                    <a class="post-title" href="${servePath}/category/${category.categoryURI}">
                                        ${category.categoryTitle} (${category.categoryPublishedArticleCount})</a>
                                    <small>${category.categoryDescription}</small>
                                </h2>
                            </header>
                        </article>
                    </#list>
                </section>
            </div>
            <br>
            <br>
            <br>
            </#if>
            <div class="content">
                <div class="tag-cloud">
                        ${sumLabel} ${tags?size} ${tagLabel}
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
            <#include "side.ftl">
            </div>
        </main>
        <#include "footer.ftl">
        <script>
            Util.buildTags();
        </script>
    </body>
</html>
