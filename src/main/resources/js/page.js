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
 * @fileoverview Page util, load highlight and process comment.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 2.8.0.1, Apr 30, 2020
 */
window.Page = function (tips) {
  this.tips = tips
}

$.extend(Page.prototype, {
  /**
   * 第三方评论
   */
  vcomment: function () {
    const $vcomment = $('#vcomment')
    if ($vcomment.length === 0) {
      return
    }
    const vcomment = new Vcomment({
      id: 'vcomment',
      postId: $vcomment.data('postid'),
      url: 'https://ld246.com',
      userName: $vcomment.data('name'),
      currentPage: 1,
      vditor: {
        lineNumber: Label.showCodeBlockLn,
        hljsEnable: !Label.luteAvailable,
        hljsStyle: Label.hljsStyle,
      },
      error () {
        $vcomment.remove()
      },
    })

    vcomment.render()
  },
  /**
   * 分享
   */
  share: function () {
    var $this = $('.article__share')
    if ($this.length === 0) {
      return
    }
    var $qrCode = $this.find('.item__qr')
    var shareURL = $this.data('url')
    var avatarURL = $this.data('avatar')
    var title = encodeURIComponent($this.data('title') + ' - ' +
      $this.data('blogtitle'))
    var url = encodeURIComponent(shareURL)

    var urls = {}
    urls.tencent = 'http://share.v.t.qq.com/index.php?c=share&a=index&title=' +
      title +
      '&url=' + url + '&pic=' + avatarURL
    urls.weibo = 'http://v.t.sina.com.cn/share/share.php?title=' +
      title + '&url=' + url + '&pic=' + avatarURL
    urls.qqz = 'https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url='
      + url + '&sharesource=qzone&title=' + title + '&pics=' + avatarURL
    urls.twitter = 'https://twitter.com/intent/tweet?status=' + title + ' ' +
      url

    $this.find('span').click(function () {
      var key = $(this).data('type')

      if (!key) {
        return
      }

      if (key === 'wechat') {
        if (typeof QRious === 'undefined') {
          Util.addScript(Label.staticServePath + '/js/lib/qrious.min.js',
            'qriousScript')
        }

        if ($qrCode.css('background-image') === 'none') {
          const qr = new QRious({
            padding: 0,
            element: $qrCode[0],
            value: shareURL,
            size: 99,
          })
          $qrCode.css('background-image', `url(${qr.toDataURL('image/jpeg')})`).
            show()
        } else {
          $qrCode.slideToggle()
        }
        return false
      }

      window.open(urls[key], '_blank', 'top=100,left=200,width=648,height=618')
    })
  },
  /*
   * @description 文章加载
   */
  load: function () {
    var that = this
    that.vcomment()
  },
  /*
   * @description 加载随机文章
   * @param {String} headTitle 随机文章标题
   */
  loadRandomArticles: function (headTitle) {
    var randomArticles1Label = this.tips.randomArticles1Label
    // getRandomArticles
    $.ajax({
      url: Label.servePath + '/articles/random.json',
      type: 'GET',
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
            title + '\' href=\'' + Label.servePath +
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
      url: Label.servePath + '/article/relevant/' + id + '.json',
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
            Label.servePath + article.articlePermalink + '\'>'
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
})
