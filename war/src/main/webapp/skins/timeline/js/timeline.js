/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @fileoverview timeline js.
 *
 * @author <a href="mailto:LLY219@gmail.com">Liyuan Li</a>
 * @version 1.0.0.1, Jan 14, 2013
 */
var timeline = {
    $articles: $(".articles"),
    layoutArticleList: function () {
        timeline.colH = [0, 20];
        timeline.$articles.find("article").each(function () {
            var $it = $(this),
            isLeft = timeline.colH[1] > timeline.colH[0],
            left = isLeft ? 0 : Math.floor(timeline.$articles.width() / 2),
            top = isLeft ? timeline.colH[0] : timeline.colH[1];
            $it.css({
                "left": left + "px",
                "top": top + "px"
            });
                
            if (isLeft) {
                $it.addClass("l");
            } else {
                $it.addClass("r");
            }
                
            timeline.colH[( isLeft ? '0' : '1' )] += parseInt($it.outerHeight(true));
        });
            
        timeline.$articles.height(timeline.colH[0] > timeline.colH[1] ? timeline.colH[0] : timeline.colH[1]);
    },
    
    initArticleList: function () {
        $(window).resize(function () {
            timeline.layoutArticleList();
        });
        $(window).resize();
        $(window).resize();
    },
    
    init: function () {
        $(window).scroll(function () {
           if ($(window).scrollTop() > 60) {
               $(".ico-top").show();
           } else {
               $(".ico-top").hide();
           }
        });
        
        if ($(".articles").length === 1) {
            timeline.initArticleList();
        }
    }
};

var goTranslate = function () {
    window.open("http://translate.google.com/translate?sl=auto&tl=auto&u=" + location.href);  
};

(function () {
    Util.init();
    Util.replaceSideEm($(".recent-comments-content"));
    Util.buildTags("tagsSide");
    
    timeline.init();
})();