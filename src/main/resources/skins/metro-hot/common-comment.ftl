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
