<div class="header">
    <div class="container fn-clear">
        <div class="left">
            <h1 class="title">
                <a href="${servePath}">
                    ${blogTitle}
                </a>
            </h1>
            <span>${blogSubtitle}</span>
        </div>
        <ul class="left">
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
                <a rel="alternate" href="${servePath}/blog-articles-feed.do">Atom <img src="${staticServePath}/images/feed.png" alt="Atom"/></a>
            </li>
        </ul>
        <form class="right" target="_blank" method="get" action="http://www.google.com/search">
            <input placeholder="${searchLabel}" id="search" type="text" name="q" />
            <input type="submit" name="btnG" value="" class="none" />
            <input type="hidden" name="oe" value="UTF-8" />
            <input type="hidden" name="ie" value="UTF-8" />
            <input type="hidden" name="newwindow" value="0" />
            <input type="hidden" name="sitesearch" value="${blogHost}" />
        </form>
    </div>
</div>