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
 *  about for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.0.0, Jul 17, 2019
 */

/* about 相关操作 */
admin.about = {
  init: function () {
    $.ajax({
      url: 'https://rhythm.b3log.org/version/solo/latest/' + Label.version,
      type: 'GET',
      cache: false,
      dataType: 'jsonp',
      success: function (data, textStatus) {
        var version = data.soloVersion
        if (version === Label.version) {
          $('#aboutLatest').text(Label.upToDateLabel)
        } else {
          $('#aboutLatest').html(Label.outOfDateLabel +
            '<a href=\'' + data.soloDownload + '\'>' + version + '</a>')
        }
      },
      complete: function (XHR, TS) {
        admin.clearTip()
      },
    })

    $.ajax({
      url: 'https://hacpai.com/apis/sponsors',
      type: 'GET',
      dataType: 'jsonp',
      jsonp: 'callback',
      success: function (data, textStatus) {
        var payments = data.data.payments
        var sponsprsHTML = ''
        for (var i = 0; i < payments.length; i++) {
          var userName = '<b>匿名好心人</b>'
          if (payments[i].paymentUserName) {
            userName = '<a href="https://hacpai.com/member/' +
              payments[i].paymentUserName + '"><b>' + payments[i].paymentUserName +
              '</b></a>'
          }
          sponsprsHTML += '<li><div class="fn__flex">' + userName + ' <span class="ft__green fn__flex-1">&nbsp;' +
            payments[i].paymentAmount + 'RMB</span><time class="ft__fade">&nbsp;&nbsp;' +
            payments[i].paymentTimeStr + '</time></div><div>' +
            payments[i].paymentMemo + '</div></li>'
        }
        $('#adminAboutSponsors').html(sponsprsHTML)
      },
      complete: function (XHR, TS) {
        admin.clearTip()
      },
    })

  },
}

/*
 * 注册到 admin 进行管理
 */
admin.register['about'] = {
  'obj': admin.about,
  'init': admin.about.init,
  'refresh': function () {
    admin.clearTip()
  },
}
