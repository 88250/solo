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
<#include "macro-common_page.ftl">

<@commonPage "${welcomeToSoloLabel}!">
    <h2>
        <span>${welcomeToSoloLabel}</span>
        <a target="_blank" href="https://b3log.org/solo">
            <span class="error">&nbsp;Solo</span>
        </a>
    </h2>

    <div class="start">
        <a href="${servePath}/login/redirect?referer=${referer}">
            <svg class="start__icon" viewBox="0 0 32 32">
                <path fill="#d23f31" style="fill: var(--color1, #d23f31)"
                      d="M5.787 17.226h17.033l5.954 9.528c0.47 0.752 0.003 1.361-1.042 1.361h-15.141z"></path>
                <path d="M10.74 3.927h17.033c1.045 0 1.512 0.609 1.042 1.361l-5.954 9.528h-19.872l6.379-10.209c0.235-0.376 0.849-0.681 1.372-0.681z"></path>
                <path d="M2.953 17.226h2.839l6.804 10.889h-1.892c-0.523 0-1.137-0.305-1.372-0.681z"></path>
            </svg>
        </a>
        <div class="start__action">
            <a class="btn" href="${servePath}/login/redirect?referer=${referer}">${useHacPaiAccountLoginLabel}</a>
            <a href="https://ld246.com/article/1576294445994" target="_blank">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
                    <path d="M19.652 25v6c0 0.55-0.45 1-1 1h-6c-0.55 0-1-0.45-1-1v-6c0-0.55 0.45-1 1-1h6c0.55 0 1 0.45 1 1zM27.552 10c0 4.75-3.225 6.575-5.6 7.9-1.475 0.85-2.4 2.575-2.4 3.3v0c0 0.55-0.425 1.2-1 1.2h-6c-0.55 0-0.9-0.85-0.9-1.4v-1.125c0-3.025 3-5.625 5.2-6.625 1.925-0.875 2.725-1.7 2.725-3.3 0-1.4-1.825-2.65-3.85-2.65-1.125 0-2.15 0.35-2.7 0.725-0.6 0.425-1.2 1.025-2.675 2.875-0.2 0.25-0.5 0.4-0.775 0.4-0.225 0-0.425-0.075-0.625-0.2l-4.1-3.125c-0.425-0.325-0.525-0.875-0.25-1.325 2.7-4.475 6.5-6.65 11.6-6.65 5.35 0 11.35 4.275 11.35 10z"></path>
                </svg>
            </a>
        </div>
    </div>
    <script type="text/javascript" src="${staticServePath}/js/lib/qrious.min.js" charset="utf-8"></script>
    <script type="text/javascript">
      if (typeof QRious === 'undefined') {
        document.querySelector('.main').innerHTML = "${staticErrorLabel}"
      }
    </script>
</@commonPage>
