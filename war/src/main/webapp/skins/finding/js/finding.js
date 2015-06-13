/*
 * Copyright (c) 2010-2015, b3log.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @fileoverview util and every page should be used.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.0, Jun 13, 2015
 */

/**
 * @description Finding 皮肤脚本
 * @static
 */
var Finding = {
    /**
     * @description 页面初始化
     */
    init: function () {
        Util.killIE();
        $(".scroll-down").click(function (event) {
            event.preventDefault();

            var $this = $(this),
                    $htmlBody = $('html, body'),
                    offset = ($this.attr('data-offset')) ? $this.attr('data-offset') : false,
                    toMove = parseInt(offset);

            $htmlBody.stop(true, false).animate({scrollTop: ($(this.hash).offset().top + toMove)}, 500);
        });

        $('body').click(function (event) {
            if ($(event.target).closest('.nav').length === 0 && $("body").hasClass('nav-opened')) {
                $("body").removeClass('nav-opened').addClass('nav-closed');
            }
        });

        $(".menu-button, .nav-close").click(function (event) {
            event.stopPropagation();
            $("body").toggleClass("nav-opened nav-closed");
        });
    },
    share: function () {
        $(".share span").click(function () {
            var key = $(this).data("type");
            var title = encodeURIComponent($.trim($(".post-title a").text()) + " - " + $.trim($(".post-author span").text())),
                    url = $(".post-title a").attr('href'),
                    pic = $(".post-content img:eq(0)").attr("src");
            var urls = {};
            urls.tencent = "http://share.v.t.qq.com/index.php?c=share&a=index&title=" + title +
                    "&url=" + url + "&pic=" + pic;
            urls.weibo = "http://v.t.sina.com.cn/share/share.php?title=" +
                    title + "&url=" + url + "&pic=" + pic;
            urls.google = "https://plus.google.com/share?url=" + url;
            urls.twitter = "https://twitter.com/intent/tweet?status=" + title + " " + url;
            window.open(urls[key], "_blank", "top=100,left=200,width=648,height=618");
        });
    }
};

Finding.init();