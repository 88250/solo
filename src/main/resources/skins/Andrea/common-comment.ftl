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
<div id="${comment.oId}"
     class="comment-body">
    <div class="comment-panel">
        <div class="left comment-author">
            <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
        </div>
        <div class="left comment-info">
        <#if "http://" == comment.commentURL>
            <a>${comment.commentName}</a>
        <#else>
            <a href="${comment.commentURL}"
               target="_blank">${comment.commentName}</a>
        </#if><#if comment.isReply>
            @
            <a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
               onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 20);"
               onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
        </#if>
            &nbsp;${comment.commentDate2?string("yyyy-MM-dd HH:mm:ss")}
            <div class="comment-content vditor-reset">
            ${comment.commentContent}
            </div>
        <#if article.commentable>
            <div>
                <a rel="nofollow" href="javascript:page.toggleEditor('${comment.oId}', '${comment.commentName}');">${replyLabel}</a>
            </div>
        </#if>
        </div>
        <div class="clear"></div>
    </div>
</div>