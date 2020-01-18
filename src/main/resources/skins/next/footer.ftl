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
            Powered by <a href="https://solo.b3log.org" target="_blank">Solo</a>

            <div class="fn-right">
                Theme ${skinDirName}
                <sup>[<a href="https://github.com/iissnan/hexo-theme-next" target="_blank">ref</a>]</sup>
                by <a href="http://vanessa.b3log.org" target="_blank">Vanessa</a>
            </div>
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
