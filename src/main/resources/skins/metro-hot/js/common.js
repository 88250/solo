/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/**
 * @fileoverview metro-hot js.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.0.0, Jan 18, 2019
 */

import '../../../js/common'

window.MetroHot = {
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
  /*
   * @description 加载随机文章
   */
  loadRandomArticles: function() {
    // getRandomArticles
    $.ajax({
      url: Label.servePath + "/articles/random.json",
      type: "GET",
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
      url: Label.servePath + '/article/relevant/' + id + '.json',
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
})();
