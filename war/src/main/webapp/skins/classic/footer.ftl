<span style="color: gray;">&copy; ${year}</span> - <a href="${servePath}">${blogTitle}</a><br/>
Powered by
<a href="http://b3log-solo.googlecode.com" target="_blank" class="logo">
    ${b3logLabel}&nbsp;
    <span style="color: orangered; font-weight: bold;">Solo</span></a>,
ver ${version}&nbsp;&nbsp;
Theme by <a rel="friend" href="http://vanessa.b3log.org" target="_blank">Vanessa</a>.
<div class='goTopIcon' onclick='Util.goTop();'></div>
<div class='goBottomIcon' onclick='Util.goBottom();'></div>
<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">
    var latkeConfig = {
        "servePath": "${servePath}",
        "staticServePath": "${staticServePath}"
    };

    var Label = {
        "skinDirName": "${skinDirName}",
        "em00Label": "${em00Label}",
        "em01Label": "${em01Label}",
        "em02Label": "${em02Label}",
        "em03Label": "${em03Label}",
        "em04Label": "${em04Label}",
        "em05Label": "${em05Label}",
        "em06Label": "${em06Label}",
        "em07Label": "${em07Label}",
        "em08Label": "${em08Label}",
        "em09Label": "${em09Label}",
        "em10Label": "${em10Label}",
        "em11Label": "${em11Label}",
        "em12Label": "${em12Label}",
        "em13Label": "${em13Label}",
        "em14Label": "${em14Label}"
    };
    
    var collapseArchive = function (it, year) {
        var tag = true,
        text = it.innerHTML;
        if (text.indexOf("-") > -1) {
            it.innerHTML = text.replace("-", "+");
            tag = false;
        } else {
            it.innerHTML = text.replace("+", "-");
        }
    
        $("#archiveSide li").each(function () {
            var $this = $(this);
            // hide other year month archives
            if ($this.data("year") === year) {
                if (tag) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
            }
        });
    };
    
    $(document).ready(function () {
        var currentYear = (new Date()).getFullYear(),
        year = currentYear;
        $("#archiveSide li").each(function (i) {
            var $this = $(this);
        
            // hide other year month archives
            if ($this.data("year") !== currentYear) {
                $(this).hide()
            }
        
            // append year archive
            if (year !== $this.data("year")) {
                year = $this.data("year");
                $this.before("<li class='pointer'><div onclick='collapseArchive(this, " + 
                    year + ")'>" + year + "&nbsp;\u5e74 +</div></li>");
            }
        });
        
        Util.init();
        Util.replaceSideEm($("#recentComments li"));
    });
</script>
${plugins}
