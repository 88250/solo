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
            <#include "side.ftl">
        </div>
        <div class="site-wrapper">
            <#include "header.ftl">
            <main id="content" class="fn-wrap">
                <#include "article-list.ftl">
            </main>
            <#include "footer.ftl">
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

            $('body').click(function (event) {
                if ($(event.target).closest('.nav').length === 0 && $("body").hasClass('nav-opened')) {
                    $("body").removeClass('nav-opened').addClass('nav-closed');
                }
            });

            $(".menu-button, .nav-close").click(function (event) {
                event.stopPropagation();
                $("body").toggleClass("nav-opened nav-closed");
            });
        </script>
    </body>
</html>
