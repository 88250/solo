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
    <div class="wrapper">
        <div class="fn-clear">
            <a href="${servePath}">${blogTitle}</a>
            <#if !staticSite> • ${onlineVisitor1Label}${onlineVisitorCnt}</#if>
            <div class="fn-right">
            <#include "../../common-template/macro-user_site.ftl"/>
            <@userSite dir="n"/>
            </div>
        </div>
        <div class="fn-clear">
            &copy; ${year}
            <a href="${servePath}">${blogTitle}</a>
            ${footerContent}
        </div>
    </div>
</footer>
<div class="back-to-top" onclick="Util.goTop()"></div>

<#include "../../common-template/label.ftl">
<script src="${staticServePath}/skins/${skinDirName}/js/common.min.js?${staticResourceVersion}"></script>
<script type="text/javascript">
    Label.tocLabel = "${tocLabel}"
    Label.siteViewLabel = "${siteViewLabel}"
</script>
${plugins}
