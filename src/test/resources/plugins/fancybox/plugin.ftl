<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

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
<script type="text/javascript">
    (function () {
        var $groups =  $("a[rel=group]"),
        $fancybox = $(".fancybox");
        if ($groups.length > 0 || $fancybox.length > 0) {
            if (document.createStyleSheet) {
                document.createStyleSheet("${staticServePath}/plugins/fancybox/js/jquery.fancybox.css");
            } else {
                $("head").append($("<link rel='stylesheet' href='${staticServePath}/plugins/fancybox/js/jquery.fancybox.css' type='text/css' charset='utf-8' />"));
            } 
            
            $.ajax({
                url: "${staticServePath}/plugins/fancybox/js/jquery.fancybox.pack.js",
                dataType: "script",
                cache: true,
                complete: function() {
                    $groups.fancybox({
                        openEffect  : 'none',
                        closeEffect : 'none',
                        prevEffect : 'none',
                        nextEffect : 'none'
                    });
                    $fancybox.fancybox({
                        openEffect  : 'none',
                        closeEffect : 'none',
                        prevEffect : 'none',
                        nextEffect : 'none'
                    });
                }
            });
        }
    })();
</script>
