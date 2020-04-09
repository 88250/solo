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
<div class="comment">
    <div class="comment__wrapper wrapper">
        <div class="comment__title">
            ${commentLabel}
        </div>
        <#if article.commentable>
        <textarea rows="3" placeholder="${commentContentCannotEmptyLabel}" id="comment"></textarea>
        </#if>

        <ul id="comments">
        <#list commentList as comment>
            <#include 'common-comment.ftl'/>
        </#list>
        </ul>
    </div>
</div>
</#macro>