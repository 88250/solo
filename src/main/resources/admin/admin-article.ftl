<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

    Solo is licensed under Mulan PSL v2.
    You can use this software according to the terms and conditions of the Mulan PSL v2.
    You may obtain a copy of Mulan PSL v2 at:
            http://license.coscl.org.cn/MulanPSL2
    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
    See the Mulan PSL v2 for more details.

-->
<div class="form">
    <div>
        <label>${title1Label}</label>
        <input id="title" type="text"/>
    </div>
    <div>
        <label>${content1Label}</label>
        <div class="fn__right">
            <label for="articleThumbnail" class="checkbox" style="margin-top: 0">
                <input type="checkbox"
                       id="articleThumbnail" onclick="$('.article__thumbnail').slideToggle()"/>
                <span>&nbsp;${useTumbnailLabel}</span>
            </label>
        </div>
        <div class="fn__clear"></div>
        <div class="article__thumbnail">
            <div class="thumbnail__img"></div>
            <button id="articleThumbnailBtn">${chageLabel}</button>
        </div>
        <div>
            <div id="articleContent" name="articleContent"
                 style="height: 500px;width:100%;"></div>
        </div>
    </div>
    <div>
        <label>${tags1WithTips1Label}</label>
        <div class="tag__select">
            <input id="tag" type="text"/>
        </div>
    </div>
    <div class="comment-content">
        <label>${abstract1Label}</label>
        <div>
            <div id="abstract" style="height: 200px;width: 100%;" name="abstract"></div>
        </div>
    </div>
    <div class="fn__flex">
        <div class="fn__flex fn__flex-1" style="align-items: center">
            <label for="permalink" class="permalink__label" style="margin-bottom: 0">${permalink1Label}</label>
            <input id="permalink" class="fn__flex-1" type="text" style="margin: 0 12px 0 6px;"/>
        </div>
        <div class="fn__right viewpwd__panel">
            <label for="viewPwd">${articleViewPwd1Label}</label>
            <input id="viewPwd" type="text" style="width: 156px"/>
        </div>
    </div>
    <div class="fn__flex">
        <span class="signs fn__flex-1">
            <label>${sign1Label}</label>
            <button style="margin-left: 0px;" id="articleSign1" class="selected">${signLabel}1</button>
            <button id="articleSign2">${signLabel}2</button>
            <button id="articleSign3">${signLabel}3</button>
            <button id="articleSign0">${noSignLabel}</button>
        </span>
        <div class="fn__flex-center article-commentable__panel">
            <span id="postToCommunityPanel">
                <label class="checkbox">
                    <input id="postToCommunity" type="checkbox"/>
                    <span>&nbsp;</span>
                    <a href="https://ld246.com/article/1546941897596" target="_blank">${syncToCommunityLabel}</a>
                </label>
            </span>
        </div>
    </div>
    <div class="fn__right">
        <button id="unSubmitArticle" class="fn__none marginRight12"
                onclick="admin.article.unPublish();">${unPublishLabel}</button>
        <button class="marginRight12" id="saveArticle">${saveLabel}${draftListLabel}</button>
        <button id="submitArticle">${publishLabel}</button>
    </div>
    <div class="fn__clear"></div>
</div>
${plugins}
