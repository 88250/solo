/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @fileoverview metro-hot js.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.3, Jul 5, 2013
 */

var MetroHot = {
    headerH: 240,
    goTranslate: function() {
        window.open("http://translate.google.com/translate?sl=auto&tl=auto&u=" + location.href);
    },
    init: function() {
        // 登录与否的显示设置
        var isLogin = $("#admin").data("login");
        if (isLogin) {
            $(".user .text").html($("#admin > span").text());
            $(".login, .register, #login, #register, .logout, .settings").hide();
        } else {
            $(".login, .register, .user, .clear, .logout, .settings, #logout, #settings").hide();
        }

        // 侧边栏点击事件
        $("#login, .login").attr("href", $("#admin > a").first().attr("href"));
        // 当先用户在线数目
        var onlineVisitorCnt = $("#top > span").first().text();
        $(".online-count .text").append(onlineVisitorCnt.substr(1, onlineVisitorCnt.length));
        // logout
        var logoutHref = "";
        $("#admin a").each(function() {
            if ($(this).attr("href").indexOf("/logout?goto=") > -1) {
                logoutHref = $(this).attr("href");
            }
        });
        $("#logout, .logout").attr("href", logoutHref);

        // 头部标题点击事件
        $(".header .title").click(function() {
            $(".navigation").slideToggle();
        });

        // 滚动处理
        $(window).scroll(function() {
            var y = $(window).scrollTop();
            if (y > MetroHot.headerH) {
                if (isLogin) {
                    $(".logout, .settings").show();
                } else {
                    $(".login, .register").show();
                }
            } else {
                if (isLogin) {
                    $(".logout, .settings").hide();
                } else {
                    $(".login, .register").hide();
                }
            }

            if (!isLogin) {
                if (y > MetroHot.headerH) {
                    $(".side > div").css({
                        "position": "fixed",
                        "top": "0px",
                        "width": "240px"
                    });
                } else {
                    $(".side > div").css("position", "static");
                }
            } else {
                if (y + window.innerHeight > $(".side > div").height() + MetroHot.headerH) {
                    $(".side > div").css({
                        "position": "fixed",
                        "bottom": "10px",
                        "width": "240px"
                    });
                } else {
                    $(".side > div").css("position", "static");
                }
            }

            if (y > MetroHot.headerH) {
                $("#goTop").fadeIn("slow");
            } else {
                $("#goTop").hide();
            }
        }).click(function(event) {
            if (event.target.className === "title" || event.target.parentElement.className === "title") {
                return;
            }
            $(".navigation").slideUp();
        });

        $(window).scroll();
    },
    
    initArticleList: function () {
        $(".article-list .article-abstract").each(function () {
            var $it = $(this);
            var $images = $it.find("img");
           if ($images.length > 0) {
               $it.addClass("article-image");
               $images.hide();
               
                $it.before("<img src='" + $($images[0]).attr("src") + "'/>");
           } 
        });
    },
            
    $body: $(".main > .wrapper"),
    $nav: $(".nav"),
    getCurrentPage: function() {
        var $next = $(".article-next");
        if ($next.length > 0) {
            window.currentPage = $next.data("page");
        }
    },
    setNavCurrent: function() {
        $(".nav ul a").each(function() {
            var $this = $(this);
            if ($this.attr("href") === latkeConfig.servePath + location.pathname) {
                $this.addClass("current");
            } else if (/\/[0-9]+$/.test(location.pathname)) {
                $(".nav ul li")[0].className = "current";
            }
        });
    },
    initCommon: function() {
        Util.init();
        Util.replaceSideEm($(".recent-comments-content"));
        Util.buildTags("tagsSide");
    },
    initArchives: function() {
        var $archives = $(".archives");
        if ($archives.length < 1) {
            return;
        }

        $(".footer").css("marginTop", "30px");
        var years = [], $archiveList = $archives.find("span").each(function() {
            var year = $(this).data("year"), tag = true;
            for (var i = 0; i < years.length; i++) {
                if (year === years[i]) {
                    tag = false;
                    break;
                }
            }
            if (tag) {
                years.push(year);
            }
        });

        var yearsHTML = "";
        for (var j = 0; j < years.length; j++) {
            var monthsHTML = "";
            for (var l = 0; l < $archiveList.length; l++) {
                var $month = $($archiveList[l]);
                if ($month.data("year") === years[j]) {
                    monthsHTML += $month.html();
                }
            }

            yearsHTML += "<div><h3 class='ft-gray'>" + years[j] + "</h3>" + monthsHTML + "</div>";
        }

        $archives.html(yearsHTML);

        // position
        var $items = $(".archives>div"), line = 0, top = 0, heights = [];

        for (var m = 0; m < $items.length; m++) {
            for (var n = 0; n < 3; n++) {
                if (m >= $items.length) {
                    break;
                }

                $items[m].style.left = (n * 310) + "px";

                if (line > 0) {
                    if ($items[m - 3].style.top !== "") {
                        top = parseInt($items[m - 3].style.top);
                    }
                    $items[m].style.top = $($items[m - 3]).height() + 60 + top + "px";

                    heights[n] = parseInt($items[m].style.top) + $($items[m]).height() + 60;
                } else {
                    heights[n] = $($items[m]).height() + 60;
                }

                if (n < 2) {
                    m += 1;
                }
            }
            line += 1;
        }

        // archive height
        $archives.height(heights.sort()[heights.length - 1]);
    },
    setDynamic: function() {
        var $dynamic = $(".dynamic");
        if ($(".dynamic").length < 1) {
            return;
        }

        var $comments = $dynamic.find(".side-comments"), $tags = $dynamic.find(".side-tags"), $mostComment = $dynamic.find(".side-most-comment"), $mostView = $dynamic.find(".side-most-view");

        if ($comments.height() > $tags.height()) {
            $tags.height($comments.height());
        } else {
            $comments.height($tags.height());
        }

        if ($mostComment.height() > $mostView.height()) {
            $mostView.height($mostComment.height());
        } else {
            $mostComment.height($mostView.height());
        }

        // emotions
        $(".article-body").each(function() {
            this.innerHTML = Util.replaceEmString($(this).html());
        });
    }
};

(function() {
    MetroHot.init();
    MetroHot.initArticleList();
})();
