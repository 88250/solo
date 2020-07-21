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
<footer class="footer">
    <#include "../../common-template/macro-user_site.ftl"/>
    <div class="ft__center">
        <@userSite dir="n"/>
    </div>
    <nav class="footer__nav mobile__none">
        <#list pageNavigations as page>
            <a class="ft__link" href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                ${page.pageTitle}
            </a>
        </#list>
        <a class="ft__link" rel="alternate" href="${servePath}/rss.xml" rel="section">RSS</a>
        <#if !staticSite>
            <#if isLoggedIn>
                <a class="ft__link" href="${servePath}/admin-index.do#main" title="${adminLabel}">${adminLabel}</a>
                <a class="ft__link" href="${logoutURL}">${logoutLabel}</a>
            <#else>
                <a class="ft__link" href="${servePath}/start">${startToUseLabel}</a>
            </#if>
        </#if>
    </nav>
    <div class="footer__border mobile__none"></div>
    <div class="wrapper fn__flex">
        <div class="fn__flex-1 mobile__none">
            <div class="ft__fade">${adminUser.userName} - ${blogSubtitle}</div>
            <br>
            <#if noticeBoard??>
                ${noticeBoard}
            </#if>
        </div>

        <#if 0 != mostUsedCategories?size>
            <div class="footer__mid fn__flex-1 mobile__none">
                <div class="ft__fade">${categoryLabel}</div>
                <br>
                <#list mostUsedCategories as category>
                    <a href="${servePath}/category/${category.categoryURI}"
                       aria-label="${category.categoryPublishedArticleCount} ${articleLabel}"
                       class="ft__link ft__nowrap vditor-tooltipped vditor-tooltipped__n">
                        ${category.categoryTitle}</a> &nbsp; &nbsp;
                </#list>
            </div>
        </#if>

        <div class="fn__flex-1 footer__copyright">
            <a class="ft__link" href="${servePath}/archives.html">
                ${statistic.statisticPublishedBlogArticleCount}
                ${articleLabel}
            </a>
           <br>
            <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span> <span class="ft-gray">${viewLabel}</span>
            <#if !staticSite>
            &nbsp; &nbsp; ${onlineVisitorCnt} <span class="ft-gray">${onlineVisitorLabel}</span>
            </#if> <br>
            &copy; ${year}
            <a class="ft__link" href="${servePath}">${blogTitle}</a>
            ${footerContent}
        </div>
    </div>
</footer>
<#include "../../common-template/label.ftl">
<script type="text/javascript" src="${staticServePath}/skins/${skinDirName}/js/headroom.min.js"></script>
<script type="text/javascript"
        src="${staticServePath}/skins/${skinDirName}/js/common.min.js?${staticResourceVersion}"
        charset="utf-8"></script>
${plugins}
