<li id="${comment.oId}" class="fn-clear">
    <img class="avatar-48" title="${comment.commentName}" src="${comment.commentThumbnailURL}">
    <div class="comment-body">
        <div class="fn-clear comment-meta">
                <span class="fn-left">
                    <#if "http://" == comment.commentURL>
                    <a>${comment.commentName}</a>
                    <#else>
                    <a href="${comment.commentURL}" target="_blank">${comment.commentName}</a>
                    </#if>
                    <#if comment.isReply>
                    @
                    <a href="${servePath}${article.permalink}#${comment.commentOriginalCommentId}"
                       onmouseover="page.showComment(this, '${comment.commentOriginalCommentId}', 23);"
                       onmouseout="page.hideComment('${comment.commentOriginalCommentId}')"
                    >${comment.commentOriginalCommentName}</a>
                    </#if>
                    <time>${comment.commentDate?string("yyyy-MM-dd HH:mm")}</time>
                </span>
            <#if article.commentable>
                <a class="fn-right" href="javascript:replyTo('${comment.oId}')">${replyLabel}</a>
            </#if>
        </div>
        <div class="comment-content post-body article-body">
            ${comment.commentContent}
        </div>
    </div>
</li>