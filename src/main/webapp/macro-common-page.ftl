<#macro commonPage title>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="robots" content="none"/>
    <title><#if blogTitle??>${blogTitle} - </#if>${title}</title>
    <link type="text/css" rel="stylesheet"
          href="${staticServePath}/css/default-init${miniPostfix}.css?${staticResourceVersion}" charset="utf-8"/>
    <link rel="icon" type="image/png" href="${staticServePath}/favicon.png"/>
</head>
<body>
<div class="wrap">
    <div class="content">
        <div class="logo">
            <a href="http://b3log.org" target="_blank">
                <img border="0" alt="Solo" title="Solo" src="${staticServePath}/images/logo.jpg"/>
            </a>
        </div>
        <div class="main">
            <#nested >
        </div>
        <span class="clear"></span>
    </div>
</div>
<div class="footerWrapper">
    <div class="footer">
        Powered by <a href="http://b3log.org" target="_blank">B3log 开源</a> • Solo ${version}
    </div>
</div>
</body>
</html>
</#macro>
