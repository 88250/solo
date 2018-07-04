<#--

    Solo - A beautiful, simple, stable, fast Java blogging system.
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
<li id="${comment.oId}">
    <div>
        <div class="avatar tooltipped tooltipped-n" aria-label="${comment.commentName}"
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
                    <a class="reply-btn" href="javascript:replyTo('${comment.oId}')">${replyLabel}</a>
                </#if>
            </div>
            <div class="content-reset">
                ${comment.commentContent}
            </div>
        </main>
    </div>
</li>