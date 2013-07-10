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
 * @version 1.0.0.6, Jul 10, 2013
 */

var MetroHot = {
    headerH: 240,
    responsiveType: "large",
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
        }).dblclick(function() {
            window.location.href = latkeConfig.servePath;
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

            if (y > MetroHot.headerH) {
                $("#goTop").fadeIn("slow");
            } else {
                $("#goTop").hide();
            }

            if ($(".side > div").height() < 620) {
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
                        "top": "auto",
                        "bottom": "10px",
                        "width": "240px"
                    });
                } else {
                    $(".side > div").css("position", "static");
                }
            }
        }).click(function(event) {
            if (event.target.className === "title" || event.target.parentElement.className === "title") {
                return;
            }
            $(".navigation").slideUp();
        }).resize(function() {
            var windowW = window.innerWidth,
                    type = "large";
            if (windowW > 460 && windowW <= 860) {
                type = "mid";
            } else if (window < 460) {
                type = "small";
            }
            if (MetroHot.responsiveType !== type) {
                $(window).scroll();
                MetroHot.responsiveType === type;
            }
        });

        $(window).scroll();
    },
    initArticleList: function() {
        $(".article-list .article-abstract").each(function() {
            var $it = $(this);
            var $images = $it.find("img");
            if ($images.length > 0) {
                $it.addClass("article-image");
                $images.hide();

                $it.before("<img src='" + $($images[0]).attr("src") + "'/>");
            }
        });
    },
    /**
     * @description 分享按钮
     */
    share: function() {
        var title = encodeURIComponent($("title").text()),
                url = window.location.href,
                pic = $(".article-body img").attr("src");
        var urls = {};
        urls.tencent = "http://share.v.t.qq.com/index.php?c=share&a=index&title=" + title +
                "&url=" + url + "&pic=" + pic;
        urls.sina = "http://v.t.sina.com.cn/share/share.php?title=" +
                title + "&url=" + url + "&pic=" + pic;
        urls.google = "https://plus.google.com/share?url=" + url;
        urls.twitter = "https://twitter.com/intent/tweet?status=" + title + " " + url;
        $(".share span").click(function() {
            var key = $(this).attr("title").toLowerCase();
            window.open(urls[key], "_blank", "top=100,left=200,width=648,height=618");
        });
    },
    /*
     * @description 加载随机文章
     */
    loadRandomArticles: function() {
        // getRandomArticles
        $.ajax({
            url: latkeConfig.servePath + "/get-random-articles.do",
            type: "POST",
            success: function(result, textStatus) {
                var randomArticles = result.randomArticles;
                if (!randomArticles || 0 === randomArticles.length) {
                    $("#randomArticles").remove();
                    return;
                }

                var listHtml = "";
                for (var i = 0; i < randomArticles.length && i < 5; i++) {
                    var article = randomArticles[i];
                    var title = article.articleTitle;
                    var randomArticleLiHtml = "<li>" + "<a rel='nofollow' title='" + title + "' href='" + latkeConfig.servePath +
                            article.articlePermalink + "'>" + title + "</a></li>";
                    listHtml += randomArticleLiHtml;
                }

                var randomArticleListHtml = "<ul>" + listHtml + "</ul>";
                $("#randomArticles .text").append(randomArticleListHtml);
            }
        });
    },
    /*
     * @description 加载相关文章
     * @param {String} id 文章 id
     */
    loadRelevantArticles: function(id) {
        $.ajax({
            url: latkeConfig.servePath + "/article/id/" + id + "/relevant/articles",
            type: "GET",
            success: function(data, textStatus) {
                var articles = data.relevantArticles;
                if (!articles || 0 === articles.length) {
                    $("#relevantArticles").remove();
                    return;
                }
                var listHtml = "";
                for (var i = 0; i < articles.length && i < 5; i++) {
                    var article = articles[i];
                    var title = article.articleTitle;
                    var articleLiHtml = "<li>"
                            + "<a rel='nofollow' title='" + title + "' href='"
                            + latkeConfig.servePath + article.articlePermalink + "'>"
                            + title + "</a></li>";
                    listHtml += articleLiHtml;
                }

                var relevantArticleListHtml = "<ul>"
                        + listHtml + "</ul>";
                $("#relevantArticles .text").append(relevantArticleListHtml);
            },
            error: function() {
                $("#relevantArticles").remove();
            }
        });
    },
    /*
     * @description 加载站外相关文章
     * @param {String} tags 文章 tags
     */
    loadExternalRelevantArticles: function(tags) {
        var tips = this.tips;
        try {
            $.ajax({
                url: "http://rhythm.b3log.org:80/get-articles-by-tags.do?tags=" + tags
                        + "&blogHost=" + tips.blogHost + "&paginationPageSize=" + tips.externalRelevantArticlesDisplayCount,
                type: "GET",
                cache: true,
                dataType: "jsonp",
                error: function() {
                    $("#externalRelevantArticles").remove();
                },
                success: function(data, textStatus) {
                    var articles = data.articles;
                    if (!articles || 0 === articles.length) {
                        $("#externalRelevantArticles").remove();
                        return;
                    }
                    var listHtml = "";
                    for (var i = 0; i < articles.length && i < 5; i++) {
                        var article = articles[i];
                        var title = article.articleTitle;
                        var articleLiHtml = "<li>"
                                + "<a rel='nofollow' title='" + title + "' target='_blank' href='" + article.articlePermalink + "'>"
                                + title + "</a></li>";
                        listHtml += articleLiHtml;
                    }

                    var randomArticleListHtml = "<ul>" + listHtml + "</ul>";
                    $("#externalRelevantArticles .text").append(randomArticleListHtml);
                }
            });
        } catch (e) {
            // 忽略相关文章加载异常：load script error
            $("#externalRelevantArticles").remove();
        }
    },
    goCmt: function() {
       $("html, body").animate({
           scrollTop: $(".comment-disabled").get(0).offsetTop
       });
    }
};

(function() {
    MetroHot.init();
    if ($(".article-header").length > 0) {
        MetroHot.share();
    } else {
        MetroHot.initArticleList();
    }
})();
