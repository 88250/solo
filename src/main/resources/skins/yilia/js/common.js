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
