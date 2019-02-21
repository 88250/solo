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
 * @fileoverview Page util, load heighlight and process comment.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.1.0.0, Feb 21, 2019
 */
var Page = function (tips) {
  this.currentCommentId = ''
  this.tips = tips
}

$.extend(Page.prototype, {
  /*
   * @description 把评论中的标识替换为图片
   * @param {Dom} selector
   */
  replaceCommentsEm: function (selector) {
    var $commentContents = $(selector)
    for (var i = 0; i < $commentContents.length; i++) {
      var str = $commentContents[i].innerHTML
      $commentContents[i].innerHTML = Util.replaceEmString(str)
    }
  },
  /*
   * @description 解析语法高亮
   * @param {Obj} obj 语法高亮配置参数
   */
  parseLanguage: function (obj) {
    var isHljs = false
    $('.content-reset pre').
      each(function () {
        isHljs = true
      })

    if (isHljs) {
      // otherelse use highlight
      // load css
      if (document.createStyleSheet) {
        document.createStyleSheet(latkeConfig.staticServePath +
          '/js/lib/highlight-9.13.1/styles/' +
          ((obj && obj.theme) || 'github') + '.css')
      } else {
        $('head').
          append(
            $('<link rel=\'stylesheet\' href=\'' + latkeConfig.staticServePath +
              '/js/lib/highlight-9.13.1/styles/' +
              ((obj && obj.theme) || 'github') + '.css\'>'))
      }
      if (!Label.markedAvailable) {
        $.ajax({
          url: latkeConfig.staticServePath +
          '/js/lib/highlight-9.13.1/highlight.pack.js',
          dataType: 'script',
          cache: true,
          success: function () {
            hljs.initHighlighting.called = false
            hljs.initHighlighting()
          },
        })
      }
    }
  },
  /*
   * @description 文章/自定义页面加载
   * @param {Obj} obj 配置设定
   * @param {Obj} obj.theme 代码高亮配置
   */
  load: function (obj) {
    var that = this
    // language
    that.parseLanguage(obj)
    // comment
    $('#comment').click(function () {
      that.toggleEditor()
    }).attr('readonly', 'readonly')

    $('#soloEditorCancel').click(function () {
      that.toggleEditor()
    })
    $('#soloEditorAdd').click(function () {
      that.submitComment()
    })
  },
  toggleEditor: function (commentId, name) {
    var that = this

    if (typeof Vditor === 'undefined') {
      $.ajax({
        method: 'GET',
        url: latkeConfig.staticServePath + '/js/lib/vditor-0.2.5/index.min.js',
        dataType: 'script',
        cache: true,
        async: false,
        success: function () {
          window.vditor = new Vditor('soloEditorComment', {
            placeholder: that.tips.commentContentCannotEmptyLabel,
            height: 180,
            hint: {
              emojiPath: latkeConfig.staticServePath + '/js/lib/emojify.js-1.1.0/images/basic'
            },
            esc: function () {
              $('#soloEditorCancel').click()
            },
            ctrlEnter: function () {
              $('#soloEditorAdd').click()
            },
            preview: {
              delay: 500,
              show: false,
              url: latkeConfig.servePath + '/console/markdown/2html',
              parse: function (element) {
                if (element.style.display === 'none') {
                  return
                }
                Util.parseMarkdown('content-reset')
                if (!Label.markedAvailable) {
                  hljs.initHighlighting.called = false
                  hljs.initHighlighting()
                }
              },
            },
            counter: 500,
            resize: {
              enable: true,
              position: 'top',
              after: function () {
                $('body').css('padding-bottom', $('#soloEditor').outerHeight())
              }
            },
            lang: that.tips.langLabel,
            toolbar: [
              'emoji',
              'headings',
              'bold',
              'italic',
              'strike',
              '|',
              'line',
              'quote',
              '|',
              'list',
              'ordered-list',
              'check',
              '|',
              'code',
              'inline-code',
              '|',
              'undo',
              'redo',
              '|',
              'link',
              'table',
              '|',
              'preview',
              'fullscreen',
              'info',
              'help',
            ],
            classes: {
              preview: 'content__reset',
            },
          })
          vditor.focus()
        },
      })
    }

    var $editor = $('#soloEditor')
    if ($editor.length === 0) {
      location.href = latkeConfig.servePath + '/start'
      return
    }

    if ($('body').css('padding-bottom') === '0px' || commentId) {
      $('#soloEditorError').text('')
      $editor.css({'bottom': '0', 'opacity': 1})
      $('body').css('padding-bottom', '238px')
      this.currentCommentId = commentId
      $('#soloEditorReplyTarget').text(name ? '@' + name : '')
      if (typeof vditor !== 'undefined') {
        vditor.focus()
      }
    } else {
      $editor.css({'bottom': '-300px', 'opacity': 0})
      $('body').css('padding-bottom', 0)
    }
  },
  /*
   * @description 加载随机文章
   * @param {String} headTitle 随机文章标题
   */
  loadRandomArticles: function (headTitle) {
    var randomArticles1Label = this.tips.randomArticles1Label
    // getRandomArticles
    $.ajax({
      url: latkeConfig.servePath + '/articles/random',
      type: 'POST',
      success: function (result, textStatus) {
        var randomArticles = result.randomArticles
        if (!randomArticles || 0 === randomArticles.length) {
          $('#randomArticles').remove()
          return
        }

        var listHtml = ''
        for (var i = 0; i < randomArticles.length; i++) {
          var article = randomArticles[i]
          var title = article.articleTitle
          var randomArticleLiHtml = '<li>' + '<a rel=\'nofollow\' title=\'' +
            title + '\' href=\'' + latkeConfig.servePath +
            article.articlePermalink + '\'>' + title + '</a></li>'
          listHtml += randomArticleLiHtml
        }

        var titleHTML = headTitle ? headTitle : '<h4>' + randomArticles1Label +
          '</h4>'
        var randomArticleListHtml = titleHTML + '<ul>' +
          listHtml + '</ul>'
        $('#randomArticles').append(randomArticleListHtml)
      },
    })
  },
  /*
   * @description 加载相关文章
   * @param {String} id 文章 id
   * @param {String} headTitle 相关文章标题
   */
  loadRelevantArticles: function (id, headTitle) {
    $.ajax({
      url: latkeConfig.servePath + '/article/id/' + id + '/relevant/articles',
      type: 'GET',
      success: function (data, textStatus) {
        var articles = data.relevantArticles
        if (!articles || 0 === articles.length) {
          $('#relevantArticles').remove()
          return
        }
        var listHtml = ''
        for (var i = 0; i < articles.length; i++) {
          var article = articles[i]
          var title = article.articleTitle
          var articleLiHtml = '<li>'
            + '<a rel=\'nofollow\' title=\'' + title + '\' href=\'' +
            latkeConfig.servePath + article.articlePermalink + '\'>'
            + title + '</a></li>'
          listHtml += articleLiHtml
        }

        var relevantArticleListHtml = headTitle
          + '<ul>'
          + listHtml + '</ul>'
        $('#relevantArticles').append(relevantArticleListHtml)
      },
      error: function () {
        $('#relevantArticles').remove()
      },
    })
  },
  /*
   * @description 加载站外相关文章
   * @param {String} tags 文章 tags
   * @param {String} headTitle 站外相关文章标题
   */
  loadExternalRelevantArticles: function (tags, headTitle) {
    var tips = this.tips
    try {
      $.ajax({
        url: 'https://rhythm.b3log.org/get-articles-by-tags.do?tags=' + tags
        + '&blogHost=' + tips.blogHost + '&paginationPageSize=' +
        tips.externalRelevantArticlesDisplayCount,
        type: 'GET',
        cache: true,
        dataType: 'jsonp',
        error: function () {
          $('#externalRelevantArticles').remove()
        },
        success: function (data, textStatus) {
          var articles = data.articles
          if (!articles || 0 === articles.length) {
            $('#externalRelevantArticles').remove()
            return
          }
          var listHtml = ''
          for (var i = 0; i < articles.length; i++) {
            var article = articles[i]
            var title = article.articleTitle
            var articleLiHtml = '<li>'
              + '<a rel=\'nofollow\' title=\'' + title +
              '\' target=\'_blank\' href=\'' + article.articlePermalink + '\'>'
              + title + '</a></li>'
            listHtml += articleLiHtml
          }

          var titleHTML = headTitle ? headTitle : '<h4>' +
            tips.externalRelevantArticles1Label + '</h4>'
          var randomArticleListHtml = titleHTML
            + '<ul>'
            + listHtml + '</ul>'
          $('#externalRelevantArticles').append(randomArticleListHtml)
        },
      })
    } catch (e) {
      // 忽略相关文章加载异常：load script error
    }
  },
  /*
   * @description 提交评论
   * @param {String} commentId 回复评论时的评论 id
   */
  submitComment: function () {
    var that = this,
      tips = this.tips,
      type = 'article'
    if (tips.externalRelevantArticlesDisplayCount === undefined) {
      type = 'page'
    }

    if (vditor.getValue().length > 1 && vditor.getValue().length < 500) {
      $('#soloEditorAdd').attr('disabled', 'disabled')
      var requestJSONObject = {
        'oId': tips.oId,
        'commentContent': vditor.getValue(),
      }

      if (this.currentCommentId) {
        requestJSONObject.commentOriginalCommentId = this.currentCommentId
      }

      $.ajax({
        type: 'POST',
        url: latkeConfig.servePath + '/' + type + '/comments',
        cache: false,
        contentType: 'application/json',
        data: JSON.stringify(requestJSONObject),
        success: function (result) {
          $('#soloEditorAdd').removeAttr('disabled')
          if (!result.sc) {
            $('#soloEditorError').html(result.msg)
            return
          }

          that.toggleEditor()
          vditor.setValue('')
          that.addCommentAjax(Util.replaceEmString(result.cmtTpl))
        },
      })
    } else {
      $('#soloEditorError').text(that.tips.commentContentCannotEmptyLabel)
    }
  },
  /*
   * @description 添加回复评论表单
   * @param {String} id 被回复的评论 id
   */
  addReplyForm: function (id, name) {
    var that = this
    that.currentCommentId = id
    this.toggleEditor(id, name)
  },
  /*
   * @description 隐藏回复评论的浮出层
   * @parma {String} id 被回复的评论 id
   */
  hideComment: function (id) {
    $('#commentRef' + id).hide()
  },
  /*
   * @description 显示回复评论的浮出层
   * @parma {Dom} it 触发事件的 dom
   * @param {String} id 被回复的评论 id
   * @param {Int} top 位置相对浮出层的高度
   * @param {String} [parentTag] it 如果嵌入在 position 为 relative 的元素 A 中时，需取到 A 元素
   */
  showComment: function (it, id, top, parentTag) {
    var positionTop = parseInt($(it).position().top)
    if (parentTag) {
      positionTop = parseInt($(it).parents(parentTag).position().top)
    }
    if ($('#commentRef' + id).length > 0) {
      // 此处重复设置 top 是由于评论为异步，原有回复评论的显示位置应往下移动
      $('#commentRef' + id).show().css('top', (positionTop + top) + 'px')
    } else {
      var $refComment = $('#' + id).clone()
      $refComment.addClass('comment-body-ref').attr('id', 'commentRef' + id)
      $refComment.find('#replyForm').remove()
      $('#comments').append($refComment)
      $('#commentRef' + id).css('top', (positionTop + top) + 'px')
    }
  },
  /*
   * @description 回复不刷新，将回复内容异步添加到评论列表中
   * @parma {String} commentHTML 回复内容 HTML
   */
  addCommentAjax: function (commentHTML) {
    if ($('#comments').children().length > 0) {
      $($('#comments').children()[0]).before(commentHTML)
    } else {
      $('#comments').html(commentHTML)
    }
    window.location.hash = '#comments'
  },
})