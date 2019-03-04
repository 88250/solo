/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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
 * @version 1.7.0.0, Mar 4, 2019
 */

/**
 * @description Util
 * @static
 */
var Util = {
  isArticlePage: function (href) {
    var isArticle = true
    if (href.indexOf(latkeConfig.servePath + '/tags/') > -1) {
      isArticle = false
    }
    if (href.indexOf(latkeConfig.servePath + '/tags.html') > -1) {
      isArticle = false
    }
    if (href.indexOf(latkeConfig.servePath + '/category/') > -1) {
      isArticle = false
    }
    if (href.indexOf(latkeConfig.servePath + '/archives.html') > -1) {
      isArticle = false
    }
    if (href.indexOf(latkeConfig.servePath + '/archives/') > -1) {
      isArticle = false
    }
    if (href.indexOf(latkeConfig.servePath + '/links.html') > -1) {
      isArticle = false
    }
    if (href === latkeConfig.servePath) {
      isArticle = false
    }
    if (/^[0-9]*$/.test(href.replace(latkeConfig.servePath + '/', ''))) {
      isArticle = false
    }
    return isArticle
  },
  /**
   * 初始化 Pjax
   * @param cb 除文章和自定义页面外的其他页面加载回调
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
        filter: function (href) {
          if (href === latkeConfig.servePath + '/rss.xml' ||
            href.indexOf(latkeConfig.servePath + '/admin-index.do') > -1) {
            return true
          }
          if (href.indexOf(latkeConfig.servePath) > -1) {
            return false
          }
          return true
        },
        callback: function () {
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
  previewImg:function () {
    $('body').on('click', '.content-reset img', function () {
      window.open(this.src);
    });
  },
  /**
   * 按需加载 MathJax 及 flow
   * @returns {undefined}
   */
  parseMarkdown: function () {
    var hasMathJax = false
    var hasFlow = false
    var className = 'content-reset'
    $('.' + className).each(function () {
      $(this).find('p').each(function () {
        if ($(this).text().split('$').length > 2 ||
          ($(this).text().split('\\(').length > 1 &&
            $(this).text().split('\\)').length > 1)) {
          hasMathJax = true
        }
      })
      if ($(this).find('code.lang-flow, code.language-flow').length > 0) {
        hasFlow = true
      }
    })

    if (hasMathJax) {
      var initMathJax = function () {
        MathJax.Hub.Config({
          tex2jax: {
            inlineMath: [['$', '$'], ['\\(', '\\)']],
            displayMath: [['$$', '$$']],
            processEscapes: true,
            processEnvironments: true,
            skipTags: ['pre', 'code', 'script'],
          },
          asciimath2jax: {
            delimiters: [['$', '$']],
          },
        })
        MathJax.Hub.Typeset()
      }

      if (typeof MathJax !== 'undefined') {
        initMathJax()
      } else {
        $.ajax({
          method: 'GET',
          url: 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.5/MathJax.js?config=TeX-MML-AM_CHTML',
          dataType: 'script',
          cache: true,
        }).done(function () {
          initMathJax()
        })
      }
    }

    if (hasFlow) {
      var initFlow = function () {
        $('.' + className + ' code.lang-flow, .' + className +
          ' code.language-flow').each(function (index) {
          var $it = $(this)
          var id = 'symFlow' + (new Date()).getTime() + index
          $it.hide()
          var diagram = flowchart.parse($.trim($it.text()))
          $it.parent().
            after('<div style="text-align: center" id="' + id + '"></div>')
          diagram.drawSVG(id)
          $it.parent().remove()
          $('#' + id).find('svg').height('auto').width('auto')
        })
      }

      if (typeof (flowchart) !== 'undefined') {
        initFlow()
      } else {
        $.ajax({
          method: 'GET',
          url: latkeConfig.staticServePath +
          '/js/lib/flowchart/flowchart.min.js',
          dataType: 'script',
          cache: true,
        }).done(function () {
          initFlow()
        })
      }
    }
  },
  /**
   * @description IE6/7，跳转到 kill-browser 页面
   */
  killIE: function (ieVersion) {
    var addKillPanel = function () {
      if (Cookie.readCookie('showKill') === '') {
        try {
          var left = ($(window).width() - 781) / 2,
            top1 = ($(window).height() - 680) / 2
          var killIEHTML = '<div style=\'display: block; height: 100%; width: 100%; position: fixed; background-color: rgb(0, 0, 0); opacity: 0.6;filter: alpha(opacity=60); top: 0px;z-index:110\'></div>'
            + '<iframe style=\'left:' + left + 'px;z-index:120;top: ' + top1 +
            'px; position: fixed; border: 0px none; width: 781px; height: 680px;\' src=\'' +
            latkeConfig.servePath + '/kill-browser\'></iframe>'
          $('body').append(killIEHTML)
        } catch (e) {
          var left = 10,
            top1 = 0
          var killIEHTML = '<div style=\'display: block; height: 100%; width: 100%; position: fixed; background-color: rgb(0, 0, 0); opacity: 0.6;filter: alpha(opacity=60); top: 0px;z-index:110\'></div>'
            + '<iframe style=\'left:' + left + 'px;z-index:120;top: ' + top1 +
            'px; position: fixed; border: 0px none; width: 781px; height: 680px;\' src=\'' +
            latkeConfig.servePath + '/kill-browser\'></iframe>'
          document.body.innerHTML = document.body.innerHTML + killIEHTML
        }
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
   * @description 替换[emXX] 为图片
   * @param {String} str 替换字符串
   * @returns {String} 替换后的字符
   */
  replaceEmString: function (str) {
    var commentSplited = str.split('[em')
    if (commentSplited.length === 1) {
      return str
    }

    str = commentSplited[0]
    for (var j = 1; j < commentSplited.length; j++) {
      var key = commentSplited[j].substr(0, 2)
      str += '<img width=\'20\' src=\'' + latkeConfig.staticServePath +
        '/images/emotions/em' + key + '.png\' alt=\'' +
        Label['em' + key + 'Label'] + '\' title=\'' +
        Label['em' + key + 'Label'] + '\'/> ' + commentSplited[j].substr(3)
    }
    return str
  },
  /**
   * @description 切换到手机版
   * @param {String} skin 切换前的皮肤名称
   */
  switchMobile: function (skin) {
    Cookie.createCookie('btouch_switch_toggle', skin, 365)
    setTimeout(function () {
      location.reload()
    }, 1250)
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
    //window.onerror = Util.error;
    Util.killIE()
    Util.setTopBar()
    Util.parseMarkdown()
    Util.initSW()
    Util.previewImg()
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
   * @description 替换侧边栏表情为图片
   * @param {Dom} comments 评论内容元素
   */
  replaceSideEm: function (comments) {
    for (var i = 0; i < comments.length; i++) {
      var $comment = $(comments[i])
      $comment.html(Util.replaceEmString($comment.html()))
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
  /**
   * @description 时间戳转化为时间格式
   * @param {String} time 时间
   * @param {String} format 格式化后日期格式
   * @returns {String} 格式化后的时间
   */
  toDate: function (time, format) {
    var dateTime = new Date(time)
    var o = {
      'M+': dateTime.getMonth() + 1, //month
      'd+': dateTime.getDate(), //day
      'H+': dateTime.getHours(), //hour
      'm+': dateTime.getMinutes(), //minute
      's+': dateTime.getSeconds(), //second
      'q+': Math.floor((dateTime.getMonth() + 3) / 3), //quarter
      'S': dateTime.getMilliseconds(), //millisecond
    }

    if (/(y+)/.test(format)) {
      format = format.replace(RegExp.$1,
        (dateTime.getFullYear() + '').substr(4 - RegExp.$1.length))
    }

    for (var k in o) {
      if (new RegExp('(' + k + ')').test(format)) {
        format = format.replace(RegExp.$1,
          RegExp.$1.length == 1 ? o[k] : ('00' + o[k]).substr(
            ('' + o[k]).length))
      }
    }
    return format
  },
}
if (!Cookie) {
  /**
   * @description Cookie 相关操作
   * @static
   */
  var Cookie = {
    /**
     * @description 读取 cookie
     * @param {String} name cookie key
     * @returns {String} 对应 key 的值，如 key 不存在则返回 ""
     */
    readCookie: function (name) {
      var nameEQ = name + '='
      var ca = document.cookie.split(';')
      for (var i = 0; i < ca.length; i++) {
        var c = ca[i]
        while (c.charAt(0) == ' ')
          c = c.substring(1, c.length)
        if (c.indexOf(nameEQ) == 0)
          return decodeURIComponent(c.substring(nameEQ.length, c.length))
      }
      return ''
    },
    /**
     * @description 清除 Cookie
     * @param {String} name 清除 key 为 name 的该条 Cookie
     */
    eraseCookie: function (name) {
      this.createCookie(name, '', -1)
    },
    /**
     * @description 创建 Cookie
     * @param {String} name 每条 Cookie 唯一的 key
     * @param {String} value 每条 Cookie 对应的值，将被 UTF-8 编码
     * @param {Int} days Cookie 保存时间
     */
    createCookie: function (name, value, days) {
      var expires = ''
      if (days) {
        var date = new Date()
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000))
        expires = '; expires=' + date.toGMTString()
      }
      document.cookie = name + '=' + encodeURIComponent(value) + expires +
        '; path=/'
    },
  }
}