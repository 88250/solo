<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

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
<div id="${comment.oId}" class="comment__item">
    <img class="comment__avatar" src="${comment.commentThumbnailURL}"/>
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
                <a class="fn-right ft-green" href="javascript:replyTo('${comment.oId}')">
                    <svg><use xlink:href="#icon-reply"></use></svg> ${reply1Label}
                </a>
            </#if>
        </div>
        <div class="content-reset">
            ${comment.commentContent}
        </div>
    </main>
</div>