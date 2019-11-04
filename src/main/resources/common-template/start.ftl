<#--

    Solo - A small and beautiful blogging system written in Java.
    Copyright (c) 2010-present, b3log.org

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
<#include "macro-common_page.ftl">

<@commonPage "${welcomeToSoloLabel}!">
<h2>
    <span>${welcomeToSoloLabel}</span>
    <a target="_blank" href="https://solo.b3log.org">
        <span class="error">&nbsp;Solo</span>
    </a>
</h2>

<div id="github">
    <div class="github__icon startAction">
        <img src="${staticServePath}/images/github.png"/>
    </div>
    <br>
    <button class="startAction">${useGitHubAccountLoginLabel}</button><br>
    <a class="github__link" href="javascript:$('ul').slideToggle()">查看 GitHub 数据使用说明</a>
    <div class="github__text">
        <ul>
            <li>获取用户名、头像等用于初始化</li>
            <li>获取公开仓库信息用于展示</li>
            <li>不会对你的已有数据进行写入</li>
        </ul>
    </div>
    <label class="github__check">
        <input type="checkbox" id="isAgreenCheck" checked>
        <span>
            是否愿意在 GitHub 上收藏该<a href="https://github.com/b3log/solo" target="_blank">项目</a>、关注<a href="https://github.com/88250" target="_blank">开发者</a>并加入 <a href="https://github.com/b3log" target="_blank">B3log 开源组织</a>
        </span>
    </label>
</div>
<script type="text/javascript" src="${staticServePath}/js/lib/jquery/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript">
    (function () {
        try {
            $('.startAction').click(function () {
                var isAgreen = $('#isAgreenCheck').prop('checked') ? '0' : '1'
                window.location.href = '${servePath}/oauth/github/redirect?referer=${referer}__' + isAgreen
                $('#github').addClass('github--loading')
            })
        } catch (e) {
            document.querySelector('.main').innerHTML = "${staticErrorLabel}"
        }
    })()
</script>
</@commonPage>
