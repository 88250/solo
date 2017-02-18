<header>
    <div class="banner">
        <div class="fn-clear wrapper">
            <h1 class="fn-inline">
                <a href="${servePath}" rel="start">
                    ${blogTitle}
                </a>
            </h1>
            <small> &nbsp; ${blogSubtitle}</small>
            <div class="fn-right">
                <#if isLoggedIn>
                    <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                        <i class="icon-setting"></i> ${adminLabel}
                    </a>
                    <a href="${logoutURL}">
                        <i class="icon-logout"></i> ${logoutLabel}
                    </a>
                    <#else>
                    <a href="${loginURL}">
                        <i class="icon-login"></i> ${loginLabel}
                    </a>
                    <a href="${servePath}/register">
                        <i class="icon-register"></i> ${registerLabel}
                    </a>
                </#if>
            </div>
        </div>
    </div>

    <div class="navbar">
        <div class="fn-clear wrapper">
            <nav class="fn-left">
                <a href="${servePath}">
                    <i class="icon-home"></i>
                    ${indexLabel}
                </a>
                <#list pageNavigations as page>
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                        ${page.pageTitle}
                    </a>
                </#list>
                <a href="${servePath}/dynamic.html" rel="section">
                    <i class="icon-refresh"></i> ${dynamicLabel}
                </a>
                <a href="${servePath}/tags.html" rel="section">
                    <i class="icon-tags"></i> ${allTagsLabel}
                </a>
                <a href="${servePath}/archives.html">
                    <i class="icon-inbox"></i> ${archiveLabel}
                </a>
                <a rel="archive" href="${servePath}/links.html">
                    <i class="icon-link"></i> ${linkLabel}
                </a>
                <a rel="alternate" href="${servePath}/blog-articles-rss.do" rel="section">
                    <i class="icon-rss"></i> RSS
                </a>
            </nav>
            <div class="fn-right">
                <form class="form" target="_blank" action="http://zhannei.baidu.com/cse/site">
                    <input placeholder="${searchLabel}" id="search" type="text" name="q"/>
                    <button type="submit"><i class="icon-search"></i></button>
                    <input type="hidden" name="cc" value="${serverHost}">
                </form>
            </div>
        </div>
    </div>
</header>
<div class="responsive fn-none">
    <i class="icon-list"></i>
    <ul class="list">
        <#if isLoggedIn>
            <li>
                <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                    <i class="icon-setting"></i> ${adminLabel}
                </a>
            </li>
            <li>
                <a href="${logoutURL}">
                    <i class="icon-logout"></i> ${logoutLabel}
                </a>
            </li>
            <#else>
                <li>
                    <a href="${loginURL}">
                        <i class="icon-login"></i> ${loginLabel}
                    </a>
                </li>
                <li>
                    <a href="${servePath}/register">
                        <i class="icon-register"></i> ${registerLabel}
                    </a>
                </li>
        </#if>
        <li>
            <a href="${servePath}">
                <i class="icon-home"></i>
                ${indexLabel}
            </a>
        </li>
        <#list pageNavigations as page>
            <li>
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                    ${page.pageTitle}
                </a>
            </li>
        </#list>
        <li>
            <a href="${servePath}/dynamic.html" rel="section">
                <i class="icon-refresh"></i> ${dynamicLabel}
            </a>
        </li>
        <li>
            <a href="${servePath}/tags.html" rel="section">
                <i class="icon-tags"></i> ${allTagsLabel}
            </a>
        </li>
        <li>
            <a href="${servePath}/archives.html">
                <i class="icon-inbox"></i> ${archiveLabel}
            </a>
        </li>
        <li>
            <a rel="archive" href="${servePath}/links.html">
                <i class="icon-link"></i> ${linkLabel}
            </a>
        </li>
        <li>
            <a rel="alternate" href="${servePath}/blog-articles-rss.do" rel="section">
                <i class="icon-rss"></i> RSS
            </a>
        </li>
    </ul>
</div>