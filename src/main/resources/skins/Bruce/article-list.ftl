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
<#list articles as article>
<div class="row article">
    <h2 class="row article-title">
        <a rel="bookmark" href="${servePath}${article.articlePermalink}">
            ${article.articleTitle}
        </a>
    </h2>

    <div class="row article-tags">
        <#list article.articleTags?split(",") as articleTag>
        <a rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">
            ${articleTag}</a><#if articleTag_has_next>, </#if>
        </#list>
    </div>

    <div class="row article-date">
        <#setting locale="en_US">
        ${article.articleUpdateDate?string("MMMM d, yyyy")}
        <#setting locale=localeString>
    </div>

    <div class="row article-content vditor-reset">
        <div class="col-sm-12" id="abstract${article.oId}">
            ${article.articleAbstract}
        </div>
    </div>
</div>
</#list>

<div class="row">
    <div class="col-sm-2"></div>

    <div class="col-sm-4">
    <#if 1 < paginationCurrentPageNum>
    <#assign prePage = paginationCurrentPageNum - 1>
    <a class="btn btn-success" href="${servePath}${path}${pagingSep}${prePage}">Newer</a>
    </#if>
    </div>

    <div class="col-sm-4 text-right">
    <#if paginationCurrentPageNum < paginationPageCount>
    <#assign nextPage = paginationCurrentPageNum + 1>
    <a class="btn btn-success" href="${servePath}${path}${pagingSep}${nextPage}">Older</a>
    </#if>
    </div>

    <div class="col-sm-2"></div>
</div>
