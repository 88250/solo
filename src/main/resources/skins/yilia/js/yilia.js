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
 * @version 1.3.0.0, Feb 27, 2019
 */

/**
 * @description yilia 皮肤脚本
 * @static
 */
var Yilia = {
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
    },
    share: function () {
      var $this = $('.share .fn-right')
      var $qrCode = $this.find('.icon-wechat')
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
    }
};

Yilia.init();