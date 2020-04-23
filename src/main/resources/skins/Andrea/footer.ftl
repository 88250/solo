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
<div class="copyright">
    &copy; ${year} <a href="${servePath}">${blogTitle}</a> ${footerContent}
</div>
<#include "../../common-template/label.ftl">
<script type="text/javascript" src="${staticServePath}/js/common.min.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">
    // init brush
    var buildBrush = function () {
        $("#brush").height($('body').height() - $(window).height() + 200).css("background-position",
        parseInt((document.documentElement.scrollWidth - 910) / 2 - 56) + "px -200px");
    };

    // init
    $(document).ready(function () {
        Util.setTopBar()

        // brush
        buildBrush();

        $(window).resize(function () {
            buildBrush();
        });

        // bg
        $("#changeBG a").click(function () {
            if (this.className !== 'selected') {
                switch (this.id) {
                    case "greyBG":
                        $("body").css("background-image", "url(/skins/${skinDirName}/images/bg-grey.jpg)");
                        break;
                    case "blueBG":
                        $("body").css("background-image", "url(/skins/${skinDirName}/images/bg-blue.jpg)");
                        break;
                    case "brownBG":
                        $("body").css("background-image", "url(/skins/${skinDirName}/images/bg-brown.jpg)");
                        break;
                }

                $("#changeBG a").removeClass();
                this.className = "selected";
            }
        });

        // page navi
        $(".side-tool li li a").hover(function () {
            if (parseInt($(this).css("padding-left")) === 9) {
                $(this).animate({
                    "padding-left": "54px"
                }, 600 );
            }
        }, function () {
            $(this).animate({
                "padding-left": "9px"
            }, 600 );
        });
    });
</script>
${plugins}
