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
<div class="footer">
    <div class="container fn-clear">
        <#include "../../common-template/macro-user_site.ftl"/>
        <div class="ft__center">
        <@userSite dir="ne"/>
        </div>
        <div class="left">
            <span>&copy; ${year}</span> <a href="${servePath}">${blogTitle}</a> ${footerContent}
        </div>
        <div class="right fn-clear">
            <span class="left">
                <span>
                    ${viewCount1Label}
                    <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span>
                    &nbsp;&nbsp;
                </span>
                <span>
                    ${articleCount1Label}
                    ${statistic.statisticPublishedBlogArticleCount}
                    &nbsp;&nbsp;
                </span>
            </span>
            <span class="ico-translate" onclick="timeline.translate()"></span>
        </div>
    </div>
</div>
<div class="ico-top none" onclick="Util.goTop()" title="TOP"></div>
<#include "../../common-template/label.ftl">
<script src="${staticServePath}/skins/${skinDirName}/js/common.min.js?${staticResourceVersion}"></script>
<script type="text/javascript">
    Label.localeString = "${localeString}"
    Label.yearLabel = "${yearLabel}"
    Label.monthLabel = "${monthLabel}"
    Label.moreLabel = "${moreLabel}"
    Label.viewLabel = "${viewLabel}"
    Label.commentLabel = "${commentLabel}"
    Label.tagLabel = "${tagLabel}"
    Label.topArticleLabel = "${topArticleLabel}"
    Label.authorLabel = "${authorLabel}"
    Label.updatedLabel = "${updatedLabel}"
</script>
${plugins}
