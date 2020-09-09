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
      url: 'https://ld246.com/apis/sponsors',
      type: 'GET',
      dataType: 'jsonp',
      jsonp: 'callback',
      success: function (data, textStatus) {
        var payments = data.data.payments
        var sponsprsHTML = ''
        for (var i = 0; i < payments.length; i++) {
          var userName = '<b>匿名好心人</b>'
          if (payments[i].paymentUserName) {
            userName = '<a href="https://ld246.com/member/' +
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
