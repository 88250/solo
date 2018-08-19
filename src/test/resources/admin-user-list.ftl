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
<table class="form" width="100%" cellpadding="0px" cellspacing="9px">
    <thead>
        <tr>
            <th style="text-align: left" colspan="2">
                ${addUserLabel}
            </th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <th width="48px">
                <label for="userName">${commentName1Label}</label>
            </th>
            <td>
                <input id="userName" type="text"/>
            </td>
        </tr>
        <tr>
            <th>
                <label for="userEmail">${commentEmail1Label}</label>
            </th>
            <td>
                <input id="userEmail" type="text"/>
            </td>
        </tr>
        <tr>
            <th>
                <label for="userURL">${userURL1Label}</label>
            </th>
            <td>
                <input id="userURL" type="text"/>
            </td>
        </tr>
        <tr>
            <th>
                <label for="userPassword">${userPassword1Label}</label>
            </th>
            <td>
                <input id="userPassword" type="password"/>
            </td>
        </tr>
        <tr>
            <th>
                <label for="userAvatar">${userAvatar1Label}</label>
            </th>
            <td>
                <input id="userAvatar" type="text"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <button onclick="admin.userList.add();">${saveLabel}</button>
            </td>
        </tr>
    </tbody>
</table>
<div id="userUpdate" class="none">
    <table class="form" width="100%" cellpadding="0px" cellspacing="9px">
        <thead>
            <tr>
                <th style="text-align: left" colspan="2">
                    ${updateUserLabel}
                </th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <th width="48px">
                    <label for="userNameUpdate">${commentName1Label}</label>
                </th>
                <td>
                    <input id="userNameUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="userEmailUpdate">${commentEmail1Label}</label>
                </th>
                <td>
                    <input id="userEmailUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="userURLUpdate">${userURL1Label}</label>
                </th>
                <td>
                    <input id="userURLUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="userPasswordUpdate">${userPassword1Label}</label>
                </th>
                <td>
                    <input id="userPasswordUpdate" type="password"/>
                </td>
            </tr>
            <tr>
                <th>
                    <label for="userAvatarUpdate">${userAvatar1Label}</label>
                </th>
                <td>
                    <input id="userAvatarUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="right">
                    <button onclick="admin.userList.update();">${updateLabel}</button>
                </td>
            </tr>
        </tbody>
    </table>
</div>
${plugins}
