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
    <div id="userTable"></div>
    <div id="userPagination" class="margin12 right"></div>
</div>
<div class="clear"></div>
<div class="form form__no-table">
${addUserLabel}
    <label for="userName">${commentName1Label}</label>
    <input id="userName" type="text"/>
    <label for="userEmail">${commentEmail1Label}</label>
    <input id="userEmail" type="text"/>
    <label for="userURL">${userURL1Label}</label>
    <input id="userURL" type="text"/>
    <label for="userPassword">${userPassword1Label}</label>
    <input id="userPassword" type="password" autocomplete="new-password"/>
    <label for="userAvatar">${userAvatar1Label}</label>
    <input id="userAvatar" type="text"/><br><br>
    <button onclick="admin.userList.add();" class="right">${saveLabel}</button>
    <div class="clear"></div>
</div>
<div id="userUpdate" class="none form form__table">
${updateUserLabel}
    <label for="userNameUpdate">${commentName1Label}</label>
    <input id="userNameUpdate" type="text"/>
    <label for="userEmailUpdate">${commentEmail1Label}</label>
    <input id="userEmailUpdate" type="text"/>
    <label for="userURLUpdate">${userURL1Label}</label>
    <input id="userURLUpdate" type="text"/>
    <label for="userPasswordUpdate">${userPassword1Label}</label>
    <input id="userPasswordUpdate" type="password"/>
    <label for="userAvatarUpdate">${userAvatar1Label}</label>
    <input id="userAvatarUpdate" type="text"/> <br><br>
    <button onclick="admin.userList.update();" class="right">${updateLabel}</button>
    <div class="clear"></div>
</div>
${plugins}
