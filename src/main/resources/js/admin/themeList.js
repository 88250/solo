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
 * @version 0.1.0.0, Mar 29, 2019
 */

/* theme list 相关操作 */
admin.themeList = {
  skinDirName: '',
  mobileSkinDirName: '',
  /*
   * 初始化
   */
  init: function () {
    $.ajax({
      url: Label.servePath + '/console/skin',
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        admin.themeList.skinDirName = result.skin.skinDirName
        admin.themeList.mobileSkinDirName = result.skin.mobileSkinDirName

        var skins = JSON.parse(result.skin.skins)
        var skinsHTML = ''

        for (var i = 0; i < skins.length; i++) {
          var selectedClass = ''
          if (skins[i].skinDirName === result.skin.skinDirName) {
            selectedClass = ' selected'
          }

          skinsHTML += '<div class="fn__left skinItem' + selectedClass +
            '"><div class="ft__center">' + skins[i].skinDirName
            + '</div><img class="skinPreview" src="'
            + Label.staticServePath + '/skins/' + skins[i].skinDirName
            + '/preview.png"/><div>'

          if (skins[i].skinDirName !== result.skin.skinDirName) {
            skinsHTML += '<button class="small update fn__left" data-name="' +
              skins[i].skinDirName + '">' + Label.enableLabel +
              '</button>'
          }

          if (skins[i].skinDirName !== result.skin.mobileSkinDirName) {
            skinsHTML += '<button class="small mobile fn__left" data-name="' +
              skins[i].skinDirName + '">' +
              Label.setMobileLabel + '</button>'
          }

          skinsHTML += '<button class="small fn__right" onclick="window.open(\'' +
            Label.servePath + '?skin=' + skins[i].skinDirName + '\')">'
            + Label.previewLabel + '</button></div></div>'
        }
        $('#skinMain').html(skinsHTML + '<div class=\'fn__clear\'></div>')

        $('.skinItem .update').click(function () {
          admin.themeList.update($(this).data('name'), 'pc')
        })
        $('.skinItem .mobile').click(function () {
          admin.themeList.update($(this).data('name'), 'mobile')
        })

        $('#loadMsg').text('')
      },
    })
  },
  /*
   * @description 更新
   */
  update: function (skinDirName, type) {
    $('#tipMsg').text('')
    $('#loadMsg').text(Label.loadingLabel)

    var requestJSONObject = {
      skin: {
        skinDirName: admin.themeList.skinDirName,
        mobileSkinDirName: admin.themeList.mobileSkinDirName,
      },
    }

    if (type === 'pc') {
      requestJSONObject.skin.skinDirName = skinDirName
    } else {
      requestJSONObject.skin.mobileSkinDirName = skinDirName
    }

    $.ajax({
      url: Label.servePath + '/console/skin',
      type: 'PUT',
      cache: false,
      data: JSON.stringify(requestJSONObject),
      success: function (result, textStatus) {
        sessionStorage.removeItem('skin')
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }
        admin.themeList.init()
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
