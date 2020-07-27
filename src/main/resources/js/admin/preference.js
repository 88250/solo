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
 * preference for admin.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.6, May 20, 2020
 */

/* preference 相关操作 */
admin.preference = {
  locale: '',
  editorMode: '',
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
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        var preference = result.preference

        $('#metaKeywords').val(preference.metaKeywords)
        $('#metaDescription').val(preference.metaDescription)
        $('#blogTitle').val(preference.blogTitle)
        $('#blogSubtitle').val(preference.blogSubtitle)
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
        $('#githubPAT').val(preference.githubPAT)

        'true' === preference.enableArticleUpdateHint ? $('#enableArticleUpdateHint').attr('checked', 'checked') : $('#enableArticleUpdateHint').removeAttr('checked')
        'true' === preference.allowVisitDraftViaPermalink ? $('#allowVisitDraftViaPermalink').attr('checked', 'checked') : $('#allowVisitDraftViaPermalink').removeAttr('checked')
        'true' === preference.syncGitHub ? $('#syncGitHub').attr('checked', 'checked') : $('#syncGitHub').removeAttr('checked')
        'true' === preference.pullGitHub ? $('#pullGitHub').attr('checked', 'checked') : $('#pullGitHub').removeAttr('checked')
        'true' === preference.showCodeBlockLn ? $('#showCodeBlockLn').attr('checked', 'checked') : $('#showCodeBlockLn').removeAttr('checked')
        'true' === preference.speech ? $('#speech').attr('checked', 'checked') : $('#speech').removeAttr('checked')
        'true' === preference.paragraphBeginningSpace ? $('#paragraphBeginningSpace').attr('checked', 'checked') : $('#paragraphBeginningSpace').removeAttr('checked')

        'true' === preference.footnotes ? $('#footnotes').attr('checked', 'checked') : $('#footnotes').removeAttr('checked')
        'true' === preference.showToC ? $('#showToC').attr('checked', 'checked') : $('#showToC').removeAttr('checked')
        'true' === preference.autoSpace ? $('#autoSpace').attr('checked', 'checked') : $('#autoSpace').removeAttr('checked')
        'true' === preference.fixTermTypo ? $('#fixTermTypo').attr('checked', 'checked') : $('#fixTermTypo').removeAttr('checked')
        'true' === preference.chinesePunct ? $('#chinesePunct').attr('checked', 'checked') : $('#chinesePunct').removeAttr('checked')
        'true' === preference.inlineMathAllowDigitAfterOpenMarker ? $('#inlineMathAllowDigitAfterOpenMarker').attr('checked', 'checked') : $('#inlineMathAllowDigitAfterOpenMarker').removeAttr('checked')

        $("input:radio[value='" + preference.editorMode + "']").attr('checked','true');
        admin.preference.editorMode = preference.editorMode

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
        'mostUsedTagDisplayCount': $('#mostUsedTagDisplayCount').val(),
        'articleListDisplayCount': $('#articleListDisplayCount').val(),
        'articleListPaginationWindowSize': $('#articleListPaginationWindowSize').val(),
        'localeString': $('#localeString').val(),
        'timeZoneId': $('#timeZoneId').val(),
        'noticeBoard': $('#noticeBoard').val(),
        'footerContent': $('#footerContent').val(),
        'htmlHead': $('#htmlHead').val(),
        'externalRelevantArticlesDisplayCount': $('#externalRelevantArticlesDisplayCount').val(),
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
        'speech': $('#speech').prop('checked'),
        'paragraphBeginningSpace': $('#paragraphBeginningSpace').prop('checked'),
        'customVars': $('#customVars').val(),
        'githubPAT': $('#githubPAT').val(),
        'footnotes': $('#footnotes').prop('checked'),
        'showToC': $('#showToC').prop('checked'),
        'autoSpace': $('#autoSpace').prop('checked'),
        'fixTermTypo': $('#fixTermTypo').prop('checked'),
        'chinesePunct': $('#chinesePunct').prop('checked'),
        'inlineMathAllowDigitAfterOpenMarker': $('#inlineMathAllowDigitAfterOpenMarker').prop('checked'),
        'editorMode': $("input[name='editorMode']:checked").val(),
      },
    }

    $.ajax({
      url: Label.servePath + '/console/preference/',
      type: 'PUT',
      cache: false,
      data: JSON.stringify(requestJSONObject),
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        if ($('#localeString').val() !== admin.preference.locale || $("input[name='editorMode']:checked").val() !== admin.preference.editorMode) {
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
