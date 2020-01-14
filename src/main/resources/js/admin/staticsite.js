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
 * staticsite for admin.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.1, Jan 14, 2020
 */

/* staticsite 相关操作 */
admin.staticsite = {
  /*
   * 初始化
   */
  init: function () {
    $('#loadMsg').text('')
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
