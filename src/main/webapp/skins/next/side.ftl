<div class="sidebar-toggle">
    <div class="sidebar-toggle-line-wrap">
        <span class="sidebar-toggle-line sidebar-toggle-line-first"></span>
        <span class="sidebar-toggle-line sidebar-toggle-line-middle"></span>
        <span class="sidebar-toggle-line sidebar-toggle-line-last"></span>
    </div>
</div>

<aside id="sidebar" class="sidebar">
    <div class="sidebar-inner">





        <section class="site-overview sidebar-panel  sidebar-panel-active ">
            <div class="site-author motion-element" itemprop="author" itemscope="" itemtype="http://schema.org/Person">
                <img class="site-author-image skip" src="http://araome.yiluup.com/xiongmao.jpg" alt="ARao Lin" itemprop="image">
                <p class="site-author-name" itemprop="name">ARao Lin</p>
            </div>
            <p class="site-description motion-element" itemprop="description">我们的征途是星辰大海</p>
            <nav class="site-state motion-element">
                <div class="site-state-item site-state-posts">
                    <a href="/archives">
                        <span class="site-state-item-count">8</span>
                        <span class="site-state-item-name">日志</span>
                    </a>
                </div>

                <div class="site-state-item site-state-categories">

                    <span class="site-state-item-count">0</span>
                    <span class="site-state-item-name">分类</span>

                </div>

                <div class="site-state-item site-state-tags">
                    <a href="/tags">
                        <span class="site-state-item-count">18</span>
                        <span class="site-state-item-name">标签</span>
                    </a>
                </div>

            </nav>


            <div class="feed-link motion-element">
                <a href="/atom.xml" rel="alternate">
                    <i class="fa fa-rss"></i>
                    RSS
                </a>
            </div>


            <div class="links-of-author motion-element">


                <span class="links-of-author-item">
                    <a href="https://github.com/araolin" target="_blank" rel="external nofollow">

                        <i class="fa fa-github"></i> GitHub

                    </a>
                </span>

                <span class="links-of-author-item">
                    <a href="http://weibo.com/208139345" target="_blank" rel="external nofollow">

                        <i class="fa fa-weibo"></i> Weibo

                    </a>
                </span>


            </div>



            <div class="cc-license motion-element" itemprop="license">
                <a href="http://creativecommons.org/licenses/by-nc-sa/4.0" class="cc-opacity" target="_blank" rel="external nofollow">
                    <img class="skip" src="/images/cc-by-nc-sa.svg" alt="Creative Commons">
                </a>
            </div>


            <div class="links-of-author motion-element">

                <p class="site-author-name">Links</p>

                <span class="links-of-author-item">
                    <a href="https://shop152714861.taobao.com" target="_blank">我的男装店</a>
                </span>


            </div>

        </section>



    </div>
</aside>
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
            <#if noticeBoard??>
            <div>${noticeBoard}</div>
            </#if>
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