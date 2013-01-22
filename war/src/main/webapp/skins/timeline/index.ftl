<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <meta name="keywords" content="${metaKeywords}"/>
        <meta name="description" content="<#list articles as article>${article.articleTitle}<#if article_has_next>,</#if></#list>"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="wrapper">
            <#include "article-list.ftl">
        </div>
        <#include "footer.ftl">
        <script>
            timeline.$articles = $(".articles");
            $(window).resize(function () {
                timeline.colH = [0, 20];
                timeline.$articles.find("article").each(function () {
                    var $it = $(this),
                    isLeft = timeline.colH[1] > timeline.colH[0],
                    left = isLeft ? 0 : Math.floor(timeline.$articles.width() / 2),
                    top = isLeft ? timeline.colH[0] : timeline.colH[1];
                    $it.css({
                        "left": left + "px",
                        "top": top + "px"
                    });
                
                    if (isLeft) {
                        $it.addClass("l");
                    } else {
                        $it.addClass("r");
                    }
                
                    timeline.colH[( isLeft ? '0' : '1' )] += parseInt($it.outerHeight(true));
                });
            
                timeline.$articles.height(timeline.colH[0] > timeline.colH[1] ? timeline.colH[0] : timeline.colH[1]);
            });
            
            $(window).resize();$(window).resize();
        </script>
    </body>
</html>
