/*
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
 * @version 0.1.0.0, Jan 29, 2018
 */

/**
 * @description 皮肤脚本
 * @static
 */
var Skin = {
    init: function () {
      $('body').on('click', '.content-reset img', function () {
        window.open(this.src);
      });

      $(window).scroll(function () {
        if ($('#headerNav').length === 0) {
          return
        }
        if ($(window).scrollTop() > 64) {
          $('#headerNav').addClass('header__nav--fixed');
          $('.main').css('margin-top', '100px');
        } else {
          $('#headerNav').removeClass('header__nav--fixed');
          $('.main').css('margin-top', '50px');
        }
      });
      $(window).scroll();
    }
};
Skin.init();