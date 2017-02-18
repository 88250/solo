/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
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
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 0.1.0.0, Feb 17, 2017
 */

/**
 * @description 皮肤脚本
 * @static
 */
var Skin = {
    _initCommon: function ($goTop) {
        $('body').on('click', '.content-reset img', function () {
            window.open(this.src);
        });

        var $banner = $('header .banner'),
        $navbar = $('header .navbar');

        $(window).scroll(function () {
            if ($(window).scrollTop() > 125) {
                $goTop.show();
            } else {
                $goTop.hide();
            }

            if ($(window).width() < 701) {
                return false;
            }

            if ($(window).scrollTop() > $banner.height()) {
                $navbar.addClass('pin');
                $('.main-wrap').parent().css('margin-top', '86px')
            } else {
                $navbar.removeClass('pin');
                $('.main-wrap').parent().css('margin-top', '0')
            }
        });
    },
    init: function () {
        this._initCommon($('.icon-up'));

        $('.navbar nav a').each(function () {
            if (this.href === location.href) {
                this.className = 'current'
            }
        });

        $('.responsive .list a').each(function () {
            if (this.href === location.href) {
                $(this).parent().addClass('current');
            }
        });

        $('.responsive .icon-list').click(function () {
            $('.responsive .list').slideToggle();
        });
    },
    _initArticleCommon: function () {
        // TOC
        if ($('.b3-solo-list li').length > 0 && $(window).width() > 1000) {
            // add color to sidebar menu
            $('.sidebar-toggle').addClass('has-toc');

            // append toc to sidebar menu
            var articleTocHTML = '<ul><li class="current" data-tab="toc">' + Label.tocLabel + '</li><li data-tab="site">' + Label.siteViewLabel + '</li></ul><section></section>';
            $('.sidebar').prepend(articleTocHTML);
            var $sectionF = $('.sidebar section:first').html($('.b3-solo-list')),
                    $sectionL = $('.sidebar section:last');
            $sectionF.height($(window).height() - 90);
            // 切换 tab
            $('.sidebar > ul > li').click(function () {
                if ($(this).data('tab') === 'toc') {
                    $sectionL.animate({
                        "opacity": '0',
                        "top": '-50px'
                    }, 300, function () {
                        $sectionF.show().css('top', '-50px');
                        $sectionF.animate({
                            "opacity": '1',
                            "top": '0'
                        }, 300);
                    });
                } else {
                    $sectionF.animate({
                        "opacity": '0',
                        "top": '-50px'
                    }, 300, function () {
                        $sectionF.hide().css('top', '-50px');
                        $sectionL.animate({
                            "opacity": '1',
                            "top": '0'
                        }, 300);
                    });
                }
                $('.sidebar > ul > li').removeClass('current');
                $(this).addClass('current');
            });
        }
    },
    initArticle: function () {
        this._initArticleCommon();
    }
};
Skin.init();