<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-2018, b3log.org & hacpai.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

-->
<div class="form">
    <div>
        <label>${title1Label}</label>
        <input id="title" type="text"/>
    </div>
    <div>
        <label>${content1Label}</label>
        <div class="right">
            <label for="articleThumbnail" style="margin-bottom: 0">
                ${useTumbnailLabel}
                <input type="checkbox" style="vertical-align: middle;"
                       id="articleThumbnail" onclick="$('.article__thumbnail').slideToggle()" />
            </label>
        </div>
        <div class="clear"></div>
        <div class="article__thumbnail">
            <div class="thumbnail__img"></div>
            <button id="articleThumbnailBtn">${chageLabel}</button>
        </div>
        <div>
            <textarea id="articleContent" name="articleContent"
                  style="height: 500px;width:100%;"></textarea>
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
            <textarea id="abstract" style="height: 200px;width: 100%;" name="abstract"></textarea>
        </div>
    </div>
    <div class="fn__flex">
        <div class="fn__flex fn__flex1" style="align-items: center">
            <label for="permalink" class="permalink__label" style="margin-bottom: 0">${permalink1Label}</label>
            <input id="permalink" class="fn__flex1" type="text" style="margin: 0 12px 0 6px;" />
        </div>
        <div class="right viewpwd__panel">
            <label for="viewPwd">${articleViewPwd1Label}</label>
            <input id="viewPwd" type="text" style="width: 156px" />
        </div>
    </div>
    <div>
        <span class="signs">
            <label>${sign1Label}</label>
            <button style="margin-left: 0px;" id="articleSign1" class="selected">${signLabel}1</button>
            <button id="articleSign2">${signLabel}2</button>
            <button id="articleSign3">${signLabel}3</button>
            <button id="articleSign0">${noSignLabel}</button>
        </span>
        <div class="right article-commentable__panel">
            <label for="articleCommentable" style="margin: 13px 0 0 0">${allowComment1Label}</label>
            <input type="checkbox" id="articleCommentable" checked="checked" />
            <span id="postToCommunityPanel" class="none">
                <label for="postToCommunity">
                    <a class="no-underline" href="https://hacpai.com/article/1440573175609" target="_blank">${postToCommunityLabel}</a>
                </label>
                <input id="postToCommunity" type="checkbox" checked="checked"/>
            </span>
        </div>
        <div class="clear"></div>
    </div>
    <div class="right">
        <button id="unSubmitArticle" class="none marginRight12" onclick="admin.article.unPublish();">${unPublishLabel}</button>
        <button class="marginRight12" id="saveArticle">${saveLabel}${draftListLabel}</button>
        <button id="submitArticle">${publishLabel}</button>
    </div>
    <div class="clear"></div>
</div>
${plugins}