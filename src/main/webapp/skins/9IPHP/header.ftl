<header class="header">
    <div class="fn-clear wrapper">
        <div class="fn-left">
            <a href="${servePath}" rel="start">
                ${blogTitle}
            </a>
            ${blogSubtitle}
        </div>
        <div class="fn-right">
            <#if isLoggedIn>
            <span class="links-of-author-item">
                <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                    <i class="icon-setting"></i> ${adminLabel}
                </a>
            </span>

                <span class="links-of-author-item">
                <a href="${logoutURL}">
                    <i class="icon-logout"></i> ${logoutLabel}
                </a>
            </span>
                <#else>
            <span class="links-of-author-item">
                <a href="${loginURL}">
                    <i class="fa fa-github"></i> ${loginLabel}
                </a>
            </span>

                    <span class="links-of-author-item">
                <a href="${servePath}/register">
                    <i class="icon-register"></i> ${registerLabel}
                </a>
            </span>
            </#if>
        </div>
    </div>

    <nav>
        <ul class="menu">
            <#list pageNavigations as page>
            <li class="menu-item">
                <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                    ${page.pageTitle}
                </a>
            </li>
            </#list>
            <li class="menu-item">
                <a href="${servePath}/dynamic.html" rel="section">
                    ${dynamicLabel}
                </a>
            </li>
            <li class="menu-item">
                <a href="${servePath}/tags.html" rel="section">
                    ${allTagsLabel}
                </a>
            </li>
            <li class="menu-item">
                <a href="${servePath}/archives.html">
                    ${archiveLabel}
                </a>
            </li>
            <li class="menu-item">
                <a rel="alternate" href="${servePath}/blog-articles-rss.do" rel="section">
                    RSS
                </a>
            </li>
        </ul>

        <div class="site-search">
            <form target="_blank" action="http://zhannei.baidu.com/cse/site">
                <input placeholder="${searchLabel}" id="search" type="text" name="q"/>
                <input type="submit" value="" class="fn-none" />
                <input type="hidden" name="cc" value="${serverHost}">
            </form>
        </div>
    </nav>
</header>