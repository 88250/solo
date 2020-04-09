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
<div id="${comment.oId}">
    <img class="left" alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
    <div class="comment-panel left">
        <div class="comment-top">
        <#if "http://" == comment.commentURL>
            <a>${comment.commentName}</a>
        <#else>
            <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
        </#if>
        <#if comment.isReply>
            @
            <a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
               onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 11);"
               onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">
            ${comment.commentOriginalCommentName}</a>
        </#if>
        ${comment.commentDate2?string("yyyy-MM-dd HH:mm:ss")}
        </div>
        <div class="comment-content vditor-reset">
        ${comment.commentContent}
        </div>
        <div class="clear"></div>
    <#if article.commentable>
        <div class="reply">
            <a rel="nofollow" href="javascript:page.toggleEditor('${comment.oId}', '${comment.commentName}');">${replyLabel}</a>
        </div>
    </#if>
    </div>
    <div class="clear"></div>
</div>