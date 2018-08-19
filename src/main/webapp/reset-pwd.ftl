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

<@commonPage "${forgotLabel}!">
<h2>
${forgotLabel}
</h2>
    <#if "email" == inputType>
    <div class="form">
        <label for="emailOrPassword">
        ${commentEmailLabel}
        </label>
        <input id="emailOrPassword"/>
        <button id="sendBtn" onclick='forgot();'>${sendLabel}</button>
        <span id="tip"></span>
    </div>
    <#else>
    <div class="form">
        <label for="emailOrPassword">
        ${userPasswordLabel}
        </label>
        <input type="password" id="emailOrPassword"/>
        <input type="hidden" id="token" value="${tokenHidden}"/>
        <button id="sendBtn" onclick='reset();'>${ok}</button>
        <span id="tip"></span>
    </div>
    </#if>

<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript">
    (function () {
        $("#emailOrPassword").focus();

        $("#emailOrPassword").keypress(function (event) {
            if (13 === event.keyCode) { // Enter pressed
                $('#sendBtn').click();
            }
        });

        // if no JSON, add it.
        try {
            JSON
        } catch (e) {
            document.write("<script src=\"${staticServePath}/js/lib/json2.js\"><\/script>");
        }
    })();

    var reset = function () {
        if ($("#emailOrPassword").val() === "") {
            $("#tip").text("${passwordEmptyLabel}");
            $("#emailOrPassword").focus();
            return;
        }
        var requestJSONObject = {
            "newPwd": $("#emailOrPassword").val(),
            "token": $("#token").val()
        };

        $("#tip").html("<img src='${staticServePath}/images/loading.gif'/> loading...");
        $.ajax({
            url: "${servePath}/reset",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestJSONObject),
            error: function () {
                // alert("reset password error!");
            },
            success: function (data, textStatus) {
                if (data.succeed) {
                    window.location.href = data.to;
                } else {
                    $("#tip").text(data.msg);
                }
            }
        });
    };

    var forgot = function () {
        if (!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#emailOrPassword" + status).val())) {
            $("#tip").text("${mailInvalidLabel}");
            $("#emailOrPassword").focus();
            return;
        }

        var requestJSONObject = {
            "userEmail": $("#emailOrPassword").val()
        };

        $("#tip").html("<img src='${staticServePath}/images/loading.gif'/> loading...")
        $.ajax({
            url: "${servePath}/forgot",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestJSONObject),
            error: function () {
                // alert("reset password error!");
            },
            success: function (data, textStatus) {
                if (data.succeed) {
                    window.location.href = data.to;
                } else {
                    $("#tip").text(data.msg);
                }
            }
        });
    };
</script>
</@commonPage>