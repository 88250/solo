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
import '../common'
import '../lib/jquery/jquery.bowknot.min'
/**
 * @description index for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.4.0.1, Jan 13, 2020
 */

window.htmlDecode = function (code) {
  var div = document.createElement('div')
  div.innerHTML = decodeURIComponent(code)
  return div.innerText
}

var Admin = function () {
  this.register = {}
  // 工具栏下的工具
  this.tools = [
    '#page-list', '#theme-list', '#link-list', '#preference',
    '#user-list', '#plugin-list', '#others', '#category-list', "#staticsite"]
}

$.extend(Admin.prototype, {
  /**
   * @description  登出
   */
  logout: function () {
    window.location.href = Label.servePath + '/logout'
  },
  toggleMenu: function () {
    if ($('#tabs').css('left') === '-240px') {
      $('#tabs').css('left', 0)
      $('.tabs__bg').show()
    } else {
      $('#tabs').css('left', '-240px')
      $('.tabs__bg').hide()
    }
  },
  /**
   * @description 清除提示
   */
  clearTip: function () {
    $('#tipMsg').text('')
    $('#loadMsg').text('')
  },
  /**
   * @description 根据当前页数设置 hash
   * @param {Int} currentPage 当前页
   */
  setHashByPage: function (currentPage) {
    var hash = window.location.hash,
      hashList = hash.split('/')
    if (/^\d*$/.test(hashList[hashList.length - 1])) {
      hashList[hashList.length - 1] = currentPage
    } else {
      hashList.push(currentPage)
    }
    window.location.hash = hashList.join('/')
  },
  /**
   * @description 设置某个 tab 被选择
   * @param {Stirng} id id tab id
   */
  selectTab: function (id) {
    window.location.hash = '#' + id
  },
  /**
   * @description 根据当前 hash 解析出当前页数及 hash 数组。
   */
  analyseHash: function () {
    var hash = window.location.hash
    var tag = hash.substr(1, hash.length - 1)
    var tagList = tag.split('/')
    var tags = {}
    tags.page = 1,
      tags.hashList = []
    for (var i = 0; i < tagList.length; i++) {
      if (i === tagList.length - 1 && (/^\d+$/.test(tagList[i]))) {
        tags.page = tagList[i]
      } else {
        tags.hashList.push(tagList[i])
      }
    }
    return tags
  },
  /**
   * @description 根据当前 hash 设置当前 tab
   */
  setCurByHash: function () {
    $(window).scrollTop(0)
    $('#tipMsg').text('')
    var tags = admin.analyseHash()
    var tab = tags.hashList[1],
      subTab = tags.hashList[2]

    if (tags.hashList.length === 1) {
      tab = tags.hashList[0]
    }

    if (tab === '') {
      return
    }

    // 离开编辑器时进行提示
    try {
      if (admin.editors.articleEditor.getContent) {
        // 除更新、发布、取消发布文章，编辑器中无内容外，离开编辑器需进行提示。
        if (tab !== 'article' && admin.article.isConfirm &&
          admin.editors.articleEditor.getContent().replace(/\s/g, '') !== ''
          && admin.article.content !==
          admin.editors.articleEditor.getContent()) {
          if (!confirm(Label.editorLeaveLabel)) {
            window.location.hash = '#article/article'
            return
          }
        }
        // 不离开编辑器，hash 需变为 "#article/article"，此时不需要做任何处理。
        if (tab === 'article' && admin.article.isConfirm &&
          admin.editors.articleEditor.getContent().replace(/\s/g, '') !== ''
          && admin.article.content !==
          admin.editors.articleEditor.getContent()) {
          return
        }
      }
    } catch (e) {
      console.log(e)
    }

    // clear article
    if (tab !== 'article' && admin.editors.articleEditor.setContent) {
      admin.article.clear()
    }
    admin.article.isConfirm = true

    $('#tabs').tabs('setCurrent', tab)
    $('#loadMsg').text(Label.loadingLabel)

    if ($('#tabsPanel_' + tab).length === 1) {
      if ($('#tabsPanel_' + tab).html().replace(/\s/g, '') === '') {
        // 还未加载 HTML
        $('#tabsPanel_' + tab).load('admin-' + tab + '.do', function () {
          // 页面加载完后，回调初始函数
          if (tab === 'article' && admin.article.status.id) {
            // 当文章页面编辑器未初始化时，调用更新文章需先初始化编辑器
            admin.register[tab].init.call(admin.register[tab].obj,
              admin.article.getAndSet)
          } else {
            admin.register[tab].init.call(admin.register[tab].obj, tags.page)
          }

          // 页面包含子 tab，需根据 hash 定位到相应的 tab
          if (subTab) {
            $('#tab' + tab.substring(0, 1).toUpperCase() + tab.substring(1)).
              tabs('setCurrent', subTab)
          }

          // 根据 hash 调用现有的插件函数
          admin.plugin.setCurByHash(tags)
        })
      } else {
        if (tab === 'article' && admin.article.status.id) {
          admin.article.getAndSet()
        }

        // 已加载过 HTML，只需调用刷新函数
        if (admin.register[tab] && admin.register[tab].refresh) {
          admin.register[tab].refresh.call(admin.register[tab].obj, tags.page)
        }

        // 页面包含子 tab，需根据 hash 定位到相应的 tab
        if (subTab) {
          $('#tab' + tab.substring(0, 1).toUpperCase() + tab.substring(1)).
            tabs('setCurrent', subTab)
        }

        // 根据 hash 调用现有的插件函数
        admin.plugin.setCurByHash(tags)
      }
    } else {
      $('#tipMsg').text('Error: No tab! ' + Label.reportIssueLabel)
      $('#loadMsg').text('')
    }
  },
  /**
   * @description 初始化整个后台
   */
  init: function () {
    Util.killIE()
    $('#loadMsg').text(Label.loadingLabel)

    // 构建 tabs
    $('#tabs').tabs()

    // tipMsg
    setInterval(function () {
      if ($('#tipMsg').text() !== '') {
        setTimeout(function () {
          $('#tipMsg').text('')
        }, 7000)
      }
    }, 6000)
    $('#loadMsg').text('')

    window.onbeforeunload = function (event) {
      if (window.location.hash === '#article/article') {
        if (event) {
          event.returnValue = Label.editorLeaveLabel
        }
        return Label.editorLeaveLabel
      }
    }

    $(document).ajaxError(function (event, xhr, options, exc) {
      if (xhr.status !== 200) {
        $('#tipMsg').text(xhr.status + ': ' + exc)
      }
    })
  },
  /**
   * @description tools and article collapse
   * @param {Bom} it 触发事件对象
   */
  collapseNav: function (it) {
    var subNav = $(it).next()
    subNav.slideToggle('normal', function () {
      if (this.style.display !== 'none') {
        $(it).
          find('.icon-chevron-down')[0].className = 'icon-chevron-up fn__right'
        $(it).addClass('tab-current')
      } else {
        $(it).
          find('.icon-chevron-up')[0].className = 'icon-chevron-down fn__right'
        $(it).removeClass('tab-current')
      }

      $('#tabs > ul').height('auto')
      $('#tabs > ul').height($('#tabs > ul').height() + 80)
    })
  },
  /**
   * @description 后台及当前页面所需插件初始化完后，对权限进行控制及当前页面属于 tools 时，tools 选项需展开。
   */
  inited: function () {
    // Removes functions with the current user role
    if (Label.userRole !== 'adminRole') {
      $('#tools').remove();
    } else {
      // 当前 tab 属于 Tools 时，设其展开
      for (var j = 0; j < this.tools.length; j++) {
        if ('#' + window.location.hash.split('/')[1] === this.tools[j]) {
          $('#tabToolsTitle').click()
          break
        }
      }
    }
    this.setCurByHash()
  },
})

window.admin = new Admin()
