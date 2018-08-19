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
    <div id="linkTable"></div>
    <div id="linkPagination" class="margin12 right"></div>
</div>
<div class="clear"></div>
<table class="form" width="100%" cellpadding="0px" cellspacing="9px">
    <thead>
        <tr>
            <th style="text-align: left" colspan="2">
                ${addLinkLabel}
            </th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <th width="48px">
                ${linkTitle1Label}
            </th>
            <td>
                <input id="linkTitle" type="text"/>
            </td>
        </tr>
        <tr>
            <th>
                ${url1Label}
            </th>
            <td>
                <input id="linkAddress" type="text"/>
            </td>
        </tr>
        <tr>
            <th>
                ${linkDescription1Label}
            </th>
            <td>
                <input id="linkDescription" type="text"/>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="right">
                <button onclick="admin.linkList.add();">${saveLabel}</button>
            </td>
        </tr>
    </tbody>
</table>
<div id="updateLink" class="none">
    <table class="form" width="100%" cellpadding="0px" cellspacing="9px">
        <thead>
            <tr>
                <th style="text-align: left" colspan="2">
                    ${updateLinkLabel}
                </th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <th width="48px">
                    ${linkTitle1Label}
                </th>
                <td>
                    <input id="linkTitleUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${url1Label}
                </th>
                <td>
                    <input id="linkAddressUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${linkDescription1Label}
                </th>
                <td>
                    <input id="linkDescriptionUpdate" type="text"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="right">
                    <button onclick="admin.linkList.update();">${updateLabel}</button>
                </td>
            </tr>
        </tbody>
    </table>
</div>
${plugins}