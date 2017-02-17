<li id="${comment.oId}">
    <div>
        <div class="avatar tooltipped tooltipped-n" aria-label="${comment.commentName}"
             style="background-image: url(${comment.commentThumbnailURL})"></div>
        <main>
            <div class="fn-clear">
                <#if "http://" == comment.commentURL>
                ${comment.commentName}
                <#else>
                <a class="user-name" href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                </#if>
                <#if comment.isReply>
                @<a class="user-name" href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                   onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 23);"
                   onmouseout="page.hideComment('${comment.commentOriginalCommentId}')"
                >${comment.commentOriginalCommentName}</a>
                </#if>
                <time class="ft-gray">${comment.commentDate2?string("yyyy-MM-dd HH:mm")}</time>

                <#if article.commentable>
                    <a class="reply-btn" href="javascript:replyTo('${comment.oId}')">${replyLabel}</a>
                </#if>
            </div>
            <div class="content-reset">
                ${comment.commentContent}
            </div>
        </main>
    </div>
</li>