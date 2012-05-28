<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${welcomeToSoloLabel} B3log Solo!</title>
        <meta name="keywords" content="GAE 博客,GAE blog,b3log,init" />
        <meta name="description" content="An open source blog based on GAE Java,GAE Java 开源博客,初始化程序" />
        <meta name="author" content="B3log Team" />
        <meta name="generator" content="B3log" />
        <meta name="copyright" content="B3log" />
        <meta name="revised" content="B3log, ${year}" />
        <meta name="robots" content="noindex, follow" />
        <meta http-equiv="Window-target" content="_top" />
        <link type="text/css" rel="stylesheet" href="${staticServePath}/css/default-init${miniPostfix}.css?${staticResourceVersion}" charset="utf-8" />
        <link rel="icon" type="image/png" href="${staticServePath}/favicon.png" />
        <script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
    </head>
    <body>
        <div class="wrapper">
            <div class="wrap">
                <div class="content">
                    <div class="logo">
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <img border="0" width="153" height="56" alt="B3log" title="B3log" src="${staticServePath}/images/logo.jpg"/>
                        </a>
                    </div>
                    <div class="main">
                        <h2>
                            <span>${welcomeToSoloLabel}</span>
                            <a target="_blank" href="http://b3log-solo.googlecode.com">
                                ${b3logLabel}
                                <span class="solo">&nbsp;Solo</span>
                            </a>
                        </h2>
                        <div id="init">
                            <div id="user">
                                <table>
                                    <tr>
                                        <td width="170px">
                                            <label for="userEmail">
                                                ${commentEmail1Label}
                                            </label>
                                        </td>
                                        <td>
                                            <input id="userEmail" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <label for="userName">
                                                ${userName1Label}
                                            </label>
                                        </td>
                                        <td>
                                            <input id="userName" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <label for="userPassword">
                                                ${userPassword1Label}
                                            </label>
                                        </td>
                                        <td>
                                            <input type="password" id="userPassword" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <label for="userPasswordConfirm">
                                                ${userPasswordConfirm1Label}
                                            </label>
                                        </td>
                                        <td>
                                            <input type="password" id="userPasswordConfirm" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="2">
                                            <button onclick='getUserInfo();'>${nextStepLabel}</button>
                                            <span id="tip"></span>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <div id="sys" class="none">
                                ${initIntroLabel}
                                <button onclick='initSys();' id="initButton">${initLabel}</button>
                                <button onclick='returnTo();'>${previousStepLabel}</button>
                                <span class="clear"></span>
                            </div>
                        </div>
                        <a href="http://b3log-solo.googlecode.com" target="_blank">
                            <img border="0" class="icon" alt="B3log" title="B3log" src="${staticServePath}/favicon.png"/>
                        </a>
                    </div>
                    <span class="clear"></span>
                </div>
            </div>

            <div class="footerWrapper">
                <div class="footer">
                    &copy; ${year}
                    Powered by
                    <a href="http://b3log-solo.googlecode.com" target="_blank">
                        ${b3logLabel}&nbsp;
                        <span class="solo">Solo</span></a>,
                    ver ${version}
                </div>
            </div>
        </div>
        <script type="text/javascript">
            $("input").keypress(function (event) {
                if (event.keyCode === 13) {
                    getUserInfo();
                }
            });
            
            var validate = function () {
                if (!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#userEmail" + status).val())) {
                    $("#tip").text("${mailInvalidLabel}");
                } else if ($("#userName").val().replace(/\s/g, "") === "") {
                    $("#tip").text("${nameEmptyLabel}");
                } else if ($("#userPassword").val().replace(/\s/g, "") === "") {
                    $("#tip").text("${passwordEmptyLabel}");
                } else if ($("#userPassword").val() !== $("#userPasswordConfirm").val()) {
                    $("#tip").text("${passwordNotMatchLabel}");
                } else {
                    $("#tip").text("");
                    return true;
                }  
                return false;
            };
            
            var getUserInfo = function () {
                if (validate()) {
                    $("#init").animate({
                        "top": -130
                    }); 
                    
                    $("#user").animate({
                        "opacity": 0
                    }); 
                    
                    $("#sys").css({
                        "display": "block",
                        "opacity": 1
                    });
                    
                    $("#initButton").focus();
                }
            };
            
            var returnTo = function () {
                $("#init").animate({
                    "top": 102
                }); 
                
                $("#user").animate({
                    "opacity": 1
                }); 
                
                $("#sys").animate({
                    "opacity": 0
                }, 800, function () {
                    this.style.display = "none";
                }); 
            };
            
            var initSys = function () {
                var requestJSONObject = {
                    "userName": $("#userName").val(),
                    "userEmail": $("#userEmail").val(),
                    "userPassword": $("#userPassword").val()
                };
                
                if(confirm("${confirmInitLabel}")){
                    $.ajax({
                        url: "${contextPath}/init",
                        type: "POST",
                        data: JSON.stringify(requestJSONObject),
                        success: function(result, textStatus){
                            if (!result.sc) {
                                alert(result.msg);
                            
                                return;
                            }
                    
                            window.location.href = "${servePath}/admin-index.do#tools/user-list";
                        }
                    });
                }
            };
        </script>
    </body>
</html>
