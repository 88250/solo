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
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.0.1.0, Jan 12, 2020
 */

/**
 * @description Util
 * @static
 */
var Util = {
  uvstat: undefined,
  /**
   * 初始化浏览数
   */
  initViewCnt: function () {
    Util.uvstat = new Uvstat()
    Util.uvstat.addStat()
    Util.uvstat.renderStat()
  },
  /**
   * 是否为文章页面
   * @param href url 地址
   * @returns {boolean}
   */
  isArticlePage: function (href) {
    var isArticle = true
    if (href.indexOf(Label.servePath + '/tags/') > -1) {
      isArticle = false
    }
    if (href.indexOf(Label.servePath + '/tags.html') > -1) {
      isArticle = false
    }
    if (href.indexOf(Label.servePath + '/category/') > -1) {
      isArticle = false
    }
    if (href.indexOf(Label.servePath + '/archives.html') > -1) {
      isArticle = false
    }
    if (href.indexOf(Label.servePath + '/archives/') > -1) {
      isArticle = false
    }
    if (href.indexOf(Label.servePath + '/links.html') > -1) {
      isArticle = false
    }
    if (href === Label.servePath) {
      isArticle = false
    }
    if (/^[0-9]*$/.test(href.replace(Label.servePath + '/', ''))) {
      isArticle = false
    }
    return isArticle
  },
  /**
   * 初始化 Pjax
   * @param cb 除文章外的其他页面加载回调
   */
  initPjax: function (cb) {
    if ($('#pjax').length === 1) {
      $.pjax({
        selector: 'a',
        container: '#pjax',
        show: '',
        cache: false,
        storage: true,
        titleSuffix: '',
        filter: function (href, element) {
          if (!href) {
            return true
          }
          if (element.getAttribute('target') === '_blank') {
            return true
          }
          if (href === Label.servePath + '/rss.xml' ||
            href.indexOf(Label.servePath + '/admin-index.do') > -1) {
            return true
          }
          // 目录
          if (href.indexOf('#') === 0) {
            return true
          }
          // 自定义导航
          if (element.href.indexOf(Label.servePath) > -1) {
            return false
          }
          return true
        },
        callback: function () {
          Util.parseMarkdown()
          Util.parseLanguage()
          Util.uvstat.addStat()
          Util.uvstat.renderStat()
          cb && cb()
        },
      })
      NProgress.configure({showSpinner: false})
      $('#pjax').bind('pjax.start', function () {
        NProgress.start()
      })
      $('#pjax').bind('pjax.end', function () {
        window.scroll(window.scrollX, 0)
        NProgress.done()
      })
    }
  },
  /**
   * 图片预览
   */
  previewImg: function () {
    $('body').on('click', '.vditor-reset img', function () {
      if ($(this).hasClass('prevent')) {
        return
      }
      window.open(this.src)
    })
  },
  /**
   * 异步添加 css
   * @param url css 文件访问地址
   * @param id css 文件标示
   */
  addStyle: function (url, id) {
    if (!document.getElementById(id)) {
      var styleElement = document.createElement('link')
      styleElement.id = id
      styleElement.setAttribute('rel', 'stylesheet')
      styleElement.setAttribute('type', 'text/css')
      styleElement.setAttribute('href', url)
      document.getElementsByTagName('head')[0].appendChild(styleElement)
    }
  },
  /**
   * 异步添加 js
   * @param url js 文件访问地址
   * @param id js 文件标示
   */
  addScript: function (url, id) {
    if (!document.getElementById(id)) {
      var xhrObj = new XMLHttpRequest()
      xhrObj.open('GET', url, false)
      xhrObj.setRequestHeader('Accept',
        'text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01')
      xhrObj.send('')
      var scriptElement = document.createElement('script')
      scriptElement.id = id
      scriptElement.type = 'text/javascript'
      scriptElement.text = xhrObj.responseText
      document.getElementsByTagName('head')[0].appendChild(scriptElement)
    }
  },
  /*
  * @description 解析语法高亮
  */
  parseLanguage: function () {
    Vditor.highlightRender({
      style: Label.hljsStyle,
      enable: !Label.luteAvailable,
    }, document)
  },
  /**
   * 按需加载数学公式、流程图、代码复制、五线谱、多媒体、图表
   * @returns {undefined}
   */
  parseMarkdown: function () {

    if (typeof Vditor === 'undefined') {
      Util.addScript(
        'https://cdn.jsdelivr.net/npm/vditor@2.0.15/dist/method.min.js',
        'vditorPreviewScript')
    }

    Vditor.codeRender(document.body, Label.langLabel)
    if (Label.luteAvailable) {
      Vditor.mathRenderByLute(document.body)
    } else {
      Vditor.mathRender(document.body)
    }

    Vditor.abcRender()
    Vditor.chartRender()
    Vditor.mediaRender(document.body)
    Vditor.mermaidRender(document.body)
    document.querySelectorAll('.vditor-reset').forEach((e) => {
      Vditor.speechRender(e, Label.langLabel)
    })
  },
  /**
   * @description IE6/7，跳转到 kill-browser 页面
   */
  killIE: function (ieVersion) {
    var addKillPanel = function () {
      try {
        var left = ($(window).width() - 781) / 2,
          top1 = ($(window).height() - 680) / 2
        var killIEHTML = '<div class="killIEIframe" style=\'display: block; height: 100%; width: 100%; position: fixed; background-color: rgb(0, 0, 0); opacity: 0.6;filter: alpha(opacity=60); top: 0px;z-index:110\'></div>'
          + '<iframe class="killIEIframe" style=\'left:' + left + 'px;z-index:120;top: ' + top1 +
          'px; position: fixed; border: 0px none; width: 781px; height: 680px;\' src=\'' +
          Label.servePath + '/kill-browser\'></iframe>'
        $('body').append(killIEHTML)
      } catch (e) {
        var left = 10, top1 = 0
        var killIEHTML = '<div class="killIEIframe" style=\'display: block; height: 100%; width: 100%; position: fixed; background-color: rgb(0, 0, 0); opacity: 0.6;filter: alpha(opacity=60); top: 0px;z-index:110\'></div>'
          + '<iframe class="killIEIframe" style=\'left:' + left + 'px;z-index:120;top: ' + top1 +
          'px; position: fixed; border: 0px none; width: 781px; height: 680px;\' src=\'' +
          Label.servePath + '/kill-browser\'></iframe>'
        document.body.innerHTML = document.body.innerHTML + killIEHTML
      }
    }

    var ua = navigator.userAgent.split('MSIE')[1]
    if (ua) {
      if (!ieVersion) {
        ieVersion = 7
      }
      if (parseFloat(ua.split(';')) <= ieVersion) {
        addKillPanel()
      }
    }
  },
  /**
   * @description topbar 相关事件
   */
  setTopBar: function () {
    var $top = $('#top')
    if ($top.length === 1) {
      var $showTop = $('#showTop')
      $showTop.click(function () {
        $top.slideDown()
        $showTop.hide()
      })
      $('#hideTop').click(function () {
        $top.slideUp()
        $showTop.show()
      })
    }
  },
  /**
   * @description 回到顶部
   */
  goTop: function () {
    $('html, body').animate({scrollTop: 0}, 800)
  },
  /**
   * @description 回到底部
   */
  goBottom: function (bottom) {
    if (!bottom) {
      bottom = 0
    }
    $('html, body').
      animate({scrollTop: $(document).height() - $(window).height() - bottom},
        800)
  },
  /**
   * @description 页面初始化执行的函数
   */
  init: function () {
    Util.killIE()
    Util.parseMarkdown()
    Util.parseLanguage()
    Util.initSW()
    Util.previewImg()
    Util.initDebugInfo()
    Util.initViewCnt()
  },
  /**
   * 调试区域文案
   */
  initDebugInfo: function () {
    console.log(
      '%cSolo%c\n  🎸一款小而美的博客系统，专为程序员设计。\n\n  solo.b3log.org v' +
      Label.version + '\n  Copyright © 2010-present',
      'font-size:96px;color:#3b3e43', 'font-size:12px;color:#4285f4;')
  },
  /**
   * @description 注册 Service Work
   */
  initSW: function () {
    if (navigator.serviceWorker) {
      navigator.serviceWorker.register('/sw.js', {scope: '/'})
    }
  },
  /**
   * @description 根据 tags，穿件云效果
   * @param {String} [id] tags 根元素 id，默认为 tags
   */
  buildTags: function (id) {
    id = id || 'tags'
    // 根据引用次数添加样式，产生云效果
    var classes = ['tags1', 'tags2', 'tags3', 'tags4', 'tags5'],
      bList = $('#' + id + ' b').get()
    var max = parseInt($('#' + id + ' b').last().text())
    var distance = Math.ceil(max / classes.length)
    for (var i = 0; i < bList.length; i++) {
      var num = parseInt(bList[i].innerHTML)
      // 算出当前 tag 数目所在的区间，加上 class
      for (var j = 0; j < classes.length; j++) {
        if (num > j * distance && num <= (j + 1) * distance) {
          bList[i].parentNode.className = classes[j]
          break
        }
      }
    }

    // 按字母或者中文拼音进行排序
    $('#' + id).html($('#' + id + ' li').get().sort(function (a, b) {
      var valA = $(a).find('span').text().toLowerCase()
      var valB = $(b).find('span').text().toLowerCase()
      // 对中英文排序的处理
      return valA.localeCompare(valB)
    }))
  },
}
