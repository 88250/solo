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
<link type="text/css" rel="stylesheet" href="${staticServePath}/plugins/symphony-interest/style.css"/>
<div id="symphonyInterestPanel">
    <div class="module-panel">
        <div class="module-header">
            <h2>${interestLabel}</h2>
        </div>
        <div class="module-body padding12">
            <div id="symphonyInterest">
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    plugins.symphonyInterest = {
        init: function () {
            $("#loadMsg").text("${loadingLabel}");

            $("#symphonyInterest").css("background",
                    "url(${staticServePath}/images/loader.gif) no-repeat scroll center center transparent");

            $.ajax({
                url: "${servePath}/blog/interest-tags",
                type: "GET",
                dataType: "json",
                error: function () {
                    $("#symphonyInterest").html("Loading Interest failed :-(").css("background", "none");
                },
                success: function (result, textStatus) {
                    var tags = result.data;
                    if (0 === tags.length) {
                        return;
                    }
                    
                    $.ajax({
                        url: "https://hacpai.com/apis/articles?p=1&size=7&tags=" + tags.join(),
                        type: "GET",
                        dataType: "jsonp",
                        jsonp: "callback",
                        error: function () {
                            $("#symphonyInterest").html("Loading Interest failed :-(").css("background", "none");
                        },
                        success: function (data, textStatus) {
                            var articles = data.articles;
                            if (0 === articles.length) {
                                return;
                            }
                            
                            var listHTML = "<ul>";
                            for (var i = 0; i < articles.length; i++) {
                                var article = articles[i];
                                
                                var articleLiHtml = "<li>"
                                        + "<a target='_blank' href='" + article.articlePermalink + "'>"
                                        + article.articleTitle + "</a>&nbsp; <span class='date'>" + $.bowknot.getDate(article.articleCreateTime, 1);
                                +"</span></li>"
                                listHTML += articleLiHtml
                            }
                            listHTML += "</ul>";

                            $("#symphonyInterest").html(listHTML).css("background", "none");
                        }
                    });
                }
            });



            $("#loadMsg").text("");
        }
    };

    /*
     * 添加插件
     */
    admin.plugin.add({
        "id": "symphonyInterest",
        "path": "/main/panel1",
        "content": $("#symphonyInterestPanel").html()
    });

    // 移除现有内容
    $("#symphonyInterestPanel").remove();
</script>
