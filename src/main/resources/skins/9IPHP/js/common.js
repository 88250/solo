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
 * @description 皮肤脚本
 * @static
 */
window.Skin = {
  _initCommon: function ($goTop) {
    var $banner = $('header .banner'),
      $navbar = $('header .navbar')

    $(window).scroll(function () {
      if ($(window).scrollTop() > 125) {
        $goTop.show()
      } else {
        $goTop.hide()
      }

      if ($(window).width() < 701) {
        return false
      }

      if ($(window).scrollTop() > $banner.height()) {
        $navbar.addClass('pin')
        $('.main-wrap').parent().css('margin-top', '81px')
        $('.article__toc').css('position', 'fixed')
      } else {
        $navbar.removeClass('pin')
        $('.main-wrap').parent().css('margin-top', '0')
        $('.article__toc').css('position', 'inherit')
      }
    })

    $(window).scroll()
  },
  init: function () {
    this._initCommon($('.icon-up'))

    $('.navbar nav a').each(function () {
      if (this.href === location.href) {
        this.className = 'current'
      }
    })

    $('.responsive .list a').each(function () {
      if (this.href === location.href) {
        $(this).parent().addClass('current')
      }
    })

    $('.responsive .icon-list').click(function () {
      $('.responsive .list').slideToggle()
    })
  },
  initToc: function () {
    var $articleToc = $('.article__toc')
    if ($articleToc.length === 0) {
      return false
    }

    $articleToc.css({
      width: $articleToc.parent().width(),
      left: $articleToc.parent().offset().left,
    }).find('a').click(function () {
      $articleToc.find('li').removeClass('toc--current')
      $(this).parent().addClass('toc--current')
      var id = $(this).attr('href')
      setTimeout(function () {
        $(window).scrollTop($(id).offset().top - 60)
      })
    })
  },
}

$(document).ready(function () {
  Skin.init()
})
