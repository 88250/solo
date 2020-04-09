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
<#include "../../common-template/macro-common_head.ftl">
<!DOCTYPE html>
<html>
<head>
        <@head title="${allTagsLabel} - ${blogTitle}">
            <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
        </@head>
</head>
<body>
${topBarReplacement}
        <#include "side-tool.ftl">
<div class="wrapper">
            <#include "header.ftl">
    <div>
        <div class="main">
            <div class="main-content vditor-content">
                <#if 0 != archiveDates?size>
                    <ul id="tags" class="fn__clear">
                    <#list archiveDates as archiveDate>
                        <li>
                            <#if "en" == localeString?substring(0, 2)>
                                <a class="post-title" href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                    ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})
                                </a>
                            <#else>
                                    <a class="post-title" href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}">
                                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})
                                    </a>
                            </#if>
                        </li>
                    </#list>
                    </ul>
                </#if>
                <div class="clear"></div>
            </div>
            <div class="main-footer"></div>
        </div>
        <div class="side-navi">
                    <#include "side.ftl">
        </div>
        <div class="clear"></div>
        <div class="brush">
            <div class="brush-icon"></div>
            <div id="brush"></div>
        </div>
        <div class="footer">
                    <#include "footer.ftl">
        </div>
    </div>
</div>
</body>
</html>
