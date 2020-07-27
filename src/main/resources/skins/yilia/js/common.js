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
 * @fileoverview util and every page should be used.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.0.0, Jan 18, 2019
 */

import '../../../js/common'
/**
 * @description yilia 皮肤脚本
 * @static
 */
window.Yilia = {
  /**
   * @description 页面初始化
   */
  init: function () {
    Util.killIE();

    this._initToc();
    this.resetTags();

    $(window).scroll(function () {
      if ($("article").length > 0 && $("article.post").length === 0) {
        $("article:not(.show)").each(function () {
          if ($(this).offset().top <= $(window).scrollTop() + $(window).height() - $(this).height() / 7) {
            $(this).addClass("show");
          }
        });
      }

      if ($(window).scrollTop() > $(window).height()) {
        $(".icon-goup").show();
      } else {
        $(".icon-goup").hide();
      }

      if ($("article.post").length === 1) {
        $("article.post").addClass('show');
      }
    });

    $(window).scroll();
  },
  _initToc: function () {
    if ($('.article__toc li').length === 0) {
      return false;
    }
    $('.side .toc-btn').show();
  },
  resetTags: function () {
    $("a.tag").each(function (i) {
      $(this).addClass("color" + Math.ceil(Math.random() * 4));
    });
  }
};

Yilia.init();
