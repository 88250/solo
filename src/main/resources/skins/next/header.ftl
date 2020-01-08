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