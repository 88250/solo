/*
 * Copyright (c) 2010-2017, b3log.org & hacpai.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * category list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 0.1.0.0, Mar 31, 2016
 */

/* category-list 相关操作 */
admin.categoryList = {
    tablePagination: new TablePaginate("category"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    categoryInfo: {
        'oId': "",
        "categoryRole": ""
    },
    /* 
     * 初始化 table, pagination
     */
    init: function(page) {
        this.tablePagination.buildTable([{
                style: "padding-left: 12px;",
                text: Label.commentNameLabel,
                index: "categoryName",
                width: 230
            }, {
                style: "padding-left: 12px;",
                text: Label.commentEmailLabel,
                index: "categoryEmail",
                minWidth: 180
            }, {
                style: "padding-left: 12px;",
                text: Label.roleLabel,
                index: "isAdmin",
                width: 120
            }]);

        this.tablePagination.initPagination();
        this.getList(page);

        $("#categoryUpdate").dialog({
            width: 700,
            height: 250,
            "modal": true,
            "hideFooter": true
        });
    },
    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function(pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        this.pageInfo.currentPage = pageNum;
        var that = this;

        $.ajax({
            url: latkeConfig.servePath + "/console/categories/" + pageNum + "/" + Label.PAGE_SIZE + "/" + Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                var categorys = result.categorys;
                var categoryData = [];
                admin.categoryList.pageInfo.currentCount = categorys.length;
                admin.categoryList.pageInfo.pageCount = result.pagination.paginationPageCount;
                if (categorys.length < 1) {
                    $("#tipMsg").text("No category  " + Label.reportIssueLabel);
                    $("#loadMsg").text("");
                    return;
                }

                for (var i = 0; i < categorys.length; i++) {
                    categoryData[i] = {};
                    categoryData[i].categoryName = categorys[i].categoryName;
                    categoryData[i].categoryEmail = categorys[i].categoryEmail;

                    if ("adminRole" === categorys[i].categoryRole) {
                        categoryData[i].isAdmin = "&nbsp;" + Label.administratorLabel;
                        categoryData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.categoryList.get('" +
                                categorys[i].oId + "', '" + categorys[i].categoryRole + "')\">" + Label.updateLabel + "</a>";
                    } else {
                        categoryData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.categoryList.get('" +
                                categorys[i].oId + "', '" + categorys[i].categoryRole + "')\">" + Label.updateLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.categoryList.del('" + categorys[i].oId + "', '" + categorys[i].categoryName + "')\">" + Label.removeLabel + "</a> " +
                                "<a href='javascript:void(0)' onclick=\"admin.categoryList.changeRole('" + categorys[i].oId + "')\">" + Label.changeRoleLabel + "</a>";
                        if ("defaultRole" === categorys[i].categoryRole) {
                            categoryData[i].isAdmin = Label.commonUserLabel;
                        }
                        else {
                            categoryData[i].isAdmin = Label.visitorUserLabel;
                        }
                    }

                    that.tablePagination.updateTablePagination(categoryData, pageNum, result.pagination);

                    $("#loadMsg").text("");
                }
            }
        });
    },
    /*
     * 添加用户
     */
    add: function() {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            var requestJSONObject = {
                "categoryName": $("#categoryName").val(),
                "categoryEmail": $("#categoryEmail").val(),
                "categoryURL": $("#categoryURL").val(),
                "categoryPassword": $("#categoryPassword").val(),
                "categoryAvatar": $("#categoryAvatar").val()
            };

            $.ajax({
                url: latkeConfig.servePath + "/console/category/",
                type: "POST",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    $("#categoryName").val("");
                    $("#categoryEmail").val("");
                    $("#categoryURL").val("");
                    $("#categoryPassword").val("");
                    $("#categoryAvatar").val("");
                    if (admin.categoryList.pageInfo.currentCount === Label.PAGE_SIZE &&
                            admin.categoryList.pageInfo.currentPage === admin.categoryList.pageInfo.pageCount) {
                        admin.categoryList.pageInfo.pageCount++;
                    }
                    var hashList = window.location.hash.split("/");
                    if (admin.categoryList.pageInfo.pageCount !== parseInt(hashList[hashList.length - 1])) {
                        admin.setHashByPage(admin.categoryList.pageInfo.pageCount);
                    }

                    admin.categoryList.getList(admin.categoryList.pageInfo.pageCount);

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 获取用户
     * @id 用户 id
     */
    get: function(id, categoryRole) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        $("#categoryUpdate").dialog("open");

        $.ajax({
            url: latkeConfig.servePath + "/console/category/" + id,
            type: "GET",
            cache: false,
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                var $categoryEmailUpdate = $("#categoryEmailUpdate");
                $("#categoryNameUpdate").val(result.category.categoryName).data("categoryInfo", {
                    'oId': id,
                    "categoryRole": categoryRole
                });
                $categoryEmailUpdate.val(result.category.categoryEmail);
                if ("adminRole" === categoryRole) {
                    $categoryEmailUpdate.attr("disabled", "disabled");
                } else {
                    $categoryEmailUpdate.removeAttr("disabled");
                }
                
                $("#categoryURLUpdate").val(result.category.categoryURL);
                $("#categoryPasswordUpdate").val(result.category.categoryPassword);
                $("#categoryAvatarUpdate").val(result.category.categoryAvatar);

                $("#loadMsg").text("");
            }
        });
    },
    /*
     * 更新用户
     */
    update: function() {
        if (this.validate("Update")) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            var categoryInfo = $("#categoryNameUpdate").data("categoryInfo");
            var requestJSONObject = {
                "categoryName": $("#categoryNameUpdate").val(),
                "oId": categoryInfo.oId,
                "categoryEmail": $("#categoryEmailUpdate").val(),
                "categoryURL": $("#categoryURLUpdate").val(),
                "categoryRole": categoryInfo.categoryRole,
                "categoryPassword": $("#categoryPasswordUpdate").val(),
                "categoryAvatar": $("#categoryAvatarUpdate").val()
            };

            $.ajax({
                url: latkeConfig.servePath + "/console/category/",
                type: "PUT",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus) {
                    $("#categoryUpdate").dialog("close");
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    admin.categoryList.getList(admin.categoryList.pageInfo.currentPage);

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 删除用户
     * @id 用户 id
     * @categoryName 用户名称
     */
    del: function(id, categoryName) {
        var isDelete = confirm(Label.confirmRemoveLabel + Label.categoryLabel + '"' + categoryName + '"?');
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            $.ajax({
                url: latkeConfig.servePath + "/console/category/" + id,
                type: "DELETE",
                cache: false,
                success: function(result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    var pageNum = admin.categoryList.pageInfo.currentPage;
                    if (admin.categoryList.pageInfo.currentCount === 1 && admin.categoryList.pageInfo.pageCount !== 1 &&
                            admin.categoryList.pageInfo.currentPage === admin.categoryList.pageInfo.pageCount) {
                        admin.categoryList.pageInfo.pageCount--;
                        pageNum = admin.categoryList.pageInfo.pageCount;
                    }
                    var hashList = window.location.hash.split("/");
                    if (pageNum !== parseInt(hashList[hashList.length - 1])) {
                        admin.setHashByPage(pageNum);
                    }
                    admin.categoryList.getList(pageNum);

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /**
     * 修改角色
     * @param id
     */
    changeRole: function(id) {
        $("#tipMsg").text("");
        $.ajax({
            url: latkeConfig.servePath + "/console/changeRole/" + id,
            type: "GET",
            cache: false,
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                var pageNum = admin.categoryList.pageInfo.currentPage;
                if (admin.categoryList.pageInfo.currentCount === 1 && admin.categoryList.pageInfo.pageCount !== 1 &&
                        admin.categoryList.pageInfo.currentPage === admin.categoryList.pageInfo.pageCount) {
                    admin.categoryList.pageInfo.pageCount--;
                    pageNum = admin.categoryList.pageInfo.pageCount;
                }
                var hashList = window.location.hash.split("/");
                if (pageNum !== parseInt(hashList[hashList.length - 1])) {
                    admin.setHashByPage(pageNum);
                }
                admin.categoryList.getList(pageNum);

                $("#loadMsg").text("");
            }
        });
    },
    /*
     * 验证字段
     * @status 更新或者添加时进行验证
     */
    validate: function(status) {
        if (!status) {
            status = "";
        }
        var categoryName = $("#categoryName" + status).val().replace(/(^\s*)|(\s*$)/g, "");
        if (2 > categoryName.length || categoryName.length > 20) {
            $("#tipMsg").text(Label.nameTooLongLabel);
            $("#categoryName" + status).focus();
        } else if ($("#categoryEmail" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.mailCannotEmptyLabel);
            $("#categoryEmail" + status).focus();
        } else if (!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#categoryEmail" + status).val())) {
            $("#tipMsg").text(Label.mailInvalidLabel);
            $("#categoryEmail" + status).focus();
        } else if ($("#categoryPassword" + status).val() === "") {
            $("#tipMsg").text(Label.passwordEmptyLabel);
            $("#categoryPassword" + status).focus();
        } else {
            return true;
        }
        return false;
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["category-list"] = {
    "obj": admin.categoryList,
    "init": admin.categoryList.init,
    "refresh": admin.categoryList.getList
}