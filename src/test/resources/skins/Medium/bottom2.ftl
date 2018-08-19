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
<div class="footer__tag wrapper">
<#if 0 != mostUsedCategories?size>
    <#list mostUsedCategories as category>
        <a class="tag pipe-tooltipped pipe-tooltipped--n"
           aria-label="${category.categoryTagCnt} ${cntLabel}${tagsLabel}"
           href="${servePath}/category/${category.categoryURI}">${category.categoryTitle}</a>
    </#list>
</#if>
<#if 0 != mostUsedTags?size>
    <#list mostUsedTags as tag>
        <a rel="tag"
           class="tag pipe-tooltipped pipe-tooltipped--n"
           aria-label="${tag.tagPublishedRefCount} ${countLabel}${articleLabel}"
           href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}">${tag.tagTitle}</a>
    </#list>
</#if>
</div>