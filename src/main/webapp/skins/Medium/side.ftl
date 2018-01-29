


        <#if 0 != mostCommentArticles?size>
            <div class="module">
                <header><h2>${mostCommentArticlesLabel}</h2></header>
                <main class="list">
                    <ul>
                        <#list mostCommentArticles as article>
                            <li>
                                <a rel="nofollow" aria-label="${article.articleCommentCount} ${commentLabel}"
                                   class="tooltipped tooltipped-e"
                                   href="${servePath}${article.articlePermalink}">
                                    ${article.articleTitle}
                                </a>
                            </li>
                        </#list>
                    </ul>
                </main>
            </div>
        </#if>

        <#if 0 != mostViewCountArticles?size>
            <div class="module">
                <header><h2>${mostViewCountArticlesLabel}</h2></header>
                <main class="list">
                    <ul>
                        <#list mostViewCountArticles as article>
                            <li>
                                <a rel="nofollow" aria-label="${article.articleCommentCount} ${commentLabel}"
                                   class="tooltipped tooltipped-e"
                                   href="${servePath}${article.articlePermalink}">
                                    ${article.articleTitle}
                                </a>
                            </li>
                        </#list>
                    </ul>
                </main>
            </div>
        </#if>