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
<li id="${comment.oId}" class="comment__item">
    <div class="fn__flex">
        <div class="comment__avatar" style="background-image: url(${comment.commentThumbnailURL})"></div>
        <main class="comment__main fn__flex-1">
            <div class="fn__clear ft__gray">
            <#if "http://" == comment.commentURL>
                ${comment.commentName}
            <#else>
            <a href="${comment.commentURL}" target="_blank" class="ft__red">${comment.commentName}</a>
            </#if>
            <#if comment.isReply>
            @<a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 2);"
                onmouseout="page.hideComment('${comment.commentOriginalCommentId}')"
                class="ft__red"
            >${comment.commentOriginalCommentName}</a>
            </#if>
                •
                <time>${comment.commentDate2?string("yyyy-MM-dd HH:mm")}</time>

            <#if article.commentable>
            <span class="fn__right comment__btn" onclick="page.toggleEditor('${comment.oId}', '${comment.commentName}')">
                <i class="icon__comments"></i>
            </span>
            </#if>
            </div>

            <div class="vditor-reset comment__content">
            ${comment.commentContent}
            </div>
        </main>
    </div>
</li>