<style type="text/css">
    #cacheContent {
        line-height: 28px;
        padding: 12px;
    }
</style>
<div id="cachePlugin">
    <div id="cacheContent"></div>
    <div id="cacheTable"></div>
    <div id="cachePagination" class="margin12 right"></div>
    <div class="clear"></div>
</div>
<script type="text/javascript">
    plugins["cache-list"] = {
        tablePagination:  new TablePaginate("cache"),
        getList: function (pageNum) {
            var that = this;
            $("#loadMsg").text("${loadingLabel}");
            
            $.ajax({
                url: latkeConfig.servePath + "/console/plugins/admin-cache/pages/" + pageNum + "/" + Label.PAGE_SIZE + "/" +  Label.WINDOW_SIZE,
                type: "GET",
                cache: false,
                success: function(result, textStatus){
                    if (!result.sc) {
                        $("#tipMsg").text(result.msg);
                    
                        return;
                    }
                
                    var caches = result.pages;
                    var cacheData = caches;
                    for (var i = 0; i < caches.length; i++) {
                        cacheData[i].cachedTitle = "<a href='" + caches[i].cachedLink + "'  target='_blank'>" 
                            + caches[i].cachedTitle + "</a>";
                        cacheData[i].cachedTime = $.bowknot.getDate(cacheData[i].cachedTime, 1);
                    }

                    that.tablePagination.updateTablePagination(cacheData, pageNum, result.pagination);
                    
                    $("#loadMsg").text("");
                }
            });
        },
    
        changeStatus: function (it) {
            $("#loadMsg").text("${loadingLabel}");
            
            var $it = $(it);
            var flag = "true";
            
            if ($it.text() === "${enabledLabel}") {
                flag = "false";
            }
            
            $.ajax({
                url: latkeConfig.servePath + "/console/plugins/admin-cache/enable/" + flag,
                type: "PUT",
                cache: false,
                success: function(result, textStatus){
                    if (!result.sc) {
                        $("#tipMsg").text(result.msg);
                    
                        return;
                    }
                
                    if ($it.text() === "${enabledLabel}") {
                        $it.text("${disabledLabel}");
                    } else {
                        $it.text("${enabledLabel}");
                    }
                    
                    $("#tipMsg").text("${updateSuccLabel}");
                    
                    $("#loadMsg").text("");
                }
            });
        },
        
        getCache: function () {
            $("#loadMsg").text("${loadingLabel}");
            
            $.ajax({
                url: latkeConfig.servePath + "/console/plugins/admin-cache/status/",
                type: "GET",
                cache: false,
                success: function(result, textStatus){
                    if (!result.sc) {
                        $("#tipMsg").text(result.msg);
                    
                        return;
                    }
                
                    var pageCacheStatusLabel = "${disabledLabel}";
                    if (result.pageCacheEnabled) {
                        pageCacheStatusLabel = "${enabledLabel}";
                    }
                    
                    var cacheHTML = "${pageCacheStatus1Label}&nbsp;<button onclick=\"window.plugins['cache-list'].changeStatus(this);\">" 
                        + pageCacheStatusLabel
                        + "</button>&nbsp;&nbsp;${pageCachedCnt1Label}<span class='f-blue'>" + result.pageCachedCnt; 
                    
                    $("#cacheContent").html(cacheHTML);
                    
                    $("#loadMsg").text("");
                }
            });
        },
    
        init: function (page) {   
            this.tablePagination.buildTable([{
                    style: "padding-left: 6px;",
                    text: "${typeLabel}",
                    index: "cachedType",
                    width: 220
                }, {
                    style: "padding-left: 6px;",
                    text: "${titleLabel}",
                    index: "cachedTitle",
                    minWidth: 300
                }, {
                    style: "padding-left: 6px;",
                    text: "${hitCountLabel}",
                    index: "cachedHitCount",
                    width: 120
                }, {
                    style: "padding-left: 6px;",
                    text: "${sizeLabel}(Byte)",
                    index: "cachedBtypesLength",
                    width: 120
                }, {
                    style: "padding-left: 6px;",
                    text: "${createDateLabel}",
                    index: "cachedTime",
                    width: 160
                }]);
    
            this.tablePagination.initPagination();
            this.getList(page);
            this.getCache();
        },
        
        refresh: function (page) {
            this.getList(page);
            this.getCache();
        }
    };
    
    /*
     * 添加插件
     */
    admin.plugin.add({
        "id": "cache-list",
        "text": "${cacheMgmtLabel}",
        "path": "/tools",
        "index": 6,
        "content": $("#cachePlugin").html()
    });
    
    // 移除现有内容
    $("#cachePlugin").remove();
</script>