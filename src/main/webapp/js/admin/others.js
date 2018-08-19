/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
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
 * others for admin.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.0, Nov 11, 2017
 */

/* oterhs 相关操作 */
admin.others = {
  /*
   * @description 初始化
   */
  init: function () {
    $("#tabOthers").tabs();

    $.ajax({
      url: latkeConfig.servePath + "/console/reply/notification/template",
      type: "GET",
      cache: false,
      success: function (result, textStatus) {
        $("#tipMsg").text(result.msg);
        if (!result.sc) {
          $("#loadMsg").text("");
          return;
        }

        $("#replayEmailTemplateTitle").val(result.replyNotificationTemplate.subject);
        $("#replayEmailTemplateBody").val(result.replyNotificationTemplate.body);

        $("#loadMsg").text("");
      }
    });
  },
  /*
   * @description 移除未使用的标签
   */
  removeUnusedTags: function () {
    $("#tipMsg").text("");

    $.ajax({
      url: latkeConfig.servePath + "/console/tag/unused",
      type: "DELETE",
      cache: false,
      success: function (result, textStatus) {
        $("#tipMsg").text(result.msg);
      }
    });
  },
  /*
   * @description 导出数据为 SQL 文件
   */
  exportSQL: function () {
    $("#tipMsg").text("");

    $.ajax({
      url: latkeConfig.servePath + "/console/export/sql",
      type: "GET",
      cache: false,
      success: function (result, textStatus) {
        // AJAX 下载文件的话这里会发两次请求，用 sc 来判断是否是文件，如果没有 sc 说明文件可以下载（实际上就是 result）
        if (!result.sc) {
          // 再发一次请求进行正式下载
          window.location = latkeConfig.servePath + "/console/export/sql";
        } else {
          $("#tipMsg").text(result.msg);
        }
      }
    });
  },
  /*
 * @description 导出数据为 JSON 文件
 */
  exportJSON: function () {
    $("#tipMsg").text("");

    $.ajax({
      url: latkeConfig.servePath + "/console/export/json",
      type: "GET",
      cache: false,
      success: function (result, textStatus) {
        // AJAX 下载文件的话这里会发两次请求，用 sc 来判断是否是文件，如果没有 sc 说明文件可以下载（实际上就是 result）
        if (!result.sc) {
          // 再发一次请求进行正式下载
          window.location = latkeConfig.servePath + "/console/export/json";
        } else {
          $("#tipMsg").text(result.msg);
        }
      }
    });
  },
  /*
  * @description 导出数据为 Hexo Markdown 文件
  */
  exportHexo: function () {
    $("#tipMsg").text("");

    $.ajax({
      url: latkeConfig.servePath + "/console/export/hexo",
      type: "GET",
      cache: false,
      success: function (result, textStatus) {
        // AJAX 下载文件的话这里会发两次请求，用 sc 来判断是否是文件，如果没有 sc 说明文件可以下载（实际上就是 result）
        if (!result.sc) {
          // 再发一次请求进行正式下载
          window.location = latkeConfig.servePath + "/console/export/hexo";
        } else {
          $("#tipMsg").text(result.msg);
        }
      }
    });
  },
  /*
   * 获取未使用的标签。
   * XXX: Not used this function yet.
   */
  getUnusedTags: function () {
    $.ajax({
      url: latkeConfig.servePath + "/console/tag/unused",
      type: "GET",
      cache: false,
      success: function (result, textStatus) {
        $("#tipMsg").text(result.msg);
        if (!result.sc) {
          $("#loadMsg").text("");
          return;
        }

        var unusedTags = result.unusedTags;
        if (0 === unusedTags.length) {
          return;
        }
      }
    });
  },
  /*
   * @description 跟新回复提醒邮件模版
   */
  update: function () {
    $("#loadMsg").text(Label.loadingLabel);
    $("#tipMsg").text("");

    var requestJSONObject = {
      "replyNotificationTemplate": {
        "subject": $("#replayEmailTemplateTitle").val(),
        "body": $("#replayEmailTemplateBody").val()
      }
    };

    $.ajax({
      url: latkeConfig.servePath + "/console/reply/notification/template",
      type: "PUT",
      cache: false,
      data: JSON.stringify(requestJSONObject),
      success: function (result, textStatus) {
        $("#tipMsg").text(result.msg);
        $("#loadMsg").text("");
      }
    });
  }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register.others = {
  "obj": admin.others,
  "init": admin.others.init,
  "refresh": function () {
    admin.clearTip();
  }
};
