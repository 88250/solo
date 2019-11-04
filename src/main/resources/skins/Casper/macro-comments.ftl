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
<div class="comment">
    <div class="comment__wrapper wrapper">
        <div class="comment__title">
            ${commentLabel}
        </div>
        <#if article.commentable>
        <textarea rows="3" placeholder="${commentContentCannotEmptyLabel}" id="comment"></textarea>
        </#if>

        <ul id="comments">
        <#list commentList as comment>
            <#include 'common-comment.ftl'/>
        </#list>
        </ul>
    </div>
</div>
</#macro>