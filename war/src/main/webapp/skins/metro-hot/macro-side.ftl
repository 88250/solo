<#macro side isArticle>
<div class="side">
    <div class="fn-clear">
        <#if !isArticle>
        <form target="_blank" method="get" action="http://www.google.com/search">
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
        </#if>

        <a rel="alternate" href="${servePath}/blog-articles-feed.do" class="atom side-tile">
            <span data-ico="&#xe135;"></span>
            <div class="title">
                ${atomLabel}
            </div>
        </a>

        <a href="javascript: MetroHot.goTranslate();" class="translate side-tile">
            <span data-ico="&#x0038;"></span>
            <div class="title">
                ${translateLabel}
            </div>
        </a>

        <#if isArticle>
        <div class="share side-tile">
            <span data-ico="&#xe1fe;"></span>
            <div class="title">
                ${shareLabel}
            </div>
            <div class="text">
                <span data-ico="&#xe0c1;" title="Tencent"></span>
                <span data-ico="&#xe185;" title="Sina"></span>
                <span data-ico="&#xe092;" title="Twitter"></span>
                <span data-ico="&#xe08e;" title="Google"></span>
            </div>
        </div>
        
        <#if relevantArticlesDisplayCount??>
        <#if nextArticlePermalink??>
        <a class="next side-tile" title="${nextArticleTitle}"
           href="${servePath}${nextArticlePermalink}">
            <span data-ico="&#xe107;"></span>
            <div class="title">
                ${nextArticleLabel}
            </div>
        </a>
        <#else>
        <div class="next-disabled side-tile">
            <span data-ico="&#xe107;"></span>
            <div class="title">
                ${newestArticleLabel}
            </div>
        </div>
        </#if>    

        <#if previousArticlePermalink??>
        <a class="prev side-tile" title="${previousArticleTitle}" rel="prev" 
           href="${servePath}${previousArticlePermalink}">
            <span data-ico="&#xe106;"></span>
            <div class="title">
                ${previousArticleLabel}
            </div>
        </a> 
        <#else>
        <div class="prev-disabled side-tile">
            <span data-ico="&#xe106;"></span>
            <div class="title">
                ${lastArticleLabel}
            </div>
        </div>
        </#if>
        
        <#if 0 != relevantArticlesDisplayCount>
        <div id="relevantArticles" class="side-tile article-relative">
            <span data-ico="&#xe020;"></span>
            <div class="title">
                ${relevantArticlesLabel}
            </div>
            <div class="text">
            </div>
        </div>
        </#if>

        <#if 0 != randomArticlesDisplayCount>
        <div id="randomArticles" class="side-tile article-relative">
            <span data-ico="&#xe024;"></span>
            <div class="title">
                ${randomArticlesLabel}
            </div>
            <div class="text">
            </div>
        </div>
        </#if>

        <#if externalRelevantArticlesDisplayCount?? && 0 != externalRelevantArticlesDisplayCount>
        <div id="externalRelevantArticles" class="side-tile article-relative">
            <span data-ico="&#xe021;"></span>
            <div class="title">
                ${externalRelevantArticlesLabel}
            </div>
            <div class="text">
            </div>
        </div>
        </#if>
        </#if>
        </#if>

        <a class="login side-tile">
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
            <span>
                <img src="${gravatar}"/>
            </span>
            <div class="fn-clear title">
                ${commentNameLabel}
            </div>
            <div class="text"></div>
        </div>

        <div class="clear side-tile">
            <span data-ico="&#xe003;"></span>
            <div class="title">
                ${clearCachePageLabel}
            </div>
            <div class="text">
                <a href="javascript:Util.clearCache();">${clearCacheLabel}</a>
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
</#macro>