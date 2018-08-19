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
<div>
    <div id="categoryTable"></div>
    <div id="categoryPagination" class="margin12 right"></div>
</div>
<div class="clear"></div>
<div class="form form__no-table">
${addCategoryLabel}
    <label for="categoryName">${linkTitle1Label}</label>
    <input id="categoryName" type="text"/>
    <label for="categoryURI">URI：</label>
    <input id="categoryURI" type="text"/>
    <label for="categoryDesc">${linkDescription1Label}</label>
    <input id="categoryDesc" type="text"/>
    <label for="categoryTags">${tags1Label}</label>
    <span class="tag__select">
        <input id="categoryTags" type="text"/>
    </span><br>
    <button onclick="admin.categoryList.add();" class="right">${saveLabel}</button>
    <div class="clear"></div>
</div>
<div id="categoryUpdate" class="none form__no-table form">
${updateCategoryLabel}
    <label for="categoryNameUpdate">${linkTitle1Label}</label>
    <input id="categoryNameUpdate" type="text"/>
    <label for="categoryURIUpdate">URI：</label>
    <input id="categoryURIUpdate" type="text"/>
    <label for="categoryDescUpdate">${linkDescription1Label}</label>
    <input id="categoryDescUpdate" type="text"/>
    <label for="categoryTagsUpdate">${tags1Label}</label>
    <input id="categoryTagsUpdate" type="text"/> <br><br>
    <button onclick="admin.categoryList.update();" class="right">${updateLabel}</button>
    <div class="clear"></div>
</div>
${plugins}
