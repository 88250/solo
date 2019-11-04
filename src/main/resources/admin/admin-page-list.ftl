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
<div>
    <div id="pageTable">
    </div>
    <div id="pagePagination" class="fn__margin12 fn__right">
    </div>
    <div class="fn__clear"></div>
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
        <label>${icon1Label}</label>
        <input id="pageIcon" type="text"/>
    </div>
    <div>
        <label>${openMethod1Label}</label>
        <select id="pageTarget">
            <option value="_self">${targetSelfLabel}</option>
            <option value="_blank">${targetBlankLabel}</option>
            <option value="_parent">${targetParentLabel}</option>
            <option value="_top">${targetTopLabel}</option>
        </select>
    </div>
    <div class="fn__right">
        <button onclick="admin.pageList.submit();">${saveLabel}</button>
    </div>
    <div class="fn__clear"></div>
</div>
<div class="fn__clear"></div>
${plugins}
