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
<#setting locale="en_US">
<li id="${comment.oId}" class="item">
    <div class="fn__clear">
        <div class="item__avatar" style="background-image: url(${comment.commentThumbnailURL})"></div>
        <div class="item__name">
            <#if "http://" == comment.commentURL>
                <span class="ft__fade">${comment.commentName}</span>
            <#else>
            <a class="ft__link" href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
            </#if>

            <#if comment.isReply>
            @ <a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                 onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 6);"
                 onmouseout="page.hideComment('${comment.commentOriginalCommentId}')"
            >${comment.commentOriginalCommentName}</a>
            </#if>
        </div>
    </div>
    <div class="vditor-reset">
    ${comment.commentContent}
    </div>
    <div class="item__meta fn__clear">
        <time>
        ${comment.commentDate2?string["MMM d, yyyy"]}
        </time>
        <#if article?? && article.commentable>
            <a class="fn__right fn__none item__reply"
               href="javascript:page.toggleEditor('${comment.oId}', '${comment.commentName}')">Reply</a>
        </#if>
    </div>
</li>