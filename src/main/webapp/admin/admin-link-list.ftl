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
    <div id="linkTable"></div>
    <div id="linkPagination" class="fn__margin12 fn__right"></div>
</div>
<div class="fn__clear"></div>
<div class="form form__no-table">
${addLinkLabel}
    <label>${linkTitle1Label}</label>
    <input id="linkTitle" type="text"/>
    <label>${url1Label}</label>
    <input id="linkAddress" type="text"/>
    <label>${linkDescription1Label}</label>
    <input id="linkDescription" type="text"/>
    <label>${linkIcon1Label}</label>
    <input id="linkIcon" type="text"/><br><br>
    <button onclick="admin.linkList.add();" class="fn__right">${saveLabel}</button>
    <div class="fn__clear"></div>
</div>
<div id="updateLink" class="fn__none form form__no-table" data-title="${updateLinkLabel}">
    <label>${linkTitle1Label}</label>
    <input id="linkTitleUpdate" type="text"/>
    <label>${url1Label}</label>
    <input id="linkAddressUpdate" type="text"/>
    <label>${linkDescription1Label}</label>
    <input id="linkDescriptionUpdate" type="text"/>
    <label>${linkIcon1Label}</label>
    <input id="linkIconUpdate" type="text"/><br><br>
    <button onclick="admin.linkList.update();" class="fn__right">${updateLabel}</button>
    <div class="fn__clear"></div>
</div>
${plugins}