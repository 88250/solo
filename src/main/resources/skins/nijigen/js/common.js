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
    $(window).scroll(function () {
      if ($(window).scrollTop() > 125) {
        $goTop.show()
      } else {
        $goTop.hide()
      }

      if ($('.side .article__toc').length > 0 && $(window).width() > 768) {
        if ($(window).scrollTop() > 50) {
          $('.side').css('position', 'fixed')
        } else {
          $('.side').css('position', 'initial')
        }
      }
    })
  },
  _initAnimation: function () {
    if (!('IntersectionObserver' in window)) {
      $('.item').addClass('item--active')
      return false
    }

    if (window.imageIntersectionObserver) {
      window.imageIntersectionObserver.disconnect()
      $('.item').each(function () {
        window.imageIntersectionObserver.observe(this)
      })
    } else {
      window.imageIntersectionObserver = new IntersectionObserver(
        function (entries) {
          entries.forEach(function (entrie) {
            if (typeof entrie.isIntersecting === 'undefined'
              ? entrie.intersectionRatio !== 0 : entrie.isIntersecting) {
              $(entrie.target).addClass('item--active')
            } else {
              if ($(entrie.target).closest('.side').length === 1 ||
                $(entrie.target).closest('#articlePage').length === 1 ||
                $(entrie.target).outerHeight() > 768) {
                return
              }
              $(entrie.target).removeClass('item--active')
            }
          })
        })
      $('.item').each(function () {
        window.imageIntersectionObserver.observe(this)
      })
    }
  },
  init: function () {

    Skin._initAnimation()

    this._initCommon($('.icon__up'))

    $('.header__nav a, .header__m a').each(function () {
      if (this.href === location.href) {
        this.className = 'current'
      }
    }).click(function () {
      $('.header__nav a, .header__m a').removeClass('current')
      this.className = 'current'
      $('.header__m .module__list').hide()
    })

    $('.header__logo').click(function () {
      $('.header__nav a, .header__m a').removeClass('current')
    })

    if (Label.staticSite) {
      return
    }
    Util.initPjax(function () {
      Skin._initAnimation()
    })
  },
  _initArticleCommon: function () {
    if ($(window).width() > 768) {
      if ($('.article__toc li').length === 0) {
        $('.side').css({
          height: 'auto',
          position: 'initial',
        })
        return
      }

      $('#articlePage').width($('.main').width() - 310)
      $('.side').css({
        right: ($(window).width() - $('.main').width()) / 2,
        position: 'fixed',
        overflow: 'auto',
        height: $(window).height() - 30,
        top: 30,
      })
      $(window).scroll()
      $('.side').scrollTop(0)
    }
  },
  initArticle: function () {
    this._initArticleCommon()

    setTimeout(function () {
      if ($('#externalRelevantArticlesWrap li').length === 0) {
        $('#externalRelevantArticlesWrap').next().remove()
        $('#externalRelevantArticlesWrap').remove()
      }

      if ($('#relevantArticlesWrap li').length === 0) {
        $('#relevantArticlesWrap').prev().remove()
        $('#relevantArticlesWrap').remove()
      }

      if ($('#randomArticlesWrap li').length === 0) {
        $('#randomArticlesWrap').prev().remove()
        $('#randomArticlesWrap').remove()
      }
    }, 1000)
  },
}
Skin.init()
