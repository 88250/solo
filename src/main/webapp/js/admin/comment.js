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
 *  common comment for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.9, May 28, 2013
 */

admin.comment = { 
    /*
     * 打开评论窗口
     * @id 该评论对应的 id
     * @fromId 该评论来自文章/草稿/自定义页面
     */
    open: function (id, fromId) {
        this.getList(id, fromId);
        $("#" + fromId + "Comments").dialog("open");
    },
    
    /*
     * 获取评论列表
     * 
     * @onId 该评论对应的实体 id，可能是文章，也可能是自定义页面
     * @fromId 该评论来自文章/草稿/自定义页面
     */
    getList: function (onId, fromId) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        $("#" + fromId + "Comments").html("");
        
        var from = "article";
        if (fromId === "page") {
            from = "page";
        }
        
        $.ajax({
            url: latkeConfig.servePath + "/console/comments/" + from + "/" + onId ,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                var comments = result.comments,
                commentsHTML = '';
                for (var i = 0; i < comments.length; i++) {
                    var hrefHTML = "<a target='_blank' href='" + comments[i].commentURL + "'>",
                    content = comments[i].commentContent,
                    contentHTML = Util.replaceEmString(content);
                        
                    if (comments[i].commentURL === "http://") {
                        hrefHTML = "<a target='_blank'>";
                    }

                    commentsHTML += "<div class='comment-title'><span class='left'>"
                    + hrefHTML + comments[i].commentName + "</a>";

                    if (comments[i].commentOriginalCommentName) {
                        commentsHTML += "@" + comments[i].commentOriginalCommentName;
                    }
                    commentsHTML += "</span><span title='" + Label.removeLabel + "' class='right deleteIcon' onclick=\"admin.comment.del('"
                    + comments[i].oId + "', '" + fromId + "', '" + onId + "')\"></span><span class='right'><a href='mailto:"
                    + comments[i].commentEmail + "'>" + comments[i].commentEmail + "</a>&nbsp;&nbsp;"
                    + $.bowknot.getDate(comments[i].commentTime)
                    + "&nbsp;</span><div class='clear'></div></div><div class='margin12'>"
                    + contentHTML + "</div>";
                }
                if ("" === commentsHTML) {
                    commentsHTML = Label.noCommentLabel;
                }
                
                $("#" + fromId + "Comments").html(commentsHTML);
                
                $("#loadMsg").text("");
            }
        });
    },
    
    /*
     * 删除评论
     * @id 评论 id
     * @fromId 该评论来自文章/草稿/自定义页面
     * @articleId 该评论对应的实体 id，可能是文章，也可能是自定义页面
     */
    del: function (id, fromId, articleId) {
        var isDelete = confirm(Label.confirmRemoveLabel + Label.commentLabel + "?");
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            var from = "article";
            if (fromId === "page") {
                from = "page";
            }
            
            $.ajax({
                url: latkeConfig.servePath + "/console/" + from + "/comment/" + id,
                type: "DELETE",
                cache: false,
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }
                    
                    admin.comment.getList(articleId, fromId);
                    
                    $("#loadMsg").text("");
                }
            });
        }
    }
};
