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
 * @version 1.0.1.0, Jan 30, 2013
 */
var timeline = {
    _COLHA: 0,
    _COLHB: 20,
    _initArticleList: function () {
        var $articles = $(".articles");
        if ($articles.length === 0 || $(".articles > .fn-clear").length > 0) {
            return;
        }
            
        $(window).resize(function () {
            var colH = [timeline._COLHA, timeline._COLHB];
            $articles.find("article").each(function () {
                var $it = $(this),
                isLeft = colH[1] > colH[0],
                top = isLeft ? colH[0] : colH[1];
                if (!$it.hasClass("r") && !$it.hasClass("l")) {
                    $it.css({
                        "top": top + "px",
                        "position": "absolute"
                    });
                
                    if (isLeft) {
                        this.className = "l";
                    } else {
                        this.className = "r";
                    }
                }
                colH[( isLeft ? '0' : '1' )] += parseInt($it.outerHeight(true));
            });
            
            $articles.height(colH[0] > colH[1] ? colH[0] : colH[1]);
        });
        
        setTimeout(function () {
            $(window).resize();            
        }, 500);
    },
    
    
    _initIndexList: function () {
        var $archives = $(".articles > .fn-clear");
        if ($archives.length === 0) {
            return;
        }
        
        // 如果为 index 页面，重构 archives 结构，使其可收缩
        var year = 0;
        $(".nav-abs li").each(function (i) {
            var $this = $(this);
            $this.hide();
            if (year !== $this.data("year")) {
                year = $this.data("year");
                $this.before("<li class='close year' onclick='timeline.toggleArchives(this, " + 
                    year + ")'>" + year + "</li>");
            }
        });
        
        // 首次加载时，当没有下一页时，使用 js 隐藏"更多"按钮 
        if ($(".article-more").parent().data("count") <= $(".article-more").parent().find("article").length) {
            $(".article-more").remove();
        }
        
        $(window).resize(function () {
            $archives.each(function () {
                var colH = [timeline._COLHA + 60, timeline._COLHB * 4];
                
                var $articles = $(this).find("article");
                if ($articles.length === 0) {
                    $(this).find("h2").remove();
                    $(this).css("margin-bottom" , 0);
                } else {
                    $articles.each(function () {
                        var $it = $(this),
                        isLeft = colH[1] > colH[0],
                        top = isLeft ? colH[0] : colH[1];
                        
                        if (!$it.hasClass("r") && !$it.hasClass("l")) {
                            $it.css({
                                "top": top + "px",
                                "position": "absolute"
                            });
                
                            if (isLeft) {
                                this.className = "l";
                            } else {
                                this.className = "r";
                            }
                        }
                        colH[( isLeft ? '0' : '1' )] += parseInt($it.outerHeight(true));    
                    });
                    $(this).height(colH[0] > colH[1] ? colH[0] : colH[1]);
                }
            });
        });
        
        setTimeout(function () {
            $(window).resize();            
        }, 500);
    },
    
    _setNavCurrent: function () {
        $(".header li a").each(function () {
            if($(this).prop("href") === location.href.split("#")[0]) {
                this.className = "current";
            } else {
                this.className = "";
            }
        })
    },
    
    init: function () {
        $(window).scroll(function () {
            if ($(window).scrollTop() > 60) {
                $(".ico-top").show();
            } else {
                $(".ico-top").hide();
            }
        });
        timeline._initIndexList();
        timeline._initArticleList();
        timeline._setNavCurrent();
    },
    
    translate: function () {
        window.open("http://translate.google.com/translate?sl=auto&tl=auto&u=" + location.href);  
    },
    
    getArchive: function (year, month, monthName) {
        var archiveDate = year + month,
        archive = year + "/" + month;
        window.location.hash = "#" + archiveDate;
        if ($("#" + archiveDate + " > article").length === 0) {
            var archiveDataTitle = year + " " + Label.yearLabel + " " + month + " " + Label.monthLabel;
            if (Label.localeString.substring(0, 2) === "en") {
                archiveDataTitle = monthName + " " + year;
            }
            var archiveHTML = '<h2><span class="article-archive">' + archiveDataTitle + '</span></h2>'
            + '<div class="article-more" onclick="timeline.getNextPage(this, \'' 
            + archive + '\')" data-page="0">' + Label.moreLabel + '</div>';
        
            $("#" + archiveDate).html(archiveHTML).css("margin-bottom", "50px");
            timeline.getNextPage($("#" + archiveDate).find(".article-more")[0], archive);
        }
    },
    
    getNextPage: function (it, archive) {
        var $more = $(it),
        currentPage = $more.data("page") + 1,
        path = "/articles/";
        if($("#tag").length === 1) {
            var pathnames = location.pathname.split("/");
            path = "/articles/tags/" + pathnames[pathnames.length - 1] + "/";
        } else if ($("#author").length === 1) {
            var pathnames = location.pathname.split("/");
            path = "/articles/authors/" + pathnames[pathnames.length - 1] + "/";
        } else if (archive) {
            path = "/articles/archives/" + archive + "/";
        }
        $.ajax({
            url: latkeConfig.servePath + path + currentPage,
            type: "GET",
            beforeSend: function () {
                $more.css("background",
                    "url(" + latkeConfig.staticServePath 
                    + "/skins/timeline/images/ajax-loader.gif) no-repeat scroll center center #60829F").text("");
            },
            success: function(result, textStatus){
                if (!result.sc) {
                    $more.css("background", "none #60829F").text("Error");  
                    return;
                }
                
                if (result.rslts.articles.length === 0) {
                    $more.remove();
                    return;
                }
            
                var articlesHTML = "",
                pagination = result.rslts.pagination;
            
                // append articles
                for (var i = 0; i < result.rslts.articles.length; i++) {
                    var article = result.rslts.articles[i];
            
                    articlesHTML += '<article><div class="module"><div class="dot"></div>'
                    + '<div class="arrow"></div><time class="article-time"><span>'
                    + Util.toDate(article.articleCreateTime, 'yy-MM-dd HH:mm')
                    + '</span></time><h3 class="article-title"><a rel="bookmark" href="' 
                    + latkeConfig.servePath + article.articlePermalink + '">'
                    +article.articleTitle + '</a>';
                
                    if (article.hasUpdated) {
                        articlesHTML += '<sup>' + Label.updatedLabel + '</sup>';
                    }
            
                    if (article.articlePutTop) {
                        articlesHTML += '<sup>' + Label.topArticleLabel + '</sup>';
                    }
            
                    articlesHTML += '</h3><p>' + article.articleAbstract + '</p>'
                    + '<span class="ico-tags ico" title="' + Label.tagLabel + '">';
                    
                    var articleTags = article.articleTags.split(",");
                    for (var j = 0; j < articleTags.length; j++) {
                        articlesHTML +=  '<a rel="category tag" href="' + latkeConfig.servePath 
                        + '/tags/' + encodeURIComponent(articleTags[j])  + '">' + articleTags[j] + '</a>';
            
                        if (j < articleTags.length - 1) {
                            articlesHTML += ",";
                        }
                    }   
                    
                    articlesHTML +=  '</span>&nbsp;<span class="ico-author ico" title="' + Label.authorLabel + '">'
                    + '<a rel="author" href="' + latkeConfig.servePath + '/authors/' + article.authorId + '">' 
                    + article.authorName + '</a></span>&nbsp;<span class="ico-comment ico" title="' 
                    + Label.commentLabel + '"><a rel="nofollow" href="' + latkeConfig.servePath + article.articlePermalink 
                    + '#comments">' + (article.articleCommentCount === 0 ? Label.noCommentLabel : article.articleCommentCount) 
                    + '</a></span>&nbsp;<span class="ico-view ico" title="' + Label.viewLabel + '">'
                    + '<a rel="nofollow" href="${servePath}${article.articlePermalink}">' + article.articleViewCount
                    + '</a></span></div></article>';
                }
                
                $more.before(articlesHTML).data("page", currentPage);
                // 最后一页处理
                if (pagination.paginationPageCount <= currentPage) {
                    $more.remove();
                } else {
                    $more.css("background", "none #60829F").text(Label.moreLabel);  
                }
                
                setTimeout(function () {
                    $(window).resize();            
                }, 500);
            }
        });
    },
    
    toggleArchives: function (it, year) {
        $(".nav-abs li").each(function (i) {
            var $it = $(this);
            if (!$it.hasClass("year")) {
                $it.hide();
                if (year === $it.data("year") && $(it).hasClass("close")) {
                    $it.show();
                }
            } 
        });
        
        $(".nav-abs li.year").each(function () {
            if (parseInt($(this).text()) === year) {
                if ($(it).hasClass("close")) {
                    it.className = "year open";
                } else {
                    it.className = "year close";
                }
            } else {
                this.className = "year close";
            }
        });
    }
};

(function () {
    Util.init();
    Util.replaceSideEm($(".recent-comments-content"));
    Util.buildTags("tagsSide");
    
    timeline.init();
})();