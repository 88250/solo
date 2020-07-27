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
 * @description Finding 皮肤脚本
 * @static
 */
window.Finding = {
  /**
   * @description 页面初始化
   */
  init: function () {
    Util.killIE();
    this._initToc();
    $(".scroll-down").click(function (event) {
      event.preventDefault();

      var $this = $(this),
        $htmlBody = $('html, body'),
        offset = ($this.attr('data-offset')) ? $this.attr('data-offset') : false,
        toMove = parseInt(offset);

      $htmlBody.stop(true, false).animate({ scrollTop: ($(this.hash).offset().top + toMove) }, 500);
    });

    $('body').append('<a class="icon-gotop fn-none" href="javascript:Util.goTop()"></a>' +
      '<span class="menu-button icon-menu"><span class="word">Menu</span></span>');

    $(".menu-button").click(function (event) {
      event.stopPropagation();
      $("body").toggleClass("nav-opened nav-closed");
    });


    $(window).scroll(function () {
      if ($(window).scrollTop() > $('.main-header').height()) {
        $(".icon-gotop").show();
      } else {
        $(".icon-gotop").hide();
      }
    });
  },
  /**
   * 文章目录
   * @returns {undefined}
   */
  _initToc: function () {
    if ($('.article__toc li').length === 0) {
      return;
    }

    if ($(window).width() > 500) {
      $("body").toggleClass("nav-opened nav-closed");
    }
  }
};

Finding.init();
