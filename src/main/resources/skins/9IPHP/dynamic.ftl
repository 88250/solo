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
        <@head title="${dynamicLabel} - ${blogTitle}">
            <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
        </@head>
    </head>
    <body>
        <#include "header.ftl">
        <div class="wrapper">
            <div class="main-wrap">
                <main class="post">
                    <#if 0 < recentComments?size>
                        <ul class="comments" id="comments">
                            <#list recentComments as comment>
                                <li id="${comment.oId}">
                                    <div>
                                        <div class="avatar vditor-tooltipped vditor-tooltipped__n" aria-label="${comment.commentName}"
                                             style="background-image: url(${comment.commentThumbnailURL})"></div>
                                        <main>
                                            <div class="fn-clear">
                                                <#if "http://" == comment.commentURL>
                                                    ${comment.commentName}
                                                    <#else>
                                                        <a class="user-name" href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                                                </#if>
                                                <time class="ft-gray">${comment.commentDate?string("yyyy-MM-dd HH:mm")}</time>
                                                <a class="reply-btn" href="${servePath}${comment.commentSharpURL}">${viewLabel}»</a>
                                            </div>
                                            <div class="vditor-reset">
                                                ${comment.commentContent}
                                            </div>
                                        </main>
                                    </div>
                                </li>
                            </#list>
                        </ul>
                    <#else>
                    <div class="vditor-reset ft-center">
                        ${noDynamicLabel}
                    </div>
                    </#if>
                </main>
                <#include "side.ftl">
            </div>
        </div>
        <#include "footer.ftl">
    </body>
</html>
