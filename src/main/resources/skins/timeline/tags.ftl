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
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="wrapper">
            <div class="container">
                <#if "" != noticeBoard>
                    <div class="module">
                        ${noticeBoard}
                    </div>
                </#if>

                <#if 0 != links?size>
                    <h2 class="title">${linkLabel}</h2>
                    <div class="module links">
                        <#list links as link>
                            <span>
                        <a rel="friend" href="${link.linkAddress}" alt="${link.linkTitle}" target="_blank">
                            <img alt="${link.linkTitle}"
                                 src="${link.linkIcon}" width="16" height="16" /></a>
                        <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">
                            ${link.linkTitle}
                        </a>
                    </span> &nbsp; &nbsp;
                        </#list>
                    </div>
                </#if>

                <#if 0 != mostUsedCategories?size>
                    <h2>${categoryLabel}</h2>
                    <ul class="module fn-clear tags">
                        <#list mostUsedCategories as category>
                            <li>
                                <a href="${servePath}/category/${category.categoryURI}">
                                    <span>${category.categoryTitle}</span>
                                    (<b>${category.categoryPublishedArticleCount}</b>)
                                </a>
                            </li>
                        </#list>
                    </ul>
                </#if>

                <h2>${tagsLabel}</h2>
                <ul id="tags" class="tags module fn-clear">
                    <#list tags as tag>
                    <li>
                        <a rel="tag" data-count="${tag.tagPublishedRefCount}"
                           href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}" title="${tag.tagTitle}">
                            <span>${tag.tagTitle}</span>
                            (<b>${tag.tagPublishedRefCount}</b>)
                        </a>
                    </li>
                    </#list>
                </ul>
            </div>
        </div>
        <#include "footer.ftl">
        <script type="text/javascript">
            Util.buildTags();
        </script>
    </body>
</html>
