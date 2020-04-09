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
 * staticsite for admin.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Feb 29, 2020
 */

/* staticsite 相关操作 */
admin.staticsite = {
  /*
   * 初始化
   */
  init: function () {
    $('#loadMsg').text('')

    const ssgURL = window.localStorage.getItem("solo_ssgurl")
    if (ssgURL) {
      $('#siteURL').val(ssgURL)
    }
  },
  /*
   * @description 更新
   */
  update: function () {
    $('#tipMsg').text('')
    $('#loadMsg').text(Label.loadingLabel)

    var requestJSONObject = {
      'url': $('#siteURL').val(),
    }

    $.ajax({
      url: Label.servePath + '/console/staticsite',
      type: 'PUT',
      cache: false,
      data: JSON.stringify(requestJSONObject),
      success: function (result) {
        $('#tipMsg').text(result.msg)
        $('#loadMsg').text('')

        console.log(requestJSONObject.url)
        window.localStorage.setItem("solo_ssgurl", requestJSONObject.url)
      },
    })
  },
}

/*
 * 注册到 admin 进行管理
 */
admin.register['staticsite'] = {
  'obj': admin.staticsite,
  'init': admin.staticsite.init,
  'refresh': admin.clearTip,
}
