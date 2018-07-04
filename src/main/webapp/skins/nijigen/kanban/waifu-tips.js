String.prototype.render = function (context) {
    var tokenReg = /(\\)?\{([^\{\}\\]+)(\\)?\}/g;

    return this.replace(tokenReg, function (word, slash1, token, slash2) {
        if (slash1 || slash2) {  
            return word.replace('\\', '');
        }

        var variables = token.replace(/\s/g, '').split('.');
        var currentObject = context;
        var i, length, variable;

        for (i = 0, length = variables.length; i < length; ++i) {
            variable = variables[i];
            currentObject = currentObject[variable];
            if (currentObject === undefined || currentObject === null) return '';
        }
        return currentObject;
    });
};

var re = /x/;
console.log(re);
re.toString = function() {
    showMessage('哈哈，你打开了控制台，是想要看看我的秘密吗？', 5000, true);
    return '';
};

$(document).on('copy', function (){
    showMessage('你都复制了些什么呀，转载要记得加上出处哦', 5000, true);
});

$('#hitokoto').mouseover(function (){
    var text = '这句一言出处是 <span style="color:#0099cc;">『{source}』</span>，是 <span style="color:#0099cc;">{author}</span> 在 {date} 时投稿的！'
    var hitokoto = JSON.parse($(this)[0].dataset.raw);
    text = text.render({source: hitokoto.source, author: hitokoto.author, date: hitokoto.date});
    showMessage(text, 3000);
});

$('.waifu-tool .fui-home').click(function (){
    window.location = 'https://imjad.cn/';
});

$('.waifu-tool .fui-eye').click(function (){
    switchNightMode();
    showMessage('你会做眼保健操吗？', 3000, true);
});

$('.waifu-tool .fui-chat').click(function (){
    showHitokoto();
});

$('.waifu-tool .fui-user').click(function (){
    loadRandModel();
    showMessage('我的新衣服好看嘛', 3000, true);
});

$('.waifu-tool .fui-info-circle').click(function (){
    window.open('https://imjad.cn/archives/lab/add-dynamic-poster-girl-with-live2d-to-your-blog-02');
});

$('.waifu-tool .fui-cross').click(function (){
    sessionStorage.setItem('waifu-dsiplay', 'none');
    showMessage('愿你有一天能与重要的人重逢', 1300, true);
    window.setTimeout(function() {$('.waifu').hide();}, 1300);
});

$('.waifu-tool .fui-photo').click(function (){
    showMessage('照好了嘛，是不是很可爱呢？', 5000, true);
    window.Live2D.captureName = 'Pio.png';
    window.Live2D.captureFrame = true;
});

$.ajax({
    cache: true,
    url: latkeConfig.staticServePath + "/skins/nijigen/kanban/waifu-tips.json?v=0.0.13",
    dataType: "json",
    success: function (result){
        $.each(result.mouseover, function (index, tips){
            $(document).on("mouseover", tips.selector, function (){
                var text = tips.text;
                if(Array.isArray(tips.text)) text = tips.text[Math.floor(Math.random() * tips.text.length + 1)-1];
                text = text.render({text: $(this).text()});
                showMessage(text, 3000);
            });
        });
        $.each(result.click, function (index, tips){
            $(document).on("click", tips.selector, function (){
                var text = tips.text;
                if(Array.isArray(tips.text)) text = tips.text[Math.floor(Math.random() * tips.text.length + 1)-1];
                text = text.render({text: $(this).text()});
                showMessage(text, 3000, true);
            });
        });
        $.each(result.seasons, function (index, tips){
            var now = new Date();
            var after = tips.date.split('-')[0];
            var before = tips.date.split('-')[1] || after;
            
            if((after.split('/')[0] <= now.getMonth()+1 && now.getMonth()+1 <= before.split('/')[0]) && 
               (after.split('/')[1] <= now.getDate() && now.getDate() <= before.split('/')[1])){
                var text = tips.text;
                if(Array.isArray(tips.text)) text = tips.text[Math.floor(Math.random() * tips.text.length + 1)-1];
                text = text.render({year: now.getFullYear()});
                showMessage(text, 6000, true);
            }
        });
    }
});

(function (){
    var text;
    var referrer = document.createElement('a');
    if(document.referrer !== ''){
        referrer.href = document.referrer;
    }
    
    if(referrer.href !== '' && referrer.hostname != 'imjad.cn'){
        var referrer = document.createElement('a');
        referrer.href = document.referrer;
        text = 'Hello! 来自 <span style="color:#0099cc;">' + referrer.hostname + '</span> 的朋友';
        var domain = referrer.hostname.split('.')[1];
        if (domain == 'baidu') {
            text = 'Hello! 来自 百度搜索 的朋友<br>你是搜索 <span style="color:#0099cc;">' + referrer.search.split('&wd=')[1].split('&')[0] + '</span> 找到的我吗？';
        }else if (domain == 'so') {
            text = 'Hello! 来自 360搜索 的朋友<br>你是搜索 <span style="color:#0099cc;">' + referrer.search.split('&q=')[1].split('&')[0] + '</span> 找到的我吗？';
        }else if (domain == 'google') {
            text = 'Hello! 来自 谷歌搜索 的朋友<br>欢迎阅读<span style="color:#0099cc;">『' + document.title.split(' - ')[0] + '』</span>';
        }
    }else {
        if (window.location.href == 'https://imjad.cn/') { //如果是主页
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
        }else {
            $.getJSON('https://api.imjad.cn/interface/lastactivity/',function(result){
                var now = result.now;
                var lastActivity = result.ts;
                var idle = now - lastActivity;
                
                if(idle >= 60 * 60 * 24 * 30){
                    text = '我家主人已经出门一个月了，可是到现在也没回来，不会是出什么事了吧，好担心啊';
                }else if(idle >= 60 * 60 * 24 * 7){
                    text = '我家主人已经出门一周了，到现在还没回来，你知道他去哪里了吗？';
                }else if(idle >= 60 * 30){
                    text = '我家主人' + formatSeconds(idle) + '前来过，先看看<span style="color:#0099cc;">『' + document.title.split(' - ')[0] + '』</span>吧，有想法可以在评论里留言哦~';
                }else if(idle >= 60 * 2){
                    text = '真是不巧，我家主人刚才还在，先看看<span style="color:#0099cc;">『' + document.title.split(' - ')[0] + '』</span>吧，有想法可以在评论里留言哦~';
                }else{
                    text = '太巧了！我家主人正好在家，对<span style="color:#0099cc;">『' + document.title.split(' - ')[0] + '』</span>有什么想法吗？在评论里留言吧，相信很快就会有回复的说~';
                }
                showMessage(text, 10000);
            });
        }
    }
    showMessage(text, 6000);
})();

window.hitokotoTimer = window.setInterval(showHitokoto,30000);

function showHitokoto(){
    $.getJSON('https://api.imjad.cn/hitokoto/?cat=&charset=utf-8&length=55&encode=json',function(result){
        showMessage(result.hitokoto, 5000);
    });
}

function showMessage(text, timeout, flag){
    if(flag || sessionStorage.getItem('waifu-text') === '' || sessionStorage.getItem('waifu-text') === null){
        if(Array.isArray(text)) text = text[Math.floor(Math.random() * text.length + 1)-1];
        //console.log(text);
        
        if(flag) sessionStorage.setItem('waifu-text', text);
        
        $('.waifu-tips').stop();
        $('.waifu-tips').html(text).fadeTo(200, 1);
        if (timeout === null) timeout = 5000;
        hideMessage(timeout);
    }
}
function hideMessage(timeout){
    $('.waifu-tips').stop().css('opacity',1);
    if (timeout === null) timeout = 5000;
    window.setTimeout(function() {sessionStorage.removeItem('waifu-text')}, timeout);
    $('.waifu-tips').delay(timeout).fadeTo(200, 0);
}

function loadRandModel(){
    var modelJSON = "/usr/themes/Moricolor/assets/live2d/potionmaker/rand.php?v=0.0.1&_=" + Date.now();
    localStorage.setItem('modelJSON', modelJSON);
    
    loadlive2d("live2d", modelJSON, console.log('live2d','模型加载完成'));
}

function formatSeconds(value) {
    var seconds = parseInt(value);// 秒
    var minutes = 0;
    var hours = 0;
    var days = 0;
    if(seconds > 60) {
        minutes = parseInt(seconds/60);
        seconds = parseInt(seconds%60);
        if(minutes > 60) {
            hours = parseInt(minutes/60);
            minutes = parseInt(minutes%60);
            if(hours > 24) {
                days = parseInt(hours/24);
                hours = parseInt(hours%24);
            }
        }
    }
    var result = "";
    if(minutes > 0)
        result = ""+parseInt(minutes)+"分";
    if(hours > 0 && hours <= 24)
        result = ""+parseInt(hours)+"小时"+result;
    if(days > 0)
        result = ""+parseInt(days)+"天"+result;
    return result;
}