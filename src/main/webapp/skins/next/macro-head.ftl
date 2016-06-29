<#macro head title>
<meta charset="utf-8" />
<title>${title}</title>
<#nested>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<meta name="author" content="${blogTitle?html}" />
<meta name="generator" content="Solo" />
<meta name="owner" content="B3log Team" />
<meta name="revised" content="${blogTitle?html}, ${year}" />
<meta name="copyright" content="B3log" />
<meta http-equiv="Window-target" content="_top" />
<link href="//fonts.useso.com/css?family=Lato:300,400,700,400italic&subset=latin,latin-ext" rel="stylesheet" type="text/css">
<link href="http://www.arao.me/vendors/font-awesome/css/font-awesome.min.css?v=4.4.0" rel="stylesheet" type="text/css">
<link href="http://www.arao.me/css/main.css?v=0.4.5.2" rel="stylesheet" type="text/css"/>
<link type="text/css" rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/css/${skinDirName}${miniPostfix}.css?${staticResourceVersion}" charset="utf-8" />
<link href="${servePath}/blog-articles-rss.do" title="RSS" type="application/rss+xml" rel="alternate" />
<link rel="icon" type="image/png" href="${servePath}/favicon.png" />
${htmlHead}
</#macro>