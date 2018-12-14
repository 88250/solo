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
<#include "macro-common-page.ftl">

<@commonPage "${welcomeToSoloLabel}!">
<h2>
${loginLabel}
</h2>
<div id="github">
    <div class="github__icon"
        onclick="window.location.href = '${servePath}/oauth/github/redirect';$('#github').addClass('github--loading')">
        <img src="${staticServePath}/images/github-init.gif"/>
    </div>
    <button class="hover" onclick="window.location.href = '${servePath}/oauth/github/redirect';$('#github').addClass('github--loading')">${useGitHubAccountLoginLabel}</button>
    <br>
    <span onclick="$('#github').hide();$('.form').show()">${useLocalAccountLabel}</span>
</div>

<div class="form none">
    <label for="userEmail">
    ${userLabel}
    </label>
    <input id="userEmail" tabindex="1" />
    <label for="userPassword">
    ${userPasswordLabel} <a href="${servePath}/forgot">(${forgotLabel})</a>
    </label>
    <input type="password" id="userPassword" tabindex="2" />
    <button onclick='login();'>${loginLabel}</button>
    <span id="tip">${resetMsg}</span>
</div>
<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript">
    (function() {
        $("#userEmail").focus();

        $("#userPassword, #userEmail").keypress(function(event) {
            if (13 === event.keyCode) { // Enter pressed
                login();
            }
        });

        // if no JSON, add it.
        try {
            JSON
        } catch (e) {
            document.write("<script src=\"${staticServePath}/js/lib/json2.js\"><\/script>");
        }
    })();

    var login = function() {
        if ($("#userPassword").val() === "") {
            $("#tip").text("${passwordEmptyLabel}");
            $("#userPassword").focus();
            return;
        }

        var requestJSONObject = {
            "userEmail": $("#userEmail").val(),
            "userPassword": $("#userPassword").val()
        };

        $("#tip").html("<img src='${staticServePath}/images/loading.gif'/> loading...")

        $.ajax({
            url: "${servePath}/login",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestJSONObject),
            error: function() {
                // alert("Login error!");
            },
            success: function(data, textStatus) {
                if (!data.isLoggedIn) {
                    $("#tip").text(data.msg);
                    return;
                }

                window.location.href = data.to;
            }
        });
    };
</script>
</@commonPage>