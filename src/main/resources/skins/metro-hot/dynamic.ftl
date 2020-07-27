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
        <@head title="${dynamicLabel} - ${blogTitle}">
            <link rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/base.css?${staticResourceVersion}"/>
        </@head>
    </head>
    <body>
        <div class="wrapper">
            <div id="header">
                <#include "header.ftl" />
                <div class="article-header">
                    <h2>${blogSubtitle}</h2>
                </div>
            </div>

            <div class="fn-clear" id="dynamic">
                <div class="main">
                    <#if 0 != links?size>
                        <div class="side-tile links-tile fn__flex">
                            <div>
                                <span data-ico="&#xe14a;"></span>
                                <div class="title">
                                    ${linkLabel}
                                </div>
                            </div>
                            <div>&nbsp; &nbsp;</div>
                            <div class="text">
                                <#list links as link>
                                    <a rel="friend" href="${link.linkAddress}" title="${link.linkDescription}" target="_blank">
                                        ${link.linkTitle}
                                    </a>
                                </#list>
                            </div>
                        </div>
                    </#if>

                    <#if 0 != mostUsedTags?size>
                        <div class="side-tile tags-tile fn__flex">
                            <div>
                                <span data-ico="&#x003b;"></span>
                                <div class="title">
                                    ${popTagsLabel}
                                </div>
                            </div>
                            <div>&nbsp; &nbsp;</div>
                            <div class="text">
                                <#list mostUsedTags as tag>
                                    <a rel="tag" href="${servePath}/tags/${tag.tagTitle?url('UTF-8')}"
                                       title="${tag.tagTitle}(${tag.tagPublishedRefCount})">
                                        ${tag.tagTitle}
                                    </a>
                                </#list>
                            </div>
                        </div>
                    </#if>
                </div>
                <div class="side">
                    <div>
                        <form action="${servePath}/search">
                            <input placeholder="Search" id="search" type="text" name="keyword" /><span onclick="$(this).parent().submit()" data-ico="&#x0067;"></span>
                            <input type="submit" value="" class="fn-none" />
                        </form>

                        <#if "" != noticeBoard>
                        <div class="notice-board side-tile">
                            <span data-ico="&#xe1e9;"></span>
                            <div class="title">
                                ${noticeBoard}
                            </div>
                            <div class="text">
                                ${noticeBoardLabel}
                            </div>
                        </div>
                        </#if>

                        <a rel="alternate" href="${servePath}/rss.xml" class="user side-tile">
                            <span data-ico="&#xe135;"></span>
                            <div class="text">
                                RSS
                            </div>
                        </a>

                        <div class="online-count side-tile">
                            <span data-ico="&#xe037;"></span>
                            <div class="text">
                                ${viewCount1Label}
                                <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span><br/>
                                ${articleCount1Label}
                                ${statistic.statisticPublishedBlogArticleCount}<br/>
                            </div>
                        </div>

                        <#include "copyright.ftl">
                    </div>
                </div>
            </div>
        </div>
        <span id="goTop" onclick="Util.goTop()" data-ico="&#xe042;" class="side-tile"></span>
        <#include "footer.ftl"/>
    </body>
</html>
