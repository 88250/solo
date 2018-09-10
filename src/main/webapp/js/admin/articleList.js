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
 * article list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.2.6, Apr 5, 2018
 */

/* article-list 相关操作 */
admin.articleList = {
    tablePagination:  new TablePaginate("article"),
    
    /* 
     * 初始化 table, pagination, comments dialog 
     */
    init: function (page) {
        this.tablePagination.buildTable([{
            text: Label.titleLabel,
            index: "title",
            minWidth: 110,
            style: "padding-left: 12px;font-size:14px;"
        }, {
            text: Label.authorLabel,
            index: "author",
            width: 150,
            style: "padding-left: 12px;"
        }, {
            text: Label.commentLabel,
            index: "comments",
            width: 80,
            style: "padding-left: 12px;"
        }, {
            text: Label.viewLabel,
            width: 60,
            index: "articleViewCount",
            style: "padding-left: 12px;"
        }, {
            text: Label.dateLabel,
            index: "date",
            width: 90,
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.tablePagination.initCommentsDialog();
        this.getList(page);

        const that = this
        $('#articleListBtn').click(function () {
          that.getList(page);
        });
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        var that = this;
        $("#loadMsg").text(Label.loadingLabel);
        $.ajax({
            url: latkeConfig.servePath + "/console/articles/status/published/" + pageNum + "/" + Label.PAGE_SIZE + "/" +  Label.WINDOW_SIZE + '?k=' + $('#articleListInput').val(),
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                var articles = result.articles,
                articleData = [];
                for (var i = 0; i < articles.length; i++) {
                    articleData[i] = {};
                    articleData[i].title = "<a href=\"" + latkeConfig.servePath + articles[i].articlePermalink + "\" target='_blank' title='" + articles[i].articleTitle + "' class='no-underline'>"
                    + articles[i].articleTitle + "</a><span class='table-tag'>" + articles[i].articleTags + "</span>";
                    articleData[i].date = $.bowknot.getDate(articles[i].articleCreateTime);
                    articleData[i].comments = articles[i].articleCommentCount;
                    articleData[i].articleViewCount = articles[i].articleViewCount;
                    articleData[i].author = articles[i].authorName;
                            
                    var topClass = articles[i].articlePutTop ? Label.cancelPutTopLabel : Label.putTopLabel;
                    articleData[i].expendRow = "<a target='_blank' href='" + latkeConfig.servePath + articles[i].articlePermalink + "'>" + Label.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.article.get('" + articles[i].oId + "', true)\">" + Label.updateLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.article.del('" + articles[i].oId + "', 'article', '" + encodeURIComponent(articles[i].articleTitle) + "')\">" + Label.removeLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.articleList.popTop(this, '" + articles[i].oId + "')\">" + topClass + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.comment.open('" + articles[i].oId + "', 'article')\">" + Label.commentLabel + "</a>";
                }
                    
                that.tablePagination.updateTablePagination(articleData, pageNum, result.pagination);
                
                $("#loadMsg").text("");
            }
        });
    },

    /* 
     * 制定或者取消置顶 
     * @it 触发事件的元素本身
     * @id 草稿 id
     */
    popTop: function (it, id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        
        var $it = $(it),
        ajaxUrl = "canceltop",
        tip = Label.putTopLabel;
        
        if ($it.html() === Label.putTopLabel) {
            ajaxUrl = "puttop";
            tip = Label.cancelPutTopLabel;
        }
        
        $.ajax({
            url: latkeConfig.servePath + "/console/article/" + ajaxUrl + "/" + id,
            type: "PUT",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                    
                $it.html(tip);
                $("#loadMsg").text("");
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["article-list"] =  {
    "obj": admin.articleList,
    "init": admin.articleList.init,
    "refresh": admin.articleList.getList
}