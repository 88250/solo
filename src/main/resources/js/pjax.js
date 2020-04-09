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
import $ from 'jquery'
const pjaxUtil = {
  support: {
    pjax: window.history && window.history.pushState &&
    window.history.replaceState && !navigator.userAgent.match(
      /((iPod|iPhone|iPad).+\bOS\s+[1-4]\D|WebApps\/.+CFNetwork)/),
    storage: !!window.localStorage,
  },
  toInt: function (obj) {
    return parseInt(obj)
  },
  stack: {},
  getTime: function () {
    return new Date * 1
  },
  // 获取URL不带hash的部分且去掉pjax=true部分
  getRealUrl: function (url) {
    url = (url || '').replace(/\#.*?$/, '')
    url = url.replace('?pjax=true&', '?').
      replace('?pjax=true', '').
      replace('&pjax=true', '')
    return url
  },
  // 获取url的hash部分
  getUrlHash: function (url) {
    return url.replace(/^[^\#]*(?:\#(.*?))?$/, '$1')
  },
  // 获取本地存储的key
  getLocalKey: function (src) {
    var s = 'pjax_' + encodeURIComponent(src)
    return {
      data: s + '_data',
      time: s + '_time',
      title: s + '_title',
    }
  },
  // 清除所有的cache
  removeAllCache: function () {
    if (!pjaxUtil.support.storage)
      return
    for (var name in localStorage) {
      if ((name.split('_') || [''])[0] === 'pjax') {
        delete localStorage[name]
      }
    }
  },
  // 获取cache
  getCache: function (src, time, flag) {
    var item, vkey, tkey, tval
    time = pjaxUtil.toInt(time)
    if (src in pjaxUtil.stack) {
      item = pjaxUtil.stack[src]
      const ctime = pjaxUtil.getTime()
      if ((item.time + time * 1000) > ctime) {
        return item
      } else {
        delete pjaxUtil.stack[src]
      }
    } else if (flag && pjaxUtil.support.storage) { // 从localStorage里查询
      var l = pjaxUtil.getLocalKey(src)
      vkey = l.data
      tkey = l.time
      item = localStorage.getItem(vkey)
      if (item) {
        tval = pjaxUtil.toInt(localStorage.getItem(tkey))
        if ((tval + time * 1000) > pjaxUtil.getTime()) {
          return {
            data: item,
            title: localStorage.getItem(l.title),
          }
        } else {
          localStorage.removeItem(vkey)
          localStorage.removeItem(tkey)
          localStorage.removeItem(l.title)
        }
      }
    }
    return null
  },
  // 设置cache
  setCache: function (src, data, title, flag) {
    var time = pjaxUtil.getTime(), key
    pjaxUtil.stack[src] = {
      data: data,
      title: title,
      time: time,
    }
    if (flag && pjaxUtil.support.storage) {
      key = pjaxUtil.getLocalKey(src)
      localStorage.setItem(key.data, data)
      localStorage.setItem(key.time, time)
      localStorage.setItem(key.title, title)
    }
  },
  // 清除cache
  removeCache: function (src) {
    src = pjaxUtil.getRealUrl(src || location.href)
    delete pjaxUtil.stack[src]
    if (pjaxUtil.support.storage) {
      var key = pjaxUtil.getLocalKey(src)
      localStorage.removeItem(key.data)
      localStorage.removeItem(key.time)
      localStorage.removeItem(key.title)
    }
  },
}
// pjax
var pjax = function (options) {
  options = $.extend({
    selector: '',
    container: '',
    callback: function () {},
    filter: function () {},
  }, options)
  if (!options.container || !options.selector) {
    throw new Error('selector & container options must be set')
  }
  $('body').delegate(options.selector, 'click', function (event) {
    if (event.which > 1 || event.metaKey) {
      return true
    }
    var $this = $(this), href = $this.attr('href')
    // 过滤
    if (typeof options.filter === 'function') {
      if (options.filter.call(this, href, this) === true) {
        return true
      }
    }
    if (href === location.href) {
      return true
    }
    // 只是hash不同
    if (pjaxUtil.getRealUrl(href) == pjaxUtil.getRealUrl(location.href)) {
      var hash = pjaxUtil.getUrlHash(href)
      if (hash) {
        location.hash = hash
        options.callback && options.callback.call(this, {
          type: 'hash',
        })
      }
      return true
    }
    event.preventDefault()
    options = $.extend(true, options, {
      url: href,
      element: this,
      push: true,
    })
    // 发起请求
    pjax.request(options)
  })
}
pjax.xhr = null
pjax.options = {}
pjax.state = {}

// 默认选项
pjax.defaultOptions = {
  timeout: 2000,
  element: null,
  cache: 24 * 3600, // 缓存时间, 0为不缓存, 单位为秒
  storage: true, // 是否使用localstorage将数据保存到本地
  url: '', // 链接地址
  push: true, // true is push, false is replace, null for do nothing
  show: '', // 展示的动画
  title: '', // 标题
  titleSuffix: '',// 标题后缀
  type: 'GET',
  data: {
    pjax: true,
  },
  dataType: 'html',
  callback: null, // 回调函数
  // for jquery
  beforeSend: function (xhr) {
    $(pjax.options.container).trigger('pjax.start', [xhr, pjax.options])
    xhr && xhr.setRequestHeader('X-PJAX', true) &&
    xhr.setRequestHeader('X-PJAX-Container', pjax.options.container)
  },
  error: function () {
    pjax.options.callback && pjax.options.callback.call(pjax.options.element, {
      type: 'error',
    })
    location.href = pjax.options.url
  },
  complete: function (xhr) {
    $(pjax.options.container).trigger('pjax.end', [xhr, pjax.options])
  },
}
// 展现动画
pjax.showFx = {
  '_default': function (data, callback, isCached) {
    this.html(data)
    callback && callback.call(this, data, isCached)
  },
  fade: function (data, callback, isCached) {
    var $this = this
    if (isCached) {
      $this.html(data)
      callback && callback.call($this, data, isCached)
    } else {
      this.fadeOut(200, function () {
        $this.html(data).fadeIn(200, function () {
          callback && callback.call($this, data, isCached)
        })
      })
    }
  },
}
// 展现函数
pjax.showFn = function (showType, container, data, fn, isCached) {
  var fx = null
  if (typeof showType === 'function') {
    fx = showType
  } else {
    if (!(showType in pjax.showFx)) {
      showType = '_default'
    }
    fx = pjax.showFx[showType]
  }
  fx && fx.call(container, data, function () {
    if (location.hash === '') {
      window.scrollTo(0, 0)
    }
    fn && fn.call(this, data, isCached)
  }, isCached)
}
// success callback
pjax.success = function (data, isCached) {
  // isCached default is success
  if (isCached !== true) {
    isCached = false
  }
  //accept Whole html
  if (pjax.html) {
    data = $(data).find(pjax.html).html()
  }
  if ((data || '').indexOf('<html') != -1) {
    pjax.options.callback && pjax.options.callback.call(pjax.options.element, {
      type: 'error',
    })
    location.href = pjax.options.url
    return false
  }

  var title = $(pjax.options.element).attr('pjax-title')
  if (!title) {
    title = pjax.options.title || ''
    if (title == '' && pjax.options.element) {
      var el = $(pjax.options.element)
      title = el.attr('title') || el.text()
    }
    var matches = data.match(/<title>(.*?)<\/title>/)
    if (matches) {
      title = matches[1]
    }
  }

  if (title) {
    if (title.indexOf(pjax.options.titleSuffix) == -1) {
      title += pjax.options.titleSuffix
    }
  }
  const activeTitle = document.title
  document.title = title
  pjax.state = {
    container: pjax.options.container,
    timeout: pjax.options.timeout,
    cache: pjax.options.cache,
    storage: pjax.options.storage,
    show: pjax.options.show,
    title: title,
    url: pjax.options.oldUrl,
  }
  var query = $.param(pjax.options.data)
  if (query != '') {
    pjax.state.url = pjax.options.url +
      (/\?/.test(pjax.options.url) ? '&' : '?') + query
  }
  if (pjax.options.push) {
    if (!pjax.active) {
      history.replaceState($.extend({}, pjax.state, {
        url: null,
        title : activeTitle,
      }), activeTitle);
      pjax.active = true
    }
    history.pushState(pjax.state, document.title, pjax.options.oldUrl)
  } else if (pjax.options.push === false) {
    history.replaceState(pjax.state, document.title, pjax.options.oldUrl)
  }
  pjax.options.showFn && pjax.options.showFn(data, function () {
    pjax.options.callback && pjax.options.callback.call(pjax.options.element, {
      type: isCached ? 'cache' : 'success',
    })
  }, isCached)
  // 设置cache
  if (pjax.options.cache && !isCached) {
    pjaxUtil.setCache(pjax.options.url, data, title, pjax.options.storage)
  }
}

// 发送请求
pjax.request = function (options) {
  if (options.hasOwnProperty('data')) {
    pjax.defaultOptions.data = options.data
  }
  pjax.defaultOptions.title = ''
  options = $.extend(true, pjax.defaultOptions, options)
  var cache, container = $(options.container)
  options.oldUrl = options.url
  options.url = pjaxUtil.getRealUrl(options.url)
  if ($(options.element).length) {
    cache = pjaxUtil.toInt($(options.element).attr('data-pjax-cache'))
    if (cache) {
      options.cache = cache
    }
  }
  if (options.cache === true) {
    options.cache = 24 * 3600
  }
  options.cache = pjaxUtil.toInt(options.cache)
  // 如果将缓存时间设为0，则将之前的缓存也清除
  if (options.cache === 0) {
    pjaxUtil.removeAllCache()
  }
  // 展现函数
  if (!options.showFn) {
    options.showFn = function (data, fn, isCached) {
      pjax.showFn(options.show, container, data, fn, isCached)
    }
  }
  pjax.options = options
  pjax.options.success = pjax.success
  if (options.cache &&
    (cache = pjaxUtil.getCache(options.url, options.cache, options.storage))) {
    options.beforeSend()
    options.title = cache.title
    pjax.success(cache.data, true)
    options.complete()
    return true
  }
  if (pjax.xhr && pjax.xhr.readyState < 4) {
    pjax.xhr.onreadystatechange = $.noop
    pjax.xhr.abort()
  }
  pjax.xhr = $.ajax(pjax.options)
}

// popstate event
var popped = ('state' in window.history), initialURL = location.href
$(window).bind('popstate', function (event) {
  var initialPop = !popped && location.href == initialURL
  popped = true
  if (initialPop) return
  var state = event.state
  if (state && state.container) {
    if ($(state.container).length) {
      var data = {
        url: state.url,
        container: state.container,
        push: null,
        timeout: state.timeout,
        cache: state.cache,
        storage: state.storage,
        title: state.title,
        element: null,
      }
      pjax.request(data)
    } else {
      window.location.href = location.href
    }
  }
})

// not support
if (!pjaxUtil.support.pjax) {
  pjax = function () {
    return true
  }
  pjax.request = function (options) {
    if (options && options.url) {
      location.href = options.url
    }
  }
}
// extra
if (!('state' in $.Event.prototype)) {
  $.event.addProp('state')
}

export default pjax
