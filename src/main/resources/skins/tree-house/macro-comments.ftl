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
<div class="comments-header"></div>
<div class="comments marginTop12" id="comments">
    <#list commentList as comment>
    <#include "common-comment.ftl"/>
    </#list>
</div>
<#if article.commentable>
<div class="comments">
    <div class="comment-top"></div>
    <div class="comment-body">
        <div class="comment-title">
            <a>${postCommentsLabel}</a>
        </div>
        <textarea style="width: 100%" rows="3" placeholder="${postCommentsLabel}" id="comment"></textarea>
    </div>
    <div class="comment-bottom"></div>
</div>
</#if>
</#macro>