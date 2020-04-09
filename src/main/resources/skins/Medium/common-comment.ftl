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
<div id="${comment.oId}" class="comment__item">
    <img class="comment__avatar" src="${comment.commentThumbnailURL}" alt="评论"/>
    <main class="comment__body">
        <div class="fn-clear">
            <span class="comment__user">
                <#if "http://" == comment.commentURL>
                ${comment.commentName}
                <#else>
                    <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                </#if>
            </span>
            <span class="ft-12">
                <#if comment.isReply>
                    <a class="ft-gray" href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                        onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 23);"
                        onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">
                        <svg class="ft-gray"><use xlink:href="#icon-reply"></use></svg>
                        ${reply1Label} ${comment.commentOriginalCommentName}
                    </a>
                </#if>
                <time class="ft-fade"> • ${comment.commentDate2?string("yyyy-MM-dd")}</time>
            </span>


            <#if article.commentable>
                <a class="fn-right ft-green" href="javascript:page.toggleEditor('${comment.oId}', '${comment.commentName}')">
                    <svg><use xlink:href="#icon-reply"></use></svg> ${reply1Label}
                </a>
            </#if>
        </div>
        <div class="vditor-reset">
            ${comment.commentContent}
        </div>
    </main>
</div>
