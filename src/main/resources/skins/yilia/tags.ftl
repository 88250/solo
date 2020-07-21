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
        <#include "side.ftl">
        <main class="classify">
            <#if 0 != mostUsedCategories?size>
                <article>
                    <header>
                        <h2>
                            <a rel="archive" href="${servePath}/tags.html">
                                ${categoryLabel}
                            </a>
                        </h2>
                    </header>
                    <ul class="tags fn-clear">
                        <#list mostUsedCategories as category>
                            <li>
                                <a class="tag" href="${servePath}/category/${category.categoryURI}">
                                    ${category.categoryTitle} (${category.categoryPublishedArticleCount})</a>
                            </li>
                        </#list>
                    </ul>
                </article>
            </#if>

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
