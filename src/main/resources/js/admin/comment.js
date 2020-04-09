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
 *  common comment for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.0, Jan 18, 2020
 */

admin.comment = {
  /*
   * 打开评论窗口
   * @id 该评论对应的 id
   * @fromId 该评论来自文章/草稿
   */
  open: function (id, fromId) {
    this.getList(id, fromId)
    $('#' + fromId + 'Comments').dialog('open')
  },

  /*
   * 获取评论列表
   *
   * @onId 该评论对应的文章 id
   * @fromId 该评论来自文章/草稿
   */
  getList: function (onId, fromId) {
    $('#loadMsg').text(Label.loadingLabel)
    $('#tipMsg').text('')
    $('#' + fromId + 'Comments').html('')

    var from = 'article'
    if (fromId === 'page') {
      from = 'page'
    }

    $.ajax({
      url: Label.servePath + '/console/comments/' + from + '/' + onId,
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (!result.sc) {
          $('#loadMsg').text('')
          return
        }

        var comments = result.comments,
          commentsHTML = ''
        for (var i = 0; i < comments.length; i++) {
          var hrefHTML = '<a target=\'_blank\' href=\'' + comments[i].commentURL +
            '\'>',
            content = comments[i].commentContent,
            contentHTML = content

          if (comments[i].commentURL === 'http://') {
            hrefHTML = '<a target=\'_blank\'>'
          }

          commentsHTML += '<div class=\'comment-title\'><span class=\'fn__left\'>'
            + hrefHTML + comments[i].commentName + '</a>'

          if (comments[i].commentOriginalCommentName) {
            commentsHTML += '@' + comments[i].commentOriginalCommentName
          }
          commentsHTML += '</span><a title=\'' + Label.removeLabel +
            '\' class=\'fn__right fn__pointer\' href="javascript:admin.comment.del(\''
            + comments[i].oId + '\', \'' + fromId + '\', \'' + onId + '\')">' +
            Label.removeLabel + '</a><span class=\'fn__right\'>&nbsp;&nbsp;'
            + $.bowknot.getDate(comments[i].commentTime)
            +
            '&nbsp;</span><div class=\'fn__clear\'></div></div><div class=\'vditor-reset\'>'
            + contentHTML + '</div>'
        }
        if ('' === commentsHTML) {
          commentsHTML = Label.noCommentLabel
        }

        $('#' + fromId + 'Comments').html(commentsHTML)

        Util.parseMarkdown()
        $('#loadMsg').text('')
      },
    })
  },

  /*
   * 删除评论
   * @id 评论 id
   * @fromId 该评论来自文章/草稿
   * @articleId 该评论对应的文章 id
   */
  del: function (id, fromId, articleId) {
    var isDelete = confirm(Label.confirmRemoveLabel + Label.commentLabel + '?')
    if (isDelete) {
      $('#loadMsg').text(Label.loadingLabel)
      var from = 'article'
      if (fromId === 'page') {
        from = 'page'
      }

      $.ajax({
        url: Label.servePath + '/console/' + from + '/comment/' + id,
        type: 'DELETE',
        cache: false,
        success: function (result, textStatus) {
          $('#tipMsg').text(result.msg)
          if (!result.sc) {
            $('#loadMsg').text('')
            return
          }

          admin.comment.getList(articleId, fromId)

          $('#loadMsg').text('')
        },
      })
    }
  },
}
