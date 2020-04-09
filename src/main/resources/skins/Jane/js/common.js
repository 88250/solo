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
