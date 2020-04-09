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
  init: function () {
    var $article__toc = $('.article__toc')
    $(window).scroll(function () {
      if ($article__toc.length === 1) {
        if ($('.article__bottom').offset().top < $(window).scrollTop()) {
          $article__toc.hide()
        } else {
          $article__toc.show()
        }
      }

      if ($('#headerNav').length === 0) {
        return
      }
      if ($(window).scrollTop() > 64) {
        $('#headerNav').addClass('header__nav--fixed')
        $('.main').css('margin-top', '100px')
      } else {
        $('#headerNav').removeClass('header__nav--fixed')
        $('.main').css('margin-top', '50px')
      }
    })
    $(window).scroll()

    Skin.initToc()

    if (Label.staticSite) {
      return
    }

    Util.initPjax(function () {
      Skin.initToc()
    })
  },
  initTags: function () {
    var $tags = $('#tags')
    var tagsArray = $tags.find('.tag')
    // 根据引用次数添加样式，产生云效果
    var max = parseInt(tagsArray.first().data('count'))
    var distance = Math.ceil(max / 5)
    for (var i = 0; i < tagsArray.length; i++) {
      var count = parseInt($(tagsArray[i]).data('count'))
      // 算出当前 tag 数目所在的区间，加上 class
      for (var j = 0; j < 5; j++) {
        if (count > j * distance && count <= (j + 1) * distance) {
          tagsArray[i].className = 'tag tag__level' + j
          break
        }
      }
    }

    // 按字母或者中文拼音进行排序
    $tags.html(tagsArray.get().sort(function (a, b) {
      var valA = $(a).text().toLowerCase()
      var valB = $(b).text().toLowerCase()
      // 对中英文排序的处理
      return valA.localeCompare(valB)
    }))
  },
  initArticle: function () {
    if ($('#articleShare').length === 0) {
      return
    }
    Skin._share('#articleShare')
    Skin._share('#articleSideShare')
    Skin._share('#articleBottomShare')

    var $postSide = $('.post__side')
    if ($(window).height() >= $('.post').height()) {
      $postSide.css('opacity', 1)
    }
    $postSide.css('left', (($('.post').offset().left - 20) / 2 - 27) + 'px')

    var sideAbsoluteTop = ($(window).height() - 249) / 2 + 125
    var beforScrollTop = $(window).scrollTop()
    $(window).scroll(function () {
      if ($('#articleShare').length === 0) {
        return
      }
      var scrollTop = $(window).scrollTop()
      var bottomTop = $('.article__bottom').offset().top
      if (scrollTop > 65) {
        $postSide.css('opacity', 1)

        if (beforScrollTop - scrollTop > 0) {
          // up
          $('.header').addClass('header--fixed').css({'top': '0'})
          $('.main').css('padding-top', '64px')
          if ($(window).height() <= $('.post').height() && scrollTop <
            bottomTop - $(window).height()) {
            $('.article__toolbar').css({
              'bottom': 0,
              'opacity': 1,
            })
          }
        } else if (beforScrollTop - scrollTop < 0) {
          // down
          $('.header').css({'top': '-64px'}).removeClass('header--fixed')
          $('.main').css('padding-top', '0')
          $('.article__toolbar').css({
            'bottom': '-44px',
            'opacity': 0,
          })
        }

      } else {
        if ($(window).height() <= $('.post').height()) {
          $postSide.css('opacity', 0)
        }

        $('.header').removeClass('header--fixed').css('top', '-64px')
        $('.main').css('padding-top', '0')
      }

      if (scrollTop > bottomTop - $(window).height()) {
        if (bottomTop < $(window).height()) {
          $postSide.css({
            'position': 'absolute',
            'top': (bottomTop - 125) + 'px',
          })
        } else {
          $postSide.css({
            'position': 'absolute',
            'top': (bottomTop - sideAbsoluteTop) + 'px',
          })
        }
      } else {
        $postSide.css({
          'position': 'fixed',
          'top': '50%',
        })
      }

      beforScrollTop = scrollTop
    })

    $(window).scroll()
  },
  _share: function (id) {
    var $this = $(id)
    var $qrCode = $this.find('.article__code')
    var shareURL = $qrCode.data('url')
    var avatarURL = $qrCode.data('avatar')
    var title = encodeURIComponent(
      $qrCode.data('title') + ' - ' + $qrCode.data('blogtitle')),
      url = encodeURIComponent(shareURL)

    var urls = {}
    urls.tencent = 'http://share.v.t.qq.com/index.php?c=share&a=index&title=' +
      title +
      '&url=' + url + '&pic=' + avatarURL
    urls.weibo = 'http://v.t.sina.com.cn/share/share.php?title=' +
      title + '&url=' + url + '&pic=' + avatarURL
    urls.qqz = 'https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url='
      + url + '&sharesource=qzone&title=' + title + '&pics=' + avatarURL
    urls.twitter = 'https://twitter.com/intent/tweet?status=' + title + ' ' +
      url

    $this.find('span').click(function () {
      var key = $(this).data('type')

      if (!key) {
        return
      }

      if (key === 'wechat') {
        if (typeof QRious === 'undefined') {
          Util.addScript(Label.staticServePath + '/js/lib/qrious.min.js',
            'qriousScript')
        }
        if ($qrCode.css('background-image') !== "none") {
          $qrCode.slideToggle()
        } else {
          const qr = new QRious({
            padding: 0,
            element: $qrCode[0],
            value: shareURL,
            size: 99,
          })
          $qrCode.css('background-image', `url(${qr.toDataURL('image/jpeg')})`)
        }
        return false
      }

      window.open(urls[key], '_blank', 'top=100,left=200,width=648,height=618')
    })
  },
  initToc: function () {
    if ($('.article__toc').length !== 0 && $(window).width() > 1000) {
      $('.article__toc').animate({
        'left': ($('.post').outerWidth() + $('.post').offset().left) + 'px',
      }, 600)
    } else {
      $('.article__toc').hide()
    }
  },
}
Skin.init()
