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
<link type="text/css" rel="stylesheet" href="${staticServePath}/plugins/symphony-interest/style.css"/>
<div id="symphonyInterestPanel">
  <div class="module-panel">
    <div class="module-header">
      <h2>${interestLabel}</h2>
    </div>
    <div class="module-body padding12">
      <div id="symphonyInterest">
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
  plugins.symphonyInterest = {
    init: function () {
      $('#loadMsg').text("${loadingLabel}")

      $('#symphonyInterest').css('background',
              "url(${staticServePath}/images/loader.gif) no-repeat scroll center center transparent")

      $.ajax({
        url: 'https://ld246.com/apis/articles?',
        type: 'GET',
        dataType: 'jsonp',
        jsonp: 'callback',
        error: function () {
          $('#symphonyInterest').html('Loading Interest failed :-(').css('background', 'none')
        },
        success: function (data, textStatus) {
          var articles = data.articles
          if (0 === articles.length) {
            return
          }

          var listHTML = '<ul>'
          for (var i = 0; i < articles.length; i++) {
            var article = articles[i]

            var articleLiHtml = '<li>'
                    + '<a target=\'_blank\' href=\'' + article.articlePermalink + '\'>'
                    + article.articleTitle + '</a>&nbsp; <span class=\'date\'>' + $.bowknot.getDate(article.articleCreateTime, 1);
            +'</span></li>'
            listHTML += articleLiHtml
          }
          listHTML += '</ul>'

          $('#symphonyInterest').html(listHTML).css('background', 'none')
        }
      })

      $('#loadMsg').text('')
    }
  }

  /*
   * 添加插件
   */
  admin.plugin.add({
    'id': 'symphonyInterest',
    'path': '/main/panel1',
    'content': $('#symphonyInterestPanel').html()
  })

  // 移除现有内容
  $('#symphonyInterestPanel').remove()
</script>
