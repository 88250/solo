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
 * draft list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.2, Jan 10, 2020
 */

/* draft-list 相关操作 */
admin.draftList = {
  tablePagination: new TablePaginate('draft'),

  /*
   * 初始化 table, pagination, comments dialog
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
  },

  /*
   * 根据当前页码获取列表
   * @pagNum 当前页码
   */
  getList: function (pageNum) {
    $('#loadMsg').text(Label.loadingLabel)
    var that = this

    $.ajax({
      url: Label.servePath + '/console/articles/status/unpublished/' + pageNum +
        '/' + Label.PAGE_SIZE + '/' + Label.WINDOW_SIZE,
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
          articleData[i].tags = articles[i].articleTags
          articleData[i].date = $.bowknot.getDate(
            articles[i].articleCreateTime)
          articleData[i].comments = `<span data-uvstatcmt="${articles[i].oId}">0</span>`
          articleData[i].articleViewCount = '<span data-uvstaturl="' +
            Label.servePath + articles[i].articlePermalink + '">0</span>'
          articleData[i].author = articles[i].authorName
          articleData[i].title = '<a class=\'no-underline\' href=\'' +
            Label.servePath +
            articles[i].articlePermalink + '\' target=\'_blank\'>' +
            articles[i].articleTitle + '</a><span class=\'table-tag\'>' +
            articles[i].articleTags + '</span>'
          articleData[i].expendRow = '<a href=\'javascript:void(0)\' onclick="admin.article.get(\'' +
            articles[i].oId + '\', false);">' + Label.updateLabel + '</a>  \
                                <a href=\'javascript:void(0)\' onclick="admin.article.del(\'' +
            articles[i].oId + '\', \'draft\', \'' +
            encodeURIComponent(articles[i].articleTitle) + '\')">' +
            Label.removeLabel + '</a>'
        }

        that.tablePagination.updateTablePagination(articleData, pageNum,
          result.pagination)
        Util.uvstat.renderStat()
        Util.uvstat.renderCmtStat()
        $('#loadMsg').text('')
      },
    })
  },
}

/*
 * 注册到 admin 进行管理
 */
admin.register['draft-list'] = {
  'obj': admin.draftList,
  'init': admin.draftList.init,
  'refresh': admin.draftList.getList,
}
