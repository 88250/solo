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
 * @version 1.1.0.0, Apr 18, 2020
 */

import '../../../js/common'

window.utilOptions = {
  cmtCountCB: (element, cnt) => {
    if (cnt > 0) {
      element.parentElement.style.display = 'inline'
      element.parentElement.nextElementSibling.style.display = 'none'
    }
  },
}
/**
 * @description 皮肤脚本
 * @static
 */
window.Skin = {
  init: function () {
    if (Label.staticSite) {
      return
    }
    Util.initPjax()
  },
  _positionToc: function ($articleToc) {
    if ($articleToc.length === 1) {
      if ($(window).width() > 876) {
        $('.post__toc').
          css('left', $('.article .item__content').offset().left +
            $('.article .item__content').outerWidth() - 80)
      } else {
        $('.post__toc a').click(function () {
          $('.post__toc').hide()
        })
      }
    }
  },
  initArticle: function () {
    page.share()

    var $articleTocs = $('.vditor-reset.item__content--article').children().filter((index, item) => {
      return item.tagName.indexOf('H') === 0 && item.id
    })
    var $articleToc = $('.article__toc')
    var $articleProgress = $('.article__progress')

    Skin._positionToc($articleToc)

    $articleToc.find('a').click(function (event) {
      var id = $(this).attr('href')
      window.location.hash = id
      $(window).scrollTop($(id).offset().top - 60)
      event.preventDefault()
      event.stopPropagation()
      return false
    })

    $(window).unbind('scroll').scroll(function (event) {
      if ($articleProgress.length === 0) {
        return false
      }

      $articleProgress.attr('value', parseInt($(window).scrollTop())).
        attr('max', parseInt($('body').outerHeight() -
          $(window).height()))

      if ($(window).scrollTop() > 236) {
        $('.article__top').css('top', 0)
      } else {
        $('.article__top').css('top', -61)
        $('.article__share .item__qr').hide();
      }

      if ($('.article__toc li').length === 0) {
        return false
      }

      if ($(window).width() > 876) {
        if ($(window).scrollTop() > 975 && $(window).scrollTop() <
          $('.article').outerHeight() + 100) {
          $('.post__toc').show()
        } else {
          $('.post__toc').hide()
        }
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
      var scrollTop = $(window).scrollTop()
      for (var i = 0, iMax = toc.length; i < iMax; i++) {
        if (scrollTop < toc[i].offsetTop - 61) {
          $articleToc.find('li').removeClass('current')
          var index = i > 0 ? i - 1 : 0
          $articleToc.find('a[href="#' + toc[index].id + '"]').
            parent().
            addClass('current')
          break
        }
      }
      if (scrollTop >= toc[toc.length - 1].offsetTop - 61) {
        $articleToc.find('li').removeClass('current')
        $articleToc.find('li:last').addClass('current')
      }
    })

    $(window).scroll()

    $(window).resize(function () {
      Skin._positionToc($articleToc)
    })
  },
}
Skin.init()
