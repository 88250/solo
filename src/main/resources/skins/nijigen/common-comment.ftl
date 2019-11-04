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