<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>${blogTitle}</title>
        <meta name="keywords" content="GAE 博客,blog,b3log,kill IE6" />
        <meta name="description" content="An open source blog based on GAE Java,GAE Java 开源博客,Let's kill IE6" />
        <meta name="owner" content="B3log Team" />
        <meta name="author" content="B3log Team" />
        <meta name="generator" content="B3log Solo" />
        <meta name="copyright" content="B3log" />
        <meta name="revised" content="B3log, ${year}" />
        <meta http-equiv="Window-target" content="_top" />
        <link type="text/css" rel="stylesheet" href="${staticServePath}/css/default-init${miniPostfix}.css?${staticResourceVersion}" charset="utf-8" />
        <link rel="icon" type="image/png" href="${staticServePath}/favicon.png" />
    </head>
    <body>
        <div class="wrapper">
            <div class="wrap">
                <div class="content">
                    <div class="logo">
                        <a href="http://b3log.org" target="_blank">
                            <img border="0" width="153" height="56" alt="B3log" title="B3log" src="${staticServePath}/images/logo.jpg"/>
                        </a>
                    </div>
                    <div class="main kill">
                        ${killBrowserLabel}
                        <img src='${staticServePath}/images/kill-browser.png' title='Kill IE6' alt='Kill IE6'/>
                        <a href="http://b3log.org" target="_blank">
                            <img border="0" class="icon" alt="B3log" title="B3log" src="${staticServePath}/favicon.png"/>
                        </a>
                    </div>
                    <span class="clear"></span>
                </div>
            </div>

            <div class="footerWrapper">
                <div class="footer">
                    &copy; ${year} - <a href="${servePath}">${blogTitle}</a><br/>
                    Powered by
                    <a href="http://b3log.org" target="_blank">
                        ${b3logLabel}&nbsp;
                        <span class="solo">Solo</span></a>,
                    ver ${version}
                </div>
            </div>
        </div>
    </body>
</html>
