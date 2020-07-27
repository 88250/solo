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
<div class="content paddingTop12 paddingBottom12">
    <div class="left">
        <br>
        <div>
            <span style="color: gray;">&copy; ${year}</span> <a href="${servePath}">${blogTitle}</a> ${footerContent}
        </div>
    </div>
    <div class="right nowrap">
        <div class="goTop right" onclick="Util.goTop();">${goTopLabel}</div>
        <br/>
        <div class="right">
            ${viewCount1Label}
            <span class='error-msg'>
                <span data-uvstaturl="${servePath}">${statistic.statisticBlogViewCount}</span>
            </span>
            &nbsp;&nbsp;
            ${articleCount1Label}
            <span class='error-msg'>
                ${statistic.statisticPublishedBlogArticleCount}
            </span>
        </div>
    </div>
    <div class="clear"></div>
</div>
<#include "../../common-template/label.ftl">
<script type="text/javascript" src="${staticServePath}/js/common.min.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">

    maxLength =10;

    $(document).ready(function () {
        // article header: user list.
        var isAuthorArticle = false;
        $(".header-user a").each(function () {
            var it = this;
            if (window.location.pathname === it.pathname) {
                it.className = "star-current-icon";
                isAuthorArticle = true;
            }
        });
        if (isAuthorArticle) {
            $(".moon-current-icon").removeClass().addClass("moon-icon");
        }

        Util.setTopBar()

        $(".footer-block").each(function (num) {
            var $lis = $(this).find("li");
            if ($lis.length > maxLength) {
                for (var i = maxLength; i < $lis.length; i++) {
                    $lis.get(i).style.display = "none";
                }
                $(this).find("h4").append("<span class='down-icon' onmouseover=\"showFooterBlock(this, " + num + ");\"></span>");
            }
        });
    });

    var showFooterBlock = function (it, num) {
        var $li = $($(".footer-block").get(num)).find("li");
        for (var i = maxLength; i < $li.length; i++) {
            if (it.className === "down-icon") {
                $($li.get(i)).slideDown("normal");
            } else {
                $($li.get(i)).slideUp("normal");
            }
        }
        if (it.className === "down-icon") {
            it.className = "up-icon";
        } else {
            it.className = "down-icon";
        }
    }
</script>
${plugins}
