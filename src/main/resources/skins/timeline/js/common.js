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
 * @fileoverview timeline js.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.0.0, Jan 18, 2019
 */

import '../../../js/common'

window.timeline = {
  _COLHA: 0,
  _COLHB: 20,
  /**
   * @description 时间戳转化为时间格式
   * @param {String} time 时间
   * @param {String} format 格式化后日期格式
   * @returns {String} 格式化后的时间
   */
  toDate: function (time, format) {
    var dateTime = new Date(time)
    var o = {
      'M+': dateTime.getMonth() + 1, //month
      'd+': dateTime.getDate(), //day
      'H+': dateTime.getHours(), //hour
      'm+': dateTime.getMinutes(), //minute
      's+': dateTime.getSeconds(), //second
      'q+': Math.floor((dateTime.getMonth() + 3) / 3), //quarter
      'S': dateTime.getMilliseconds(), //millisecond
    }

    if (/(y+)/.test(format)) {
      format = format.replace(RegExp.$1,
        (dateTime.getFullYear() + '').substr(4 - RegExp.$1.length))
    }

    for (var k in o) {
      if (new RegExp('(' + k + ')').test(format)) {
        format = format.replace(RegExp.$1,
          RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(
            ('' + o[k]).length))
      }
    }
    return format
  },
  _initArticleList: function() {
    var $articles = $(".articles");
    if ($articles.length === 0 || $(".articles > .fn-clear").length > 0) {
      return;
    }

    $(window).resize(function() {
      if ($("#hideTop").css("top") === "auto") {
        var colH = [timeline._COLHA, timeline._COLHB];
        $articles.find("article").each(function() {
          var $it = $(this),
            isLeft = colH[1] > colH[0],
            top = isLeft ? colH[0] : colH[1];
          if (parseInt($it.css("top")) !== top || top === 0) {
            $it.css({
              "top": top + "px",
              "position": "absolute"
            });

            if (isLeft) {
              this.className = "l";
            } else {
              this.className = "r";
            }
          }
          colH[(isLeft ? '0' : '1')] += parseInt($it.outerHeight(true));
        });

        $articles.height(colH[0] > colH[1] ? colH[0] : colH[1]);
      } else {
        $articles.find("article").each(function() {
          $(this).css({
            "position": "inherit",
            "top": "auto"
          }).removeClass("r l");
        });
        $articles.css("height", "auto");
      }
    });

    $(window).resize();
    $(".module img").imagesLoaded(function() {
      $(window).resize();
    });
  },
  _initIndexList: function() {
    var $archives = $(".articles > .fn-clear");
    if ($archives.length === 0) {
      return;
    }

    // 如果为 index 页面，重构 archives 结构，使其可收缩
    var year = 0;
    $(".nav-abs li").each(function(i) {
      var $this = $(this);
      $this.hide();
      if (year !== $this.data("year")) {
        year = $this.data("year");
        $this.before("<li class='close year' onclick='timeline.toggleArchives(this, " +
          year + ")'>" + year + "</li>");
      }
    });

    // 首次加载时，当没有下一页时，使用 js 隐藏"更多"按钮
    if ($(".article-more").parent().data("count") <= $(".article-more").parent().find("article").length) {
      $(".article-more").remove();
    }

    $(window).resize(function() {
      $archives.each(function() {
        if ($("#hideTop").css("top") === "auto") {
          var colH = [timeline._COLHA + 60, timeline._COLHB * 4];

          var $articles = $(this).find("article");
          if ($articles.length === 0) {
            $(this).find("h2").remove();
            $(this).css("margin-bottom", 0);
          } else {
            $articles.each(function() {
              var $it = $(this),
                isLeft = colH[1] > colH[0],
                top = isLeft ? colH[0] : colH[1];

              if (parseInt($it.css("top")) !== top || top === 0) {
                $it.css({
                  "top": top + "px",
                  "position": "absolute"
                });

                if (isLeft) {
                  this.className = "l";
                } else {
                  this.className = "r";
                }
              }
              colH[(isLeft ? '0' : '1')] += parseInt($it.outerHeight(true));
            });
            $(this).height(colH[0] > colH[1] ? colH[0] : colH[1]);
          }
        } else {
          var $articles = $(this).find("article");
          if ($articles.length === 0) {
            $(this).find("h2").remove();
            $(this).css("margin-bottom", 0);
          } else {
            $articles.each(function() {
              $(this).css({
                "position": "inherit",
                "top": "auto"
              }).removeClass("r l");
            });
            $(this).css("height", "auto");
          }
        }
      });
    });

    $(window).resize();
    $(".module img").imagesLoaded(function() {
      $(window).resize();
    });
  },
  _setNavCurrent: function() {
    $(".header li a").each(function() {
      if ($(this).prop("href") === location.href.split("#")[0]) {
        this.className = "current";
      } else {
        this.className = "";
      }
    })
  },
  init: function() {
    $(window).scroll(function() {
      if ($(window).scrollTop() > 60) {
        $(".ico-top").show();
      } else {
        $(".ico-top").hide();
      }
    });
    timeline._initIndexList();
    timeline._initArticleList();
    timeline._setNavCurrent();

    // init header list
    $(".ico-list").click(function() {
      if ($(".header > .container > form").css("height") === "0px") {
        $(".header > .container > ul, .header > .container > form").css({
          "height": "auto"
        });
      } else {
        $(".header > .container > ul, .header > .container > form").animate({
          "height": "0px"
        });
      }
    });
  },
  translate: function() {
    window.open("http://translate.google.com/translate?sl=auto&tl=auto&u=" + location.href);
  },
  getArchive: function(year, month, monthName) {
    var archiveDate = year + month,
      archive = year + "/" + month;
    window.location.hash = "#" + archiveDate;
    if ($("#" + archiveDate + " > article").length === 0) {
      var archiveDataTitle = year + " " + Label.yearLabel + " " + month + " " + Label.monthLabel;
      if (Label.localeString.substring(0, 2) === "en") {
        archiveDataTitle = monthName + " " + year;
      }
      var archiveHTML = '<h2><span class="article-archive">' + archiveDataTitle + '</span></h2>'
        + '<div class="article-more" onclick="timeline.getNextPage(this, \''
        + archive + '\')" data-page="0">' + Label.moreLabel + '</div>';

      $("#" + archiveDate).html(archiveHTML).css("margin-bottom", "50px");
      timeline.getNextPage($("#" + archiveDate).find(".article-more")[0], archive);
    }
  },
  getNextPage: function(it, archive) {
    var $more = $(it),
      currentPage = $more.data("page") + 1,
      path = "/articles/";
    if ($("#tag").length === 1) {
      var pathnames = location.pathname.split("/");
      path = "/articles/tags/" + pathnames[pathnames.length - 1];
    } else if ($("#author").length === 1) {
      var pathnames = location.pathname.split("/");
      path = "/articles/authors/" + pathnames[pathnames.length - 1];
    } else if ($("#category").length === 1) {
      var pathnames = location.pathname.split("/");
      path = "/articles/category/" + pathnames[pathnames.length - 1];
    } else if (archive) {
      path = "/articles/archives/" + archive;
    }
    $.ajax({
      url: Label.servePath + path + '?p=' + currentPage,
      type: "GET",
      beforeSend: function() {
        $more.css("background",
          "url(" + Label.staticServePath
          + "/skins/timeline/images/ajax-loader.gif) no-repeat scroll center center #60829F").text("");
      },
      success: function(result, textStatus) {
        if (0 !== result.code) {
          $more.css("background", "none #60829F").text("Error");
          return;
        }

        if (result.rslts.articles.length === 0) {
          $more.remove();
          return;
        }

        var articlesHTML = "",
          pagination = result.rslts.pagination;

        // append articles
        for (var i = 0; i < result.rslts.articles.length; i++) {
          var article = result.rslts.articles[i];

          articlesHTML += '<article><div class="module"><div class="dot"></div>'
            + '<div class="arrow"></div><time class="article-time"><span>'
            + timeline.toDate(article.articleCreateTime, 'yy-MM-dd HH:mm')
            + '</span></time><h3 class="article-title"><a rel="bookmark" href="'
            + Label.servePath + article.articlePermalink + '">'
            + article.articleTitle + '</a>';

          if (article.articlePutTop) {
            articlesHTML += '<sup>' + Label.topArticleLabel + '</sup>';
          }

          if (article.hasUpdated) {
            articlesHTML += '<sup><a href="'
              + Label.servePath + article.articlePermalink + '">' + Label.updatedLabel + '</a></sup>';
          }

          articlesHTML += '</h3><p>' + article.articleAbstract + '</p>'
            + '<span class="ico-tags ico" title="' + Label.tagLabel + '">';

          var articleTags = article.articleTags.split(",");
          for (var j = 0; j < articleTags.length; j++) {
            articlesHTML += '<a rel="category tag" href="' + Label.servePath
              + '/tags/' + encodeURIComponent(articleTags[j]) + '">' + articleTags[j] + '</a>';

            if (j < articleTags.length - 1) {
              articlesHTML += ",";
            }
          }

          articlesHTML += '</span>&nbsp;<span class="ico-author ico" title="' + Label.authorLabel + '">'
            + '<a rel="author" href="' + Label.servePath + '/authors/' + article.authorId + '">'
            + article.authorName + '</a></span>&nbsp;<span class="ico-comment ico" title="'
            + Label.commentLabel + '"><a rel="nofollow" href="' + Label.servePath + article.articlePermalink
            + '#b3logsolocomments" data-uvstatcmt="' + article.oId + '">0</a></span>&nbsp;<span class="ico-view ico" title="' + Label.viewLabel + '">'
            + '<a rel="nofollow" href="${servePath}${article.articlePermalink}">'
            + '<span data-uvstaturl="' + Label.servePath + article.articlePermalink + '">0</span>'
            + '</a></span></div></article>';
        }

        $more.before(articlesHTML).data("page", currentPage);
        // 最后一页处理
        if (pagination.paginationPageCount <= currentPage) {
          $more.remove();
        } else {
          $more.css("background", "none #60829F").text(Label.moreLabel);
        }

        $(window).resize();
        $(".module img").imagesLoaded(function() {
          $(window).resize();
        });

        Util.uvstat.renderStat()
        Util.uvstat.renderCmtStat()
      }
    });
  },
  toggleArchives: function(it, year) {
    $(".nav-abs li").each(function(i) {
      var $it = $(this);
      if (!$it.hasClass("year")) {
        $it.hide();
        if (year === $it.data("year") && $(it).hasClass("close")) {
          $it.show();
        }
      }
    });

    $(".nav-abs li.year").each(function() {
      if (parseInt($(this).text()) === year) {
        if ($(it).hasClass("close")) {
          it.className = "year open";
        } else {
          it.className = "year close";
        }
      } else {
        this.className = "year close";
      }
    });
  }
};

/*!
 * jQuery imagesLoaded plugin v2.1.1
 * http://github.com/desandro/imagesloaded
 *
 * MIT License. by Paul Irish et al.
 */

/*jshint curly: true, eqeqeq: true, noempty: true, strict: true, undef: true, browser: true */
/*global jQuery: false */

;
(function($, undefined) {
  'use strict';

  // blank image data-uri bypasses webkit log warning (thx doug jones)
  var BLANK = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';

  $.fn.imagesLoaded = function(callback) {
    var $this = this,
      deferred = $.isFunction($.Deferred) ? $.Deferred() : 0,
      hasNotify = $.isFunction(deferred.notify),
      $images = $this.find('img').add($this.filter('img')),
      loaded = [],
      proper = [],
      broken = [];

    // Register deferred callbacks
    if ($.isPlainObject(callback)) {
      $.each(callback, function(key, value) {
        if (key === 'callback') {
          callback = value;
        } else if (deferred) {
          deferred[key](value);
        }
      });
    }

    function doneLoading() {
      var $proper = $(proper),
        $broken = $(broken);

      if (deferred) {
        if (broken.length) {
          deferred.reject($images, $proper, $broken);
        } else {
          deferred.resolve($images);
        }
      }

      if ($.isFunction(callback)) {
        callback.call($this, $images, $proper, $broken);
      }
    }

    function imgLoadedHandler(event) {
      imgLoaded(event.target, event.type === 'error');
    }

    function imgLoaded(img, isBroken) {
      // don't proceed if BLANK image, or image is already loaded
      if (img.src === BLANK || $.inArray(img, loaded) !== -1) {
        return;
      }

      // store element in loaded images array
      loaded.push(img);

      // keep track of broken and properly loaded images
      if (isBroken) {
        broken.push(img);
      } else {
        proper.push(img);
      }

      // cache image and its state for future calls
      $.data(img, 'imagesLoaded', {
        isBroken: isBroken,
        src: img.src
      });

      // trigger deferred progress method if present
      if (hasNotify) {
        deferred.notifyWith($(img), [isBroken, $images, $(proper), $(broken)]);
      }

      // call doneLoading and clean listeners if all images are loaded
      if ($images.length === loaded.length) {
        setTimeout(doneLoading);
        $images.unbind('.imagesLoaded', imgLoadedHandler);
      }
    }

    // if no images, trigger immediately
    if (!$images.length) {
      doneLoading();
    } else {
      $images.bind('load.imagesLoaded error.imagesLoaded', imgLoadedHandler)
      .each(function(i, el) {
        var src = el.src;

        // find out if this image has been already checked for status
        // if it was, and src has not changed, call imgLoaded on it
        var cached = $.data(el, 'imagesLoaded');
        if (cached && cached.src === src) {
          imgLoaded(el, cached.isBroken);
          return;
        }

        // if complete is true and browser supports natural sizes, try
        // to check for image status manually
        if (el.complete && el.naturalWidth !== undefined) {
          imgLoaded(el, el.naturalWidth === 0 || el.naturalHeight === 0);
          return;
        }

        // cached images don't fire load sometimes, so we reset src, but only when
        // dealing with IE, or image is complete (loaded) and failed manual check
        // webkit hack from http://groups.google.com/group/jquery-dev/browse_thread/thread/eee6ab7b2da50e1f
        if (el.readyState || el.complete) {
          el.src = BLANK;
          el.src = src;
        }
      });
    }

    return deferred ? deferred.promise($this) : $this;
  };

})($);

(function() {
  Util.setTopBar()
  Util.buildTags("tagsSide");

  timeline.init();
})();
