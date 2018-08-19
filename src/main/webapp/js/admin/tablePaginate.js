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
 * table and paginate util
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.0.8, Jun 11, 2012
 */

var TablePaginate = function (id) {
    this.id = id;
    this.currentPage = 1;
};

$.extend(TablePaginate.prototype, {
    /*
     * 构建 table 框架
     * @colModel table 列宽，标题等数据
     */
    buildTable: function (colModel, noExpend) {
        var tableData = {
            colModel: colModel,
            noDataTip: Label.noDataLabel
        }
        if (!noExpend) {
            tableData.expendRow = {
                index: "expendRow"
            }
        }
        $("#" + this.id + "Table").table(tableData);
    
    },
    
    /*
     * 初始化分页
     */
    initPagination: function () {
        var id = this.id;
        $("#" + id + "Pagination").paginate({
            "bind": function(currentPage, errorMessage) {
                if (errorMessage) {
                    $("#tipMsg").text(errorMessage);
                } else {
                    admin.setHashByPage(currentPage);
                }
            },
            "currentPage": 1,
            "errorMessage": Label.inputErrorLabel,
            "nextPageText": '>',
            "previousPageText": '<',
            "goText": Label.gotoLabel,
            "type": "custom",
            "custom": [1],
            "pageCount": 1
        });
    },

    /*
     * 初始化评论对话框
     */
    initCommentsDialog: function () {
        var that = this;
        $("#" + this.id + "Comments").dialog({
            "modal": true,
            "hideFooter": true,
            "close": function () {
                admin[that.id + "List"].getList(that.currentPage);
                return true;
            }
        });
    },
    
    /*
     * 更新 table & paginateion
     */
    updateTablePagination: function (data, currentPage, pageInfo) {
        currentPage = parseInt(currentPage);
        if (currentPage > pageInfo.paginationPageCount && currentPage > 1) {
            $("#tipMsg").text(Label.pageLabel + currentPage + Label.notFoundLabel);
            $("#loadMsg").text("");
            return;
        }
        $("#" + this.id + "Table").table("update", {
            data: [{
                groupName: "all",
                groupData: data
            }]
        });
                    
        if (pageInfo.paginationPageCount === 0) {
            pageInfo.paginationPageCount = 1;
        }
        
        $("#" + this.id + "Pagination").paginate("update", {
            pageCount: pageInfo.paginationPageCount,
            currentPage: currentPage,
            custom: pageInfo.paginationPageNums
        });
        this.currentPage = currentPage;
    }
});
