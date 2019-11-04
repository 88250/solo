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

        <div class="container">
            <div class="row">
                <div class="col-sm-2"></div>

                <div class="col-sm-8 site">
                    <h2>${categoryLabel}</h2>
                    <ul>
                        <#list mostUsedCategories as category>
                            <li>
                                <a href="${servePath}/category/${category.categoryURI}">
                                    <span>${category.categoryTitle}</span>
                                    (<b>${category.categoryTagCnt}</b>)</a>
                                </a>
                            </li>
                        </#list>
                    </ul>

                    <h2>${tagsLabel}</h2>
                    <ul>
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
            
            <div class="col-sm-2"></div>
        </div>
       
        <#include "footer.ftl">
    </body>
</html>
