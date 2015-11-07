<footer class="footer">
    <div class="fn-clear">
        <span class="fn-right">
            ${viewCount1Label}${statistic.statisticBlogViewCount}
            &nbsp;
            ${articleCount1Label}${statistic.statisticPublishedBlogArticleCount}
            &nbsp; 
            ${commentCount1Label}${statistic.statisticPublishedBlogCommentCount}
            &nbsp; 
            ${onlineVisitor1Label}${onlineVisitorCnt}
        </span>
    </div>
    <div class="fn-clear">
        <a href="${servePath}">${blogTitle}</a> 
        &copy; ${year}
        ${footerContent}
        <span class="fn-right">
            Powered by <a href="http://b3log.org" target="_blank">B3log 开源</a> •
            <a href="http://b3log.org/services/#solo" target="_blank">Solo</a> ${version}
        </span>
    </div>
    <span onclick="Util.goTop()" class="icon-goup"></span>
</footer>


<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${staticServePath}/js/common${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript" src="${staticServePath}/skins/${skinDirName}/js/${skinDirName}${miniPostfix}.js?${staticResourceVersion}" charset="utf-8"></script>
<script type="text/javascript">
    var latkeConfig = {
        "servePath": "${servePath}",
        "staticServePath": "${staticServePath}",
        "isLoggedIn": "${isLoggedIn?string}",
        "userName": "${userName}"
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
</script>
${plugins}
