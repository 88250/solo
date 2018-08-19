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
    <div id="pageTable">
    </div>
    <div id="pagePagination" class="margin12 right">
    </div>
    <div class="clear"></div>
</div>
<div class="form">
    <div>
        <label>${title1Label}</label>
        <input id="pageTitle" type="text"/>
    </div>
    <div>
        <label>${permalink1Label}</label>
        <input id="pagePermalink" type="text"/>
    </div>
    <div>
        <label>${openMethod1Label}</label>
        <select id="pageTarget">
            <option value="_self">${targetSelfLabel}</option>
            <option value="_blank">${targetBlankLabel}</option>
            <option value="_parent">${targetParentLabel}</option>
            <option value="_top">${targetTopLabel}</option>
        </select>&nbsp;&nbsp;&nbsp;&nbsp;
        <label>${type1Label}</label>
        <button data-type="link" class="selected fn-type">${pageLinkLabel}</button>
        <button data-type="page" class="fn-type">${pageLabel}</button>
    </div>
    <div id="pagePagePanel" class="none">
        <textarea id="pageContent" style="height: 430px;width: 100%;" name="pageContent"></textarea>
        <label>${allowComment1Label}</label>
        <input type="checkbox" id="pageCommentable" checked="checked" />
    </div>
    <div class="right">
        <button onclick="admin.pageList.submit();">${saveLabel}</button>
    </div>
    <div class="clear"></div>
</div>
<div id="pageComments" class="none"></div>
<div class="clear"></div>
${plugins}
