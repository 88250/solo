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
<style type="text/css">
    #top {
        background-color: #FFF;
        background-image: linear-gradient(#FFFFFF, #E5E5E5);
        background-image: -ms-linear-gradient(#FFFFFF, #E5E5E5);
        background-image: -o-linear-gradient(#FFFFFF, #E5E5E5);
        background-image: -webkit-linear-gradient(#FFFFFF, #E5E5E5);
        filter: progid:DXImageTransform.Microsoft.gradient(GradientType=0, startColorstr='#FFFFFF', endColorstr='#E5E5E5');
        border-bottom: 1px solid #E5E5E5;
        line-height: 26px;
        display: none;
    }

    #top a, #top span span {
        border-right: 1px solid #D9D9D9;
        color: #4C4C4C;
        float: left;
        line-height: 14px;
        margin: 6px 0;
        padding: 0 6px;
        text-decoration: none;
        text-shadow: 0 -1px 0 #FFFFFF;
        font-weight: normal;
    }

    #top a:hover, #top a.hover {
        background-color: #EEF2FA;
        border-left-color: #707070;
        border-radius: 0 13px 13px 0;
        margin: 0px;
        line-height: 26px;
    }

    #showTop {
        background-image: url("${staticServePath}/images/arrow-right.png");
        cursor: pointer;
        height: 26px;
        right: 0;
        position: absolute;
        top: 0;
        width: 26px;
        border-radius: 0 0 0 15px;
        z-index: 10;
    }

    #showTop:hover {
        background-image: url("${staticServePath}/images/arrow-right.gif");
    }

    #top #hideTop {
        background-image: url("${staticServePath}/images/arrow-left.png");
        height: 26px;
        margin: 0;
        padding: 0;
        width: 26px;
        border: 0;
    }

    #top #hideTop:hover {
        background-image: url("${staticServePath}/images/arrow-left.gif");
        border-radius: 0;
    }
</style>
<div id="showTop"></div>
<div id="top">
    <a href="https://github.com/88250/solo" target="_blank" class="hover">
        Solo
    </a>
    <span class="left">&nbsp;${onlineVisitor1Label}${onlineVisitorCnt}</span>
    <span class="right" id="admin" data-login="${isLoggedIn?string}">
        <#if isLoggedIn>
            <span id="adminName">${userName}</span>
            <#if !isVisitor>
            <a href="${servePath}/admin-index.do#main" title="${adminLabel}">
                ${adminLabel}
            </a>
        </#if>
            <a href="${logoutURL}" title="${logoutLabel}">${logoutLabel}</a>
        <#else>
            <a href="${servePath}/start" title="${startToUseLabel}">${startToUseLabel}</a>
        </#if>
        <a href="javascript:void(0)" id="hideTop"></a>
    </span>
    <div class="clear"></div>
</div>
