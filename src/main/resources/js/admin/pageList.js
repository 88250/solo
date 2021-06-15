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
 * page list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.3.0.1, Apr 22, 2019
 */

/* page-list 相关操作 */
admin.pageList = {
    tablePagination: new TablePaginate("page"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    id: "",
    /*
     * 初始化 table, pagination
     */
    init: function (page) {
        this.tablePagination.buildTable([{
                text: "",
                index: "pageOrder",
                width: 60,
                style: "padding-left: 12px;font-size:14px;"
            }, {
                style: "padding-left: 12px;",
                text: Label.titleLabel,
                index: "pageTitle",
                width: 300
            }, {
                style: "padding-left: 12px;",
                text: Label.permalinkLabel,
                index: "pagePermalink",
                minWidth: 100
            }, {
                style: "padding-left: 12px;",
                text: Label.openMethodLabel,
                index: "pageTarget",
                width: 120
            }]);
        this.tablePagination.initPagination();
        this.getList(page);
    },
    /*
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        var that = this;

        $.ajax({
            url: Label.servePath + "/console/pages/" + pageNum + "/" + Label.PAGE_SIZE + "/" + Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (0 !== result.code) {
                    $("#loadMsg").text("");
                    return;
                }

                var pages = result.pages;
                var pageData = [];
                admin.pageList.pageInfo.currentCount = pages.length;
                admin.pageList.pageInfo.pageCount = result.pagination.paginationPageCount === 0 ? 1 : result.pagination.paginationPageCount;
                for (var i = 0; i < pages.length; i++) {
                    pageData[i] = {};
                    if (i === 0) {
                        if (pages.length === 1) {
                            pageData[i].pageOrder = "";
                        } else {
                            pageData[i].pageOrder = '<div class="table-center" style="width:14px">\
                                        <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'down\');" \
                                        class="icon-move-down"></span></div>';
                        }
                    } else if (i === pages.length - 1) {
                        pageData[i].pageOrder = '<div class="table-center" style="width:14px">\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'up\');" class="icon-move-up"></span>\
                                    </div>';
                    } else {
                        pageData[i].pageOrder = '<div class="table-center" style="width:38px">\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'up\');" class="icon-move-up"></span>\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'down\');" class="icon-move-down"></span>\
                                    </div>';
                    }

                    var pageIcon = '';
                    if (pages[i].pageIcon !== '') {
                      pageIcon = "<img class='navigation-icon' src='" + pages[i].pageIcon + "'/> ";
                    }
                    pageData[i].pageTitle = pageIcon + "<a class='no-underline' href='" + pages[i].pagePermalink + "' target='_blank'>" +
                            pages[i].pageTitle + "</a>";
                    pageData[i].pagePermalink = "<a class='no-underline' href='" + pages[i].pagePermalink + "' target='_blank'>"
                            + pages[i].pagePermalink + "</a>";
                    pageData[i].pageTarget = pages[i].pageOpenTarget;
                    pageData[i].expendRow = "<span><a href='" + pages[i].pagePermalink + "' target='_blank'>" + Label.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.pageList.get('" + pages[i].oId + "')\">" + Label.updateLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.pageList.del('" + pages[i].oId + "', '" + encodeURIComponent(pages[i].pageTitle) + "')\">" + Label.removeLabel + "</a></span>";
                }

                that.tablePagination.updateTablePagination(pageData, pageNum, result.pagination);

                $("#loadMsg").text("");
            }
        });
    },
    /*
     * 获取自定义页面
     * @id 自定义页面 id
     */
    get: function (id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");

        $.ajax({
            url: Label.servePath + "/console/page/" + id,
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (0 !== result.code) {
                    $("#loadMsg").text("");
                    return;
                }

                admin.pageList.id = id;

                $("#pageTitle").val(result.page.pageTitle);
                $("#pagePermalink").val(result.page.pagePermalink);
                $("#pageTarget").val(result.page.pageOpenTarget);
                $("#pageIcon").val(result.page.pageIcon);
                $("#loadMsg").text("");
            }
        });
    },
    /*
     * 删除自定义页面
     * @id 自定义页面 id
     * @title 自定义页面标题
     */
    del: function (id, title) {
        var isDelete = confirm(Label.confirmRemoveLabel + Label.navLabel + '"' + htmlDecode(title) + '"?');
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            $.ajax({
                url: Label.servePath + "/console/page/" + id,
                type: "DELETE",
                cache: false,
                success: function (result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (0 !== result.code) {
                        $("#loadMsg").text("");
                        return;
                    }

                    var pageNum = admin.pageList.pageInfo.currentPage;
                    if (admin.pageList.pageInfo.currentCount === 1 && admin.pageList.pageInfo.pageCount !== 1 &&
                            admin.pageList.pageInfo.currentPage === admin.pageList.pageInfo.pageCount) {
                        admin.pageList.pageInfo.pageCount--;
                        pageNum = admin.pageList.pageInfo.pageCount;
                    }
                    var hashList = window.location.hash.split("/");
                    if (pageNum == hashList[hashList.length - 1]) {
                        admin.pageList.getList(pageNum);
                    } else {
                        admin.setHashByPage(pageNum);
                    }

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 添加自定义页面
     */
    add: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            var pagePermalink = $("#pagePermalink").val().replace(/(^\s*)|(\s*$)/g, "");

            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "pagePermalink": pagePermalink,
                    "pageOpenTarget": $("#pageTarget").val(),
                    "pageIcon": $("#pageIcon").val()
                }
            };

            $.ajax({
                url: Label.servePath + "/console/page/",
                type: "POST",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function (result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (0 !== result.code) {
                        $("#loadMsg").text("");
                        return;
                    }

                    admin.pageList.id = "";
                    $("#pagePermalink").val("");
                    $("#pageTitle").val("");
                    $("#pageIcon").val("");
                    $("#pageTarget").val("_self");

                    if (admin.pageList.pageInfo.currentCount === Label.PAGE_SIZE &&
                            admin.pageList.pageInfo.currentPage === admin.pageList.pageInfo.pageCount) {
                        admin.pageList.pageInfo.pageCount++;
                    }
                    var hashList = window.location.hash.split("/");
                    if (admin.pageList.pageInfo.pageCount == hashList[hashList.length - 1]) {
                        admin.pageList.getList(admin.pageList.pageInfo.pageCount);
                    } else {
                        admin.setHashByPage(admin.pageList.pageInfo.pageCount);
                    }

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 更新自定义页面
     */
    update: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            var pagePermalink = $("#pagePermalink").val().replace(/(^\s*)|(\s*$)/g, "");

            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "oId": this.id,
                    "pagePermalink": pagePermalink,
                    "pageOpenTarget": $("#pageTarget").val(),
                    "pageIcon": $("#pageIcon").val()
                }
            };

            $.ajax({
                url: Label.servePath + "/console/page/",
                type: "PUT",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function (result, textStatus) {
                    $("#tipMsg").text(result.msg);

                    if (0 !== result.code) {
                        $("#loadMsg").text("");
                        return;
                    }
                    admin.pageList.id = "";

                    admin.pageList.getList(admin.pageList.pageInfo.currentPage);
                    $("#pageTitle").val("");
                    $("#pageIcon").val("");
                    $("#pagePermalink").val("");
                    $("#pageTarget").val("_self");
                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 验证字段
     */
    validate: function () {
        if ($("#pageTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#pageTitle").focus();
        } else if ($("#pagePermalink").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.linkEmptyLabel);
        } else {
            return true;
        }
        return false;
    },
    /*
     * 提交自定义页面
     */
    submit: function () {
        if (this.id !== "") {
            this.update();
        } else {
            this.add();
        }
    },
    /*
     * 调换顺序
     */
    changeOrder: function (id, order, status) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");

        var requestJSONObject = {
            "oId": id.toString(),
            "direction": status
        };

        $.ajax({
            url: Label.servePath + "/console/page/order/",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);

                // Refreshes the page list
                admin.pageList.getList(admin.pageList.pageInfo.currentPage);

                $("#loadMsg").text("");
            }
        });
    }
};

/*
 * 注册到 admin 进行管理
 */
admin.register["page-list"] = {
    "obj": admin.pageList,
    "init": admin.pageList.init,
    "refresh": admin.pageList.getList
}
