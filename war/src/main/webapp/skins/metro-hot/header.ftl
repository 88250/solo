<div class="fn-clear header">
    <h1 class="fn-left">
        <a class="title" href="javascript: void(0)">
            ${blogTitle}
            <span data-ico="&#xe0f3;"></span>
        </a>
    </h1>
    <ul class="navigation">
        <li>
            <a rel="nofollow" href="${servePath}/">${indexLabel}</a>
        </li>
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
        <li class="last">
            <a href="${servePath}/links.html">${linkLabel}</a>
        </li>
    </ul>
    <div class="fn-right top-info">
        <a title="${loginLabel}" id="login" data-ico="&#xe03f;"></a>
        <a href="${servePath}/admin-index.do#main" title="${adminLabel}" id="settings" data-ico="&#x0070;"></a>
        <hr>
        <a id="logout" title="${logoutLabel}" data-ico="&#xe040;"></a>
        <a href="${servePath}/register" title="${registerLabel}" id="register" data-ico="&#xe02b;"></a>
    </div>
</div>