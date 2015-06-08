<header class="main-header"<#if !isIndex> style='height:30vh;'</#if>>
        <div class="fn-clear">
        <a class="menu-button icon-menu" href="#"><span class="word">Menu</span></a>
    </div>
    <div class="fn-vertical">
        <div class="main-header-content fn-wrap">
            <h1 class="page-title">
                ${blogTitle}
            </h1>
            <h2 class="page-description">${blogSubtitle}</h2>
        </div>
    </div>
    <#if isIndex><a class="scroll-down icon-arrow-left" href="#content" data-offset="-45"></a></#if>
</header>