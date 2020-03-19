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
<div id="${comment.oId}" class="fn-clear">
    <img title="${comment.commentName}"
         alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
    <div class="comment-main">
        <div class="fn-clear">
        <#if "http://" == comment.commentURL>
            <span>${comment.commentName}</span>
        <#else>
            <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
        </#if>

        <#if comment.isReply>
            <span class="at">@</span>
            <a class="user-name" href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
               onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 20);"
               onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
        </#if>

        <#if article.commentable>
            <a data-ico="&#x0056;" rel="nofollow" href="javascript:page.toggleEditor('${comment.oId}', '${comment.commentName}');" title="${replyLabel}"></a>
        </#if>

            <div class="fn-right" data-ico="&#xe200;">
            ${comment.commentDate2?string("yy-MM-dd HH:mm")}
            </div>
        </div>
        <div class="vditor-reset">${comment.commentContent}</div>
    </div>
</div>
