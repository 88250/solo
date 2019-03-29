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
 * preference for admin.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 0.1.0.0, Mar 29, 2019
 */

/* theme list 相关操作 */
admin.themeList = {
  /*
   * 初始化
   */
  init: function () {
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

        // skin
        var skins = eval('(' + preference.skins + ')')
        var skinsHTML = ''
        for (var i = 0; i < skins.length; i++) {
          var selectedClass = ''
          if (skins[i].skinName === preference.skinName
            && skins[i].skinDirName === preference.skinDirName) {
            selectedClass += ' selected'
          }
          skinsHTML += '<div class="fn__left skinItem' + selectedClass +
            '"><div class="ft__center">' +
            skins[i].skinName
            + '</div><img class="skinPreview" src="'
            + Label.staticServePath + '/skins/' + skins[i].skinDirName
            + '/preview.png"/><div><button class="small update fn__left" data-name="' +
            skins[i].skinDirName + '">' + Label.enableLabel +
            '</button><button class="small fn__right" onclick="window.open(\'' +
            Label.servePath + '?skin=' + skins[i].skinName + '\')">'
            + Label.previewLabel + '</button><button class="small mobile fn__left">' +
            Label.setMobileLabel + '</button></div></div>'
        }
        $('#skinMain').append(skinsHTML + '<div class=\'fn__clear\'></div>')

        $('.skinItem .update').click(function () {
          $('.skinItem').removeClass('selected')
          $(this).closest('.skinItem').addClass('selected')
          admin.preference.update()
        })
        $('.skinItem .mobile').click(function () {
          $('.skinItem').removeClass('selected')
          $(this).closest('.skinItem').addClass('selected')
          admin.preference.update()
        })

        $('#loadMsg').text('')
      },
    })
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

    var requestJSONObject = {
      'preference': {
        'skinDirName': $('#skinMain').data('skinDirName'),
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
admin.register['theme-list'] = {
  'obj': admin.themeList,
  'init': admin.themeList.init,
  'refresh': function () {
    $('#loadMsg').text('')
  },
}
