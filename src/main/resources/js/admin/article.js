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
 * @fileoverview article for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.6.1.1, Jun 24, 2020
 */
admin.article = {
  // 当发文章，取消发布，更新文章时设置为 false。不需在离开编辑器时进行提示。
  isConfirm: true,
  status: {
    id: undefined,
    isArticle: undefined,
  },
  content: '',
  /**
   * @description 获取文章并把值塞入发布文章页面
   * @param {String} id 文章 id
   * @param {Boolean} isArticle 文章或者草稿
   */
  get: function (id, isArticle) {
    this.status.id = id
    this.status.isArticle = isArticle
    admin.selectTab('article/article')
  },
  /**
   * @description 获取文章内容
   */
  getAndSet: function () {
    $('#loadMsg').text(Label.loadingLabel)
    $('#tipMsg').text('')
    $.ajax({
      url: Label.servePath + '/console/article/' +
        admin.article.status.id,
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        // set default value for article.
        $('#title').val(result.article.articleTitle)
        admin.editors.articleEditor.setContent(result.article.articleContent)
        admin.editors.abstractEditor.setContent(result.article.articleAbstract)
        admin.article.content = admin.editors.articleEditor.getContent()

        var tags = result.article.articleTags,
          tagsString = ''
        for (var i = 0; i < tags.length; i++) {
          if (0 === i) {
            tagsString = tags[i].tagTitle
          } else {
            tagsString += ',' + tags[i].tagTitle
          }
        }

        $('#tag').val(tagsString)
        $('#permalink').val(result.article.articlePermalink)
        $('#viewPwd').val(result.article.articleViewPwd)

        // signs
        var signs = result.article.signs
        $('.signs button').each(function (i) {
          if (parseInt(result.article.articleSignId) ===
            parseInt(signs[i].oId)) {
            $('#articleSign' + signs[i].oId).addClass('selected')
          } else {
            $('#articleSign' + signs[i].oId).removeClass('selected')
          }
        })

        admin.article.setStatus()
        $('#loadMsg').text('')
      },
    })
  },
  /**
   * @description 删除文章
   * @param {String} id 文章 id
   * @param {String} fromId 文章来自草稿夹(draft)/文件夹(article)
   * @param {String} title 文章标题
   */
  del: function (id, fromId, title) {
    if (confirm(Label.confirmRemoveLabel + Label.articleLabel + '"' + htmlDecode(title) + '"?')) {
      $('#loadMsg').text(Label.loadingLabel)
      $('#tipMsg').text('')

      $.ajax({
        url: Label.servePath + '/console/article/' + id,
        type: 'DELETE',
        cache: false,
        success: function (result, textStatus) {
          $('#tipMsg').text(result.msg)
          if (0 !== result.code) {
            $('#loadMsg').text('')
            return
          }

          if (document.querySelectorAll('tr').length === 2) {
            const refreshPage = Math.max(
              (admin[fromId + 'List'].tablePagination.currentPage - 1), 1)
            admin[fromId + 'List'].getList(refreshPage)
            admin.setHashByPage(refreshPage)
          } else {
            admin[fromId + 'List'].getList(
              admin[fromId + 'List'].tablePagination.currentPage)
          }
        },
      })
    }
  },
  /**
   * @@description 添加文章
   * @param {Boolean} articleStatus 0：已发布，1：草稿
   */
  add: function (articleStatus) {
    if (admin.article.validate()) {
      var that = this
      that._addDisabled()

      $('#loadMsg').text(Label.loadingLabel)
      $('#tipMsg').text('')
      var signId = ''
      $('.signs button').each(function () {
        if (this.className === 'selected') {
          signId = this.id.substr(this.id.length - 1, 1)
        }
      })

      var articleContent = admin.editors.articleEditor.getContent(),
        articleAbstract = admin.editors.abstractEditor.getContent()

      if ($('#articleThumbnail').prop('checked')) {
        var bgImage = $('.thumbnail__img').css('background-image')
        articleContent = '![](' + bgImage.substring(5, bgImage.length - 2).
            replace('w/768', 'w/960').
            replace('h/432', 'h/540') +
          ')\n\n' + articleContent
      }

      var requestJSONObject = {
        'article': {
          'articleTitle': $('#title').val(),
          'articleContent': articleContent,
          'articleAbstract': articleAbstract,
          'articleTags': this.trimUniqueArray($('#tag').val()).toString(),
          'articlePermalink': $('#permalink').val(),
          'articleStatus': articleStatus,
          'articleSignId': signId,
          'postToCommunity': $('#postToCommunity').prop('checked'),
          'articleViewPwd': $('#viewPwd').val(),
        },
      }

      $.ajax({
        url: Label.servePath + '/console/article/',
        type: 'POST',
        cache: false,
        data: JSON.stringify(requestJSONObject),
        success: function (result) {
          $('#tipMsg').text(result.msg)
          if (0 !== result.code) {
            return
          }

          admin.article.status.id = undefined
          if (articleStatus === 0) {
            admin.selectTab('article/article-list')
          } else {
            admin.selectTab('article/draft-list')
          }

          admin.article.isConfirm = false
        },
        complete: function (jqXHR, textStatus) {
          that._removeDisabled()
          $('#loadMsg').text('')
        },
      })
    }
  },
  /**
   * @description 更新文章
   * @param {Boolean} articleStatus 0：已发布，1：草稿
   */
  update: function (articleStatus) {
    if (admin.article.validate()) {
      var that = this
      that._addDisabled()

      $('#loadMsg').text(Label.loadingLabel)
      $('#tipMsg').text('')
      var signId = ''
      $('.signs button').each(function () {
        if (this.className === 'selected') {
          signId = this.id.substr(this.id.length - 1, 1)
        }
      })

      var articleContent = admin.editors.articleEditor.getContent(),
        articleAbstract = admin.editors.abstractEditor.getContent()
      if ($('#articleThumbnail').prop('checked')) {
        var bgImage = $('.thumbnail__img').css('background-image')
        articleContent = '![](' + bgImage.substring(5, bgImage.length - 2).
            replace('w/768', 'w/960').
            replace('h/432', 'h/540') +
          ') \n\n' + articleContent
      }
      var requestJSONObject = {
        'article': {
          'oId': this.status.id,
          'articleTitle': $('#title').val(),
          'articleContent': articleContent,
          'articleAbstract': articleAbstract,
          'articleTags': this.trimUniqueArray($('#tag').val()).toString(),
          'articlePermalink': $('#permalink').val(),
          'articleStatus': articleStatus,
          'articleSignId': signId,
          'articleViewPwd': $('#viewPwd').val(),
          'postToCommunity': $('#postToCommunity').prop('checked'),
        },
      }

      $.ajax({
        url: Label.servePath + '/console/article/',
        type: 'PUT',
        cache: false,
        data: JSON.stringify(requestJSONObject),
        success: function (result, textStatus) {
          $('#tipMsg').text(result.msg)
          if (0 !== result.code) {
            return
          }

          if (articleStatus === 0) {
            admin.selectTab('article/article-list')
          } else {
            admin.selectTab('article/draft-list')
          }

          $('#tipMsg').text(Label.updateSuccLabel)

          admin.article.status.id = undefined
          admin.article.isConfirm = false
        },
        complete: function (jqXHR, textStatus) {
          that._removeDisabled()
          $('#loadMsg').text('')
        },
      })
    }
  },
  /**
   * @description 发布文章页面设置文章按钮、发布到社区等状态的显示
   */
  setStatus: function () {
    $.ajax({// Gets all tags
      url: Label.servePath + '/console/tags',
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        if (0 >= result.tags.length) {
          return
        }

        $('#tagCheckboxPanel>span').remove('')

        var spans = ''
        for (var i = 0; i < result.tags.length; i++) {
          spans += '<span>' + result.tags[i].tagTitle + '</span>'
        }
        $('#tagCheckboxPanel').html(spans + '<div class="fn__clear"></div>')

        $('#loadMsg').text('')
      },
    })

    // set button status
    if (this.status) {
      if (this.status.isArticle) {
        $('#unSubmitArticle').show()
        $('#saveArticle').hide()
        $('#submitArticle').show()
      } else {
        $('#submitArticle').show()
        $('#unSubmitArticle').hide()
        $('#saveArticle').show()
      }
    } else {
      $('#submitArticle').show()
      $('#unSubmitArticle').hide()
      $('#saveArticle').show()
      $('#postToCommunityPanel').show()
    }
  },
  /**
   * @description 清除发布文章页面的输入框的内容
   */
  clear: function () {
    this.status = {
      id: undefined,
      isArticle: undefined,
    }
    this.setStatus()

    $('#title').val('')

    admin.editors.articleEditor.setContent('')
    admin.editors.abstractEditor.setContent('')

    // reset tag
    $('#tag').val('')
    $('#tagCheckboxPanel').hide().find('span').removeClass('selected')

    $('#permalink').val('')
    $('#articleCammentable').prop('checked', true)
    $('#postToCommunity').prop('checked', false)
    $('.signs button').each(function (i) {
      if (i === 0) {
        this.className = 'selected'
      } else {
        this.className = ''
      }
    })

    if ($('#articleThumbnail').prop('checked')) {
      $('#articleThumbnail').click()
    }
  },
  /**
   * @description 初始化发布文章页面
   */
  init: function (fun) {
    // Inits Signs.
    $('.signs button').click(function (i) {
      $('.signs button').removeClass('selected')
      $(this).addClass('selected')
    })

    $('#tipMsg').text(Label.uploadMsg)

    // For tag auto-completion
    $.ajax({// Gets all tags
      url: Label.servePath + '/console/tags',
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        if (0 >= result.tags.length) {
          return
        }

        var tags = []
        for (var i = 0; i < result.tags.length; i++) {
          tags.push(result.tags[i].tagTitle)
        }

        $('#tag').completed({
          height: 160,
          buttonText: Label.selectLabel,
          data: tags,
        })

        $('#loadMsg').text('')
      },
    })

    // submit action
    $('#submitArticle').click(function () {
      if (admin.article.status.id) {
        admin.article.update(0)
      } else {
        admin.article.add(0)
      }
    })

    $('#saveArticle').click(function () {
      if (admin.article.status.id) {
        admin.article.update(admin.article.status.isArticle ? 0 : 1)
      } else {
        admin.article.add(1)
      }
    })

    // editor
    admin.editors.articleEditor = new SoloEditor({
      outline: true,
      id: 'articleContent',
      height: 500,
      fun: fun,
      previewMode: 'both',
      resize: false,
      typewriterMode: true,
    })

    admin.editors.abstractEditor = new SoloEditor({
      id: 'abstract',
      height: 200,
      previewMode: 'editor',
      resize: true,
      typewriterMode: false,
    })

    // thumbnail
    $('#articleThumbnailBtn').click(function () {
      $.ajax({// Gets all tags
        url: Label.servePath + '/console/thumbs?n=1&w=768&h=432',
        type: 'GET',
        cache: false,
        success: function (result, textStatus) {
          if (0 !== result.code) {
            $('#loadMsg').text(result.msg)
            return
          }

          $('#articleThumbnailBtn').
            prev().
            css('background-image', 'url(' + result.data[0] + ')')
        },
      })
    }).click()
  },
  /**
   * @description 验证发布文章字段的合法性
   */
  validate: function () {
    var articleContent = admin.editors.articleEditor.getContent()

    if ($('#title').val().replace(/\s/g, '') === '') {
      $('#tipMsg').text(Label.titleEmptyLabel)
      $('#title').focus().val('')
    } else if (articleContent.replace(/\s/g, '') === '') {
      $('#tipMsg').text(Label.contentEmptyLabel)
    } else {
      return true
    }
    return false
  },
  /**
   * @description 取消发布
   */
  unPublish: function () {
    var that = this
    that._addDisabled()
    $.ajax({
      url: Label.servePath + '/console/article/unpublish/' +
        admin.article.status.id,
      type: 'PUT',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          return
        }

        admin.selectTab('article/draft-list')
        admin.article.status.id = undefined
        admin.article.isConfirm = false
      },
      complete: function (jqXHR, textStatus) {
        that._removeDisabled()
        $('#loadMsg').text('')
      },
    })
  },
  /**
   * @description 数组中无重复
   * @param {String} str 被解析的字符串
   * @returns {String} 无重复的字符串
   */
  trimUniqueArray: function (str) {
    str = str.toString()
    var arr = str.split(',')
    for (var i = 0; i < arr.length; i++) {
      arr[i] = arr[i].replace(/(^\s*)|(\s*$)/g, '')
      if (arr[i] === '') {
        arr.splice(i, 1)
        i--
      }
    }
    var unique = $.unique(arr)
    return unique.toString()
  },
  /**
   * @description 点击发文文章时的处理
   */
  prePost: function () {
    $('#loadMsg').text(Label.loadingLabel)
    admin.article.content = ''
    if (!admin.editors.articleEditor.getContent) {
      return
    }

    var articleContent = admin.editors.articleEditor.getContent()

    if (window.location.hash === '#article/article' &&
      articleContent.replace(/\s/g, '') !== '') {
      if (confirm(Label.editorPostLabel)) {
        admin.article.clear()
      }
    }
    $('#tipMsg').text('')
    $('#loadMsg').text('')
  },
  /**
   * @description: 防重复提交，点击一次后，按钮设置为 disabled
   */
  _addDisabled: function () {
    $('#unSubmitArticle').attr('disabled', 'disabled')
    $('#saveArticle').attr('disabled', 'disabled')
    $('#submitArticle').attr('disabled', 'disabled')
  },
  /**
   * @description: 防重复提交，当后台有数据返回后，按钮移除 disabled 状态
   */
  _removeDisabled: function () {
    $('#unSubmitArticle').removeAttr('disabled')
    $('#saveArticle').removeAttr('disabled')
    $('#submitArticle').removeAttr('disabled')
  },
}

/**
 * @description 注册到 admin 进行管理
 */
admin.register.article = {
  'obj': admin.article,
  'init': admin.article.init,
  'refresh': function () {
    admin.editors.abstractEditor.setContent('')
    admin.editors.articleEditor.setContent('')
    $('#loadMsg').text('')
    $('#tipMsg').text(Label.uploadMsg)
  },
}
