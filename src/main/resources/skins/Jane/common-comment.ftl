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