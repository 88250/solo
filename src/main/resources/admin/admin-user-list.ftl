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
    <div id="userTable"></div>
    <div id="userPagination" class="fn__margin12 fn__right"></div>
</div>
<div class="fn__clear"></div>
<div class="form form__no-table">
<div id="userUpdate" class="fn__none form form__no-table">
    <label for="userNameUpdate">${userName1Label}</label>
    <input id="userNameUpdate" type="text" readonly/>
    <label for="userAvatarUpdate">${userAvatar1Label}</label>
    <input id="userAvatarUpdate" type="text" readonly/>
    <label for="userURLUpdate">${userURL1Label}</label>
    <input id="userURLUpdate" type="text"/>
    <label for="userB3KeyUpdate">B3 Key</label>
    <input id="userB3KeyUpdate" type="text"/>
    <br><br>
    <button onclick="admin.userList.update();" class="fn__right">${updateLabel}</button>
    <div class="fn__clear"></div>
</div>
${plugins}
