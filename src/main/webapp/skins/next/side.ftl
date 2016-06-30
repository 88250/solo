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
            <div class="site-author motion-element">
                <img class="site-author-image skip" src="${adminUser.userAvatar}" title="${userName}"/>
                <p class="site-author-name">${userName}</p>
            </div>
            <#if "" != noticeBoard>
            <p class="site-description motion-element">${blogSubtitle}</p>
            </#if>
            <nav class="site-state motion-element">
                <div class="site-state-item site-state-posts">
                    <a href="/archives">
                        <span class="site-state-item-count">${statistic.statisticPublishedBlogArticleCount}</span>
                        <span class="site-state-item-name">日志</span>
                    </a>
                </div>

                <div class="site-state-item site-state-categories">
                    <span class="site-state-item-count">${statistic.statisticBlogViewCount}</span>
                    <span class="site-state-item-name">浏览</span>
                </div>

                <div class="site-state-item site-state-tags">
                    <a href="/tags">
                        <span class="site-state-item-count">${statistic.statisticPublishedBlogCommentCount}</span>
                        <span class="site-state-item-name">评论</span>
                    </a>
                </div>
            </nav>

            <div class="feed-link motion-element">
                <a href="${servePath}/blog-articles-rss.do" rel="alternate">
                    <i class="fa fa-rss"></i>
                    RSS
                </a>
            </div>

            <div class="links-of-author motion-element">
                <#if isLoggedIn>
                <span class="links-of-author-item">
                    <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                        <i class="fa fa-github"></i> ${adminLabel}
                    </a>
                </span>

                <span class="links-of-author-item">
                    <a href="${logoutURL}">
                        <i class="fa fa-weibo"></i> ${logoutLabel}
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
                        <i class="fa fa-weibo"></i> ${registerLabel}
                    </a>
                </span>
                </#if> 
            </div>
            
            <#if noticeBoard??>
            <div class="links-of-author motion-element">
                ${noticeBoard}
            </div>
            </#if>

            <#if 0 != links?size>
            <div class="links-of-author motion-element">
                <p class="site-author-name">Links</p>
                <#list links as link>
                <span class="links-of-author-item">
                    <a rel="friend" href="${link.linkAddress}" 
                       title="${link.linkDescription}" target="_blank">
                        ${link.linkTitle}
                    </a>
                </span>
                </#list>
            </div>
            </#if>

        </section>
    </div>
</aside>