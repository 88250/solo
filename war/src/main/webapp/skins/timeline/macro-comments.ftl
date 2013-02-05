<#macro comments commentList article>
<ul id="comments" class="comments list">
    <#list commentList as comment>
    <li id="${comment.oId}">
        <img title="${comment.commentName}"
             alt="${comment.commentName}" src="${comment.commentThumbnailURL}"/>
        <div>
            <span class="author">
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
            </span>
            <small><b> ${comment.commentDate?string("yy-MM-dd HH:mm")}</b></small>
            <#if article.commentable>
            <span class="ico-reply ico right">
                <a rel="nofollow" href="javascript:replyTo('${comment.oId}');">${replyLabel}</a>
            </span>
            </#if>
            <div class="article-body">${comment.commentContent}</div>
        </div>
    </li>
    </#list>
</ul>
<#if article.commentable>
<h3>${commentLabel}</h3>
<table class="comment-form" id="commentForm">
    <tbody>
        <tr>
            <td>
                <input type="text" id="commentName"/>
                <label for="commentName">${commentNameLabel} *</label>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="text" id="commentEmail"/>
                <label for="commentEmail">${commentEmailLabel} *</label>
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" id="commentURL"/>
                <label for="commentURL">${commentURLLabel}</label>
            </td>
        </tr>
        <tr>
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
            <td>
                <textarea style="width:96%" rows="10"  id="comment"></textarea>
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" id="commentValidate"/>
                <img id="captcha" alt="validate" src="${servePath}/captcha.do" />
            </td>
        </tr>
        <tr>
            <td>
                <button id="submitCommentButton" onclick="page.submitComment();">${submmitCommentLabel}</button>
                <span id="commentErrorTip"></span>
            </td>
        </tr>
    </tbody>
</table>
<#if externalRelevantArticlesDisplayCount?? && 0 != externalRelevantArticlesDisplayCount>
<div id="externalRelevantArticles"></div>
</#if>
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

    var addComment = function (result, state) {
        var commentHTML = '<li id="' + result.oId + '"><img \
            title="' + $("#commentName" + state).val() + '" alt="' + $("#commentName" + state).val() + 
            '" src="' + result.commentThumbnailURL + '"/><div><span class="author">' + result.replyNameHTML;

        if (state !== "") {
            var commentOriginalCommentName = $("#" + page.currentCommentId + " .author > a").first().text();
            commentHTML += '&nbsp;@&nbsp;<a href="${servePath}' + result.commentSharpURL.split("#")[0] + '#' + page.currentCommentId + '"'
                + 'onmouseover="page.showComment(this, \'' + page.currentCommentId + '\', 20);"'
                + 'onmouseout="page.hideComment(\'' + page.currentCommentId + '\')">' + commentOriginalCommentName + '</a>';
        }
            
        commentHTML += '</span>&nbsp;<small><b>' +  result.commentDate.substring(2, 16)
            + '</b></small><span class="ico-reply ico right"><a rel="nofollow" href="javascript:replyTo(\'' + result.oId 
            + '\');">${replyLabel}</a></span><div class="article-body">' 
            + Util.replaceEmString($("#comment" + state).val().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g,"<br/>"))
            + '</div></div></li>';

        return commentHTML;
    }

    var replyTo = function (id) {
        var commentFormHTML = "<table class='comment-form' id='replyForm'>";
        page.addReplyForm(id, commentFormHTML);
        $("#replyForm label").each(function () {
            $this = $(this);
            $this.attr("for", $this.attr("for") + "Reply");
        });
    };

    $(document).ready(function () {
        page.load();
        // emotions
        page.replaceCommentsEm("#comments li .article-body");
        <#nested>
        });
</script>
</#macro>