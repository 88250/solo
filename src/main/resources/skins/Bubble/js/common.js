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
 * @version 1.0.1.0, Apr 11, 2020
 */

import '../../../js/common'

/**
 * @description 皮肤脚本
 * @static
 */
window.Skin = {
  init: function () {
    $('#headerDown').click(function () {
      $('html, body').animate({scrollTop: $(window).height()}, 300)
    })

    $(window).scroll(function (event) {
      $('.fn__progress').attr('value', parseInt($(window).scrollTop())).
        attr('max', parseInt($('body').outerHeight() -
          $(window).height()))

      if ($(window).scrollTop() > $(window).height() / 2 - 20) {
        $('.side__menu').addClass('side__menu--edge')
        $('#sideTop').removeClass('side__top--bottom')
      } else {
        $('.side__menu').removeClass('side__menu--edge')
        $('#sideTop').addClass('side__top--bottom')
      }
    })

    $('.side__menu').click(function () {
      $('.side__main').addClass('side__main--show').show()
    })
    $('.side__bg, .side__close').click(function () {
      $('.side__main').removeClass('side__main--show')
      setTimeout(function () {
        $('.side__main').hide()
      }, 1000)
    })
    $('#sideTop').click(function () {
      if ($(this).hasClass('side__top--bottom')) {
        Util.goBottom()
      } else {
        Util.goTop()
      }
    })

    var timer = 0
    var blogTitle = $('.header__h1').text()
    document.addEventListener('visibilitychange', function () {
      if (timer) clearTimeout(timer)
      if (document.hidden) {
        timer = setTimeout(function () {
          document.title = '(◍´꒳`◍)' + ' - ' + blogTitle
        }, 500)
      } else {
        document.title = '(*´∇｀*) 欢迎回来！'
        timer = setTimeout(function () {
          document.title = blogTitle
        }, 1000)
      }
    }, false)

    new Ribbons({
      colorSaturation: '60%',
      colorBrightness: '50%',
      colorAlpha: 0.5,
      colorCycleSpeed: 5,
      verticalPosition: 'random',
      horizontalSpeed: 200,
      ribbonCount: 3,
      strokeSize: 0,
      parallaxAmount: -0.2,
      animateSections: true,
    })

    if ($('#comments').length === 1) {
      return
    } else {
      $(window).scroll()
    }

    $('.header').circleMagic({
      clearOffset: 0.3,
      color: 'rgba(255,255,255, .2)',
      density: 0.2,
      radius: 15,
    })
  },
  initArticle: function () {
    page.share()

    initCanvas('articleTop')

    if ($(window).width() >= 768) {
      $('.post__toc').css({
        left: document.querySelector('.article__content').
          getBoundingClientRect().right + 20,
        right: 'auto',
        display: 'block',
      })
    } else {
      $('.side__top--toc').click(function () {
        $('.post__toc').slideToggle()
      })
    }
    var $articleTocs = $('.vditor-reset.article__content').children().filter((index, item) => {
      return item.tagName.indexOf('H') === 0 && item.id
    })
    var $articleToc = $('.article__toc')

    $articleToc.find('a').click(function (event) {
      var id = $(this).attr('href')
      window.location.hash = id
      $(window).scrollTop($(id).offset().top)
      if ($(window).width() < 768) {
        $('.post__toc').slideToggle()
      }
      event.preventDefault()
      event.stopPropagation()
      return false
    })

    $(window).scroll(function (event) {
      if ($('.article__toc li').length === 0) {
        return false
      }

      // 界面各种图片加载会导致帖子目录定位
      var toc = []
      $articleTocs.each(function (i) {
        toc.push({
          id: this.id,
          offsetTop: $(this).offset().top,
        })
      })

      // 当前目录样式
      var scrollTop = $(window).scrollTop() + 10
      for (var i = 0, iMax = toc.length; i < iMax; i++) {
        if (scrollTop < toc[i].offsetTop) {
          $articleToc.find('li').removeClass('current')
          var index = i > 0 ? i - 1 : 0
          $articleToc.find('a[href="#' + toc[index].id + '"]').
            parent().
            addClass('current')
          break
        }
      }
      if (scrollTop >= toc[toc.length - 1].offsetTop) {
        $articleToc.find('li').removeClass('current')
        $articleToc.find('li:last').addClass('current')
      }
    })
    $(window).scroll()
  },
}

$(document).ready(function () {
  Skin.init()
})
