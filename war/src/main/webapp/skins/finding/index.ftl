<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <#if metaKeywords??>
        <meta name="keywords" content="${metaKeywords}"/>
        </#if>
        <#if metaDescription??>
        <meta name="description" content="${metaDescription}"/>
        </#if>
        </@head>
    </head>
    <body class="nav-closed">
        <div class="nav">
            ${topBarReplacement}

            <div class="left">
                <#list pageNavigations as page>
                <span>
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}">${page.pageTitle}</a>&nbsp;&nbsp;
                </span>
                </#list>
                <a href="${servePath}/tags.html">${allTagsLabel}</a>&nbsp;&nbsp;
                <a rel="alternate" href="${servePath}/blog-articles-feed.do">${atomLabel}</a><a href="${servePath}/blog-articles-feed.do"><img src="${staticServePath}/images/feed.png" alt="Atom"/></a>
            </div>

            <div class="right" id="statistic">
                <span>${viewCount1Label}
                    <span class='error-msg'>
                        ${statistic.statisticBlogViewCount}
                    </span>
                    &nbsp;&nbsp;
                </span>
                <span>
                    ${articleCount1Label}
                    <span class='error-msg'>
                        ${statistic.statisticPublishedBlogArticleCount}
                    </span>
                    &nbsp;&nbsp;
                </span>
                <span>
                    ${commentCount1Label}
                    <span class='error-msg'>
                        ${statistic.statisticPublishedBlogCommentCount}
                    </span>
                </span>
            </div>

            <#include "side.ftl">
        </div>
        <div class="site-wrapper">
            <header class="main-header">
                <div class="fn-clear">
                    <a class="menu-button icon-menu" href="#"><span class="word">Menu</span></a>
                </div>
                <div class="fn-vertical">
                    <div class="main-header-content fn-wrap">
                        <h1 class="page-title">
                            ${blogTitle}
                        </h1>
                        <h2 class="page-description">${blogSubtitle}</h2>
                    </div>
                </div>
                <a class="scroll-down icon-arrow-left" href="#content" data-offset="-45"></a>
            </header>
            <main id="content" class="fn-wrap">
                <#include "article-list.ftl">
            </main>
            <footer class="site-footer fn-clear">
                <#include "footer.ftl">
            </footer>
        </div>
        <script>
            $(".scroll-down").click(function (event) {
                event.preventDefault();

                var $this = $(this),
                        $htmlBody = $('html, body'),
                        offset = ($this.attr('data-offset')) ? $this.attr('data-offset') : false,
                        toMove = parseInt(offset);

                $htmlBody.stop(true, false).animate({scrollTop: ($(this.hash).offset().top + toMove)}, 500);
            });

            $(".menu-button").click(function () {
                $("body").toggleClass("nav-opened nav-closed");
            });
        </script>
    </body>
</html>
