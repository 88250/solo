<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
