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
<#if article.commentable>
<div class="comment-disabled">
    <h3>${postCommentsLabel}</h3>
    <textarea rows="3" style="margin-bottom: 20px;width: 100%;box-sizing: border-box" placeholder="${postCommentsLabel}" id="comment"></textarea>
</div>
<#else>
<div class="comment-disabled">
    <h3>${notAllowCommentLabel}</h3>
</div>
</#if>

<#if commentList?size == 0>
<div class="comment-disabled">
    <h3>${noCommentLabel}</h3>
</div>
<div id="comments"></div>
<#else>
<div class="comment-disabled">
    <h3>${commentLabel}</h3>
</div>
<div id="comments">
    <#list commentList as comment>
    <#include "common-comment.ftl"/>
    </#list>
</div>
</#if>
<span id="goTop" onclick="Util.goTop();" data-ico="&#xe042;" class="side-tile"></span>
<span id="goCmt" onclick="MetroHot.goCmt();" data-ico="&#x005b;" class="side-tile"></span>
</#macro>
