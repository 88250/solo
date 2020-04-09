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