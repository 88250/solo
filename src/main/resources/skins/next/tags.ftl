<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
                                        ${category.categoryTitle} (${category.categoryTagCnt})</a>
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
