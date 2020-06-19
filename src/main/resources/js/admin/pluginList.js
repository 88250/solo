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
 * plugin list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.5, Apr 5, 2018
 */

/* plugin-list 相关操作 */
admin.pluginList = {
    tablePagination: new TablePaginate("plugin"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    /*
     * 初始化 table, pagination
     */
    init: function(page) {
        this.tablePagination.buildTable([{
                style: "padding-left: 12px;",
                text: Label.pluginNameLabel,
                index: "name",
                width: 230
            }, {
                style: "padding-left: 12px;",
                text: Label.statusLabel,
                index: "status",
                minWidth: 80
            }, {
                style: "padding-left: 12px;",
                text: Label.authorLabel,
                index: "author",
                width: 200
            }, {
                style: "padding-left: 12px;",
                text: Label.versionLabel,
                index: "version",
                width: 120
            }]);

        this.tablePagination.initPagination();
        $("#pluginSetting").dialog({
            width: 700,
            height: 180,
            "modal": true,
            "hideFooter": true
        });
        this.getList(page);
    },
    /*
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function(pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        var that = this;

        $.ajax({
            url: Label.servePath + "/console/plugins/" + pageNum + "/" + Label.PAGE_SIZE + "/" + Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (0 !== result.code) {
                    $("#loadMsg").text("");
                    return;
                }

                admin.pluginList.pageInfo.currentPage = pageNum;
                var datas = result.plugins;
                for (var i = 0; i < datas.length; i++) {
                    datas[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.pluginList.changeStatus('" +
                            datas[i].oId + "', '" + datas[i].status + "')\">";
                    if (datas[i].status === "ENABLED") {
                        datas[i].status = Label.enabledLabel;
                        datas[i].expendRow += Label.disableLabel;
                    } else {
                        datas[i].status = Label.disabledLabel;
                        datas[i].expendRow += Label.enableLabel;
                    }
                    datas[i].expendRow += "</a>  ";

                    if (datas[i].setting != "{}") {
                        datas[i].expendRow += "<a href='javascript:void(0)' onclick=\"admin.pluginList.toSetting('" + datas[i].oId + "')\"> " + Label.settingLabel + " </a>  ";
                    }
                }

                that.tablePagination.updateTablePagination(result.plugins, pageNum, result.pagination);

                $("#loadMsg").text("");
            }
        });
    },
    toSetting: function(pluginId) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        var requestJSONObject = {
            "oId": pluginId
        };

        $.ajax({
            url: Label.servePath + "/console/plugin/toSetting",
            type: "POST",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                $("#pluginSetting").html(result);
                $("#pluginSetting").dialog("open");
                $("#loadMsg").text("");
            }
        });
    },
    changeStatus: function(pluginId, status) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        if (status === "ENABLED") {
            status = "DISABLED";
        } else {
            status = "ENABLED";
        }

        var requestJSONObject = {
            "oId": pluginId,
            "status": status
        };

        $.ajax({
            url: Label.servePath + "/console/plugin/status/",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (0 !== result.code) {
                    $("#loadMsg").text("");
                    return;
                }

                $("#loadMsg").text("");
                window.location.reload();
            }
        });
    }
};

/*
 * 注册到 admin 进行管理
 */
admin.register["plugin-list"] = {
    "obj": admin.pluginList,
    "init": admin.pluginList.init,
    "refresh": function() {
        $("#loadMsg").text("");
    }
};
