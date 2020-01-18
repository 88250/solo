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
