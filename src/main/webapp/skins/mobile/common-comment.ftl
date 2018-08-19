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
<li id="${comment.oId}">
    <div class="comwrap">
        <div class="comtop"><!--TODO comment->comment_approved == '0') : comtop preview;-->
            <img alt='${comment.commentName}' src='${comment.commentThumbnailURL}' class='avatar avatar-64 photo' height='64' width='64' />
            <div class="com-author">
            <#if "http://" == comment.commentURL>
                <a>${comment.commentName}</a>
            <#else>
                <a href='${comment.commentURL}' rel='external nofollow' target="_blank" class='url'>${comment.commentName}</a>
            </#if>
            <#if comment.isReply>
                @
                <a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}">${comment.commentOriginalCommentName}</a>
            </#if>
            </div>
        <#if article.commentable>
            <div class="comdater">
                <!--<span>TODO wptouch_moderate_comment_link(get_comment_ID())</span>-->
            ${comment.commentDate2?string("yyyy-MM-dd HH:mm:ss")}
                <a rel="nofollow" href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
            </div>
        </#if>
        </div><!--end comtop-->
        <div class="combody article-body">
            <p>${comment.commentContent}</p>
        </div>
    </div>
</li>