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
                <#if comment.isReply>
                @<a class="user-name" href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                   onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 23);"
                   onmouseout="page.hideComment('${comment.commentOriginalCommentId}')"
                >${comment.commentOriginalCommentName}</a>
                </#if>
                <time class="ft-gray">${comment.commentDate2?string("yyyy-MM-dd HH:mm")}</time>

                <#if article.commentable>
                    <a class="reply-btn" href="javascript:page.toggleEditor('${comment.oId}', '${comment.commentName}')">${replyLabel}</a>
                </#if>
            </div>
            <div class="vditor-reset">
                ${comment.commentContent}
            </div>
        </main>
    </div>
</li>