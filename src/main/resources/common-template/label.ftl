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
<script>
  var Label = {
    speech: ${speech?c},
    servePath: "${servePath}",
    staticServePath: "${staticServePath}",
    luteAvailable: ${luteAvailable?c},
    hljsStyle: '${hljsTheme}',
    langLabel: "${langLabel}",
    version: "${version}",
    staticSite: ${staticSite?c},
    showCodeBlockLn: ${showCodeBlockLn},
    <#if article??>
    articleId: "${article.oId}",
    </#if>
  }
</script>
