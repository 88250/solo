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
 * user list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.1, Apr 6, 2020
 */

/* user-list 相关操作 */
admin.userList = {
  tablePagination: new TablePaginate('user'),
  pageInfo: {
    currentCount: 1,
    pageCount: 1,
    currentPage: 1,
  },
  userInfo: {
    'oId': '',
    'userRole': '',
  },
  /*
   * 初始化 table, pagination
   */
  init: function (page) {
    this.tablePagination.buildTable([
      {
        style: 'padding-left: 12px;',
        text: Label.userNameLabel,
        index: 'userName',
        width: 230,
      }, {
        style: 'padding-left: 12px;',
        text: Label.roleLabel,
        index: 'isAdmin',
        width: 120,
      }])

    this.tablePagination.initPagination()
    this.getList(page)

    $('#userUpdate').dialog({
      width: 700,
      height: 450,
      'modal': true,
      'hideFooter': true,
    })
  },
  /*
   * 根据当前页码获取列表
   * @pagNum 当前页码
   */
  getList: function (pageNum) {
    $('#loadMsg').text(Label.loadingLabel)
    $('#tipMsg').text('')
    this.pageInfo.currentPage = pageNum
    var that = this

    $.ajax({
      url: Label.servePath + '/console/users/' + pageNum + '/' +
        Label.PAGE_SIZE + '/' + Label.WINDOW_SIZE,
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        var users = result.users
        var userData = []
        admin.userList.pageInfo.currentCount = users.length
        admin.userList.pageInfo.pageCount = result.pagination.paginationPageCount
        if (users.length < 1) {
          $('#tipMsg').text('No user  ' + Label.reportIssueLabel)
          $('#loadMsg').text('')
          return
        }
        $('#tipMsg').text(Label.uploadMsg)

        for (var i = 0; i < users.length; i++) {
          userData[i] = {}
          userData[i].userName = `${users[i].userName} 
&nbsp; <a target="_blank" href="https://ld246.com/member/${users[i].userName}" title="社区个人主页"><span class="icon-hacpai"></span></a>`

          if ('adminRole' === users[i].userRole) {
            userData[i].isAdmin = '&nbsp;' + Label.administratorLabel
            userData[i].expendRow = '<a href=\'javascript:void(0)\' onclick="admin.userList.get(\'' +
              users[i].oId + '\', \'' + users[i].userRole + '\')">' +
              Label.updateLabel + '</a>'
          } else {
            userData[i].expendRow = '<a href=\'javascript:void(0)\' onclick="admin.userList.get(\'' +
              users[i].oId + '\', \'' + users[i].userRole + '\')">' +
              Label.updateLabel + '</a>\
                                <a href=\'javascript:void(0)\' onclick="admin.userList.del(\'' +
              users[i].oId + '\', \'' + encodeURIComponent(users[i].userName) +
              '\')">' + Label.removeLabel + '</a> ' +
              '<a href=\'javascript:void(0)\' onclick="admin.userList.changeRole(\'' +
              users[i].oId + '\')">' + Label.changeRoleLabel + '</a>'
            if ('defaultRole' === users[i].userRole) {
              userData[i].isAdmin = Label.commonUserLabel
            } else {
              userData[i].isAdmin = Label.visitorUserLabel
            }
          }

          that.tablePagination.updateTablePagination(userData, pageNum,
            result.pagination)

        }
        $('#loadMsg').text('')
      },
    })
  },
  /*
   * 获取用户
   * @id 用户 id
   */
  get: function (id, userRole) {
    $('#loadMsg').text(Label.loadingLabel)
    $('#tipMsg').text('')
    $('#userUpdate').dialog('open')

    $.ajax({
      url: Label.servePath + '/console/user/' + id,
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        $('#userURLUpdate').val(result.user.userURL)
        $('#userAvatarUpdate').val(result.user.userAvatar)
        $('#userB3KeyUpdate').val(result.user.userB3Key)
        $('#userNameUpdate').val(result.user.userName).data('userInfo', {
          'oId': id,
          'userRole': userRole,
        })

        $('#loadMsg').text('')
      },
    })
  },
  /*
   * 更新用户
   */
  update: function () {
    if (this.validate('Update')) {
      $('#loadMsg').text(Label.loadingLabel)
      $('#tipMsg').text('')

      var userInfo = $('#userNameUpdate').data('userInfo')
      var requestJSONObject = {
        'userName': $('#userNameUpdate').val(),
        'oId': userInfo.oId,
        'userURL': $('#userURLUpdate').val(),
        'userRole': userInfo.userRole,
        'userAvatar': $('#userAvatarUpdate').val(),
        'userB3Key': $('#userB3KeyUpdate').val(),
      }

      $.ajax({
        url: Label.servePath + '/console/user/',
        type: 'PUT',
        cache: false,
        data: JSON.stringify(requestJSONObject),
        success: function (result, textStatus) {
          $('#userUpdate').dialog('close')
          $('#tipMsg').text(result.msg)
          if (0 !== result.code) {
            $('#loadMsg').text('')
            return
          }

          admin.userList.getList(admin.userList.pageInfo.currentPage)

          $('#loadMsg').text('')
        },
      })
    }
  },
  /*
   * 删除用户
   * @id 用户 id
   * @userName 用户名称
   */
  del: function (id, userName) {
    var isDelete = confirm(Label.confirmRemoveLabel + Label.userLabel + '"' +
      htmlDecode(userName) + '"?')
    if (isDelete) {
      $('#loadMsg').text(Label.loadingLabel)
      $('#tipMsg').text('')

      $.ajax({
        url: Label.servePath + '/console/user/' + id,
        type: 'DELETE',
        cache: false,
        success: function (result, textStatus) {
          $('#tipMsg').text(result.msg)
          if (0 !== result.code) {
            $('#loadMsg').text('')
            return
          }

          var pageNum = admin.userList.pageInfo.currentPage
          if (admin.userList.pageInfo.currentCount === 1 &&
            admin.userList.pageInfo.pageCount !== 1 &&
            admin.userList.pageInfo.currentPage ===
            admin.userList.pageInfo.pageCount) {
            admin.userList.pageInfo.pageCount--
            pageNum = admin.userList.pageInfo.pageCount
          }
          var hashList = window.location.hash.split('/')
          if (pageNum !== parseInt(hashList[hashList.length - 1])) {
            admin.setHashByPage(pageNum)
          }
          admin.userList.getList(pageNum)

          $('#loadMsg').text('')
        },
      })
    }
  },
  /**
   * 修改角色
   * @param id
   */
  changeRole: function (id) {
    $('#tipMsg').text('')
    $.ajax({
      url: Label.servePath + '/console/changeRole/' + id,
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
        if (0 !== result.code) {
          $('#loadMsg').text('')
          return
        }

        var pageNum = admin.userList.pageInfo.currentPage
        if (admin.userList.pageInfo.currentCount === 1 &&
          admin.userList.pageInfo.pageCount !== 1 &&
          admin.userList.pageInfo.currentPage ===
          admin.userList.pageInfo.pageCount) {
          admin.userList.pageInfo.pageCount--
          pageNum = admin.userList.pageInfo.pageCount
        }
        var hashList = window.location.hash.split('/')
        if (pageNum !== parseInt(hashList[hashList.length - 1])) {
          admin.setHashByPage(pageNum)
        }
        admin.userList.getList(pageNum)

        $('#loadMsg').text('')
      },
    })
  },
  /*
   * 验证字段
   * @status 更新或者添加时进行验证
   */
  validate: function (status) {
    if (!status) {
      status = ''
    }
    var userName = $('#userName' + status).val().replace(/(^\s*)|(\s*$)/g, '')
    if (2 > userName.length || userName.length > 20) {
      $('#tipMsg').text(Label.nameTooLongLabel)
      $('#userName' + status).focus()
    } else {
      return true
    }

    return false
  },
}

/*
 * 注册到 admin 进行管理
 */
admin.register['user-list'] = {
  'obj': admin.userList,
  'init': admin.userList.init,
  'refresh': admin.userList.getList,
}
