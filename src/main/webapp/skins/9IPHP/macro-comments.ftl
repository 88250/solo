<#macro comments commentList article>
<header class='title'><h2>${commentLabel}</h2></header>
<ul class="comments" id="comments">
    <#list commentList as comment>
        <#include 'common-comment.ftl'/>
    </#list>
</ul>
<#if article.commentable>
    <header class='title'><h2>${postCommentsLabel}</h2></header>
        <table id="commentForm" class="form">
            <tbody>
                <#if !isLoggedIn>
                <tr>
                    <td>
                        <input placeholder="${commentNameLabel}" type="text" class="normalInput" id="commentName"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input placeholder="${commentEmailLabel}" type="email" class="normalInput" id="commentEmail"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input placeholder="${commentURLLabel}" type="url" id="commentURL"/>
                    </td>
                </tr>
                </#if>
                <tr>
                    <td id="emotions" class="emotions">
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
                        <textarea rows="5" cols="96" id="comment"></textarea>
                    </td>
                </tr>
                <#if !isLoggedIn>
                <tr>
                    <td>
                        <input style="width:50%" placeholder="${captchaLabel}" type="text" class="normalInput" id="commentValidate"/>
                        <img class="captcha" id="captcha" alt="validate" src="${servePath}/captcha.do" />
                    </td>
                </tr>
                </#if>
                <tr>
                    <td colspan="2" align="right">
                        <span class="error-msg" id="commentErrorTip"></span>
                        <button id="submitCommentButton" onclick="page.submitComment();">${submmitCommentLabel}</button>
                    </td>
                </tr>
            </tbody>
        </table>
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
                        var replyTo = function (id) {
                            var commentFormHTML = "<table class='form comment-reply' id='replyForm'>";
                            page.addReplyForm(id, commentFormHTML);
                        };
                        (function () {
                            page.load();
                            Skin.initArticle("${tocLabel}", "${siteViewLabel}");
                            // emotions
                            page.replaceCommentsEm("#comments .content-reset");
                            <#nested>
                        })();
</script>
</#macro>