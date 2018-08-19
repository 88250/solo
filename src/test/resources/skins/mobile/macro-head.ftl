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
<#macro head title>
<meta charset="utf-8" />
<title>${title}</title>
<#nested>
<meta name="author" content="${blogTitle?html}" />
<meta name="generator" content="Solo" />
<meta name="copyright" content="B3log" />
<meta name="owner" content="B3log Team" />
<meta name="revised" content="${blogTitle?html}, ${year}" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=2.0, user-scalable=yes" />
<meta http-equiv="Window-target" content="_top" />
<link type="text/css" rel="stylesheet" href="${staticServePath}/skins/${skinDirName}/themes/default/style.css?${staticResourceVersion}" charset="utf-8" />
<style type="text/css">
#headerbar, #wptouch-login, #wptouch-search {
	background: #000000 url(/skins/${skinDirName}/themes/core/core-images/head-fade-bk.png);
}
#headerbar-title, #headerbar-title a {
	color: #eeeeee;
}
#wptouch-menu-inner a:hover {
	color: #006bb3;
}
#catsmenu-inner a:hover {
	color: #006bb3;
}
#drop-fade {
background: #333333;
}
a, h3#com-head {
	color: #006bb3;
}

a.h2, a.sh2, .page h2 {
font-family: 'Helvetica Neue';
}


a.h2{
text-overflow: ellipsis;
white-space: nowrap;
overflow: hidden;
}

</style>
<link href="${servePath}/blog-articles-rss.do" title="RSS" type="application/rss+xml" rel="alternate" />
<link rel="icon" type="image/png" href="${servePath}/favicon.png" />
<script type='text/javascript' src='${staticServePath}/skins/${skinDirName}/js/l10n${miniPostfix}.js?${staticResourceVersion}'></script>
<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type='text/javascript' src='${staticServePath}/skins/${skinDirName}/themes/core/core.js?${staticResourceVersion}'></script>
<script type="text/javascript">
	// Hides the addressbar on non-post pages
	function hideURLbar() { window.scrollTo(0,1); }
	addEventListener('load', function() { setTimeout(hideURLbar, 0); }, false );
</script>
${htmlHead}
</#macro>