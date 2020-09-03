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
<#include "macro-common_head.ftl"/>
<!DOCTYPE html>
<html>
<head>
    <@head title="${searchLabel} - ${blogTitle}${searchLabel}">
        <link type="text/css" rel="stylesheet"
              href="${staticServePath}/scss/start.css?${staticResourceVersion}" charset="utf-8"/>
    </@head>
</head>
<body class="search__body">
<div class="search__header fn-clear">
    <a href="${servePath}"><img width="32" border="0" alt="Solo" title="Solo" src="${faviconURL}"/></a>
    <div class="search__input">
        <input value="${keyword}" id="keyword" type="text" onkeypress="if(event.keyCode===13){document.getElementById('searchBtn').click()}">
        <button id="searchBtn" onclick="window.location.href='${servePath}/search?keyword=' + document.getElementById('keyword').value">搜索</button>
    </div>
    <span class="fn-right">
    <#if isLoggedIn>
        <a href="${servePath}/admin-index.do#main">${adminLabel}</a> &nbsp;
        <a href="${logoutURL}">${logoutLabel}</a>
    <#else>
        <a href="${servePath}/start">${startToUseLabel}</a>
    </#if>
        </span>
</div>

<div class="search">
    <div class="search__articles">
    <#list articles as article>
        <article>
            <header>
                <h1>
                    <a rel="bookmark" href="${servePath}${article.articlePermalink}">
                    ${article.articleTitle}
                    </a>
                </h1>

                <div class="meta">
                    <time>
                    ${article.articleCreateDate?string("yyyy-MM-dd")}
                    </time>
                </div>
            </header>
            <div class="vditor-reset">
            ${article.articleAbstract}
            </div>
            <footer>
                <#list article.articleTags?split(",") as articleTag>
                    <a class="tag" rel="tag" href="${servePath}/tags/${articleTag?url('UTF-8')}">${articleTag}</a>
                </#list>
            </footer>
        </article>
    </#list>
    </div>


<#if 0 != articles?size>
    <nav class="search__pagination">
        <#if 1 != pagination.paginationPageNums?first>
            <a href="${servePath}/search?keyword=${keyword}&p=${pagination.paginationCurrentPageNum - 1}">&laquo;</a>
            <a href="${servePath}/search?keyword=${keyword}&p=1">1</a> <span class="page-number">...</span>
        </#if>
        <#list pagination.paginationPageNums as paginationPageNum>
            <#if paginationPageNum == pagination.paginationCurrentPageNum>
                <span>${paginationPageNum}</span>
            <#else>
                <a href="${servePath}/search?keyword=${keyword}&p=${paginationPageNum}">${paginationPageNum}</a>
            </#if>
        </#list>
        <#if pagination.paginationPageNums?last != pagination.paginationPageCount>
            <span>...</span>
            <a href="${servePath}/search?keyword=${keyword}&p=${pagination.paginationPageCount}">${pagination.paginationPageCount}</a>
            <a href="${servePath}/search?keyword=${keyword}&p=${pagination.paginationCurrentPageNum + 1}">&raquo;</a>
        </#if>
    </nav>
<#else>
No Result, Return to <a href="${servePath}">Index</a> or <a href="https://ld246.com">LianDi</a>.
</#if>
</div>

<div class="footerWrapper">
    <div class="footer">
        Powered by <a href="https://b3log.org" target="_blank">B3log 开源</a> • Solo ${version}
    </div>
</div>
</body>
</html>
