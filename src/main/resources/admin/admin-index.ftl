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
<#include "../common-template/macro-common_head.ftl"/>
<!DOCTYPE html>
<html>
<head>
    <@head title="${adminConsoleLabel} - ${blogTitle}">

        <link type="text/css" rel="stylesheet" href="${staticServePath}/scss/admin.css?${staticResourceVersion}"/>
        <meta name="robots" content="fn__none"/>
    </@head>
</head>
<body onhashchange="admin.setCurByHash();">
<div class="tip"><span id="loadMsg">${loadingLabel}</span></div>
<div class="tip tip-msg"><span id="tipMsg"></span></div>
<div id="allPanel">
    <div id="top">
        <a href="${servePath}" target="_blank" class="hover">
            Solo
        </a>
        <span class="icon-unordered-list top__menu fn__none"
              onclick="admin.toggleMenu()"></span>
        <span class="fn__right">
                    <a href="${servePath}" title='${indexLabel}'>
                        <div class="avatar" style="background-image: url(${gravatar})"></div>
                        ${userName}
                    </a>
                    <a href='javascript:admin.logout();' title='${logoutLabel}'>${logoutLabel}</a>
                </span>
    </div>
    <div id="tabs">
        <ul>
            <li>
                <div id="tabs_main">
                    <a href="#main">
                        <span class="icon-refresh"></span> ${adminIndexLabel}
                    </a>
                </div>
            </li>
            <li>
                <div id="tabArticleTitle" class="tab-current" onclick="admin.collapseNav(this)">
                    <span class="icon-article"></span>
                    ${articleLabel}
                    <span class="icon-chevron-up fn__right"></span>
                </div>
                <ul id="tabArticleMgt">
                    <li>
                        <div id="tabs_article">
                            <a href="#article/article" onclick="admin.article.prePost()">${postArticleLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_article-list">
                            <a href="#article/article-list">${articleListLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_draft-list">
                            <a href="#article/draft-list">${draftListLabel}</a>
                        </div>
                    </li>
                </ul>
            </li>
            <li>
                <div id="tabs_comment-list">
                    <a href="#comment-list">
                        <span class="icon-cmts"></span> ${commentListLabel}
                    </a>
                </div>
            </li>
            <li id="tools">
                <div id="tabToolsTitle" onclick="admin.collapseNav(this)">
                    <span class="icon-setting"></span>
                    ${ToolLabel}
                    <span class="icon-chevron-down fn__right"></span>
                </div>
                <ul class="fn__none" id="tabTools">
                    <li>
                        <div id="tabs_preference">
                            <a href="#tools/preference">${preferenceLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_theme-list">
                            <a href="#tools/theme-list">${skinLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_category-list">
                            <a href="#tools/category-list">${categoryListLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_page-list">
                            <a href="#tools/page-list">${navMgmtLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_link-list">
                            <a href="#tools/link-list">${linkManagementLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_user-list">
                            <a href="#tools/user-list">${userManageLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_plugin-list">
                            <a href="#tools/plugin-list">${pluginMgmtLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_staticsite">
                            <a href="#tools/staticsite">${staticsiteMgmtLabel}</a>
                        </div>
                    </li>
                    <li>
                        <div id="tabs_others">
                            <a href="#tools/others/tag">${othersLabel}</a>
                        </div>
                    </li>
                </ul>
            </li>
            <li>
                <div id="tabs_about">
                    <a href="#about">
                        <span class="icon-info"></span> ${aboutLabel}
                    </a>
                </div>
            </li>
        </ul>
    </div>
    <div class="tabs__bg" onclick="admin.toggleMenu()"></div>
    <div id="tabsPanel">
        <div id="tabsPanel_main" class="fn__none"></div>
        <div id="tabsPanel_article" class="fn__none"></div>
        <div id="tabsPanel_article-list" class="fn__none"></div>
        <div id="tabsPanel_draft-list" class="fn__none"></div>
        <div id="tabsPanel_link-list" class="fn__none"></div>
        <div id="tabsPanel_preference" class="fn__none"></div>
        <div id="tabsPanel_theme-list" class="fn__none"></div>
        <div id="tabsPanel_category-list" class="fn__none"></div>
        <div id="tabsPanel_page-list" class="fn__none"></div>
        <div id="tabsPanel_others" class="fn__none"></div>
        <div id="tabsPanel_user-list" class="fn__none"></div>
        <div id="tabsPanel_comment-list" class="fn__none"></div>
        <div id="tabsPanel_plugin-list" class="fn__none"></div>
        <div id="tabsPanel_staticsite" class="fn__none"></div>
        <div id="tabsPanel_about" class="fn__none"></div>
    </div>
    <div class="fn__clear"></div>
    <div class="footer">
        Powered by <a href="https://b3log.org" target="_blank">B3log 开源</a> •
        <a href="https://solo.b3log.org" target="_blank">Solo</a> ${version}
    </div>
</div>
<#include "admin-label.ftl">
<script src="${staticServePath}/js/admin/admin.min.js?${staticResourceVersion}"></script>
${plugins}
<script>
  admin.init()
  admin.inited()
</script>
</body>
</html>
