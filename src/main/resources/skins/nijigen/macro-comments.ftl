<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

-->
<#macro comments commentList article>
<div class="comments__item">
    <div class="comments__meta comments__meta--only">${commentLabel}</div>
</div>

<ul class="comments" id="comments">
    <#list commentList as comment>
        <#include 'common-comment.ftl'/>
    </#list>
</ul>

<#if article.commentable>
<div class="comments__item">
    <div class="comments__meta">
        ${postCommentsLabel}
    </div>
    <div class="comments__content">
        <div class="form">
            <textarea rows="3" placeholder="${postCommentsLabel}" id="comment"></textarea>
        </div>
    </div>
</div>
</#if>
</#macro>