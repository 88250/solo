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
 * others for admin.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.4.1.1, May 21, 2020
 */

/* others 相关操作 */
admin.others = {
  intervalId: 0,
  /*
   * @description 初始化
   */
  init: function () {
    $('#tabOthers').tabs()
    $('#loadMsg').text('')
    clearInterval(admin.others.intervalId)
    admin.others.intervalId = setInterval(() => {
      admin.others.getLog()
    }, 5000)
    admin.others.getLog()
  },
  getLog: () => {
    $.ajax({
      url: Label.servePath + '/console/log',
      cache: false,
      timeout: 3000,
      success: function (result) {
        const $textarea = $('#tabOthersPanel_log textarea')
        $textarea.val(result.log).scrollTop($textarea[0].scrollHeight)
      },
    })
  },
  /*
   * @description 移除未使用的存档
   */
  removeUnusedArchives: function () {
    $('#tipMsg').text('')

    $.ajax({
      url: Label.servePath + '/console/archive/unused',
      type: 'DELETE',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
      },
    })
  },
  /*
   * @description 移除未使用的标签
   */
  removeUnusedTags: function () {
    $('#tipMsg').text('')

    $.ajax({
      url: Label.servePath + '/console/tag/unused',
      type: 'DELETE',
      cache: false,
      success: function (result, textStatus) {
        $('#tipMsg').text(result.msg)
      },
    })
  },
  /*
   * @description 导出数据为 SQL 文件
   */
  exportSQL: function () {
    $('#tipMsg').text('')

    $.ajax({
      url: Label.servePath + '/console/export/sql',
      type: 'GET',
      cache: false,
      success: function (result, textStatus) {
        // AJAX 下载文件的话这里会发两次请求，用 code 来判断是否是文件，如果没有 code 说明文件可以下载（实际上就是 result）
        if (!result.code) {
          // 再发一次请求进行正式下载
          window.location = Label.servePath + '/console/export/sql'
        } else {
          $('#tipMsg').text(result.msg)
        }
      },
    })
  },
  /*
 * @description 导出数据为 JSON 文件
 */
  exportJSON: function () {
    $('#tipMsg').text('')
    window.open(Label.servePath + '/console/export/json')
  },
  /*
  * @description 导出数据为 Hexo Markdown 文件
  */
  exportHexo: function () {
    $('#tipMsg').text('')
    window.open(Label.servePath + '/console/export/hexo')
  },
  importZip () {
    const formData = new FormData()
    const $input = $('#otherImportFileInput')
    formData.append('file', $input[0].files[0])
    $.ajax(Label.servePath + '/console/import/markdown-zip', {
      method: 'POST',
      data: formData,
      processData: false,
      contentType: false,
      success: function (res) {
        $input.val('')
        $('#tipMsg').text(res.msg)
      },
      complete: function () {
        $input.val('')
      },
    })
  }
}

/*
 * 注册到 admin 进行管理
 */
admin.register.others = {
  'obj': admin.others,
  'init': admin.others.init,
  'refresh': function () {
    admin.clearTip()
  },
}
