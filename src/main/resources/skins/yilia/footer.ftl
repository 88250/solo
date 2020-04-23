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
    <div class="fn-clear">
        ${viewCount1Label}<span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span>
        &nbsp;
        ${articleCount1Label}${statistic.statisticPublishedBlogArticleCount}
        <#if !staticSite>
            &nbsp; ${onlineVisitor1Label}${onlineVisitorCnt}
        </#if>
        <br>
        &copy; ${year}
        <a href="${servePath}">${blogTitle}</a>
        ${footerContent}
    </div>
    <span onclick="Util.goTop()" class="icon-goup"></span>
</footer>
<#include "../../common-template/label.ftl">
<script src="${staticServePath}/skins/${skinDirName}/js/common.min.js?${staticResourceVersion}"></script>
${plugins}
