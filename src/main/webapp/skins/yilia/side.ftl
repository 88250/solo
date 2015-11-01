<div class="side">
    <div class="overlay"></div>
    <header class="content">
        <a href="${servePath}">
            <img class="avatar" src="${adminUser.userAvatar}" title="${userName}"/>
        </a>
        <hgroup>
            <h1>
                <a href="${servePath}">${blogTitle}</a>
            </h1>
        </hgroup>
        <#if "" != noticeBoard>
        <p class="subtitle">
            ${blogSubtitle}
        </p>
        </#if>
        <nav>
            <ul>
                <#list pageNavigations as page>
                <li>
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}">${page.pageTitle}</a>
                </li>
                </#list>
                <li>
                    <a href="${servePath}/dynamic.html">${dynamicLabel}</a>
                </li>
                <li>
                    <a href="${servePath}/tags.html">${allTagsLabel}</a>
                </li>
                <li>
                    <a href="${servePath}/archives.html">${archiveLabel}</a>
                </li>
                <li>
                    <a href="${servePath}/links.html">${linkLabel}</a>
                </li>
            </ul>
        </nav>
        <footer>
            <#if isLoggedIn>
            <a href="${servePath}/admin-index.do#main" title="${adminLabel}" class="icon-setting"></a>
            &nbsp; &nbsp; 
            <a title="${logoutLabel}" class="icon-logout" href="${logoutURL}"></a>
            <#else>
            <a title="${loginLabel}" href="${loginURL}" class="icon-login"></a>
            &nbsp; &nbsp; 
            <a href="${servePath}/register" title="${registerLabel}" class="icon-register"></a>
            </#if> &nbsp; &nbsp; 
            <a rel="alternate" href="${servePath}/blog-articles-rss.do" title="${subscribeLabel}" class="icon-rss"></a>
        </footer>
    </header>
</div>