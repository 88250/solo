/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
 *
 * Solo is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
/**
 *  plugin manager for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.0.7, Mar 28, 2012
 */
window.plugins = {};
admin.plugin = {
    plugins: [],

    /*
     * 添加插件进行管理
     */
    add: function (data) {
        // 添加所有插件
        data.isInit = false;
        data.hash = data.path.replace("/", "#") + "/" + data.id;
        this.plugins.push(data);

        var pathList = this._analysePath(data.path);
        // 添加一二级 Tab
        if (data.index && pathList.length < 2) {
            this._addNew(data, pathList);
        }
    },

    /*
     * 根据当前 hash 初始化或刷新插件
     */
    setCurByHash: function (tags) {
        var pluginList = this.plugins;
        for (var i = 0; i < pluginList.length; i++) {
            var data = pluginList[i];
            var pathList = this._analysePath(data.path),
            isCurrentPlugin = false;

            // 根据当前 hash 和插件 path 判别是非为当前插件
            if (data.index && window.location.hash.indexOf(data.hash) > -1) {
                isCurrentPlugin = true;
            } else if(data.path.replace("/", "#") === window.location.hash ||
                (window.location.hash === "#main" && data.path.indexOf("/main/panel") > -1)) {
                isCurrentPlugin = true;
            }

            if (isCurrentPlugin) {
                if (data.isInit) {
                    // 插件已经初始化过，只需进行刷新
                    if (plugins[data.id].refresh) {
                        plugins[data.id].refresh(tags.page);
                    }
                } else {
                    // 初始化插件
                    if (!data.index){
                        this._addToExist(data, pathList);
                    } else if (pathList.length === 2) {
                        this._addNew(data, pathList);
                    }
                    plugins[data.id].init(tags.page);
                    data.isInit = true;
                }
            }
        }
    },

    /*
     * 解析添加路径
     */
    _analysePath: function (path) {
        var paths = path.split("/");
        paths.splice(0, 1);
        return paths;
    },

    /*
     * 添加一二级 tab
     */
    _addNew: function (data, pathList) {
        if (pathList.length === 2) {
            data.target = $("#tabPreference li").get(data.index - 1);
            $("#tabPreference").tabs("add", data);
            return;
        } else if (pathList[0] === "") {
            data.target = $("#tabs>ul>li").get(data.index - 1);
        } else if (pathList[0] === "article") {
            data.target = $("#tabArticleMgt>li").get(data.index - 1);
        } else if (pathList[0] === "tools") {
            admin.tools.push("#" + data.id);
            data.target = $("#tabTools>li").get(data.index - 1);
        }

        if (!data.target) {
            alert("data.index is error!");
        }

        $("#tabs").tabs("add", data);
    },

    /*
     * 在已有页面上进行添加
     */
    _addToExist: function (data, pathList) {
        switch (pathList[0]) {
            case "main":
                $("#mainPanel" + pathList[1].charAt(5)).append(data.content);
                break;
            case "tools":
            case "article":
                if (pathList.length === 2) {
                    $("#tabsPanel_" + pathList[1]).append(data.content);
                } else {
                    $("#tabPreferencePanel_" + pathList[2]).append(data.content);
                }
                break;
            case "comment-list":
                $("#tabsPanel_comment-list").append(data.content);
                break;
        }
    }
};
