<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

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
<#include "macro-head.ftl">
<#include "macro-comments.ftl">
<!DOCTYPE html>
<html>
<head>
<@head title="${page.pageTitle} - ${blogTitle}">
    <meta name="keywords" content="${metaKeywords},${page.pageTitle}"/>
    <meta name="description" content="${metaDescription}"/>
</@head>
</head>
<body>
<#include "header.ftl">
<#include "nav.ftl">
<div class="main">
<#if noticeBoard??>
    <div class="board">
    ${noticeBoard}
    </div>
</#if>
    <div class="wrapper content">
        <article class="post">
            <section class="content-reset">
            ${page.pageContent}
            </section>

        </article>
    </div>
</div>
<div class="article__bottom">
<@comments commentList=pageComments article=page></@comments>
</div>
<div style="margin: 0 20px">
<#include "bottom.ftl">
<#include "footer.ftl">
</div>
<@comment_script oId=page.oId></@comment_script>
</body>
</html>
