var soloKanbanniang = {
  clearTime: '',
  showMessage: function(text, timeout) {
    if (sessionStorage.getItem('soloKanbanniang') === 'close') {
      return;
    }
    if (Array.isArray(text)) {
      text = text[Math.floor(Math.random() * text.length + 1) - 1];
    }
    $('.solo-kanbanniang__tip').html(text).fadeTo(200, 1);
    clearTimeout(this.clearTime);
    this.clearTime = setTimeout(function() {
      $('.solo-kanbanniang__tip').fadeTo(200, 0);
    }, timeout);
  },
  _initTips: function() {
    $.ajax({
      cache: true,
      url: 'https://static-solo.b3log.org/plugins/kanbanniang/assert/tips.json',
      dataType: 'json',
      success: function(result) {
        $.each(result.mouseover, function(index, tips) {
          $(document).on('mouseover', tips.selector, function() {
            soloKanbanniang.showMessage(
                tips.text.replace('{text}', $(this).text()), 3000);
          });
        });
        $.each(result.click, function(index, tips) {
          $(document).on('click', tips.selector, function() {
            var text = tips.text[Math.floor(Math.random() * tips.text.length +
                1) - 1];
            soloKanbanniang.showMessage(text, 3000, true);
          });
        });
        $.each(result.seasons, function(index, tips) {
          var now = new Date();
          var after = tips.date.split('-')[0];
          var before = tips.date.split('-')[1] || after;

          if ((after.split('/')[0] <= now.getMonth() + 1 &&
              now.getMonth() + 1 <= before.split('/')[0]) &&
              (after.split('/')[1] <= now.getDate() &&
                  now.getDate() <= before.split('/')[1])) {
            soloKanbanniang.showMessage(
                tips.text.replace('{year}', now.getFullYear()), 6000, true);
          }
        });
      },
    });
  },
  _initMenu: function() {
    $('#soloKanbanniangHome').click(function() {
      window.location = latkeConfig.servePath;
    });

    $('#soloKanbanniangRSS').click(function() {
      window.location = latkeConfig.servePath + '/blog-articles-rss.do';
    });

    $('#soloKanbanniangGithub').click(function() {
      window.location = 'https://github.com/b3log/solo';
    });

    $('#soloKanbanniangChat').click(function() {
      soloKanbanniang.showChat();
    });

    $('#soloKanbanniangChange').click(function() {
      loadlive2d('soloKanbanniang', latkeConfig.servePath +
          '/plugins/kanbanniang/assert/model?t=' + (new Date()).getTime(),
          soloKanbanniang.showMessage('我的新衣服好看嘛', 3000, true));
    });

    $('#soloKanbanniangClose').click(function() {
      soloKanbanniang.showMessage('愿你有一天能与重要的人重逢', 1300, true);
      sessionStorage.setItem('soloKanbanniang', 'close');
      window.setTimeout(function() {
        $('.solo-kanbanniang').hide();
      }, 1300);
    });

    $('#soloKanbanniangPhoto').click(function() {
      soloKanbanniang.showMessage('照好了嘛，是不是很可爱呢？', 5000, true);
      window.Live2D.captureName = 'solo.png';
      window.Live2D.captureFrame = true;
    });
  },
  _initFirstMsg: function() {
    var text;
    var referrer = document.createElement('a');
    if (document.referrer !== '') {
      referrer.href = document.referrer;
    }

    if (referrer.href !== '' && referrer.hostname !==
        latkeConfig.servePath.split('//')[1].split(':')[0]) {
      var referrer = document.createElement('a');
      referrer.href = document.referrer;
      text = 'Hello! 来自 <span style="color:#4285f4;">' + referrer.hostname +
          '</span> 的朋友';
      var domain = referrer.hostname.split('.')[1];
      if (domain == 'baidu') {
        text = 'Hello! 来自 百度搜索 的朋友<br>你是搜索 <span style="color:#4285f4;">' +
            referrer.search.split('&wd=')[1].split('&')[0] + '</span> 找到的我吗？';
      } else if (domain == 'so') {
        text = 'Hello! 来自 360搜索 的朋友<br>你是搜索 <span style="color:#4285f4;">' +
            referrer.search.split('&q=')[1].split('&')[0] + '</span> 找到的我吗？';
      } else if (domain == 'google') {
        text = 'Hello! 来自 谷歌搜索 的朋友<br>欢迎阅读<span style="color:#4285f4;">『' +
            document.title.split(' - ')[0] + '』</span>';
      }
    } else {
      var now = (new Date()).getHours();
      if (now > 23 || now <= 5) {
        text = '你是夜猫子呀？这么晚还不睡觉，明天起的来嘛';
      } else if (now > 5 && now <= 7) {
        text = '早上好！一日之计在于晨，美好的一天就要开始了';
      } else if (now > 7 && now <= 11) {
        text = '上午好！工作顺利嘛，不要久坐，多起来走动走动哦！';
      } else if (now > 11 && now <= 14) {
        text = '中午了，工作了一个上午，现在是午餐时间！';
      } else if (now > 14 && now <= 17) {
        text = '午后很容易犯困呢，今天的运动目标完成了吗？';
      } else if (now > 17 && now <= 19) {
        text = '傍晚了！窗外夕阳的景色很美丽呢，最美不过夕阳红~';
      } else if (now > 19 && now <= 21) {
        text = '晚上好，今天过得怎么样？';
      } else if (now > 21 && now <= 23) {
        text = '已经这么晚了呀，早点休息吧，晚安~';
      } else {
        text = '嗨~ 快来逗我玩吧！';
      }
    }
    soloKanbanniang.showMessage(text, 6000);
  },
  init: function() {
    this._initTips();
    this._initMenu();
    this._initFirstMsg();
    window.setInterval(soloKanbanniang.showChat, 30000);

    var re = /solo/;
    console.log(re);
    re.toString = function() {
      soloKanbanniang.showMessage('哈哈，你打开了控制台，是想要看看我的秘密吗？', 5000, true);
      return '';
    };

    $(document).on('copy', function() {
      soloKanbanniang.showMessage('你都复制了些什么呀，转载要记得加上出处哦', 5000, true);
    });
  },
  showChat() {
    $.getJSON(
        'https://api.imjad.cn/hitokoto/?cat=&charset=utf-8&length=55&encode=json',
        function(result) {
          soloKanbanniang.showMessage(result.hitokoto, 5000);
        });
  },
};

$(document).ready(function() {
  if (sessionStorage.getItem('soloKanbanniang') === 'close') {
    $('.solo-kanbanniang').remove();
    return;
  }

  soloKanbanniang.init();

  loadlive2d('soloKanbanniang',  latkeConfig.servePath +
      '/plugins/kanbanniang/assert/model?t=' + (new Date()).getTime());
});