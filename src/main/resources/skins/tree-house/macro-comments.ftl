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
<#macro comments commentList article>
<div class="comments-header"></div>
<div class="comments marginTop12" id="comments">
    <#list commentList as comment>
    <#include "common-comment.ftl"/>
    </#list>
</div>
<#if article.commentable>
<div class="comments">
    <div class="comment-top"></div>
    <div class="comment-body">
        <div class="comment-title">
            <a>${postCommentsLabel}</a>
        </div>
        <textarea style="width: 100%" rows="3" placeholder="${postCommentsLabel}" id="comment"></textarea>
    </div>
    <div class="comment-bottom"></div>
</div>
</#if>
</#macro>