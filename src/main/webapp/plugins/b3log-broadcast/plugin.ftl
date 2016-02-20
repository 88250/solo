<link type="text/css" rel="stylesheet" href="${staticServePath}/plugins/b3log-broadcast/style.css"/>
<div id="b3logBroadcastPanel">
    <div id="b3logBroadcast">
        <div class="module-panel">
            <div class="module-header">
                <h2 class="left">
                    <a target="_blank" href="https://hacpai.com/tags/B3log%20Broadcast">
                        ${userBroadcastLabel}
                    </a>
                </h2>
                <button class="none right msg"></button>
                <span class="clear"></span>
            </div>
            <div class="module-body padding12">
                <div id="b3logBroadcastList">
                </div>
            </div>
        </div>
    </div>
    <div id="b3logBroadcastDialog" class="form">
        <label>${titleLabel1}</label>
        <span class="none msg">${noEmptyLabel}</span>
        <input type="text" id="b3logBroadcastTitle">
        <label>${linkLabel1}</label>
        <input type="text" id="b3logBroadcastLink">
        <label>${contentLabel1}</label>
        <span class="none msg">${noEmptyLabel}</span>
        <textarea id="b3logBroadcastContent"></textarea>
        <button class="marginTop12">${submitLabel}</button><span class="none msg">${submitErrorLabel}</span>
    </div>
</div>
<script type="text/javascript">
    plugins.b3logBroadcast = {
        init: function() {
            $("#loadMsg").text("${loadingLabel}");

            // dialog
            $("#b3logBroadcastDialog").dialog({
                width: 700,
                height: 245,
                "modal": true,
                "hideFooter": true
            });

            // 打开广播窗口
            $("#b3logBroadcast .module-header > button").click(function() {
                $("#b3logBroadcastDialog").dialog("open");
            });

            // 广播提交
            $("#b3logBroadcastDialog button").click(function() {
                var data = {
                    "broadcast": {
                        "title": $("#b3logBroadcastTitle").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "content": $("#b3logBroadcastContent").val().replace(/(^\s*)|(\s*$)/g, ""),
                        "link": $("#b3logBroadcastLink").val()
                    }
                };

                if (data.broadcast.title === "") {
                    $("#b3logBroadcastTitle").prev().show();
                }

                if (data.broadcast.content === "") {
                    $("#b3logBroadcastContent").prev().show();
                }

                if (data.broadcast.title === "" || data.broadcast.content === "") {
                    return;
                }

                $.ajax({
                    type: "POST",
                    url: latkeConfig.servePath + "/console/plugins/b3log-broadcast",
                    data: JSON.stringify(data),
                    success: function(result) {
                        if (result.sc) {
                            $("#b3logBroadcastTitle").val("");
                            $("#b3logBroadcastLink").val("");
                            $("#b3logBroadcastContent").val("");
                            $("#b3logBroadcastDialog").dialog("close");
                            $("#b3logBroadcastDialog > button").next().hide();
                            $("#b3logBroadcastTitle").prev().hide();
                            $("#b3logBroadcastContent").prev().hide();
                            broadcastChange();
                        } else {
                            $("#b3logBroadcastDialog > button").next().show();
                        }
                    }
                });
            });

            // 获取广播
            $.ajax({
                type: "GET",
                url: "https://hacpai.com/apis/broadcasts",
                dataType: "jsonp",
                jsonp: "callback",
                beforeSend: function() {
                    $("#b3logBroadcastList").css("background",
                            "url(${staticServePath}/images/loader.gif) no-repeat scroll center center transparent");
                },
                error: function() {
                    $("#b3logBroadcastList").html("Loading Symphony broadcasts failed :-(").css("background", "none");
                },
                success: function(result) {
                    var articles = result.articles;

                    if (0 === articles.length) {
                        return;
                    }

                    var listHTML = "<ul>";
                    for (var i = 0; i < articles.length; i++) {
                        var article = articles[i];
                        var articleLiHtml = "<li>"
                                + "<a target='_blank' href='" + article.articlePermalink + "'>"
                                + article.articleTitle + "</a>&nbsp; <span class='date'>" + $.bowknot.getDate(article.articleCreateTime, 1);
                        +"</span></li>";
                        listHTML += articleLiHtml;
                    }
                    listHTML += "</ul>";

                    $("#b3logBroadcastList").html(listHTML).css("background", "none");
                },
                complete: function(XMLHttpRequest, textStatus) {
                    $("#loadMsg").text("");
                }
            });

            // 广播机会
            var interval;
            var broadcastChange = function() {
                $.ajax({
                    type: "GET",
                    url: latkeConfig.servePath + "/console/plugins/b3log-broadcast/chance",
                    cache: false,
                    success: function(result) {
                        if (result.sc) {
                            var showCountDown = function() {
                                var now = new Date();
                                var leftTime = result.broadcastChanceExpirationTime - now.getTime();
                                var leftsecond = parseInt(leftTime / 1000);
                                if (leftsecond < 0) {
                                    $("#b3logBroadcast .module-header > button").hide();
                                    clearInterval(interval);
                                    interval = undefined;
                                    return;
                                } else {
                                    var minute = Math.floor(leftsecond / 60),
                                            second = Math.floor(leftsecond - minute * 60);
                                    $("#b3logBroadcast .module-header > button").text("${chanceBroadcastLabel1}" + minute + ":" + second).show();
                                }
                            };

                            if (!interval) {
                                interval = window.setInterval(function() {
                                    showCountDown();
                                }, 1000);
                            }
                        } else {
                            $("#b3logBroadcast .module-header > button").hide();
                            clearInterval(interval);
                            interval = undefined;
                        }
                    }
                });
            };

            setInterval(function() {
                broadcastChange();
            }, 10000);
            
            broadcastChange();
        }
    };
    /*
     * 添加插件
     */
    admin.plugin.add({
        "id": "b3logBroadcast",
        "path": "/main/panel2",
        "content": $("#b3logBroadcastPanel").html()
    });

    // 移除现有内容
    $("#b3logBroadcastPanel").remove();
</script>