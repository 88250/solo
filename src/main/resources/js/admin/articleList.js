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
import { TablePaginate } from './tablePaginate'
/**
 * article list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.3, Jun 24, 2020
 */

/* article-list 相关操作 */
admin.articleList = {
  tablePagination: new TablePaginate('article'),

  /*
   * 初始化 table, pagination
   */
  init: function (page) {
    this.tablePagination.buildTable([
      {
        text: Label.titleLabel,
        index: 'title',
        minWidth: 110,
        style: 'padding-left: 12px;font-size:14px;',
      }, {
        text: Label.authorLabel,
        index: 'author',
        width: 150,
        style: 'padding-left: 12px;',
      }, {
        text: Label.commentLabel,
        index: 'comments',
        width: 80,
        style: 'padding-left: 12px;',
      }, {
        text: Label.viewLabel,
        width: 60,
        index: 'articleViewCount',
        style: 'padding-left: 12px;',
      }, {
        text: Label.dateLabel,
        index: 'date',
        width: 90,
        style: 'padding-left: 12px;',
      }])
    this.tablePagination.initPagination()
    this.getList(page)

    var that = this
    $('#articleListBtn').click(function () {
      that.getList(page)
    })
    $('#articleListInput').keypress(function (event) {
      if (event.keyCode === 13) {
        that.getList(page)
      }
    })
  },

  /**
   * 同步到社区
   * @param id 文章 id
   */
  syncToHacpai: function (id) {
    const licenseConfirm = '文章推送社区后将以 署名-相同方式共享 4.0 国际 (CC BY-SA 4.0) (https://creativecommons.org/licenses/by-sa/4.0/deed.zh) 许可协议发布，请确认是否推送？'
    if (!confirm(licenseConfirm)) {
      return;
    }

    $.ajax({
      url: Label.servePath + '/console/article/push2rhy?id=' + id,
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').html(Label.pushSuccLabel)
      },
    })
  },

  /*
   * 根据当前页码获取列表
   * @pagNum 当前页码
   */
  getList: function (pageNum) {
    var that = this
    $('#loadMsg').text(Label.loadingLabel)
    $.ajax({
      url: Label.servePath + '/console/articles/status/published/' +
        pageNum + '/' + Label.PAGE_SIZE + '/' + Label.WINDOW_SIZE + '?k=' +
        $('#articleListInput').val(),
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        var articles = result.articles,
          articleData = []
        for (var i = 0; i < articles.length; i++) {
          articleData[i] = {}
          articleData[i].title = '<a href="' + Label.servePath +
            articles[i].articlePermalink + '" target=\'_blank\' title=\'' +
            articles[i].articleTitle + '\' class=\'no-underline\'>'
            + articles[i].articleTitle + '</a><span class=\'table-tag\'>' +
            articles[i].articleTags + '</span>'
          articleData[i].date = $.bowknot.getDate(articles[i].articleCreateTime)
          articleData[i].comments = `<span data-uvstatcmt="${articles[i].oId}">0</span>`
          articleData[i].articleViewCount = '<span data-uvstaturl="' +
            Label.servePath + articles[i].articlePermalink + '">0</span>'
          articleData[i].author = articles[i].authorName

          var topClass = articles[i].articlePutTop
            ? Label.cancelPutTopLabel
            : Label.putTopLabel
          articleData[i].expendRow = '<a href=\'javascript:void(0)\' onclick="admin.article.get(\'' +
            articles[i].oId + '\', true)">' + Label.updateLabel + '</a>  \
                                <a href=\'javascript:void(0)\' onclick="admin.article.del(\'' +
            articles[i].oId + '\', \'article\', \'' +
            encodeURIComponent(articles[i].articleTitle) + '\')">' +
            Label.removeLabel + '</a>  \
                                <a href=\'javascript:void(0)\' onclick="admin.articleList.syncToHacpai(\'' +
            articles[i].oId + '\')">' + Label.pushToHacpaiLabel + '</a>  \
                                <a href=\'javascript:void(0)\' onclick="admin.articleList.popTop(this, \'' +
            articles[i].oId + '\')">' + topClass + '</a>'
        }

        that.tablePagination.updateTablePagination(articleData, pageNum,
          result.pagination)

        Util.uvstat.renderStat()
        Util.uvstat.renderCmtStat()

        $('#loadMsg').text('')
      },
    })
  },

  /*
   * 制定或者取消置顶
   * @it 触发事件的元素本身
   * @id 草稿 id
   */
  popTop: function (it, id) {
    $('#loadMsg').text(Label.loadingLabel)
    $('#tipMsg').text('')

    var $it = $(it),
      ajaxUrl = 'canceltop',
      tip = Label.putTopLabel

    if ($it.html() === Label.putTopLabel) {
      ajaxUrl = 'puttop'
      tip = Label.cancelPutTopLabel
    }

    $.ajax({
      url: Label.servePath + '/console/article/' + ajaxUrl + '/' + id,
      type: 'PUT',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        $it.html(tip)
        $('#loadMsg').text('')
      },
    })
  },
}

/*
 * 注册到 admin 进行管理
 */
admin.register['article-list'] = {
  'obj': admin.articleList,
  'init': admin.articleList.init,
  'refresh': admin.articleList.getList,
}
