/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/**
 * @fileoverview metro-hot js.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.1.0.0, Feb 27, 2019
 */

var MetroHot = {
    headerH: $("#header").height() + 30 + ($("#header > div").get(1) ? 30 : 0),
    responsiveType: "large",
    goTranslate: function() {
        window.open("http://translate.google.com/translate?sl=auto&tl=auto&u=" + location.href);
    },
    init: function() {
        // logout
        var logoutHref = "";
        $("#admin a").each(function() {
            if ($(this).attr("href").indexOf("/logout") > -1) {
                logoutHref = $(this).attr("href");
            }
        });
        $("#logout, .logout").attr("href", logoutHref);

        // 头部标题点击事件
        $(".header .title, .navigation").mouseover(function() {
            $(".navigation").show();
        }).mouseout(function() {
            $(".navigation").hide();
        });
        $(".header .title").click(function() {
            window.location.href = Label.servePath;
        });

        // 当先用户在线数目
        var onlineVisitorCnt = $("#top > span").first().text();
        $(".online-count .text").append(onlineVisitorCnt.substr(1, onlineVisitorCnt.length));

        if ($("#dynamic").length === 1) {
            // 滚动处理
            $(window).scroll(function() {
                var y = $(window).scrollTop();
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

            $("body").css("min-height", "inherit");
            return;
        }

        // 滚动处理
        $(window).scroll(function() {
            var y = $(window).scrollTop();
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
    /**
     * @description 计算图片 margin-top
     * @param {BOM} it 图片元素
     */
    loadImg: function(it) {
        it.style.marginTop = ("margin-top", (220 - it.height) / 2 + "px");
    },
    /**
     * @description 分享按钮
     */
    share: function() {
      var $this = $('.share .text')
      var $qrCode = $this.find('.icon-wechat')
      var shareURL = $qrCode.data('url')
      var avatarURL = $qrCode.data('avatar')
      var title = encodeURIComponent($qrCode.data('title') + ' - ' +
        $qrCode.data('blogtitle')),
        url = encodeURIComponent(shareURL)

      var urls = {}
      urls.weibo = 'http://v.t.sina.com.cn/share/share.php?title=' +
        title + '&url=' + url + '&pic=' + avatarURL
      urls.qqz = 'https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url='
        + url + '&sharesource=qzone&title=' + title + '&pics=' + avatarURL
      urls.twitter = 'https://twitter.com/intent/tweet?status=' + title + ' ' +
        url

      $this.find('span').click(function () {
        var key = $(this).data('type')

        if (!key) {
          return
        }

        if (key === 'wechat') {
          if ($qrCode.find('canvas').length === 0) {
            $.ajax({
              method: 'GET',
              url: Label.staticServePath +
              '/js/lib/jquery.qrcode.min.js',
              dataType: 'script',
              cache: true,
              success: function () {
                $qrCode.qrcode({
                  width: 111,
                  height: 111,
                  text: shareURL,
                })
              },
            })
          } else {
            $qrCode.find('canvas').slideToggle()
          }
          return false
        }

        window.open(urls[key], '_blank', 'top=100,left=200,width=648,height=618')
      })
    },
    /*
     * @description 加载随机文章
     */
    loadRandomArticles: function() {
        // getRandomArticles
        $.ajax({
            url: Label.servePath + "/articles/random",
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
                    var randomArticleLiHtml = "<li>" + "<a rel='nofollow' title='" + title + "' href='" + Label.servePath +
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
            url: Label.servePath + "/article/id/" + id + "/relevant/articles",
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
                            + Label.servePath + article.articlePermalink + "'>"
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
    }
})();
