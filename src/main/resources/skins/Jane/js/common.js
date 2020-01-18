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
  initToc: function () {
    if ($('.article__toc li').length > 0 && $(window).width() > 768) {
      $('.article__toc').css({
        right: '50px',
        'border-right': '1px solid #fff',
        opacity: 1,
      })
      $('#pjax.wrapper').css({
        'max-width': '968px',
        'padding-right': '270px',
      })
    } else {
      $('#pjax.wrapper').removeAttr('style')
    }
  },
  init: function () {
    Skin.initToc()

    $('.header a').each(function () {
      if (this.href === location.href) {
        this.className = 'current vditor-tooltipped vditor-tooltipped__w'
      }
    }).click(function () {
      $('.header a').removeClass('current')
      this.className = 'current vditor-tooltipped vditor-tooltipped__w'
    })

    if (Label.staticSite) {
      return
    }
    Util.initPjax(function () {
      $('.header a').each(function () {
        if (this.href === location.href) {
          this.className = 'current vditor-tooltipped vditor-tooltipped__w'
        } else {
          this.className = 'vditor-tooltipped vditor-tooltipped__w'
        }
      })

      Skin.initToc()
    })
  },
}
Skin.init()
