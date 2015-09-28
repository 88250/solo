<header class="main-header"<#if !isIndex> style='height:30vh;'</#if>>
        <div class="fn-clear">
        <a class="menu-button icon-menu" href="#"><span class="word">Menu</span></a>
    </div>
    <div class="fn-vertical">
        <div class="main-header-content fn-wrap">
            <h1 class="page-title">
                <a href="${servePath}">${blogTitle}</a>
                <#if "" != noticeBoard>
                <small class="page-description"> &nbsp; ${blogSubtitle}</small>
                </#if>
            </h1>
            <h2 class="page-description">
                <#if "" != noticeBoard>
                ${noticeBoard}
                <#else>
                ${blogSubtitle}
                </#if>
            </h2>
        </div>
    </div>
    <#if isIndex><a class="scroll-down icon-arrow-left" href="#content" data-offset="-45"></a></#if>
</header>