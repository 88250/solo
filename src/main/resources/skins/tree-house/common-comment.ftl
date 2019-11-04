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
<div id="${comment.oId}" class="comment">
    <div class="comment-panel">
        <div class="comment-top"></div>
        <div class="comment-body">
            <div class="comment-title">
            <#if "http://" == comment.commentURL>
                <a name="${comment.oId}" class="left">${comment.commentName}</a>
            <#else>
                <a name="${comment.oId}" href="${comment.commentURL}"
                   target="_blank" class="left">${comment.commentName}</a>
            </#if>
            <#if comment.isReply>
                &nbsp;@&nbsp;<a
                    href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                    onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 17);"
                    onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
            </#if>
            <#if article.commentable>
                <div class="right">
                ${comment.commentDate2?string("yyyy-MM-dd HH:mm:ss")}
                    <a rel="nofollow" class="no-underline"
                       href="javascript:page.toggleEditor('${comment.oId}', '${comment.commentName}');">${replyLabel}</a>
                </div>
            </#if>
                <div class="clear"></div>
            </div>
            <div>
                <img class="comment-picture left" alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                <div class="comment-content vditor-reset">
                ${comment.commentContent}
                </div>
                <div class="clear"></div>
            </div>
        </div>
        <div class="comment-bottom"></div>
    </div>
</div>