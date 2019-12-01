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
                 onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 20);"
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