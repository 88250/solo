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
 * @version 0.1.0.0, Feb 27, 2019
 */

/**
 * @description 皮肤脚本
 * @static
 */
var Skin = {
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
  },
  _initToc: function () {
    if ($('.article__toc').length === 0) {
      return
    }
    $('.post__toc').css('left', $('.post').offset().left + $('.post').outerWidth())

    var $articleTocs = $('.vditor-reset [id^=b3_solo_h]'),
      $articleToc = $('.article__toc');

    $(window).unbind('scroll').scroll(function (event) {
      if ($('.article__toc li').length === 0) {
        return false;
      }

      if ($(window).scrollTop() > 72) {
        $('.post__toc').show()
      } else {
        $('.post__toc').hide()
        return
      }

      // 界面各种图片加载会导致帖子目录定位
      var toc = [];
      $articleTocs.each(function (i) {
        toc.push({
          id: this.id,
          offsetTop: this.offsetTop
        });
      });

      // 当前目录样式
      var scrollTop = $(window).scrollTop();
      for (var i = 0, iMax = toc.length; i < iMax; i++) {
        if (scrollTop < toc[i].offsetTop - 20) {
          $articleToc.find('li').removeClass('current');
          var index = i > 0 ? i - 1 : 0;
          $articleToc.find('a[href="#' + toc[index].id + '"]').parent().addClass('current');
          break;
        }
      }
      if (scrollTop >= toc[toc.length - 1].offsetTop - 20) {
        $articleToc.find('li').removeClass('current');
        $articleToc.find('li:last').addClass('current');
      }
    });

    $(window).scroll()
  },
  _initShare: function () {
    var $this = $('.post__share')
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
        if ($qrCode.find('canvas').length === 0) {
          $.ajax({
            method: 'GET',
            url: Label.staticServePath +
            '/js/lib/jquery.qrcode.min.js',
            dataType: 'script',
            cache: true,
            success: function () {
              $qrCode.qrcode({
                width: 128,
                height: 128,
                text: shareURL,
              })
            },
          })
        } else {
          $qrCode.find('canvas').slideToggle()
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