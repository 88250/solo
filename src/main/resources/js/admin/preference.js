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
 * preference for admin.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.4, Jan 24, 2020
 */

/* preference 相关操作 */
admin.preference = {
  locale: '',
  /*
   * 初始化
   */
  init: function () {
    $('#tabPreference').tabs()

    $.ajax({
      url: Label.servePath + '/console/preference/',
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (!result.sc) {
          $('#loadMsg').text('')
          return
        }

        var preference = result.preference

        $('#metaKeywords').val(preference.metaKeywords)
        $('#metaDescription').val(preference.metaDescription)
        $('#blogTitle').val(preference.blogTitle)
        $('#blogSubtitle').val(preference.blogSubtitle)
        $('#mostCommentArticleDisplayCount').val(preference.mostCommentArticleDisplayCount)
        $('#mostViewArticleDisplayCount').val(preference.mostViewArticleDisplayCount)
        $('#recentCommentDisplayCount').val(preference.recentCommentDisplayCount)
        $('#mostUsedTagDisplayCount').val(preference.mostUsedTagDisplayCount)
        $('#articleListDisplayCount').val(preference.articleListDisplayCount)
        $('#articleListPaginationWindowSize').val(preference.articleListPaginationWindowSize)
        $('#localeString').val(preference.localeString)
        $('#timeZoneId').val(preference.timeZoneId)
        $('#noticeBoard').val(preference.noticeBoard)
        $('#footerContent').val(preference.footerContent)
        $('#htmlHead').val(preference.htmlHead)
        $('#externalRelevantArticlesDisplayCount').val(preference.externalRelevantArticlesDisplayCount)
        $('#relevantArticlesDisplayCount').val(preference.relevantArticlesDisplayCount)
        $('#randomArticlesDisplayCount').val(preference.randomArticlesDisplayCount)
        $('#customVars').val(preference.customVars)

        'true' === preference.enableArticleUpdateHint ? $('#enableArticleUpdateHint').attr('checked', 'checked') : $('#enableArticleUpdateHint').removeAttr('checked')
        'true' === preference.allowVisitDraftViaPermalink ? $('#allowVisitDraftViaPermalink').attr('checked', 'checked') : $('#allowVisitDraftViaPermalink').removeAttr('checked')
        'true' === preference.commentable ? $('#commentable').attr('checked', 'checked') : $('#commentable').removeAttr('checked')
        'true' === preference.syncGitHub ? $('#syncGitHub').attr('checked', 'checked') : $('#syncGitHub').removeAttr('checked')
        'true' === preference.pullGitHub ? $('#pullGitHub').attr('checked', 'checked') : $('#pullGitHub').removeAttr('checked')
        'true' === preference.showCodeBlockLn ? $('#showCodeBlockLn').attr('checked', 'checked') : $('#showCodeBlockLn').removeAttr('checked')

        'true' === preference.footnotes ? $('#footnotes').attr('checked', 'checked') : $('#footnotes').removeAttr('checked')
        'true' === preference.showToC ? $('#showToC').attr('checked', 'checked') : $('#showToC').removeAttr('checked')
        'true' === preference.autoSpace ? $('#autoSpace').attr('checked', 'checked') : $('#autoSpace').removeAttr('checked')
        'true' === preference.fixTermTypo ? $('#fixTermTypo').attr('checked', 'checked') : $('#fixTermTypo').removeAttr('checked')
        'true' === preference.chinesePunct ? $('#chinesePunct').attr('checked', 'checked') : $('#chinesePunct').removeAttr('checked')
        'true' === preference.inlineMathAllowDigitAfterOpenMarker ? $('#inlineMathAllowDigitAfterOpenMarker').attr('checked', 'checked') : $('#inlineMathAllowDigitAfterOpenMarker').removeAttr('checked')

        admin.preference.locale = preference.localeString

        // sign
        var signs = eval('(' + preference.signs + ')')
        for (var j = 1; j < signs.length; j++) {
          $('#preferenceSign' + j).val(signs[j].signHTML)
        }

        $('#articleListDisplay').val(preference.articleListStyle)
        $('#hljsTheme').val(preference.hljsTheme)
        $('#feedOutputMode').val(preference.feedOutputMode)
        $('#feedOutputCnt').val(preference.feedOutputCnt)
        $('#faviconURL').val(preference.faviconURL)

        $('#loadMsg').text('')
      },
    })
  },
  /*
   * @description 参数校验
   */
  validate: function () {
    if (!/^\d+$/.test($('#mostUsedTagDisplayCount').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' +
        Label.indexTagDisplayCntLabel + '] ' +
        Label.nonNegativeIntegerOnlyLabel)
      $('#mostUsedTagDisplayCount').focus()
      return false
    } else if (!/^\d+$/.test($('#recentCommentDisplayCount').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' +
        Label.indexRecentCommentDisplayCntLabel + '] ' +
        Label.nonNegativeIntegerOnlyLabel)
      $('#recentCommentDisplayCount').focus()
      return false
    } else if (!/^\d+$/.test($('#mostCommentArticleDisplayCount').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' +
        Label.indexMostCommentArticleDisplayCntLabel + '] ' +
        Label.nonNegativeIntegerOnlyLabel)
      $('#mostCommentArticleDisplayCount').focus()
      return false
    } else if (!/^\d+$/.test($('#mostViewArticleDisplayCount').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' +
        Label.indexMostViewArticleDisplayCntLabel + '] ' +
        Label.nonNegativeIntegerOnlyLabel)
      $('#mostViewArticleDisplayCount').focus()
      return false
    } else if (!/^\d+$/.test($('#articleListDisplayCount').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' + Label.pageSizeLabel +
        '] ' + Label.nonNegativeIntegerOnlyLabel)
      $('#articleListDisplayCount').focus()
      return false
    } else if (!/^\d+$/.test($('#articleListPaginationWindowSize').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' + Label.windowSizeLabel +
        '] ' + Label.nonNegativeIntegerOnlyLabel)
      $('#articleListPaginationWindowSize').focus()
      return false
    } else if (!/^\d+$/.test($('#randomArticlesDisplayCount').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' +
        Label.randomArticlesDisplayCntLabel + '] ' +
        Label.nonNegativeIntegerOnlyLabel)
      $('#randomArticlesDisplayCount').focus()
      return false
    } else if (!/^\d+$/.test($('#relevantArticlesDisplayCount').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' +
        Label.relevantArticlesDisplayCntLabel + '] ' +
        Label.nonNegativeIntegerOnlyLabel)
      $('#relevantArticlesDisplayCount').focus()
      return false
    } else if (!/^\d+$/.test(
      $('#externalRelevantArticlesDisplayCount').val())) {
      $('#tipMsg').text('[' + Label.paramSettingsLabel + ' - ' +
        Label.externalRelevantArticlesDisplayCntLabel + '] ' +
        Label.nonNegativeIntegerOnlyLabel)
      $('#externalRelevantArticlesDisplayCount').focus()
      return false
    }
    return true
  },
  /*
   * @description 更新
   */
  update: function () {
    if (!admin.preference.validate()) {
      return
    }

    $('#tipMsg').text('')
    $('#loadMsg').text(Label.loadingLabel)
    var signs = [
      {
        'oId': 0,
        'signHTML': '',
      }, {
        'oId': 1,
        'signHTML': $('#preferenceSign1').val(),
      }, {
        'oId': 2,
        'signHTML': $('#preferenceSign2').val(),
      }, {
        'oId': 3,
        'signHTML': $('#preferenceSign3').val(),
      }
    ]

    var requestJSONObject = {
      'preference': {
        'metaKeywords': $('#metaKeywords').val(),
        'metaDescription': $('#metaDescription').val(),
        'blogTitle': $('#blogTitle').val(),
        'blogSubtitle': $('#blogSubtitle').val(),
        'mostCommentArticleDisplayCount': $('#mostCommentArticleDisplayCount').val(),
        'mostViewArticleDisplayCount': $('#mostViewArticleDisplayCount').val(),
        'recentCommentDisplayCount': $('#recentCommentDisplayCount').val(),
        'mostUsedTagDisplayCount': $('#mostUsedTagDisplayCount').val(),
        'articleListDisplayCount': $('#articleListDisplayCount').val(),
        'articleListPaginationWindowSize': $('#articleListPaginationWindowSize').val(),
        'localeString': $('#localeString').val(),
        'timeZoneId': $('#timeZoneId').val(),
        'noticeBoard': $('#noticeBoard').val(),
        'footerContent': $('#footerContent').val(),
        'htmlHead': $('#htmlHead').val(),
        'externalRelevantArticlesDisplayCount': $(
          '#externalRelevantArticlesDisplayCount').val(),
        'relevantArticlesDisplayCount': $('#relevantArticlesDisplayCount').val(),
        'randomArticlesDisplayCount': $('#randomArticlesDisplayCount').val(),
        'enableArticleUpdateHint': $('#enableArticleUpdateHint').prop('checked'),
        'signs': signs,
        'allowVisitDraftViaPermalink': $('#allowVisitDraftViaPermalink').prop('checked'),
        'articleListStyle': $('#articleListDisplay').val(),
        'hljsTheme': $('#hljsTheme').val(),
        'feedOutputMode': $('#feedOutputMode').val(),
        'feedOutputCnt': $('#feedOutputCnt').val(),
        'faviconURL': $('#faviconURL').val(),
        'syncGitHub': $('#syncGitHub').prop('checked'),
        'pullGitHub': $('#pullGitHub').prop('checked'),
        'showCodeBlockLn': $('#showCodeBlockLn').prop('checked'),
        'commentable': $('#commentable').prop('checked'),
        'customVars': $('#customVars').val(),
        'footnotes': $('#footnotes').prop('checked'),
        'showToC': $('#showToC').prop('checked'),
        'autoSpace': $('#autoSpace').prop('checked'),
        'fixTermTypo': $('#fixTermTypo').prop('checked'),
        'chinesePunct': $('#chinesePunct').prop('checked'),
        'inlineMathAllowDigitAfterOpenMarker': $('#inlineMathAllowDigitAfterOpenMarker').prop('checked'),
      },
    }

    $.ajax({
      url: Label.servePath + '/console/preference/',
      type: 'PUT',
      cache: false,
      data: JSON.stringify(requestJSONObject),
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (!result.sc) {
          $('#loadMsg').text('')
          return
        }

        if ($('#localeString').val() !== admin.preference.locale) {
          window.location.reload()
        }

        $('#loadMsg').text('')
      },
    })
  },
}

/*
 * 注册到 admin 进行管理
 */
admin.register['preference'] = {
  'obj': admin.preference,
  'init': admin.preference.init,
  'refresh': function () {
    admin.clearTip()
  },
}
