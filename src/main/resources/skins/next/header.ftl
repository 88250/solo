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
<header class="header">
    <div class="header-line"></div>
    <div class="fn-clear wrapper">
        <div class="logo-wrap">
            <a href="${servePath}" rel="start">
                <span class="logo-line-before"><i></i></span>
                <span class="site-title">${blogTitle}</span>
                <span class="logo-line-after"><i></i></span>
            </a>
        </div>

        <div class="site-nav-toggle fn-right"
             onclick="$('.header-line').toggle();$('nav').children('.menu').slideToggle();">
            <span class="btn-bar"></span>
            <span class="btn-bar"></span>
            <span class="btn-bar"></span>
        </div>

        <nav>
            <ul class="menu">
                <#list pageNavigations as page>
                    <li class="menu-item">
                        <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                            ${page.pageTitle}
                        </a>
                    </li>
                </#list>
            </ul>

            <#if !staticSite>
                <div class="site-search">
                    <form action="${servePath}/search">
                        <input placeholder="${searchLabel}" id="search" type="text" name="keyword"/>
                        <input type="submit" value="" class="fn-none"/>
                    </form>
                </div>
            </#if>
        </nav>
    </div>
</header>