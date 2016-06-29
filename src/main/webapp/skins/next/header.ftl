<header id="header" class="header">
    <div class="header-inner">
        <div class="site-meta ">
            <div class="custom-logo-site-title">
                <a href="${servePath}" class="brand" rel="start">
                    <span class="logo-line-before"><i></i></span>
                    <span class="site-title">${blogTitle}</span>
                    <span class="logo-line-after"><i></i></span>
                </a>
            </div>
            <p class="site-subtitle">${blogSubtitle}</p>
        </div>

        <div class="site-nav-toggle">
            <button>
                <span class="btn-bar"></span>
                <span class="btn-bar"></span>
                <span class="btn-bar"></span>
            </button>
        </div>

        <nav class="site-nav">
            <ul id="menu" class="menu menu-left">
                <#list pageNavigations as page>
                <li class="menu-item menu-item-archives">
                    <a href="${page.pagePermalink}" target="${page.pageOpenTarget}" rel="section">
                        <i class="menu-item-icon fa fa-archive fa-fw"></i> <br>
                        ${page.pageTitle}
                    </a>
                </li>
                </#list>  
                <li class="menu-item menu-item-archives">
                    <a href="${servePath}/dynamic.html" rel="section">
                        <i class="menu-item-icon fa fa-archive fa-fw"></i> <br>
                        ${dynamicLabel}
                    </a>
                </li>
                <li class="menu-item menu-item-archives">
                    <a href="${servePath}/tags.html" rel="section">
                        <i class="menu-item-icon fa fa-archive fa-fw"></i> <br>
                        ${allTagsLabel}
                    </a>  
                </li>
                <li class="menu-item menu-item-archives">
                    <a rel="alternate" href="${servePath}/blog-articles-rss.do" rel="section">
                        <i class="menu-item-icon fa fa-archive fa-fw"></i> <br>
                        RSS
                    </a>
                </li>
            </ul>

            <div class="site-search">
                <form target="_blank" action="http://zhannei.baidu.com/cse/site">
                    <input placeholder="${searchLabel}" id="search" type="text" name="q"  class="st-search-input st-default-search-input" />
                    <input type="submit" value="" class="fn-none" />
                    <input type="hidden" name="cc" value="${serverHost}">
                </form>
            </div>
        </nav>
    </div>
</header>