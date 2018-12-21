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

<@commonPage "${registerSoloUserLabel}!">
<h2>${registerSoloUserLabel}</h2>
<div id="github">
    <div class="github__icon" href="${servePath}/oauth/github/redirect"
         onclick="window.location.href = '${servePath}/oauth/github/redirect';$('#github').addClass('github--loading')">
        <img src="${staticServePath}/images/github-init.gif"/>
    </div>
    <button class="hover"
            onclick="window.location.href = '${servePath}/oauth/github/redirect';$('#github').addClass('github--loading')">${useGitHubAccountLoginLabel}</button>
    <br>
    <span onclick="$('#github').hide();$('.form').show()">${useLocalAccountLabel}</span>
</div>
<div class="form none">
    <label for="userEmail">
    ${commentEmail1Label}
    </label>
    <input id="userEmail" />
    <label for="userName">
    ${userName1Label}
    </label>
    <input id="userName" />
    <label for="userURL">
    ${userURL1Label}
    </label>
    <input id="userURL" />
    <label for="userPassword">
    ${userPassword1Label}
    </label>
    <input type="password" id="userPassword" />
    <label for="userPasswordConfirm">
    ${userPasswordConfirm1Label}
    </label>
    <input type="password" id="userPasswordConfirm" />
    <button onclick='getUserInfo();'>${registerSoloUserLabel}</button>
    <span id="tip" ></span>
</div>
<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript">
    var validate = function() {
        var userName = $("#userName").val().replace(/(^\s*)|(\s*$)/g, "");
        if (!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#userEmail").val())) {
            $("#tip").text("${mailInvalidLabel}");
            $("#userEmail").focus();
        } else if (2 > userName.length || userName.length > 20) {
            $("#tip").text("${nameTooLongLabel}");
            $("#userName").focus();
        } else if ($("#userPassword").val() === "") {
            $("#tip").text("${passwordEmptyLabel}");
            $("#userPassword").focus();
        } else if ($("#userPassword").val() !== $("#userPasswordConfirm").val()) {
            $("#tip").text("${passwordNotMatchLabel}");
            $("#userPasswordConfirm").focus();
        } else {
            $("#tip").text("");
            return true;
        }
        return false;
    };

    var getUserInfo = function() {
        if (validate()) {
            var requestJSONObject = {
                "userName": $("#userName").val(),
                "userEmail": $("#userEmail").val(),
                "userURL": $("#userURL").val(),
                "userPassword": $("#userPassword").val()
            };

            $("#tip").html("<img src='${staticServePath}/images/loading.gif'/> loading...")
            $.ajax({
                url: "${servePath}" + "/console/user/",
                type: "POST",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus) {
                    $("#tip").text(result.msg);
                    if (!result.sc) {
                        return;
                    }
                    setTimeout(function() {
                        window.location.href = "${servePath}";
                    }, 1000);
                }
            })
        }
    }

    $(function() {
        $("#userPasswordConfirm").keypress(function(event) {
            if (event.keyCode === 13) {
                getUserInfo();
            }
        });

        $('.wrap').css('padding', "40px 0");
    });

</script>
</@commonPage>