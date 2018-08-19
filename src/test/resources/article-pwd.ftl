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
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${articleViewPwdLabel}</title>
        <meta name="keywords" content="Solo,Java 博客,开源" />
        <meta name="description" content="An open source blog with Java. Java 开源博客" />
        <meta name="owner" content="B3log Team" />
        <meta name="author" content="B3log Team" />
        <meta name="generator" content="Solo" />
        <meta name="copyright" content="B3log" />
        <meta name="revised" content="B3log, ${year}" />
        <meta name="robots" content="noindex, follow" />
        <meta http-equiv="Window-target" content="_top" />
        <link type="text/css" rel="stylesheet" href="${staticServePath}/css/default-init${miniPostfix}.css?${staticResourceVersion}" charset="utf-8" />
        <link rel="icon" type="image/png" href="${staticServePath}/favicon.png" />
        <script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
    </head>
    <body>
        <div class="wrapper">
            <div class="wrap">
                <div class="content">
                    <div class="logo">
                        <a href="https://b3log.org" target="_blank">
                            <img border="0" width="153" height="56" alt="B3log" title="B3log" src="${staticServePath}/images/logo.jpg"/>
                        </a>
                    </div>
                    <div class="main article-pwd">
                        <h2>
                            ${articleTitle}   
                        </h2>
                        <div>
                            ${articleAbstract}
                        </div>
                        <#if msg??>
                        <div>${msg}</div>
                        </#if>
                        <form method="POST" action="${servePath}/console/article-pwd">
                            <label for="pwdTyped">访问密码：</label>
                            <input type="password" id="pwdTyped" name="pwdTyped" />
                            <input type="hidden" name="articleId" value="${articleId}" />
                            <button id="confirm" type="submit">${confirmLabel}</button>
                        </form>
                        <a href="https://b3log.org" target="_blank">
                            <img border="0" class="icon" alt="B3log" title="B3log" src="${staticServePath}/favicon.png"/>
                        </a>
                    </div>
                    <span class="clear"></span>
                </div>
            </div>

            <div class="footerWrapper">
                <div class="footer">
                    &copy; ${year} - <a href="${servePath}">${blogTitle}</a><br/>
                    Powered by <a href="https://b3log.org" target="_blank">B3log 开源</a> • <a href="https://b3log.org/services/#solo" target="_blank">Solo</a> ${version}
                </div>
            </div>
        </div>
    </body>
</html>
