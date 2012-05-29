<#macro comments commentList article>
<h2 class="marginBottom12">${commentLabel}</h2>  
<div class="comments" id="comments">
    <#if 0 == commentList?size>
    ${noCommentLabel}
    </#if>
    <#list commentList as comment>
    <div id="${comment.oId}">
        <div class="comment-panel">
            <div class="comment-title">
                <#if "http://" == comment.commentURL>
                <a>${comment.commentName}</a>
                <#else>
                <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                </#if>
                <#if comment.isReply>
                @
                <a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                   onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 23);"
                   onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                </#if>
                <div class="right">
                    ${comment.commentDate?string("yyyy-MM-dd HH:mm:ss")}

                    <#if article.commentable>
                    <a class="no-underline"
                       href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
                    </#if>
                </div>
                <div class="clear"></div>
            </div>
            <div class="comment-body">
                <div class="left comment-picture">
                    <img alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
                </div>
                <div class="comment-content">
                    ${comment.commentContent}
                </div>
                <div class="clear"></div>
            </div>
        </div>
    </div>
    </#list>
</div>
<#if article.commentable>
<div class="comment-title">
    ${postCommentsLabel}
</div>
<div class="comment-body">
    <table id="commentForm" class="form">
        <tbody>
            <tr>
                <th>
                    ${commentName1Label}
                </th>
                <td colspan="2">
                    <input type="text" class="normalInput" id="commentName"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${commentEmail1Label}
                </th>
                <td colspan="2">
                    <input type="text" class="normalInput" id="commentEmail"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${commentURL1Label}
                </th>
                <td colspan="2">
                    <input type="text" id="commentURL"/>
                </td>
            </tr>
            <tr>
                <th>
                    ${commentEmotions1Label}
                </th>
                <td id="emotions">
                    <span class="em00" title="${em00Label}"></span>
                    <span class="em01" title="${em01Label}"></span>
                    <span class="em02" title="${em02Label}"></span>
                    <span class="em03" title="${em03Label}"></span>
                    <span class="em04" title="${em04Label}"></span>
                    <span class="em05" title="${em05Label}"></span>
                    <span class="em06" title="${em06Label}"></span>
                    <span class="em07" title="${em07Label}"></span>
                    <span class="em08" title="${em08Label}"></span>
                    <span class="em09" title="${em09Label}"></span>
                    <span class="em10" title="${em10Label}"></span>
                    <span class="em11" title="${em11Label}"></span>
                    <span class="em12" title="${em12Label}"></span>
                    <span class="em13" title="${em13Label}"></span>
                    <span class="em14" title="${em14Label}"></span>
                </td>
            </tr>
            <tr>
                <th valign="top">
                    ${commentContent1Label}
                </th>
                <td colspan="2">
                    <textarea rows="10" cols="96" id="comment"></textarea>
                </td>
            </tr>
            <tr>
                <th>
                    ${captcha1Label}
                </th>
                <td>
                    <input type="text" class="normalInput" id="commentValidate"/>
                    <img id="captcha" alt="validate" src="${servePath}/captcha.do" />
                </td>
                <th>
                    <span class="error-msg" id="commentErrorTip"></span>
                </th>
            </tr>
            <tr>
                <td colspan="3" align="right">
                    <button id="submitCommentButton" onclick="page.submitComment();">${submmitCommentLabel}</button>
                </td>
            </tr>
        </tbody>
    </table>
</div>
</#if>
</#macro>

<#macro comment_script oId>
<script type="text/javascript" src="${staticServePath}/js/page${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">
    var page = new Page({
        "nameTooLongLabel": "${nameTooLongLabel}",
        "mailCannotEmptyLabel": "${mailCannotEmptyLabel}",
        "mailInvalidLabel": "${mailInvalidLabel}",
        "commentContentCannotEmptyLabel": "${commentContentCannotEmptyLabel}",
        "captchaCannotEmptyLabel": "${captchaCannotEmptyLabel}",
        "loadingLabel": "${loadingLabel}",
        "oId": "${oId}",
        "skinDirName": "${skinDirName}",
        "blogHost": "${blogHost}",
        "randomArticles1Label": "${randomArticles1Label}",
        "externalRelevantArticles1Label": "${externalRelevantArticles1Label}"
    });

    var addComment = function (result, state) {
        var commentHTML = '<div id="' + result.oId + 
            '"><div class="comment-panel"><div class="comment-title">' + result.replyNameHTML;

        if (state !== "") {
            var commentOriginalCommentName = $("#" + page.currentCommentId).find(".comment-title a").first().text();
            commentHTML += '&nbsp;@&nbsp;<a href="${servePath}' + result.commentSharpURL.split("#")[0] + '#' + page.currentCommentId + '"'
                + 'onmouseover="page.showComment(this, \'' + page.currentCommentId + '\', 23);"'
                + 'onmouseout="page.hideComment(\'' + page.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
        }

        commentHTML += '<div class="right">' + result.commentDate
            + '&nbsp;<a class="no-underline" href="javascript:replyTo(\'' + result.oId + '\');">${replyLabel}</a>'
            + '</div><div class="clear"></div></div><div class="comment-body">'
            + '<div class="left comment-picture"><img alt="' + $("#commentName" + state).val()
            + '" src="' + result.commentThumbnailURL + '"/>'
            + '</div><div class="comment-content">' + 
            Util.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
            + '</div><div class="clear"></div>'
            + '</div></div></div>';
        return commentHTML;
    }

    var replyTo = function (id) {
        var commentFormHTML = "<table class='form comment-reply' id='replyForm'>";
        page.addReplyForm(id, commentFormHTML);
    };

    (function () {
        page.load();
        // emotions
        page.replaceCommentsEm("#comments .comment-content");
            <#nested>
        })();
</script>
</#macro>