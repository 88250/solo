<div class="side">
    <div class="fn-clear">
        <form class="right" target="_blank" method="get" action="http://www.google.com/search">
            <input placeholder="Search" id="search" type="text" name="q" /><span data-ico="&#x0067;"></span>
            <input type="submit" name="btnG" value="" class="fn-none" />
            <input type="hidden" name="oe" value="UTF-8" />
            <input type="hidden" name="ie" value="UTF-8" />
            <input type="hidden" name="newwindow" value="0" />
            <input type="hidden" name="sitesearch" value="${blogHost}" />
        </form>

        <#if "" != noticeBoard>
        <div class="notice-board side-tile">
            <span data-ico="&#xe1e9;"></span>
            <div class="title">
                  ${noticeBoard}
            </div>
            <div class="text">
                 ${noticeBoardLabel}
            </div>
        </div>
        </#if>

        <div class="online-count side-tile">
            <span data-ico="&#xe037;"></span>
            <div class="text">
                ${viewCount1Label}
                ${statistic.statisticBlogViewCount}<br/>
                ${articleCount1Label}
                ${statistic.statisticPublishedBlogArticleCount}<br/>
                ${commentCount1Label}
                ${statistic.statisticPublishedBlogCommentCount}<br/>
            </div>
        </div>

        <a rel="alternate" href="${servePath}/blog-articles-feed.do" class="atom side-tile">
            <span data-ico="&#xe135;"></span>
            <div class="title">
                Atom
            </div>
        </a>

        <a href="javascript: MetroHot.goTranslate();" class="translate side-tile">
            <span data-ico="&#x0038;"></span>
            <div class="title">
              ${translateLabel}
            </div>
        </a>

        <a href="" class="login side-tile">
            <span data-ico="&#xe03f;"></span>
            <div class="title">
               ${loginLabel}
            </div>
        </a>

        <a href="${servePath}/register" class="register side-tile">
            <span data-ico="&#xe02b;"></span>
            <div class="title">
               ${registerLabel}
            </div>
        </a>

         <div class="user side-tile">
            <span data-ico="&#x0060;"></span>
            <div class="fn-clear title">
                ${commentNameLabel}
            </div>
            <div class="text"></div>
         </div>

         <div class="clear side-tile">
            <span data-ico="&#xe003;"></span>
            <div class="title">
                ${clearCacheLabel}
            </div>
            <div class="text">
                <a href="javascript:Util.clearCache();">${clearCachePageLabel}</a>
                <br />
                <a href="javascript:Util.clearCache('all');">${clearAllCacheLabel}</a>
            </div>
         </div>

          <a href="${servePath}/admin-index.do#main" class="settings side-tile">
              <span data-ico="&#x0070;"></span>
              <div class="title">
                 ${adminLabel}
              </div>
           </a>

          <a href="${servePath}/register" class="logout side-tile">
              <span data-ico="&#xe040;"></span>
              <div class="title">
                ${logoutLabel}
              </div>
          </a>
    </div>
</div>