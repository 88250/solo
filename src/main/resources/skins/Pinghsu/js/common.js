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
    var header = new Headroom($('header')[0], {
      tolerance: 0,
      offset: 70,
      classes: {
        initial: 'header',
        pinned: 'header--down',
        unpinned: 'header--up',
        top: 'header',
        notTop: 'header',
        bottom: 'header',
        notBottom: 'header',
      },
    })
    header.init()

    $('.header__nav a').each(function () {
      if (this.href === location.href) {
        this.className = 'current'
      }
    }).click(function () {
      $('.header__nav a').removeClass('current')
      if (this.href === location.href) {
        this.className = 'current'
      }
    })

    if (Label.staticSite) {
      return
    }

    Util.initPjax(function () {
      if ($('.post__fix').length === 0) {
        $('body').addClass('body--gray')
      } else {
        $('body').removeClass('body--gray')
      }
      $('.header__nav a').each(function () {
        $('.header__nav a').removeClass('current')
        if (this.href === location.href) {
          this.className = 'current'
        }
      })

      Skin._initToc()
    })
  },
  _initToc: function () {
    if ($('.article__toc').length === 0) {
      return
    }
    $('.post__toc').
      css('left', $('.post').offset().left + $('.post').outerWidth())

    var $articleTocs = $('.post .vditor-reset').
      children().
      filter((index, item) => {
        return item.tagName.indexOf('H') === 0 && item.id
      })
    var $articleToc = $('.article__toc')

    $(window).unbind('scroll').scroll(function (event) {
      if ($('.article__toc li').length === 0) {
        return false
      }

      if ($(window).scrollTop() > 72) {
        $('.post__toc').show()
      } else {
        $('.post__toc').hide()
        return
      }

      // 界面各种图片加载会导致帖子目录定位
      var toc = []
      $articleTocs.each(function (i) {
        toc.push({
          id: this.id,
          offsetTop: this.offsetTop,
        })
      })

      // 当前目录样式
      var scrollTop = $(window).scrollTop()
      for (var i = 0, iMax = toc.length; i < iMax; i++) {
        if (scrollTop < toc[i].offsetTop - 20) {
          $articleToc.find('li').removeClass('current')
          var index = i > 0 ? i - 1 : 0
          $articleToc.find('a[href="#' + toc[index].id + '"]').
            parent().
            addClass('current')
          break
        }
      }
      if (scrollTop >= toc[toc.length - 1].offsetTop - 20) {
        $articleToc.find('li').removeClass('current')
        $articleToc.find('li:last').addClass('current')
      }
    })

    $(window).scroll()
  },
  _initShare: function () {
    var $this = $('.post__share')
    if ($this.length === 0) {
      return
    }
    var $qrCode = $this.find('.post__code')
    var shareURL = $qrCode.data('url')
    var avatarURL = $qrCode.data('avatar')
    var title = encodeURIComponent($qrCode.data('title') + ' - ' +
      $qrCode.data('blogtitle')),
      url = encodeURIComponent(shareURL)

    var urls = {}
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
        const $qrImg = $('.qrcode')
        if (typeof QRious === 'undefined') {
          Util.addScript(Label.staticServePath + '/js/lib/qrious.min.js',
            'qriousScript')
        }
        if ($qrImg.css('background-image') === 'none') {
          const qr = new QRious({
            padding: 0,
            element: $qrCode[0],
            value: shareURL,
            size: 99,
          })
          $qrImg.css('background-image', `url(${qr.toDataURL('image/jpeg')})`)
        } else {
          $qrImg.slideToggle()
        }
        return false
      }

      window.open(urls[key], '_blank', 'top=100,left=200,width=648,height=618')
    })
  },
  initArticle: function () {
    var postSharer = new Headroom($('.post__fix')[0], {
      tolerance: 0,
      offset: 48,
      classes: {
        initial: 'post__fix',
        pinned: 'post__fix--pinned',
        unpinned: 'post__fix--unpinned',
        top: 'post__fix',
        notTop: 'post__fix',
        bottom: 'post__fix',
        notBottom: 'post__fix',
      },
    })
    postSharer.init()

    Skin._initShare()
    Skin._initToc()
  },
}
Skin.init()
