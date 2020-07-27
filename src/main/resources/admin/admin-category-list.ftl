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
<div>
    <div id="categoryTable"></div>
    <div id="categoryPagination" class="fn__margin12 fn__right"></div>
</div>
<div class="fn__clear"></div>
<div class="form form__no-table">
    <label for="categoryName">${linkTitle1Label}</label>
    <input id="categoryName" type="text"/>
    <label for="categoryURI">URI：</label>
    <input id="categoryURI" type="text"/>
    <label for="categoryDesc">${linkDescription1Label}</label>
    <input id="categoryDesc" type="text"/>
    <label for="categoryTags">${tags1Label}</label>
    <span class="tag__select">
        <input id="categoryTags" type="text"/>
    </span><br>
    <button onclick="admin.categoryList.add();" class="fn__right">${saveLabel}</button>
    <div class="fn__clear"></div>
</div>
${plugins}
