<#macro comments commentList article>
<div id="comments">
    <#list commentList as comment>
    <div id="${comment.oId}">
        <img class="comment-header" title="${comment.commentName}"
             alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
        <div class="comment-panel">
            <div class="left">
                <#if "http://" == comment.commentURL>
                <a>${comment.commentName}</a>
                <#else>
                <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                </#if>
                <#if comment.isReply>@
                <a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                   onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 20);"
                   onmouseout="page.hideComment('${comment.commentOriginalCommentId}')">${comment.commentOriginalCommentName}</a>
                </#if>
            </div>
            <#if article.commentable>
            <div class="right  ft-gray">
                ${comment.commentDate?string("yy-MM-dd HH:mm")}
                <a rel="nofollow" href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
            </div>
            </#if>
            <span class="clear"></span>
            <div class="article-body">${comment.commentContent}</div>
        </div>
        <span class="clear"></span>
    </div>
    </#list>
</div>
<#if article.commentable>
<div class="form">
    <table id="commentForm">
        <tbody>
            <#if !isLoggedIn>
            <tr>
                <td colspan="2">
                    <input type="text" class="normalInput" id="commentName"/>
                    <label for="commentName">${commentNameLabel}</label>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="text" class="normalInput" id="commentEmail"/>
                    <label for="commentEmail">${commentEmailLabel}</label>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="text" id="commentURL"/>
                    <label for="commentURL">${commentURLLabel}</label>
                </td>
            </tr>
            </#if>
            <tr>
                <td id="emotions" colspan="2">
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
                <td colspan="2">
                    <textarea rows="10" cols="96" id="comment"></textarea>
                </td>
            </tr>
            <#if !isLoggedIn>
            <tr>
                <td colspan="2">
                    <input type="text" class="normalInput" id="commentValidate"/>
                    <img id="captcha" alt="validate" src="${servePath}/captcha.do" />
                </td>
            </tr>
            </#if>
            <tr>
                <td>
                    <span class="ft-gray" id="commentErrorTip"></span>
                </td>
                <td align="right">
                    <button id="submitCommentButton" onclick="page.submitComment();">${submmitCommentLabel}</button>
                </td>
            </tr>
        </tbody>
    </table>
</div>
<#if externalRelevantArticlesDisplayCount?? && 0 != externalRelevantArticlesDisplayCount>
<div id="externalRelevantArticles" class="article-relative"></div>
</#if>
<span class="clear"></span>
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
                           "captchaErrorLabel": "${captchaErrorLabel}",
                           "loadingLabel": "${loadingLabel}",
                           "oId": "${oId}",
                           "skinDirName": "${skinDirName}",
                           "blogHost": "${blogHost}",
                           "randomArticles1Label": "${randomArticlesLabel}",
                           "externalRelevantArticles1Label": "${externalRelevantArticlesLabel}"
                       });

                       var addComment = function(result, state) {
                           var commentHTML = '<div id="' + result.oId + '"><img class="comment-header" \
                                    title="' + result.userName + '" alt="' + result.userName +
                                   '" src="' + result.commentThumbnailURL 
                                   + '"/><div class="comment-panel"><div class="left">' + result.replyNameHTML;

                           if (state !== "") {
                               var commentOriginalCommentName = $("#" + page.currentCommentId 
                                       + " .comment-panel>.left a").first().text();
                               commentHTML += '&nbsp;@&nbsp;<a href="${servePath}' 
                                       + result.commentSharpURL.split("#")[0] + '#' + page.currentCommentId + '"'
                                       + 'onmouseover="page.showComment(this, \'' + page.currentCommentId + '\', 20);"'
                                       + 'onmouseout="page.hideComment(\'' + page.currentCommentId + '\')">' 
                                       + commentOriginalCommentName + '</a>';
                           }

                           commentHTML += '</div><div class="right ft-gray">' + result.commentDate.substring(2, 16)
                                   + '&nbsp;<a rel="nofollow" href="javascript:replyTo(\'' + result.oId
                                   + '\');">${replyLabel}</a></div><span class="clear"></span><div class="article-body">' +
                                   Util.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g, "<br/>"))
                                   + '</div></div><span class="clear"></span></div>';

                           return commentHTML;
                       };

                       var replyTo = function(id) {
                           var commentFormHTML = "<table class='form' id='replyForm'>";
                           page.addReplyForm(id, commentFormHTML);
                           $("#replyForm label").each(function() {
                               $this = $(this);
                               $this.attr("for", $this.attr("for") + "Reply");
                           });
                       };

                       $(document).ready(function() {
                           page.load();
                           ease.scrollToCmt();
                           // emotions
                           page.replaceCommentsEm("#comments .article-body");
                           <#nested>
                       });
</script>
</#macro>