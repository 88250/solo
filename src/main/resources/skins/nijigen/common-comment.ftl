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
<li id="${comment.oId}" class="comments__item">
    <div class="comments__meta fn__flex">
        <div class="fn__flex-1">
            <#if "http://" == comment.commentURL>
                ${comment.commentName}
            <#else>
            <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
            </#if>
            <#if comment.isReply>
            @<a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 28);"
                onmouseout="page.hideComment('${comment.commentOriginalCommentId}')"
            >${comment.commentOriginalCommentName}</a>
            </#if>
        </div>
        <time>${comment.commentDate2?string("yyyy-MM-dd HH:mm")}</time>
    </div>
    <main class="comments__content fn__clear">
        <div class="comments__avatar" style="background-image: url(${comment.commentThumbnailURL})"></div>
        <div class="vditor-reset">
        ${comment.commentContent}
        </div>
        <#if article?? && article.commentable>
            <a class="fn__right breadcrumb" href="javascript:page.toggleEditor('${comment.oId}', '${comment.commentName}')">${replyLabel}</a>
        <#else>
         <a class="fn__right breadcrumb" href="${servePath}${comment.commentSharpURL}">${viewLabel}»</a>
        </#if>
    </main>
</li>