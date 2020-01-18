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
<div class="copyright">
    &copy; ${year} <a href="${servePath}">${blogTitle}</a> ${footerContent}<br/>
    Powered by <a href="https://solo.b3log.org" target="_blank">Solo</a><br/>
    Theme ${skinDirName} by <a rel="friend" href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
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
