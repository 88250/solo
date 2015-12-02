/*
 * Copyright (c) 2010-2015, b3log.org
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
 * @description index for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.2.2, May 28, 2013
 */
var Admin = function() {
    this.register = {};
    // 工具栏下的工具
    this.tools = ['#page-list', '#file-list', '#link-list', '#preference',
        '#user-list', '#plugin-list', '#others'];
    // 多用户时，一般用户不能使用的功能
    this.adTools = ['link-list', 'preference', 'file-list', 'page-list',
        'user-list', 'plugin-list', 'others'];
};

$.extend(Admin.prototype, {
    /**
     * @description  登出
     */
    logout: function() {
        window.location.href = latkeConfig.servePath + "/logout?goto=" + latkeConfig.servePath;
    },
    /**
     * @description 清除提示
     */
    clearTip: function() {
        $("#tipMsg").text("");
        $("#loadMsg").text("");
    },
    /**
     * @description 根据当前页数设置 hash
     * @param {Int} currentPage 当前页
     */
    setHashByPage: function(currentPage) {
        var hash = window.location.hash,
                hashList = hash.split("/");
        if (/^\d*$/.test(hashList[hashList.length - 1])) {
            hashList[hashList.length - 1] = currentPage;
        } else {
            hashList.push(currentPage);
        }
        window.location.hash = hashList.join("/");
    },
    /**
     * @description 设置某个 tab 被选择
     * @param {Stirng} id id tab id
     */
    selectTab: function(id) {
        window.location.hash = "#" + id;
    },
    /**
     * @description 根据当前 hash 解析出当前页数及 hash 数组。
     */
    analyseHash: function() {
        var hash = window.location.hash;
        var tag = hash.substr(1, hash.length - 1);
        var tagList = tag.split("/");
        var tags = {};
        tags.page = 1,
                tags.hashList = [];
        for (var i = 0; i < tagList.length; i++) {
            if (i === tagList.length - 1 && (/^\d+$/.test(tagList[i]))) {
                tags.page = tagList[i];
            } else {
                tags.hashList.push(tagList[i]);
            }
        }
        return tags;
    },
    /**
     * @description 根据当前 hash 设置当前 tab
     */
    setCurByHash: function() {
        $("#tipMsg").text("");
        var tags = admin.analyseHash();
        var tab = tags.hashList[1],
                subTab = tags.hashList[2];

        if (tags.hashList.length === 1) {
            tab = tags.hashList[0];
        }

        if (tab === "") {
            return;
        }

        if (tab !== "article") {
            admin.article.clearDraftTimer();
        } else if (tab === "article") {
            admin.article.autoSaveDraftTimer = setInterval(function() {
                admin.article._autoSaveToDraft();
            }, admin.article.AUTOSAVETIME);
        }

        // 离开编辑器时进行提示
        try {
            // 除更新、发布、取消发布文章，编辑器中无内容外，离开编辑器需进行提示。
            if (tab !== "article" && admin.article.isConfirm &&
                    admin.editors.articleEditor.getContent().replace(/\s/g, '') !== ""
                    && admin.article.content !== admin.editors.articleEditor.getContent()) {
                if (!confirm(Label.editorLeaveLabel)) {
                    window.location.hash = "#article/article";
                    return;
                }
            }
            // 不离开编辑器，hash 需变为 "#article/article"，此时不需要做任何处理。
            if (tab === "article" && admin.article.isConfirm &&
                    admin.editors.articleEditor.getContent().replace(/\s/g, '') !== ""
                    && admin.article.content !== admin.editors.articleEditor.getContent()) {
                return;
            }
        } catch (e) {
            var $articleContent = $('#articleContent');
            if ($articleContent.length > 0) {
                if (tab !== "article" && admin.article.isConfirm &&
                        $articleContent.val().replace(/\s/g, '') !== ""
                        && admin.article.content !== $articleContent.val()) {
                    if (!confirm(Label.editorLeaveLabel)) {
                        window.location.hash = "#article/article";
                        return;
                    }
                }
                // 不离开编辑器，hash 需变为 "#article/article"，此时不需要做任何处理。
                if (tab === "article" && admin.article.isConfirm &&
                        $articleContent.val().replace(/\s/g, '') !== ""
                        && admin.article.content !== $articleContent.val()) {
                    return;
                }
            }
        }

        // clear article 
        if (tab !== "article" && admin.editors.articleEditor.setContent) {
            admin.article.clear();
        }
        admin.article.isConfirm = true;

        $("#tabs").tabs("setCurrent", tab);
        $("#loadMsg").text(Label.loadingLabel);

        if ($("#tabsPanel_" + tab).length === 1) {
            if ($("#tabsPanel_" + tab).html().replace(/\s/g, "") === "") {
                // 还未加载 HTML
                $("#tabsPanel_" + tab).load("admin-" + tab + ".do", function() {
                    // 页面加载完后，回调初始函数
                    if (tab === "article" && admin.article.status.id) {
                        // 当文章页面编辑器未初始化时，调用更新文章需先初始化编辑器
                        admin.register[tab].init.call(admin.register[tab].obj, admin.article.getAndSet);
                    } else {
                        admin.register[tab].init.call(admin.register[tab].obj, tags.page);
                    }

                    // 页面包含子 tab，需根据 hash 定位到相应的 tab
                    if (subTab) {
                        $("#tab" + tab.substring(0, 1).toUpperCase() + tab.substring(1)).
                                tabs("setCurrent", subTab);
                    }

                    // 根据 hash 调用现有的插件函数
                    admin.plugin.setCurByHash(tags);
                });
            } else {
                if (tab === "article" && admin.article.status.id) {
                    admin.article.getAndSet();
                }

                // 已加载过 HTML，只需调用刷新函数
                if (admin.register[tab] && admin.register[tab].refresh) {
                    admin.register[tab].refresh.call(admin.register[tab].obj, tags.page);
                }

                // 页面包含子 tab，需根据 hash 定位到相应的 tab
                if (subTab) {
                    $("#tab" + tab.substring(0, 1).toUpperCase() + tab.substring(1)).
                            tabs("setCurrent", subTab);
                }

                // 根据 hash 调用现有的插件函数
                admin.plugin.setCurByHash(tags);
            }
        } else {
            $("#tipMsg").text("Error: No tab! " + Label.reportIssueLabel);
            $("#loadMsg").text("");
        }
    },
    /**
     * @description 初始化整个后台
     */
    init: function() {
        //window.onerror = Util.error;

        Util.killIE();
        $("#loadMsg").text(Label.loadingLabel);

        // 构建 tabs
        $("#tabs").tabs();

        // tipMsg
        setInterval(function() {
            if ($("#tipMsg").text() !== "") {
                setTimeout(function() {
                    $("#tipMsg").text("");
                }, 7000);
            }
        }, 6000);
        $("#loadMsg").text("");
    },
    /**
     * @description tools and article collapse
     * @param {Bom} it 触发事件对象
     */
    collapseNav: function(it) {
        var subNav = $(it).next();
        subNav.slideToggle("normal", function() {
            if (this.style.display !== "none") {
                $(it).find(".ico-arrow-down")[0].className = "ico-arrow-up";
            } else {
                $(it).find(".ico-arrow-up")[0].className = "ico-arrow-down";
            }
        });
    },
    /**
     * @description 后台及当前页面所需插件初始化完后，对权限进行控制及当前页面属于 tools 时，tools 选项需展开。
     */
    inited: function() {
        // Removes functions with the current user role
        if (Label.userRole !== "adminRole") {
            for (var i = 0; i < this.adTools.length; i++) {
                $("#tabs").tabs("remove", this.adTools[i]);
            }
        } else {
            // 当前 tab 属于 Tools 时，设其展开
            for (var j = 0; j < this.tools.length; j++) {
                if ("#" + window.location.hash.split("/")[1] === this.tools[j]) {
                    $("#tabToolsTitle").click();
                    break;
                }
            }
        }
        this.setCurByHash();
    }
});

var admin = new Admin();/*
 * Copyright (c) 2010-2015, b3log.org
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
 * @fileoverview editor
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.1.0.4, May 30, 2015
 */

admin.editors = {};

/*
 * @description Create Editor can use all editor. 
 *                e.g: TinyMCE, wnd 
 * @constructor
 * @param conf 编辑器初始化参数
 * @param conf.kind 编辑器类型 simple/all
 * @param conf.id 编辑器渲染元素 id
 * @param conf.language 编辑器使用语言
 * @param conf.type 编辑器种类
 * @param conf.codeMirrorLanguage codeMirror 编辑器当前解析语言
 */
var Editor = function (conf) {
    this._defaults = {
        type: "tinyMCE",
        kind: "",
        id: "",
        language: ""
    };
    conf.type = Label.editorType;
    this.conf = conf;
    this._init();
};

$.extend(Editor.prototype, {
    /*
     * @description 初始化
     */
    _init: function () {
        this.init();
    },
    /*
     * @description 初始化编辑器
     */
    init: function (type) {
        var conf = this.conf;
        if (type) {
            conf.type = type;
        }
        
        var types = conf.type.split("-");
        if (types.length === 2) {
            conf.codeMirrorLanguage = types[1];
            conf.type = types[0];
        }

        admin.editors[conf.type].init(conf);
    },
    /*
     * @description 获取编辑器值
     * @returns {string} 编辑器值
     */
    getContent: function () {
        var conf = this.conf;
        return admin.editors[conf.type].getContent(conf.id);
    },
    /*
     * @description 设置编辑器值
     * @param {string} content 编辑器回填内容 
     */
    setContent: function (content) {
        var conf = this.conf;
        admin.editors[conf.type].setContent(conf.id, content);
    },
    /*
     * @description 移除编辑器值
     */
    remove: function () {
        var conf = this.conf;
        admin.editors[conf.type].remove(conf.id);
    }
});

admin.editors.articleEditor = {};
admin.editors.abstractEditor = {};
admin.editors.pageEditor = {};
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * @fileoverview tinyMCE editor
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.1.0.6, May 30, 2015
 */
admin.editors.tinyMCE = {
    /*
     * @description 初始化编辑器
     * @param conf 编辑器初始化参数
     * @param conf.kind 编辑器类型
     * @param conf.id 编辑器渲染元素 id
     * @param conf.fun 编辑器首次加载完成后回调函数
     */
    init: function (conf) {
        var language = Label.localeString.substring(0, 2);
        if (language === "zh") {
            language = "zh-cn";
        }

        if (conf.kind && conf.kind === "simple") {
            try {
                tinyMCE.init({
                    // General options
                    language: language,
                    mode: "exact",
                    elements: conf.id,
                    theme: "advanced",
                    plugins: "media",
                    // Theme options
                    theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,undo,redo,|,bullist,numlist,|,code",
                    theme_advanced_buttons2: "",
                    theme_advanced_buttons3: "",
                    theme_advanced_toolbar_location: "top",
                    theme_advanced_toolbar_align: "left",
                    valid_children: "+body[style]"
                });
            } catch (e) {
                $("#tipMsg").text("TinyMCE load fail");
            }
        } else {
            try {
                tinyMCE.init({
                    // General options
                    language: language,
                    mode: "exact",
                    elements: conf.id,
                    theme: "advanced",
                    plugins: "autosave,style,advhr,advimage,advlink,preview,inlinepopups,media,paste,syntaxhl,wordcount",
                    // Theme options
                    theme_advanced_buttons1: "formatselect,fontselect,fontsizeselect,|,bold,italic,underline,strikethrough,forecolor,|,advhr,blockquote,syntaxhl,",
                    theme_advanced_buttons2: "undo,redo,|,bullist,numlist,outdent,indent,|,justifyleft,justifycenter,justifyright,justifyfull,|,pastetext,pasteword,|,link,unlink,image,iespell,media,|,cleanup,code,preview,",
                    theme_advanced_buttons3: "",
                    theme_advanced_toolbar_location: "top",
                    theme_advanced_toolbar_align: "left",
                    theme_advanced_resizing: true,
                    theme_advanced_statusbar_location: "bottom",
                    extended_valid_elements: "link[type|rel|href|charset],pre[name|class],iframe[src|width|height|name|align],+a[*]",
                    valid_children: "+body[style]",
                    relative_urls: false,
                    remove_script_host: false,
                    oninit: function () {
                        if (typeof (conf.fun) === "function") {
                            conf.fun();
                        }
                    }
                });
            } catch (e) {
                $("#tipMsg").text("TinyMCE load fail");
            }
        }
    },
    /*
     * @description 获取编辑器值
     * @param {string} id 编辑器id
     * @returns {string} 编辑器值
     */
    getContent: function (id) {
        var content = "";
        try {
            content = tinyMCE.get(id).getContent();
        } catch (e) {
            content = $("#" + id).val();
        }
        return content;
    },
    /*
     * @description 设置编辑器值
     * @param {string} id 编辑器 id
     * @param {string} content 设置编辑器值
     */
    setContent: function (id, content) {
        try {
            if (tinyMCE.get(id)) {
                tinyMCE.get(id).setContent(content);
            } else {
                $("#" + id).val(content);
            }
        } catch (e) {
            $("#" + id).val(content);
        }
    },
    /*
     * @description 移除编辑器
     * @param {string} id 编辑器 id
     */
    remove: function (id) {
        tinyMCE.get(id).remove();
    }
};
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * @fileoverview KindEditor
 * @description 修改点：plugins/image/image.js 注释 173-176
 *                     plugins/media/media.js 注释 26 & 28
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.1.0.2, May 30, 2015
 */
admin.editors.KindEditor = {
    /*
     * @description 初始化编辑器
     * @param conf 编辑器初始化参数
     * @param conf.kind 编辑器类型
     * @param conf.id 编辑器渲染元素 id
     * @param conf.fun 编辑器首次加载完成后回调函数
     */
    init: function (conf) {
        var language = "zh_CN";
        if ("en_US" === Label.localeString) {
            language = "en"
        }

        if (conf.kind && conf.kind === "simple") {
            try {
                this[conf.id] = KindEditor.create('#' + conf.id, {
                    langType: language,
                    resizeType: 0,
                    items: ["bold", "italic", "underline", "strikethrough", "|", "undo", "redo", "|",
                        "insertunorderedlist", "insertorderedlist", "|", "source"
                    ]
                });
            } catch (e) {
                $("#tipMsg").text("KindEditor load fail");
            }
        } else {
            try {
                this[conf.id] = KindEditor.create('#' + conf.id, {
                    'uploadJson' : 'kindeditor/php/upyunUpload.php',
                    langType: language,
                    items: ["formatblock", "fontname", "fontsize", "|", "bold", "italic", "underline", "strikethrough", "forecolor", "|",
                        "link", "unlink", "image", "media", "|", "pagebreak", "emoticons", "code", "/",
                        "undo", "redo", "|", "insertunorderedlist", "insertorderedlist", "indent", "outdent", "|",
                        "justifyleft", "justifycenter", "justifyright", "justifyfull", "|", "plainpaste", "wordpaste", "|",
                        "clearhtml", "source", "preview"
                    ],
                    afterCreate: function () {
                        if (typeof (conf.fun) === "function") {
                            conf.fun();
                        }
                    }
                });
            } catch (e) {
                $("#tipMsg").text("KindEditor load fail");
            }
        }
    },
    /*
     * @description 获取编辑器值
     * @param {string} id 编辑器id
     * @returns {string} 编辑器值
     */
    getContent: function (id) {
        var content = "";
        try {
            content = this[id].html();
        } catch (e) {
            content = $("#" + id).val();
        }
        return content;
    },
    /*
     * @description 设置编辑器值
     * @param {string} id 编辑器 id
     * @param {string} content 设置编辑器值
     */
    setContent: function (id, content) {
        try {
            this[id].html(content);
        } catch (e) {
            $("#" + id).val(content);
        }
    },
    /*
     * @description 移除编辑器
     * @param {string} id 编辑器 id
     */
    remove: function (id) {
        this[id].remove();
    }
};
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * @fileoverview markdowm CodeMirror editor 
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.4, Aug 13, 2015
 */
admin.editors.CodeMirror = {
    /*
     * @description 初始化编辑器
     * @param conf 编辑器初始化参数
     * @param conf.kind 编辑器类型
     * @param conf.id 编辑器渲染元素 id
     * @param conf.fun 编辑器首次加载完成后回调函数
     * @param conf.height 编辑器高度
     * @param conf.codeMirrorLanguage codeMirror 编辑器当前解析语言
     * @returns {obj} editor
     */
    init: function (conf) {
        var it = this;

        // load preview and clear
        var previewHTML = "<div class='clear'></div>";
        if (conf.kind !== "simple") {
            previewHTML = "<div class='markdown-preivew'>" +
                    "<div class='markdown-help ico-close'></div>" +
                    "<div class='clear'></div>" +
                    "<div class='markdown-preview-main none'></div>" +
                    "<div class='markdown-help-main'>" + Label.markdownHelpLabel + "</div>"
                    + "</div><div class='clear'></div>";
        }
        $("#" + conf.id).after(previewHTML);

        // init codemirror
        if (conf.kind === "simple") {
            this[conf.id] = CodeMirror.fromTextArea(document.getElementById(conf.id), {
                mode: 'markdown',
                lineWrapping: true,
                lineNumbers: true,
                matchBrackets: true,
                theme: "default",
                height: conf.height
            });
        } else {
            // preview 执行队列
            it[conf.id + "Timers"] = [];

            // 该编辑器是否第一次触发 preivew 事件
            it[conf.id + "IsFirst"] = true;

            var $preview = $("#" + conf.id).parent().find(".markdown-preivew"),
                    $help = $("#" + conf.id).parent().find(".markdown-preivew").find(".markdown-help");
            this[conf.id] = CodeMirror.fromTextArea(document.getElementById(conf.id), {
                mode: 'markdown',
                lineWrapping: true,
                lineNumbers: true,
                matchBrackets: true,
                theme: "default",
                height: conf.height,
                onUpdate: function () {
                    var update = function () {
                        if (it[conf.id].getValue() === "") {
                            return;
                        }

                        $.ajax({
                            url: latkeConfig.servePath + "/console/markdown/2html",
                            type: "POST",
                            cache: false,
                            data: JSON.stringify({markdownText: it[conf.id].getValue()}),
                            success: function (data, textStatus) {
                                if (data.sc) {
                                    if (it[conf.id + "IsFirst"] && $help.hasClass("ico-close")) {
                                        $help.click();
                                    }
                                    it[conf.id + "IsFirst"] = false;

                                    $preview.find(".markdown-preview-main").html(data.html);
                                } else {
                                    $preview.find(".markdown-preview-main").html(data.msg);
                                }
                            }
                        });
                    }

                    it[conf.id + "Timers"].push(update);
                }
            });

            this._callPreview(conf.id, it[conf.id + "Timers"]);
        }

        if (conf.kind === "simple") {
            // 摘要不需要 preview，设置其宽度
            $("#" + conf.id).next().width("99%");
        } else {
            // 有 preview 时，绑定 preview 事件
            this._bindEvent(conf.id);
        }

        // after render, call back function
        if (typeof (conf.fun) === "function") {
            conf.fun();
        }
    },
    /*
     * @description 当有更新时每隔3秒 preview
     * @param {string} id 编辑器 id
     */
    _callPreview: function (id) {
        setInterval(function () {
            var timers = admin.editors.CodeMirror[id + "Timers"];
            $(document).queue("myAnimation", [timers[timers.length - 1]]);
            $(document).dequeue("myAnimation");
            admin.editors.CodeMirror[id + "Timers"] = [];
        }, 2000);
    },
    /*
     * @description 绑定编辑器 preview 事件
     * @param {string} id 编辑器id
     */
    _bindEvent: function (id) {
        var $preview = $("#" + id).parent().find(".markdown-preivew");

        $preview.find(".markdown-help").click(function () {
            var $it = $(this);
            if ($it.hasClass("ico-help")) {
                $it.removeClass("ico-help").addClass("ico-close");
                $preview.find(".markdown-preview-main").hide();
                $preview.find(".markdown-help-main").show();
            } else {
                $it.addClass("ico-help").removeClass("ico-close");
                $preview.find(".markdown-preview-main").show();
                $preview.find(".markdown-help-main").hide();
            }
        });
    },
    /*
     * @description 获取编辑器值
     * @param {string} id 编辑器id
     * @returns {string} 编辑器值
     */
    getContent: function (id) {
        return this[id].getValue();
    },
    /*
     * @description 设置编辑器值
     * @param {string} id 编辑器 id
     * @param {string} content 设置编辑器值
     */
    setContent: function (id, content) {
        this[id].setValue(content);
        var $preview = $("#" + id).parent().find(".markdown-preivew");
        $preview.find(".markdown-preview-main").html(content);
    },
    /*
     * @description 销毁编辑器值
     * @param {string} id 编辑器 id
     */
    remove: function (id) {
        this[id].toTextArea();
        $(".markdown-preivew").remove();
    }
};/*
 * Copyright (c) 2010-2015, b3log.org
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
 * table and paginate util
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.0.8, Jun 11, 2012
 */

var TablePaginate = function (id) {
    this.id = id;
    this.currentPage = 1;
};

$.extend(TablePaginate.prototype, {
    /*
     * 构建 table 框架
     * @colModel table 列宽，标题等数据
     */
    buildTable: function (colModel, noExpend) {
        var tableData = {
            colModel: colModel,
            noDataTip: Label.noDataLabel
        }
        if (!noExpend) {
            tableData.expendRow = {
                index: "expendRow"
            }
        }
        $("#" + this.id + "Table").table(tableData);
    
    },
    
    /*
     * 初始化分页
     */
    initPagination: function () {
        var id = this.id;
        $("#" + id + "Pagination").paginate({
            "bind": function(currentPage, errorMessage) {
                if (errorMessage) {
                    $("#tipMsg").text(errorMessage);
                } else {
                    admin.setHashByPage(currentPage);
                }
            },
            "currentPage": 1,
            "errorMessage": Label.inputErrorLabel,
            "nextPageText": Label.nextPagePabel,
            "previousPageText": Label.previousPageLabel,
            "goText": Label.gotoLabel,
            "type": "custom",
            "custom": [1],
            "pageCount": 1
        });
    },

    /*
     * 初始化评论对话框
     */
    initCommentsDialog: function () {
        var that = this;
        $("#" + this.id + "Comments").dialog({
            "modal": true,
            "hideFooter": true,
            "close": function () {
                admin[that.id + "List"].getList(that.currentPage);
                return true;
            }
        });
    },
    
    /*
     * 更新 table & paginateion
     */
    updateTablePagination: function (data, currentPage, pageInfo) {
        currentPage = parseInt(currentPage);
        if (currentPage > pageInfo.paginationPageCount && currentPage > 1) {
            $("#tipMsg").text(Label.pageLabel + currentPage + Label.notFoundLabel);
            $("#loadMsg").text("");
            return;
        }
        $("#" + this.id + "Table").table("update", {
            data: [{
                groupName: "all",
                groupData: data
            }]
        });
                    
        if (pageInfo.paginationPageCount === 0) {
            pageInfo.paginationPageCount = 1;
        }
        
        $("#" + this.id + "Pagination").paginate("update", {
            pageCount: pageInfo.paginationPageCount,
            currentPage: currentPage,
            custom: pageInfo.paginationPageNums
        });
        this.currentPage = currentPage;
    }
});
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * @fileoverview article for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.3.3, Sep 16, 2015
 */
admin.article = {
    currentEditorType: '',
    // 当发文章，取消发布，更新文章时设置为 false。不需在离开编辑器时进行提示。
    isConfirm: true,
    status: {
        id: undefined,
        isArticle: undefined,
        articleHadBeenPublished: undefined
    },
    content: "",
    // 自动保存草稿定时器
    autoSaveDraftTimer: "",
    // 自动保存间隔
    AUTOSAVETIME: 1000 * 60,
    /**
     * @description 获取文章并把值塞入发布文章页面 
     * @param {String} id 文章 id
     * @param {Boolean} isArticle 文章或者草稿
     */
    get: function (id, isArticle) {
        this.status.id = id;
        this.status.isArticle = isArticle;
        admin.selectTab("article/article");
    },
    /**
     * @description 获取文章内容
     */
    getAndSet: function () {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        $.ajax({
            url: latkeConfig.servePath + "/console/article/" + admin.article.status.id,
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                // set default value for article.
                $("#title").val(result.article.articleTitle);
                admin.article.status.articleHadBeenPublished = result.article.articleHadBeenPublished;

                if (admin.article.currentEditorType !== result.article.articleEditorType) {
                    admin.editors.articleEditor.remove();
                    admin.editors.abstractEditor.remove();

                    admin.article.currentEditorType = result.article.articleEditorType;
                    admin.editors.articleEditor.init(result.article.articleEditorType);
                    admin.editors.abstractEditor.init(result.article.articleEditorType);
                }

                admin.editors.articleEditor.setContent(result.article.articleContent);
                admin.editors.abstractEditor.setContent(result.article.articleAbstract);
                admin.article.content = admin.editors.articleEditor.getContent();

                var tags = result.article.articleTags,
                        tagsString = '';
                for (var i = 0; i < tags.length; i++) {
                    if (0 === i) {
                        tagsString = tags[i].tagTitle;
                    } else {
                        tagsString += "," + tags[i].tagTitle;
                    }
                }

                $("#tag").val(tagsString);
                $("#permalink").val(result.article.articlePermalink);
                $("#viewPwd").val(result.article.articleViewPwd);

                $("#articleCommentable").prop("checked", result.article.articleCommentable);

                // signs
                var signs = result.article.signs;
                $(".signs button").each(function (i) {
                    if (parseInt(result.article.articleSignId) === parseInt(signs[i].oId)) {
                        $("#articleSign" + signs[i].oId).addClass("selected");
                    } else {
                        $("#articleSign" + signs[i].oId).removeClass("selected");
                    }
                });

                admin.article.setStatus();
                $("#loadMsg").text("");
            }
        });
    },
    /**
     * @description 删除文章
     * @param {String} id 文章 id
     * @param {String} fromId 文章来自草稿夹(draft)/文件夹(article)
     * @param {String} title 文章标题
     */
    del: function (id, fromId, title) {
        var isDelete = confirm(Label.confirmRemoveLabel + Label.articleLabel + '"' + title + '"?');
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            $.ajax({
                url: latkeConfig.servePath + "/console/article/" + id,
                type: "DELETE",
                cache: false,
                success: function (result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    admin[fromId + "List"].getList(1);
                }
            });
        }
    },
    /**
     * @@description 添加文章
     * @param {Boolean} articleIsPublished 文章是否发布过
     * @param {Boolean} isAuto 是否为自动保存
     */
    add: function (articleIsPublished, isAuto) {
        if (admin.article.validate()) {
            var that = this;
            that._addDisabled();

            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var signId = "";
            $(".signs button").each(function () {
                if (this.className === "selected") {
                    signId = this.id.substr(this.id.length - 1, 1);
                }
            });

            var articleContent = admin.editors.articleEditor.getContent(),
                    articleAbstract = admin.editors.abstractEditor.getContent();

            var requestJSONObject = {
                "article": {
                    "articleTitle": $("#title").val(),
                    "articleContent": articleContent,
                    "articleAbstract": articleAbstract,
                    "articleTags": this.trimUniqueArray($("#tag").val()).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": articleIsPublished,
                    "articleSignId": signId,
                    "postToCommunity": $("#postToCommunity").prop("checked"),
                    "articleCommentable": $("#articleCommentable").prop("checked"),
                    "articleViewPwd": $("#viewPwd").val()
                }
            };

            $.ajax({
                url: latkeConfig.servePath + "/console/article/",
                type: "POST",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function (result, textStatus) {
                    if (isAuto) {
                        $("#tipMsg").text(Label.autoSaveLabel);
                        admin.article.status.id = result.oId;
                        return;
                    }

                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        return;
                    }

                    if (articleIsPublished) {
                        admin.article.status.id = undefined;
                        admin.selectTab("article/article-list");
                    } else {
                        admin.selectTab("article/draft-list");
                    }

                    admin.article.isConfirm = false;
                },
                complete: function (jqXHR, textStatus) {
                    that._removeDisabled();
                    $("#loadMsg").text("");
                    if (jqXHR.status === 403) {
                        $.get("/admin-index.do");
                        that.add(articleIsPublished);
                    }
                }
            });
        }
    },
    /**
     * @description 更新文章
     * @param {Boolean} articleIsPublished 文章是否发布过
     * @param {Boolean} isAuto 是否为自动保存
     */
    update: function (articleIsPublished, isAuto) {
        if (admin.article.validate()) {
            var that = this;
            that._addDisabled();

            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var signId = "";
            $(".signs button").each(function () {
                if (this.className === "selected") {
                    signId = this.id.substr(this.id.length - 1, 1);
                }
            });

            var articleContent = admin.editors.articleEditor.getContent(),
                    articleAbstract = admin.editors.abstractEditor.getContent();

            var requestJSONObject = {
                "article": {
                    "oId": this.status.id,
                    "articleTitle": $("#title").val(),
                    "articleContent": articleContent,
                    "articleAbstract": articleAbstract,
                    "articleTags": this.trimUniqueArray($("#tag").val()).toString(),
                    "articlePermalink": $("#permalink").val(),
                    "articleIsPublished": articleIsPublished,
                    "articleSignId": signId,
                    "articleCommentable": $("#articleCommentable").prop("checked"),
                    "articleViewPwd": $("#viewPwd").val(),
                    "postToCommunity": $("#postToCommunity").prop("checked"),
                    "articleEditorType": admin.article.currentEditorType
                }
            };

            $.ajax({
                url: latkeConfig.servePath + "/console/article/",
                type: "PUT",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function (result, textStatus) {
                    if (isAuto) {
                        $("#tipMsg").text(Label.autoSaveLabel);
                        return;
                    }

                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        return;
                    }

                    if (articleIsPublished) {
                        admin.selectTab("article/article-list");
                    } else {
                        admin.selectTab("article/draft-list");
                    }

                    $("#tipMsg").text(Label.updateSuccLabel);

                    admin.article.status.id = undefined;
                    admin.article.isConfirm = false;
                },
                complete: function (jqXHR, textStatus) {
                    that._removeDisabled();
                    $("#loadMsg").text("");
                    if (jqXHR.status === 403) {
                        $.get("/admin-index.do");
                        that.update(articleIsPublished);
                    }
                }
            });
        }
    },
    /**
     * @description 发布文章页面设置文章按钮、发布到社区等状态的显示
     */
    setStatus: function () {
        // set button status
        if (this.status) {
            if (this.status.isArticle) {
                $("#unSubmitArticle").show();
                $("#submitArticle").hide();
            } else {
                $("#submitArticle").show();
                $("#unSubmitArticle").hide();
            }
            if (this.status.articleHadBeenPublished) {
                $("#postToCommunityPanel").hide();
            } else {
                $("#postToCommunityPanel").show();
            }
        } else {
            $("#submitArticle").show();
            $("#unSubmitArticle").hide();
            $("#postToCommunityPanel").show();
        }

        $("#postToCommunity").attr("checked", "checked");
    },
    /**
     * @description 清除发布文章页面的输入框的内容
     */
    clear: function () {
        this.status = {
            id: undefined,
            isArticle: undefined,
            articleHadBeenPublished: undefined
        };
        this.setStatus();

        $("#title").val("");

        admin.editors.articleEditor.setContent("");
        admin.editors.abstractEditor.setContent("");

        // reset tag
        $("#tag").val("");
        $("#tagCheckboxPanel").hide().find("span").removeClass("selected");

        $("#permalink").val("");
        $("#articleCammentable").prop("checked", true);
        $("#postToCommunity").prop("checked", true);
        $(".signs button").each(function (i) {
            if (i === 0) {
                this.className = "selected";
            } else {
                this.className = "";
            }
        });

        $(".markdown-preview-main").html("");
    },
    /**
     * @description 初始化发布文章页面
     * @param {Function} fun 切面函数
     */
    init: function (fun) {
        this.currentEditorType = Label.editorType;
        // Inits Signs.
        $.ajax({
            url: latkeConfig.servePath + "/console/signs/",
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                $(".signs button").each(function (i) {
                    // Sets signs.
                    if (i === result.signs.length) {
                        $("#articleSign1").addClass("selected");
                    } else {
                        $("#articleSign" + result.signs[i].oId).tip({
                            content: result.signs[i].signHTML === "" ? Label.signIsNullLabel :
                                    result.signs[i].signHTML.replace(/\n/g, "").replace(/<script.*<\/script>/ig, ""),
                            position: "top"
                        });
                    }
                    // Binds checkbox event.
                    $(this).click(function () {
                        if (this.className !== "selected") {
                            $(".signs button").each(function () {
                                this.className = "";
                            });
                            this.className = "selected";
                        }
                    });
                });

                $("#loadMsg").text("");
            }
        });

        // For tag auto-completion
        $.ajax({// Gets all tags
            url: latkeConfig.servePath + "/console/tags",
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                if (0 >= result.tags.length) {
                    return;
                }

                var tags = [];
                for (var i = 0; i < result.tags.length; i++) {
                    tags.push(result.tags[i].tagTitle);
                }

                $("#tag").completed({
                    height: 160,
                    buttonText: Label.selectLabel,
                    data: tags
                });

                $("#loadMsg").text("");
            }
        });

        // submit action
        $("#submitArticle").click(function () {
            if (admin.article.status.id) {
                admin.article.update(true);
            } else {
                admin.article.add(true);
            }
        });

        $("#saveArticle").click(function () {
            if (admin.article.status.id) {
                admin.article.update(admin.article.status.isArticle);
            } else {
                admin.article.add(false);
            }
        });

        // upload
        var qiniu = window.qiniu;

        $('#articleUpload').fileupload({
             multipart: true,
            url: "http://upload.qiniu.com/",
            formData: function (form) {
                var data = form.serializeArray();
                data.push({name: 'token', value: qiniu.qiniuUploadToken});
                return data;
            }, 
            done: function (e, data) {
                var qiniuKey = data.result.key;
                if (!qiniuKey) {
                    alert("Upload error");
                    return;
                }
                
                var t = new Date().getTime();
                    $('#articleUpload').after('<div><a target="_blank" href="http://' + qiniu.qiniuDomain + qiniuKey + '?' + t
                            + '">[' + data.files[0].name + ']</a> http://' 
                            + qiniu.qiniuDomain + qiniuKey + '?' + t + '</div>');
            },
            fail: function (e, data) {
                alert("Upload error: " + data.errorThrown);
            }
        }).on('fileuploadprocessalways', function (e, data) {
            var currentFile = data.files[data.index];
            if (data.files.error && currentFile.error) {
                alert(currentFile.error);
            }
        });
        
        // editor
        admin.editors.articleEditor = new Editor({
            id: "articleContent",
            kind: "all",
            fun: fun,
            height: 500
        });

        admin.editors.abstractEditor = new Editor({
            id: "abstract",
            kind: "simple",
            height: 200
        });

        admin.article.clearDraftTimer();
        admin.article.autoSaveDraftTimer = setInterval(function () {
            admin.article._autoSaveToDraft();
        }, admin.article.AUTOSAVETIME);
    },
    /**
     * @description 自动保存草稿件
     */
    _autoSaveToDraft: function () {
        if ($("#title").val().replace(/\s/g, "") === "" ||
                admin.editors.articleEditor.getContent().replace(/\s/g, "") === "" ||
                $("#tag").val().replace(/\s/g, "") === "") {
            return;
        }
        if (admin.article.status.id) {
            if (!admin.article.status.isArticle) {
                admin.article.update(false, true);
            }
        } else {
            admin.article.add(false, true);
            admin.article.status.isArticle = false;
        }
    },
    /**
     * @description 关闭定时器
     */
    clearDraftTimer: function () {
        if (admin.article.autoSaveDraftTimer !== "") {
            window.clearInterval(admin.article.autoSaveDraftTimer);
            admin.article.autoSaveDraftTimer = "";
        }
    },
    /**
     * @description 验证发布文章字段的合法性
     */
    validate: function () {
        var articleContent = admin.editors.articleEditor.getContent();

        if ($("#title").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#title").focus().val("");
        } else if (articleContent.replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.contentEmptyLabel);
        } else if ($("#tag").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.tagsEmptyLabel);
            $("#tag").focus().val("");
        } else {
            return true;
        }
        return false;
    },
    /**
     * @description 取消发布 
     * @param {Boolean} isAuto 是否为自动保存
     */
    unPublish: function (isAuto) {
        var that = this;
        that._addDisabled();
        $.ajax({
            url: latkeConfig.servePath + "/console/article/unpublish/" + admin.article.status.id,
            type: "PUT",
            cache: false,
            success: function (result, textStatus) {
                if (isAuto) {
                    $("#tipMsg").text(Label.autoSaveLabel);
                    return;
                }

                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    return;
                }

                admin.selectTab("article/draft-list");
                admin.article.status.id = undefined;
                admin.article.isConfirm = false;
            },
            complete: function (jqXHR, textStatus) {
                that._removeDisabled();
                $("#loadMsg").text("");
                if (jqXHR.status === 403) {
                    $.get("/admin-index.do");
                    that.unPublish();
                }
            }
        });
    },
    /**
     * @description 数组中无重复
     * @param {String} str 被解析的字符串
     * @returns {String} 无重复的字符串
     */
    trimUniqueArray: function (str) {
        str = str.toString();
        var arr = str.split(",");
        for (var i = 0; i < arr.length; i++) {
            arr[i] = arr[i].replace(/(^\s*)|(\s*$)/g, "");
            if (arr[i] === "") {
                arr.splice(i, 1);
                i--;
            }
        }
        var unique = $.unique(arr);
        return unique.toString();
    },
    /**
     * @description 点击发文文章时的处理
     */
    prePost: function () {
        $("#loadMsg").text(Label.loadingLabel);
        admin.article.content = "";
        if (!admin.editors.articleEditor.getContent) {
            return;
        }

        var articleContent = admin.editors.articleEditor.getContent();

        if (window.location.hash === "#article/article" &&
                articleContent.replace(/\s/g, '') !== "") {
            if (confirm(Label.editorPostLabel)) {
                admin.article.clear();
            }
        }
        $("#tipMsg").text("");
        $("#loadMsg").text("");

        if (admin.article.currentEditorType !== Label.editorType) {
            admin.editors.articleEditor.remove();
            admin.editors.abstractEditor.remove();

            admin.article.currentEditorType = Label.editorType;
            admin.editors.articleEditor.init(Label.editorType);
            admin.editors.abstractEditor.init(Label.editorType);
        }
    },
    /**
     * @description: 仿重复提交，点击一次后，按钮设置为 disabled
     */
    _addDisabled: function () {
        $("#unSubmitArticle").attr("disabled", "disabled");
        $("#saveArticle").attr("disabled", "disabled");
        $("#submitArticle").attr("disabled", "disabled");
    },
    /**
     * @description: 仿重复提交，当后台有数据返回后，按钮移除 disabled 状态
     */
    _removeDisabled: function () {
        $("#unSubmitArticle").removeAttr("disabled");
        $("#saveArticle").removeAttr("disabled");
        $("#submitArticle").removeAttr("disabled");
    }
};

/**
 * @description 注册到 admin 进行管理 
 */
admin.register.article = {
    "obj": admin.article,
    "init": admin.article.init,
    "refresh": function () {
        admin.editors.abstractEditor.setContent('');
        admin.editors.articleEditor.setContent('');
        $("#loadMsg").text("");
        $("#tipMsg").text("");
    }
};/*
 * Copyright (c) 2010-2015, b3log.org
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
 *  common comment for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.9, May 28, 2013
 */

admin.comment = { 
    /*
     * 打开评论窗口
     * @id 该评论对应的 id
     * @fromId 该评论来自文章/草稿/自定义页面
     */
    open: function (id, fromId) {
        this.getList(id, fromId);
        $("#" + fromId + "Comments").dialog("open");
    },
    
    /*
     * 获取评论列表
     * 
     * @onId 该评论对应的实体 id，可能是文章，也可能是自定义页面
     * @fromId 该评论来自文章/草稿/自定义页面
     */
    getList: function (onId, fromId) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        $("#" + fromId + "Comments").html("");
        
        var from = "article";
        if (fromId === "page") {
            from = "page";
        }
        
        $.ajax({
            url: latkeConfig.servePath + "/console/comments/" + from + "/" + onId ,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                var comments = result.comments,
                commentsHTML = '';
                for (var i = 0; i < comments.length; i++) {
                    var hrefHTML = "<a target='_blank' href='" + comments[i].commentURL + "'>",
                    content = comments[i].commentContent,
                    contentHTML = Util.replaceEmString(content);
                        
                    if (comments[i].commentURL === "http://") {
                        hrefHTML = "<a target='_blank'>";
                    }

                    commentsHTML += "<div class='comment-title'><span class='left'>"
                    + hrefHTML + comments[i].commentName + "</a>";

                    if (comments[i].commentOriginalCommentName) {
                        commentsHTML += "@" + comments[i].commentOriginalCommentName;
                    }
                    commentsHTML += "</span><span title='" + Label.removeLabel + "' class='right deleteIcon' onclick=\"admin.comment.del('"
                    + comments[i].oId + "', '" + fromId + "', '" + onId + "')\"></span><span class='right'><a href='mailto:"
                    + comments[i].commentEmail + "'>" + comments[i].commentEmail + "</a>&nbsp;&nbsp;"
                    + $.bowknot.getDate(comments[i].commentTime)
                    + "&nbsp;</span><div class='clear'></div></div><div class='margin12'>"
                    + contentHTML + "</div>";
                }
                if ("" === commentsHTML) {
                    commentsHTML = Label.noCommentLabel;
                }
                
                $("#" + fromId + "Comments").html(commentsHTML);
                
                $("#loadMsg").text("");
            }
        });
    },
    
    /*
     * 删除评论
     * @id 评论 id
     * @fromId 该评论来自文章/草稿/自定义页面
     * @articleId 该评论对应的实体 id，可能是文章，也可能是自定义页面
     */
    del: function (id, fromId, articleId) {
        var isDelete = confirm(Label.confirmRemoveLabel + Label.commentLabel + "?");
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            var from = "article";
            if (fromId === "page") {
                from = "page";
            }
            
            $.ajax({
                url: latkeConfig.servePath + "/console/" + from + "/comment/" + id,
                type: "DELETE",
                cache: false,
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }
                    
                    admin.comment.getList(articleId, fromId);
                    
                    $("#loadMsg").text("");
                }
            });
        }
    }
};
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * article list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.5, May 28, 2013
 */

/* article-list 相关操作 */
admin.articleList = {
    tablePagination:  new TablePaginate("article"),
    
    /* 
     * 初始化 table, pagination, comments dialog 
     */
    init: function (page) {
        this.tablePagination.buildTable([{
            text: Label.titleLabel,
            index: "title",
            minWidth: 110,
            style: "padding-left: 12px;font-size:14px;"
        }, {
            text: Label.authorLabel,
            index: "author",
            width: 150,
            style: "padding-left: 12px;"
        }, {
            text: Label.commentLabel,
            index: "comments",
            width: 80,
            style: "padding-left: 12px;"
        }, {
            text: Label.viewLabel,
            width: 60,
            index: "articleViewCount",
            style: "padding-left: 12px;"
        }, {
            text: Label.createDateLabel,
            index: "date",
            width: 90,
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.tablePagination.initCommentsDialog();
        this.getList(page);
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        var that = this;
        $("#loadMsg").text(Label.loadingLabel);
        $.ajax({
            url: latkeConfig.servePath + "/console/articles/status/published/" + pageNum + "/" + Label.PAGE_SIZE + "/" +  Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                var articles = result.articles,
                articleData = [];
                for (var i = 0; i < articles.length; i++) {
                    articleData[i] = {};
                    articleData[i].title = "<a href='" + latkeConfig.servePath + articles[i].articlePermalink + "' target='_blank' title='" + articles[i].articleTitle + "' class='no-underline'>"
                    + articles[i].articleTitle + "</a><span class='table-tag'>" + articles[i].articleTags + "</span>";
                    articleData[i].date = $.bowknot.getDate(articles[i].articleCreateTime);
                    articleData[i].comments = articles[i].articleCommentCount;
                    articleData[i].articleViewCount = articles[i].articleViewCount;
                    articleData[i].author = articles[i].authorName;
                            
                    var topClass = articles[i].articlePutTop ? Label.cancelPutTopLabel : Label.putTopLabel;
                    articleData[i].expendRow = "<a target='_blank' href='" + latkeConfig.servePath + articles[i].articlePermalink + "'>" + Label.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.article.get('" + articles[i].oId + "', true)\">" + Label.updateLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.article.del('" + articles[i].oId + "', 'article', '" + articles[i].articleTitle + "')\">" + Label.removeLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.articleList.popTop(this, '" + articles[i].oId + "')\">" + topClass + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.comment.open('" + articles[i].oId + "', 'article')\">" + Label.commentLabel + "</a>";
                }
                    
                that.tablePagination.updateTablePagination(articleData, pageNum, result.pagination);
                
                $("#loadMsg").text("");
            }
        });
    },

    /* 
     * 制定或者取消置顶 
     * @it 触发事件的元素本身
     * @id 草稿 id
     */
    popTop: function (it, id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        
        var $it = $(it),
        ajaxUrl = "canceltop",
        tip = Label.putTopLabel;
        
        if ($it.html() === Label.putTopLabel) {
            ajaxUrl = "puttop";
            tip = Label.cancelPutTopLabel;
        }
        
        $.ajax({
            url: latkeConfig.servePath + "/console/article/" + ajaxUrl + "/" + id,
            type: "PUT",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                    
                $it.html(tip);
                $("#loadMsg").text("");
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["article-list"] =  {
    "obj": admin.articleList,
    "init": admin.articleList.init,
    "refresh": admin.articleList.getList
}/*
 * Copyright (c) 2010-2015, b3log.org
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
 * draft list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.4, Feb 23, 2013
 */

/* draft-list 相关操作 */
admin.draftList = {
    tablePagination:  new TablePaginate("draft"),
    
    /* 
     * 初始化 table, pagination, comments dialog 
     */
    init: function (page) {
        this.tablePagination.buildTable([{
            text: Label.titleLabel,
            index: "title",
            minWidth: 110,
            style: "padding-left: 12px;font-size:14px;"
        }, {
            text: Label.authorLabel,
            index: "author",
            width: 150,
            style: "padding-left: 12px;"
        }, {
            text: Label.commentLabel,
            index: "comments",
            width: 80,
            style: "padding-left: 12px;"
        }, {
            text: Label.viewLabel,
            width: 60,
            index: "articleViewCount",
            style: "padding-left: 12px;"
        }, {
            text: Label.createDateLabel,
            index: "date",
            width: 90,
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.tablePagination.initCommentsDialog();
        this.getList(page);
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        var that = this;
        
        $.ajax({
            url: latkeConfig.servePath + "/console/articles/status/unpublished/" + pageNum + "/" + Label.PAGE_SIZE + "/" +  Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                var articles = result.articles,
                articleData = [];
                for (var i = 0; i < articles.length; i++) {
                    articleData[i] = {};
                    articleData[i].tags = articles[i].articleTags;
                    articleData[i].date = $.bowknot.getDate(articles[i].articleCreateTime);
                    articleData[i].comments = articles[i].articleCommentCount;
                    articleData[i].articleViewCount = articles[i].articleViewCount;
                    articleData[i].author = articles[i].authorName;
                    articleData[i].title = "<a class='no-underline' href='" + latkeConfig.servePath +
                    articles[i].articlePermalink + "' target='_blank'>" + 
                    articles[i].articleTitle + "</a><span class='table-tag'>" + articles[i].articleTags + "</span>";
                    articleData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.article.get('" + articles[i].oId + "', false);\">" + Label.updateLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.article.del('" + articles[i].oId + "', 'draft', '" + articles[i].articleTitle + "')\">" + Label.removeLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.comment.open('" + articles[i].oId + "', 'draft')\">" + Label.commentLabel + "</a>";
                }
                    
                that.tablePagination.updateTablePagination(articleData, pageNum, result.pagination);
                
                $("#loadMsg").text("");
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["draft-list"] =  {
    "obj": admin.draftList,
    "init": admin.draftList.init,
    "refresh": admin.draftList.getList
};/*
 * Copyright (c) 2010-2015, b3log.org
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
 * page list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.2.4, May 30, 2015
 */

/* page-list 相关操作 */
admin.pageList = {
    currentEditorType: '',
    tablePagination: new TablePaginate("page"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    id: "",
    type: "link",
    /* 
     * 初始化 table, pagination, comments dialog
     */
    init: function (page) {
        this.tablePagination.buildTable([{
                text: "",
                index: "pageOrder",
                width: 60,
                style: "padding-left: 12px;font-size:14px;"
            }, {
                style: "padding-left: 12px;",
                text: Label.titleLabel,
                index: "pageTitle",
                width: 300
            }, {
                style: "padding-left: 12px;",
                text: Label.permalinkLabel,
                index: "pagePermalink",
                minWidth: 300
            }, {
                style: "padding-left: 12px;",
                text: Label.openMethodLabel,
                index: "pageTarget",
                width: 120
            }, {
                style: "padding-left: 12px;",
                text: Label.typeLabel,
                index: "pageType",
                width: 80
            }, {
                text: Label.commentLabel,
                index: "comments",
                width: 80,
                style: "padding-left: 12px;"
            }]);
        this.tablePagination.initPagination();
        this.tablePagination.initCommentsDialog();
        this.getList(page);

        var language = Label.localeString.substring(0, 2);
        if (language === "zh") {
            language = "zh-cn";
        }

        admin.pageList.currentEditorType = Label.editorType;
        admin.editors.pageEditor = new Editor({
            language: language,
            kind: "all",
            id: "pageContent"
        });

        // select type
        $(".fn-type").click(function () {
            var $it = $(this);
            if ($it.hasClass("selected")) {
                return;
            }

            $(".fn-type").removeClass("selected");
            $it.addClass("selected");

            admin.pageList.type = $it.data("type");

            if (admin.pageList.type === "page") {
                $("#pagePagePanel").slideDown();

                // 使用 CodeMirror 编辑器时，当编辑器初始之前，元素为 display:none 时，行号显示不正常
                if (Label.editorType === "CodeMirror-Markdown"
                        && admin.editors.pageEditor.getContent() === "") {
                    admin.editors.pageEditor.setContent("");
                }
            } else {
                $("#pagePagePanel").slideUp();
            }
        });
    },
    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        var that = this;

        $.ajax({
            url: latkeConfig.servePath + "/console/pages/" + pageNum + "/" + Label.PAGE_SIZE + "/" + Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                var pages = result.pages;
                var pageData = [];
                admin.pageList.pageInfo.currentCount = pages.length;
                admin.pageList.pageInfo.pageCount = result.pagination.paginationPageCount === 0 ? 1 : result.pagination.paginationPageCount;
                for (var i = 0; i < pages.length; i++) {
                    pageData[i] = {};
                    if (i === 0) {
                        if (pages.length === 1) {
                            pageData[i].pageOrder = "";
                        } else {
                            pageData[i].pageOrder = '<div class="table-center" style="width:14px">\
                                        <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'down\');" \
                                        class="table-downIcon"></span></div>';
                        }
                    } else if (i === pages.length - 1) {
                        pageData[i].pageOrder = '<div class="table-center" style="width:14px">\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                                    </div>';
                    } else {
                        pageData[i].pageOrder = '<div class="table-center" style="width:38px">\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                                    <span onclick="admin.pageList.changeOrder(' + pages[i].oId + ', ' + i + ', \'down\');" class="table-downIcon"></span>\
                                    </div>';
                    }

                    pageData[i].pageTitle = "<a class='no-underline' href='" + pages[i].pagePermalink + "' target='_blank'>" +
                            pages[i].pageTitle + "</a>";
                    pageData[i].pagePermalink = "<a class='no-underline' href='" + pages[i].pagePermalink + "' target='_blank'>"
                            + pages[i].pagePermalink + "</a>";
                    pageData[i].pageTarget = pages[i].pageOpenTarget;
                    pageData[i].pageType = pages[i].pageType;
                    pageData[i].comments = pages[i].pageCommentCount;
                    pageData[i].expendRow = "<span><a href='" + pages[i].pagePermalink + "' target='_blank'>" + Label.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.pageList.get('" + pages[i].oId + "')\">" + Label.updateLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.pageList.del('" + pages[i].oId + "', '" + pages[i].pageTitle + "')\">" + Label.removeLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.comment.open('" + pages[i].oId + "', 'page')\">" + Label.commentLabel + "</a></span>";
                }

                that.tablePagination.updateTablePagination(pageData, pageNum, result.pagination);

                $("#loadMsg").text("");
            }
        });
    },
    /*
     * 获取自定义页面
     * @id 自定义页面 id
     */
    get: function (id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");

        $.ajax({
            url: latkeConfig.servePath + "/console/page/" + id,
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                admin.pageList.id = id;

                $("#pageTitle").val(result.page.pageTitle);
                $("#pagePermalink").val(result.page.pagePermalink);
                $("#pageTarget").val(result.page.pageOpenTarget);
                if (result.page.pageType === "page") {
                    $($(".fn-type").get(1)).click();
                } else {
                    $($(".fn-type").get(0)).click();
                }
                $("#pageCommentable").prop("checked", result.page.pageCommentable);

                if (admin.pageList.currentEditorType !== result.page.pageEditorType) {
                    admin.editors.pageEditor.remove();

                    admin.pageList.currentEditorType = result.page.pageEditorType;
                    admin.editors.pageEditor.init(result.page.pageEditorType);
                }

                admin.editors.pageEditor.setContent(result.page.pageContent);

                $("#loadMsg").text("");
            }
        });
    },
    /* 
     * 删除自定义页面
     * @id 自定义页面 id
     * @title 自定义页面标题
     */
    del: function (id, title) {
        var isDelete = confirm(Label.confirmRemoveLabel + Label.navLabel + '"' + title + '"?');
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            $.ajax({
                url: latkeConfig.servePath + "/console/page/" + id,
                type: "DELETE",
                cache: false,
                success: function (result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    var pageNum = admin.pageList.pageInfo.currentPage;
                    if (admin.pageList.pageInfo.currentCount === 1 && admin.pageList.pageInfo.pageCount !== 1 &&
                            admin.pageList.pageInfo.currentPage === admin.pageList.pageInfo.pageCount) {
                        admin.pageList.pageInfo.pageCount--;
                        pageNum = admin.pageList.pageInfo.pageCount;
                    }
                    var hashList = window.location.hash.split("/");
                    if (pageNum == hashList[hashList.length - 1]) {
                        admin.pageList.getList(pageNum);
                    } else {
                        admin.setHashByPage(pageNum);
                    }

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 添加自定义页面
     */
    add: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            var pageContent = admin.editors.pageEditor.getContent();

            var pagePermalink = $("#pagePermalink").val().replace(/(^\s*)|(\s*$)/g, "");
            if (admin.pageList.type === "link") {
                pagePermalink = Util.proessURL(pagePermalink);
            }

            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "pageContent": pageContent,
                    "pagePermalink": pagePermalink,
                    "pageCommentable": $("#pageCommentable").prop("checked"),
                    "pageType": admin.pageList.type,
                    "pageOpenTarget": $("#pageTarget").val()
                }
            };

            $.ajax({
                url: latkeConfig.servePath + "/console/page/",
                type: "POST",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function (result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    admin.pageList.id = "";
                    $("#pagePermalink").val("");
                    $("#pageTitle").val("");
                    $("#pageCommentable").prop("cheked", false);
                    $("#pageTarget").val("_self");
                    $($(".fn-type").get(0)).click();

                    admin.editors.pageEditor.setContent("");

                    if (admin.pageList.pageInfo.currentCount === Label.PAGE_SIZE &&
                            admin.pageList.pageInfo.currentPage === admin.pageList.pageInfo.pageCount) {
                        admin.pageList.pageInfo.pageCount++;
                    }
                    var hashList = window.location.hash.split("/");
                    if (admin.pageList.pageInfo.pageCount == hashList[hashList.length - 1]) {
                        admin.pageList.getList(admin.pageList.pageInfo.pageCount);
                    } else {
                        admin.setHashByPage(admin.pageList.pageInfo.pageCount);
                    }

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 更新自定义页面
     */
    update: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            var pageContent = admin.editors.pageEditor.getContent();

            var pagePermalink = $("#pagePermalink").val().replace(/(^\s*)|(\s*$)/g, "");
            if (admin.pageList.type === "link") {
                pagePermalink = Util.proessURL(pagePermalink);
            }


            var requestJSONObject = {
                "page": {
                    "pageTitle": $("#pageTitle").val(),
                    "oId": this.id,
                    "pageContent": pageContent,
                    "pagePermalink": pagePermalink,
                    "pageCommentable": $("#pageCommentable").prop("checked"),
                    "pageType": admin.pageList.type,
                    "pageOpenTarget": $("#pageTarget").val(),
                    "pageEditorType": admin.pageList.currentEditorType
                }
            };

            $.ajax({
                url: latkeConfig.servePath + "/console/page/",
                type: "PUT",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function (result, textStatus) {
                    $("#tipMsg").text(result.msg);

                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }
                    admin.pageList.id = "";

                    admin.pageList.getList(admin.pageList.pageInfo.currentPage);
                    $("#pageTitle").val("");
                    $("#pagePermalink").val("");
                    $("#pageCommentable").prop("cheked", false);
                    $("#pageTarget").val("_self");
                    $($(".fn-type").get(0)).click();

                    admin.editors.pageEditor.setContent("");

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 验证字段
     */
    validate: function () {
        if ($("#pageTitle").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#pageTitle").focus();
        } else if (admin.pageList.type === "link" &&
                $("#pagePermalink").val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.linkEmptyLabel);
        } else {
            return true;
        }
        return false;
    },
    /*
     * 提交自定义页面
     */
    submit: function () {
        if (this.id !== "") {
            this.update();
        } else {
            this.add();
        }

        if (admin.pageList.currentEditorType !== Label.editorType) {
            admin.editors.pageEditor.remove();

            admin.pageList.currentEditorType = Label.editorType;
            admin.editors.pageEditor.init(Label.editorType);
        }
    },
    /*
     * 调换顺序
     */
    changeOrder: function (id, order, status) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");

        var requestJSONObject = {
            "oId": id.toString(),
            "direction": status
        };

        $.ajax({
            url: latkeConfig.servePath + "/console/page/order/",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);

                // Refershes the page list
                admin.pageList.getList(admin.pageList.pageInfo.currentPage);

                $("#loadMsg").text("");
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["page-list"] = {
    "obj": admin.pageList,
    "init": admin.pageList.init,
    "refresh": admin.pageList.getList
}
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * others for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.8, May 28, 2013
 */

/* oterhs 相关操作 */
admin.others = {
    /*
     * @description 初始化
     */
    init: function () {
        $("#tabOthers").tabs();
        
        $.ajax({
            url: latkeConfig.servePath + "/console/reply/notification/template",
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                $("#replayEmailTemplateTitle").val(result.replyNotificationTemplate.subject);
                $("#replayEmailTemplateBody").val(result.replyNotificationTemplate.body);
                
                $("#loadMsg").text("");
            }
        });        
    },
    
    /*
     * @description 移除未使用的标签。
     */
    removeUnusedTags: function () {
        $("#tipMsg").text("");
        
        $.ajax({
            url: latkeConfig.servePath + "/console/tag/unused",
            type: "DELETE",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);                
            }
        });
    },
    
    /*
     * 获取未使用的标签。
     * XXX: Not used this function yet.
     */
    getUnusedTags: function () {
        $.ajax({
            url: latkeConfig.servePath + "/console/tag/unused",
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                var unusedTags = result.unusedTags;
                if (0 === unusedTags.length) {
                    return;
                }
            }
        });
    },
    
    /*
     * @description 跟新回复提醒邮件模版
     */
    update: function () {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        
        var requestJSONObject = {
            "replyNotificationTemplate": {
                "subject": $("#replayEmailTemplateTitle").val(),
                "body": $("#replayEmailTemplateBody").val()
            }
        };
            
        $.ajax({
            url: latkeConfig.servePath + "/console/reply/notification/template",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                $("#loadMsg").text("");
            }
        });     
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register.others =  {
    "obj": admin.others,
    "init":admin.others.init,
    "refresh": function () {
        admin.clearTip();
    }
};
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * link list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.4, Feb 23, 2013
 */

/* link-list 相关操作 */
admin.linkList = {
    tablePagination:  new TablePaginate("link"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    id: "",
    
    /* 
     * 初始化 table, pagination
     */
    init: function (page) {
        this.tablePagination.buildTable([{
            text: "",
            index: "linkOrder",
            width: 60
        },{
            style: "padding-left: 12px;",
            text: Label.linkTitleLabel,
            index: "linkTitle",
            width: 230
        }, {
            style: "padding-left: 12px;",
            text: Label.urlLabel,
            index: "linkAddress",
            minWidth: 180
        }, {
            style: "padding-left: 12px;",
            text: Label.linkDescriptionLabel,
            index: "linkDescription",
            width: 360
        }]);
    
        this.tablePagination.initPagination();
        this.getList(page);
        
        $("#updateLink").dialog({
            width: 700,
            height: 180,
            "modal": true,
            "hideFooter": true
        });
    },

    /* 
     * 根据当前页码获取链接列表
     * 
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        if (pageNum === 0) {
            pageNum = 1;
        }
        this.pageInfo.currentPage = pageNum;
        var that = this;
        
        $.ajax({
            url: latkeConfig.servePath + "/console/links/" + pageNum + "/" + Label.PAGE_SIZE + "/" +  Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                var links = result.links;
                var linkData = [];
                admin.linkList.pageInfo.currentCount = links.length;
                admin.linkList.pageInfo.pageCount = result.pagination.paginationPageCount === 0 ? 1 : result.pagination.paginationPageCount;

                for (var i = 0; i < links.length; i++) {
                    linkData[i] = {};
                    if (i === 0) {
                        if (links.length === 1) {
                            linkData[i].linkOrder = "";
                        } else {
                            linkData[i].linkOrder = '<div class="table-center" style="width:14px">\
                                <span onclick="admin.linkList.changeOrder(' + links[i].oId + ', ' + i + ', \'down\');" class="table-downIcon"></span>\
                            </div>';
                        }
                    } else if (i === links.length - 1) {
                        linkData[i].linkOrder = '<div class="table-center" style="width:14px">\
                                <span onclick="admin.linkList.changeOrder(' + links[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                            </div>';
                    } else {
                        linkData[i].linkOrder = '<div class="table-center" style="width:38px">\
                                <span onclick="admin.linkList.changeOrder(' + links[i].oId + ', ' + i + ', \'up\');" class="table-upIcon"></span>\
                                <span onclick="admin.linkList.changeOrder(' + links[i].oId + ', ' + i + ', \'down\');" class="table-downIcon"></span>\
                            </div>';
                    }
                    
                    linkData[i].linkTitle = links[i].linkTitle;
                    linkData[i].linkAddress = "<a target='_blank' class='no-underline' href='" + links[i].linkAddress + "'>"
                    + links[i].linkAddress + "</a>";
                    linkData[i].linkDescription = links[i].linkDescription;
                    linkData[i].expendRow = "<span><a href='" + links[i].linkAddress + "' target='_blank'>" + Label.viewLabel + "</a>  \
                                <a href='javascript:void(0)' onclick=\"admin.linkList.get('" + links[i].oId + "')\">" + Label.updateLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.linkList.del('" + links[i].oId + "', '" + links[i].linkTitle + "')\">" + Label.removeLabel + "</a></span>";
                }

                that.tablePagination.updateTablePagination(linkData, pageNum, result.pagination);
                
                $("#loadMsg").text("");
            }
        });
    },
    
    /*
     * 添加链接
     */
    add: function () {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#linkTitle").val(),
                    "linkAddress": $("#linkAddress").val(),
                    "linkDescription": $("#linkDescription").val()
                }
            };
            
            $.ajax({
                url: latkeConfig.servePath + "/console/link/",
                type: "POST",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }
                    
                    $("#linkTitle").val("");
                    $("#linkAddress").val("");
                    $("#linkDescription").val("");
                    if (admin.linkList.pageInfo.currentCount === Label.PAGE_SIZE &&
                        admin.linkList.pageInfo.currentPage === admin.linkList.pageInfo.pageCount) {
                        admin.linkList.pageInfo.pageCount++;
                    }
                    var hashList = window.location.hash.split("/");
                    if (admin.linkList.pageInfo.pageCount !== parseInt(hashList[hashList.length - 1])) {
                        admin.setHashByPage(admin.linkList.pageInfo.pageCount);
                    }
                    
                    admin.linkList.getList(admin.linkList.pageInfo.pageCount);
                    
                    $("#loadMsg").text("");
                }
            });
        }
    },
    
    /*
     * 获取链接
     * @id 链接 id
     */
    get: function (id) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#updateLink").dialog("open");
        
        $.ajax({
            url: latkeConfig.servePath + "/console/link/" + id,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                admin.linkList.id = id;
                
                $("#linkTitleUpdate").val(result.link.linkTitle);
                $("#linkAddressUpdate").val(result.link.linkAddress);
                $("#linkDescriptionUpdate").val(result.link.linkDescription);
                
                $("#loadMsg").text("");
            }
        });
    },
    
    /*
     * 更新链接
     */
    update: function () {
        if (this.validate("Update")) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            var requestJSONObject = {
                "link": {
                    "linkTitle": $("#linkTitleUpdate").val(),
                    "oId": this.id,
                    "linkAddress": $("#linkAddressUpdate").val(),
                    "linkDescription": $("#linkDescriptionUpdate").val()
                }
            };
            
            $.ajax({
                url: latkeConfig.servePath + "/console/link/",
                type: "PUT",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus){
                    $("#updateLink").dialog("close");
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }
                    
                    admin.linkList.getList(admin.linkList.pageInfo.currentPage);
                    
                    $("#loadMsg").text("");
                }
            });
        }
    },
    
    /*
     * 删除链接
     * @id 链接 id
     * @title 链接标题
     */
    del: function (id, title) {
        var isDelete = confirm(Label.confirmRemoveLabel + Label.permalinkLabel + '"' + title + '"?');
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");
            
            $.ajax({
                url: latkeConfig.servePath + "/console/link/" + id,
                type: "DELETE",
                cache: false,
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }
                    
                    var pageNum = admin.linkList.pageInfo.currentPage;
                    if (admin.linkList.pageInfo.currentCount === 1 && admin.linkList.pageInfo.pageCount !== 1 &&
                        admin.linkList.pageInfo.currentPage === admin.linkList.pageInfo.pageCount) {
                        admin.linkList.pageInfo.pageCount--;
                        pageNum = admin.linkList.pageInfo.pageCount;
                    }
                    
                    var hashList = window.location.hash.split("/");
                    if (pageNum !== parseInt(hashList[hashList.length - 1])) {
                        admin.setHashByPage(pageNum);
                    }
                    
                    admin.linkList.getList(pageNum);
                    
                    $("#loadMsg").text("");
                }
            });
        }
    },
    
    /*
     * 验证字段
     * @status 更新或者添加时进行验证
     */
    validate: function (status) {
        if (!status) {
            status = "";
        }
        if ($("#linkTitle" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.titleEmptyLabel);
            $("#linkTitle" + status).focus().val("");
        } else if ($("#linkAddress" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.addressEmptyLabel);
            $("#linkAddress" + status).focus().val("");
        } else if (!/^\w+:\/\//.test($("#linkAddress" + status).val())) {
            $("#tipMsg").text(Label.addressInvalidLabel);
            $("#linkAddress" + status).focus().val("");
        } else {
            return true;
        }
        return false;
    },
    
    /*
     * 调换顺序
     */
    changeOrder: function (id, order, status) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        
        var requestJSONObject = {
            "oId": id.toString(),
            "direction": status
        };
        
        $.ajax({
            url: latkeConfig.servePath + "/console/link/order/",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                
                // Refershes the link list
                admin.linkList.getList(admin.linkList.pageInfo.currentPage);
                
                $("#loadMsg").text("");
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["link-list"] =  {
    "obj": admin.linkList,
    "init": admin.linkList.init,
    "refresh": admin.linkList.getList
}/*
 * Copyright (c) 2010-2015, b3log.org
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
 * preference for admin.
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.1.9, Nov 20, 2015
 */

/* preference 相关操作 */
admin.preference = {
    locale: "",
    editorType: "",
    /*
     * 初始化
     */
    init: function () {
        $("#tabPreference").tabs();

        $.ajax({
            url: latkeConfig.servePath + "/console/preference/",
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                var preference = result.preference;

                $("#metaKeywords").val(preference.metaKeywords),
                        $("#metaDescription").val(preference.metaDescription),
                        $("#blogTitle").val(preference.blogTitle),
                        $("#blogSubtitle").val(preference.blogSubtitle),
                        $("#mostCommentArticleDisplayCount").val(preference.mostCommentArticleDisplayCount);
                $("#mostViewArticleDisplayCount").val(preference.mostViewArticleDisplayCount),
                        $("#recentCommentDisplayCount").val(preference.recentCommentDisplayCount);
                $("#mostUsedTagDisplayCount").val(preference.mostUsedTagDisplayCount);
                $("#articleListDisplayCount").val(preference.articleListDisplayCount);
                $("#articleListPaginationWindowSize").val(preference.articleListPaginationWindowSize);
                $("#localeString").val(preference.localeString);
                $("#timeZoneId").val(preference.timeZoneId);
                $("#noticeBoard").val(preference.noticeBoard);
                $("#footerContent").val(preference.footerContent);
                $("#htmlHead").val(preference.htmlHead);
                $("#externalRelevantArticlesDisplayCount").val(preference.externalRelevantArticlesDisplayCount);
                $("#relevantArticlesDisplayCount").val(preference.relevantArticlesDisplayCount);
                $("#randomArticlesDisplayCount").val(preference.randomArticlesDisplayCount);
                $("#keyOfSolo").val(preference.keyOfSolo);
                
                "true" === preference.enableArticleUpdateHint ? $("#enableArticleUpdateHint").attr("checked", "checked") : $("#enableArticleUpdateHint").removeAttr("checked");
                "true" === preference.allowVisitDraftViaPermalink ? $("#allowVisitDraftViaPermalink").attr("checked", "checked") : $("allowVisitDraftViaPermalink").removeAttr("checked");
                "true" === preference.allowRegister ? $("#allowRegister").attr("checked", "checked") : $("#allowRegister").removeAttr("checked");
                "true" === preference.commentable ? $("#commentable").attr("checked", "checked") : $("commentable").removeAttr("checked");

                admin.preference.locale = preference.localeString;
                admin.preference.editorType = preference.editorType;

                // skin
                $("#skinMain").data("skinDirName", preference.skinDirName);
                var skins = eval('(' + preference.skins + ')');
                var skinsHTML = "";
                for (var i = 0; i < skins.length; i++) {
                    var selectedClass = "";
                    if (skins[i].skinName === preference.skinName
                            && skins[i].skinDirName === preference.skinDirName) {
                        selectedClass += " selected";
                    }
                    skinsHTML += "<div title='" + skins[i].skinDirName
                            + "' class='left skinItem" + selectedClass + "'><img class='skinPreview' src='"
                            + latkeConfig.staticServePath + "/skins/" + skins[i].skinDirName
                            + "/preview.png'/><div>" + skins[i].skinName + "</div></div>";
                }
                $("#skinMain").append(skinsHTML + "<div class='clear'></div>");

                $(".skinItem").click(function () {
                    $(".skinItem").removeClass("selected");
                    $(this).addClass("selected");
                    $("#skinMain").data("skinDirName", this.title);
                });

                // sign
                var signs = eval('(' + preference.signs + ')');
                for (var j = 1; j < signs.length; j++) {
                    $("#preferenceSign" + j).val(signs[j].signHTML);
                    $("#preferenceSignButton" + j).tip({
                        content: signs[j].signHTML === "" ? Label.signIsNullLabel : signs[j].signHTML.replace(/\n/g, "").replace(/<script.*<\/script>/ig, ""),
                        position: "bottom"
                    });
                }

                $("#articleListDisplay").val(preference.articleListStyle);
                $("#editorType").val(preference.editorType);
                $("#feedOutputMode").val(preference.feedOutputMode);
                $("#feedOutputCnt").val(preference.feedOutputCnt);
                
                $("#loadMsg").text("");
            }
        });

        $.ajax({
            url: latkeConfig.servePath + "/console/preference/qiniu",
            type: "GET",
            cache: false,
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                $("#qiniuAccessKey").val(result.qiniu.qiniuAccessKey);
                $("#qiniuSecretKey").val(result.qiniu.qiniuSecretKey);
                $("#qiniuDomain").val(result.qiniu.qiniuDomain);
                $("#qiniuBucket").val(result.qiniu.qiniuBucket);
            }
        });
    },
    /* 
     * @description 参数校验
     */
    validate: function () {
        if (!/^\d+$/.test($("#mostUsedTagDisplayCount").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.indexTagDisplayCntLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#mostUsedTagDisplayCount").focus();
            return false;
        } else if (!/^\d+$/.test($("#recentCommentDisplayCount").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.indexRecentCommentDisplayCntLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#recentCommentDisplayCount").focus();
            return false;
        } else if (!/^\d+$/.test($("#mostCommentArticleDisplayCount").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.indexMostCommentArticleDisplayCntLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#mostCommentArticleDisplayCount").focus();
            return false;
        } else if (!/^\d+$/.test($("#mostViewArticleDisplayCount").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.indexMostViewArticleDisplayCntLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#mostViewArticleDisplayCount").focus();
            return false;
        } else if (!/^\d+$/.test($("#articleListDisplayCount").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.pageSizeLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#articleListDisplayCount").focus();
            return false;
        } else if (!/^\d+$/.test($("#articleListPaginationWindowSize").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.windowSizeLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#articleListPaginationWindowSize").focus();
            return false;
        } else if (!/^\d+$/.test($("#randomArticlesDisplayCount").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.randomArticlesDisplayCntLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#randomArticlesDisplayCount").focus();
            return false;
        } else if (!/^\d+$/.test($("#relevantArticlesDisplayCount").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.relevantArticlesDisplayCntLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#relevantArticlesDisplayCount").focus();
            return false;
        } else if (!/^\d+$/.test($("#externalRelevantArticlesDisplayCount").val())) {
            $("#tipMsg").text("[" + Label.paramSettingsLabel + " - " + Label.externalRelevantArticlesDisplayCntLabel + "] " + Label.nonNegativeIntegerOnlyLabel);
            $("#externalRelevantArticlesDisplayCount").focus();
            return false;
        }
        return true;
    },
    /*
     * @description 更新
     */
    update: function () {
        if (!admin.preference.validate()) {
            return;
        }

        $("#tipMsg").text("");
        $("#loadMsg").text(Label.loadingLabel);
        var signs = [{
                "oId": 0,
                "signHTML": ""
            }, {
                "oId": 1,
                "signHTML": $("#preferenceSign1").val()
            }, {
                "oId": 2,
                "signHTML": $("#preferenceSign2").val()
            }, {
                "oId": 3,
                "signHTML": $("#preferenceSign3").val()
            }];

        var requestJSONObject = {
            "preference": {
                "metaKeywords": $("#metaKeywords").val(),
                "metaDescription": $("#metaDescription").val(),
                "blogTitle": $("#blogTitle").val(),
                "blogSubtitle": $("#blogSubtitle").val(),
                "mostCommentArticleDisplayCount": $("#mostCommentArticleDisplayCount").val(),
                "mostViewArticleDisplayCount": $("#mostViewArticleDisplayCount").val(),
                "recentCommentDisplayCount": $("#recentCommentDisplayCount").val(),
                "mostUsedTagDisplayCount": $("#mostUsedTagDisplayCount").val(),
                "articleListDisplayCount": $("#articleListDisplayCount").val(),
                "articleListPaginationWindowSize": $("#articleListPaginationWindowSize").val(),
                "skinDirName": $("#skinMain").data("skinDirName"),
                "localeString": $("#localeString").val(),
                "timeZoneId": $("#timeZoneId").val(),
                "noticeBoard": $("#noticeBoard").val(),
                "footerContent": $("#footerContent").val(),
                "htmlHead": $("#htmlHead").val(),
                "externalRelevantArticlesDisplayCount": $("#externalRelevantArticlesDisplayCount").val(),
                "relevantArticlesDisplayCount": $("#relevantArticlesDisplayCount").val(),
                "randomArticlesDisplayCount": $("#randomArticlesDisplayCount").val(),
                "enableArticleUpdateHint": $("#enableArticleUpdateHint").prop("checked"),
                "signs": signs,
                "keyOfSolo": $("#keyOfSolo").val(),
                "allowVisitDraftViaPermalink": $("#allowVisitDraftViaPermalink").prop("checked"),
                "articleListStyle": $("#articleListDisplay").val(),
                "editorType": $("#editorType").val(),
                "feedOutputMode": $("#feedOutputMode").val(),
                "feedOutputCnt": $("#feedOutputCnt").val(),
                "commentable": $("#commentable").prop("checked"),
                "allowRegister": $("#allowRegister").prop("checked")
            }
        };

        $.ajax({
            url: latkeConfig.servePath + "/console/preference/",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                if ($("#localeString").val() !== admin.preference.locale ||
                        $("#editorType").val() !== admin.preference.editorType) {
                    window.location.reload();
                }

                // update article and preferences signs
                for (var i = 1; i < signs.length; i++) {
                    if ($("#articleSign" + signs[i].oId).length === 1) {
                        $("#articleSign" + signs[i].oId).tip("option", "content",
                                signs[i].signHTML === "" ? Label.signIsNullLabel : signs[i].signHTML.replace(/\n/g, "").replace(/<script.*<\/script>/ig, ""));
                    }
                    $("#preferenceSignButton" + signs[i].oId).tip("option", "content",
                            signs[i].signHTML === "" ? Label.signIsNullLabel : signs[i].signHTML.replace(/\n/g, "").replace(/<script.*<\/script>/ig, ""));
                }

                $("#loadMsg").text("");
            }
        });
    },
    /*
     * @description 更新 Qiniu 参数
     */
    updateQiniu: function () {
        $("#tipMsg").text("");
        $("#loadMsg").text(Label.loadingLabel);

        var requestJSONObject = {
            "qiniuAccessKey": $("#qiniuAccessKey").val(),
            "qiniuSecretKey": $("#qiniuSecretKey").val(),
            "qiniuDomain": $("#qiniuDomain").val(),
            "qiniuBucket": $("#qiniuBucket").val()
        };

        $.ajax({
            url: latkeConfig.servePath + "/console/preference/qiniu",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function (result, textStatus) {
                $("#tipMsg").text(result.msg);

                $("#loadMsg").text("");
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["preference"] = {
    "obj": admin.preference,
    "init": admin.preference.init,
    "refresh": function () {
        admin.clearTip();
    }
};
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * plugin list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.4, May 28, 2013
 */

/* plugin-list 相关操作 */
admin.pluginList = {
    tablePagination: new TablePaginate("plugin"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    /* 
     * 初始化 table, pagination
     */
    init: function(page) {
        this.tablePagination.buildTable([{
                style: "padding-left: 12px;",
                text: Label.pluginNameLabel,
                index: "name",
                width: 230
            }, {
                style: "padding-left: 12px;",
                text: Label.statusLabel,
                index: "status",
                minWidth: 180
            }, {
                style: "padding-left: 12px;",
                text: Label.authorLabel,
                index: "author",
                width: 200
            }, {
                style: "padding-left: 12px;",
                text: Label.versionLabel,
                index: "version",
                width: 120
            }]);

        this.tablePagination.initPagination();
        $("#pluginSetting").dialog({
            width: 700,
            height: 180,
            "modal": true,
            "hideFooter": true
        });
        this.getList(page);
    },
    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function(pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        var that = this;

        $.ajax({
            url: latkeConfig.servePath + "/console/plugins/" + pageNum + "/" + Label.PAGE_SIZE + "/" + Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                admin.pluginList.pageInfo.currentPage = pageNum;
                var datas = result.plugins;
                for (var i = 0; i < datas.length; i++) {
                    datas[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.pluginList.changeStatus('" +
                            datas[i].oId + "', '" + datas[i].status + "')\">";
                    if (datas[i].status === "ENABLED") {
                        datas[i].status = Label.enabledLabel;
                        datas[i].expendRow += Label.disableLabel;
                    } else {
                        datas[i].status = Label.disabledLabel;
                        datas[i].expendRow += Label.enableLabel;
                    }
                    datas[i].expendRow += "</a>  ";

                    if (datas[i].setting != "{}") {
                        datas[i].expendRow += "<a href='javascript:void(0)' onclick=\"admin.pluginList.toSetting('" + datas[i].oId + "')\"> " + Label.settingLabel + " </a>  ";
                    }
                }

                that.tablePagination.updateTablePagination(result.plugins, pageNum, result.pagination);

                $("#loadMsg").text("");
            }
        });
    },
    toSetting: function(pluginId) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        var requestJSONObject = {
            "oId": pluginId
        };

        $.ajax({
            url: latkeConfig.servePath + "/console/plugin/toSetting",
            type: "POST",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);

                $("#pluginSetting").html(result);
                $("#pluginSetting").dialog("open");

                $("#loadMsg").text("");
            }
        });
    },
    changeStatus: function(pluginId, status) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        if (status === "ENABLED") {
            status = "DISABLED";
        } else {
            status = "ENABLED";
        }

        var requestJSONObject = {
            "oId": pluginId,
            "status": status
        };

        $.ajax({
            url: latkeConfig.servePath + "/console/plugin/status/",
            type: "PUT",
            cache: false,
            data: JSON.stringify(requestJSONObject),
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                $("#loadMsg").text("");
                window.location.reload();
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["plugin-list"] = {
    "obj": admin.pluginList,
    "init": admin.pluginList.init,
    "refresh": function() {
        admin.claerTip();
    }
};
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * user list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.1.1.7, Oct 17, 2015
 */

/* user-list 相关操作 */
admin.userList = {
    tablePagination: new TablePaginate("user"),
    pageInfo: {
        currentCount: 1,
        pageCount: 1,
        currentPage: 1
    },
    userInfo: {
        'oId': "",
        "userRole": ""
    },
    /* 
     * 初始化 table, pagination
     */
    init: function(page) {
        this.tablePagination.buildTable([{
                style: "padding-left: 12px;",
                text: Label.commentNameLabel,
                index: "userName",
                width: 230
            }, {
                style: "padding-left: 12px;",
                text: Label.commentEmailLabel,
                index: "userEmail",
                minWidth: 180
            }, {
                style: "padding-left: 12px;",
                text: Label.roleLabel,
                index: "isAdmin",
                width: 120
            }]);

        this.tablePagination.initPagination();
        this.getList(page);

        $("#userUpdate").dialog({
            width: 700,
            height: 250,
            "modal": true,
            "hideFooter": true
        });
    },
    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function(pageNum) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        this.pageInfo.currentPage = pageNum;
        var that = this;

        $.ajax({
            url: latkeConfig.servePath + "/console/users/" + pageNum + "/" + Label.PAGE_SIZE + "/" + Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                var users = result.users;
                var userData = [];
                admin.userList.pageInfo.currentCount = users.length;
                admin.userList.pageInfo.pageCount = result.pagination.paginationPageCount;
                if (users.length < 1) {
                    $("#tipMsg").text("No user  " + Label.reportIssueLabel);
                    $("#loadMsg").text("");
                    return;
                }

                for (var i = 0; i < users.length; i++) {
                    userData[i] = {};
                    userData[i].userName = users[i].userName;
                    userData[i].userEmail = users[i].userEmail;

                    if ("adminRole" === users[i].userRole) {
                        userData[i].isAdmin = "&nbsp;" + Label.administratorLabel;
                        userData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.userList.get('" +
                                users[i].oId + "', '" + users[i].userRole + "')\">" + Label.updateLabel + "</a>";
                    } else {
                        userData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.userList.get('" +
                                users[i].oId + "', '" + users[i].userRole + "')\">" + Label.updateLabel + "</a>\
                                <a href='javascript:void(0)' onclick=\"admin.userList.del('" + users[i].oId + "', '" + users[i].userName + "')\">" + Label.removeLabel + "</a>" +
                                "<a href='javascript:void(0)' onclick=\"admin.userList.changeRole('" + users[i].oId + "')\">" + Label.changeRoleLabel + "</a>";
                        if ("defaultRole" === users[i].userRole) {
                            userData[i].isAdmin = Label.commonUserLabel;
                        }
                        else {
                            userData[i].isAdmin = Label.visitorUserLabel;
                        }
                    }

                    that.tablePagination.updateTablePagination(userData, pageNum, result.pagination);

                    $("#loadMsg").text("");
                }
            }
        });
    },
    /*
     * 添加用户
     */
    add: function() {
        if (this.validate()) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            var requestJSONObject = {
                "userName": $("#userName").val(),
                "userEmail": $("#userEmail").val(),
                "userURL": $("#userURL").val(),
                "userPassword": $("#userPassword").val(),
                "userAvatar": $("#userAvatar").val()
            };

            $.ajax({
                url: latkeConfig.servePath + "/console/user/",
                type: "POST",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    $("#userName").val("");
                    $("#userEmail").val("");
                    $("#userURL").val("");
                    $("#userPassword").val("");
                    $("#userAvatar").val("");
                    if (admin.userList.pageInfo.currentCount === Label.PAGE_SIZE &&
                            admin.userList.pageInfo.currentPage === admin.userList.pageInfo.pageCount) {
                        admin.userList.pageInfo.pageCount++;
                    }
                    var hashList = window.location.hash.split("/");
                    if (admin.userList.pageInfo.pageCount !== parseInt(hashList[hashList.length - 1])) {
                        admin.setHashByPage(admin.userList.pageInfo.pageCount);
                    }

                    admin.userList.getList(admin.userList.pageInfo.pageCount);

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 获取用户
     * @id 用户 id
     */
    get: function(id, userRole) {
        $("#loadMsg").text(Label.loadingLabel);
        $("#tipMsg").text("");
        $("#userUpdate").dialog("open");

        $.ajax({
            url: latkeConfig.servePath + "/console/user/" + id,
            type: "GET",
            cache: false,
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                var $userEmailUpdate = $("#userEmailUpdate");
                $("#userNameUpdate").val(result.user.userName).data("userInfo", {
                    'oId': id,
                    "userRole": userRole
                });
                $userEmailUpdate.val(result.user.userEmail);
                if ("adminRole" === userRole) {
                    $userEmailUpdate.attr("disabled", "disabled");
                } else {
                    $userEmailUpdate.removeAttr("disabled");
                }
                
                $("#userURLUpdate").val(result.user.userURL);
                $("#userPasswordUpdate").val(result.user.userPassword);
                $("#userAvatarUpdate").val(result.user.userAvatar);

                $("#loadMsg").text("");
            }
        });
    },
    /*
     * 更新用户
     */
    update: function() {
        if (this.validate("Update")) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            var userInfo = $("#userNameUpdate").data("userInfo");
            var requestJSONObject = {
                "userName": $("#userNameUpdate").val(),
                "oId": userInfo.oId,
                "userEmail": $("#userEmailUpdate").val(),
                "userURL": $("#userURLUpdate").val(),
                "userRole": userInfo.userRole,
                "userPassword": $("#userPasswordUpdate").val(),
                "userAvatar": $("#userAvatarUpdate").val()
            };

            $.ajax({
                url: latkeConfig.servePath + "/console/user/",
                type: "PUT",
                cache: false,
                data: JSON.stringify(requestJSONObject),
                success: function(result, textStatus) {
                    $("#userUpdate").dialog("close");
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    admin.userList.getList(admin.userList.pageInfo.currentPage);

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /*
     * 删除用户
     * @id 用户 id
     * @userName 用户名称
     */
    del: function(id, userName) {
        var isDelete = confirm(Label.confirmRemoveLabel + Label.userLabel + '"' + userName + '"?');
        if (isDelete) {
            $("#loadMsg").text(Label.loadingLabel);
            $("#tipMsg").text("");

            $.ajax({
                url: latkeConfig.servePath + "/console/user/" + id,
                type: "DELETE",
                cache: false,
                success: function(result, textStatus) {
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }

                    var pageNum = admin.userList.pageInfo.currentPage;
                    if (admin.userList.pageInfo.currentCount === 1 && admin.userList.pageInfo.pageCount !== 1 &&
                            admin.userList.pageInfo.currentPage === admin.userList.pageInfo.pageCount) {
                        admin.userList.pageInfo.pageCount--;
                        pageNum = admin.userList.pageInfo.pageCount;
                    }
                    var hashList = window.location.hash.split("/");
                    if (pageNum !== parseInt(hashList[hashList.length - 1])) {
                        admin.setHashByPage(pageNum);
                    }
                    admin.userList.getList(pageNum);

                    $("#loadMsg").text("");
                }
            });
        }
    },
    /**
     * 修改角色
     * @param id
     */
    changeRole: function(id) {
        $("#tipMsg").text("");
        $.ajax({
            url: latkeConfig.servePath + "/console/changeRole/" + id,
            type: "GET",
            cache: false,
            success: function(result, textStatus) {
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }

                var pageNum = admin.userList.pageInfo.currentPage;
                if (admin.userList.pageInfo.currentCount === 1 && admin.userList.pageInfo.pageCount !== 1 &&
                        admin.userList.pageInfo.currentPage === admin.userList.pageInfo.pageCount) {
                    admin.userList.pageInfo.pageCount--;
                    pageNum = admin.userList.pageInfo.pageCount;
                }
                var hashList = window.location.hash.split("/");
                if (pageNum !== parseInt(hashList[hashList.length - 1])) {
                    admin.setHashByPage(pageNum);
                }
                admin.userList.getList(pageNum);

                $("#loadMsg").text("");
            }
        });
    },
    /*
     * 验证字段
     * @status 更新或者添加时进行验证
     */
    validate: function(status) {
        if (!status) {
            status = "";
        }
        var userName = $("#userName" + status).val().replace(/(^\s*)|(\s*$)/g, "");
        if (2 > userName.length || userName.length > 20) {
            $("#tipMsg").text(Label.nameTooLongLabel);
            $("#userName" + status).focus();
        } else if ($("#userEmail" + status).val().replace(/\s/g, "") === "") {
            $("#tipMsg").text(Label.mailCannotEmptyLabel);
            $("#userEmail" + status).focus();
        } else if (!/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test($("#userEmail" + status).val())) {
            $("#tipMsg").text(Label.mailInvalidLabel);
            $("#userEmail" + status).focus();
        } else if ($("#userPassword" + status).val() === "") {
            $("#tipMsg").text(Label.passwordEmptyLabel);
            $("#userPassword" + status).focus();
        } else {
            return true;
        }
        return false;
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["user-list"] = {
    "obj": admin.userList,
    "init": admin.userList.init,
    "refresh": function() {
        admin.clearTip();
    }
}/*
 * Copyright (c) 2010-2015, b3log.org
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
 * comment list for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.1.5, Feb 23, 2013
 */

/* comment-list 相关操作 */
admin.commentList = {
    tablePagination:  new TablePaginate("comment"),
    pageInfo: {
        currentPage: 1
    },
    
    /* 
     * 初始化 table, pagination, comments dialog 
     */
    init: function (page) {
        this.tablePagination.buildTable([{
            text: Label.commentContentLabel,
            index: "content",
            minWidth: 300,
            style: "padding-left: 12px;"
        }, {
            text: Label.authorLabel,
            index: "title",
            width: 230,
            style: "padding-left: 12px;"
        }, {
            text: Label.createDateLabel,
            index: "date",
            width: 90,
            style: "padding-left: 12px;"
        }]);
        this.tablePagination.initPagination();
        this.getList(page);
    },

    /* 
     * 根据当前页码获取列表
     * @pagNum 当前页码
     */
    getList: function (pageNum) {
        var that = this;
        $("#loadMsg").text(Label.loadingLabel);
        
        $.ajax({
            url: latkeConfig.servePath + "/console/comments/" + pageNum + "/" + Label.PAGE_SIZE + "/" +  Label.WINDOW_SIZE,
            type: "GET",
            cache: false,
            success: function(result, textStatus){
                $("#tipMsg").text(result.msg);
                if (!result.sc) {
                    $("#loadMsg").text("");
                    return;
                }
                
                that.pageInfo.currentPage = pageNum;
                var comments = result.comments,
                commentsData = [];
                for (var i = 0; i < comments.length; i++) {
                    var type = "Article"
                    if (comments[i].type === "pageComment") {
                        type = "Page"
                    }
                    
                    commentsData[i] = {};
                    
                    commentsData[i].content = Util.replaceEmString(comments[i].commentContent) + 
                    "<span class='table-tag'> on &nbsp;&nbsp;</span><a href='" + latkeConfig.servePath + comments[i].commentSharpURL + 
                    "' target='_blank'>" + comments[i].commentTitle +
                    "</a>";
                
                    commentsData[i].expendRow = "<a href='javascript:void(0)' onclick=\"admin.commentList.del('" +
                    comments[i].oId + "', '" + type + "')\">" + Label.removeLabel + "</a>";
                
                    commentsData[i].title = "<img class='small-head' src='" + 
                    comments[i].commentThumbnailURL + "'/>";
                    if ("http://" === comments[i].commentURL) {
                        commentsData[i].title += comments[i].commentName;
                    } else {
                        commentsData[i].title += "<a href='" + comments[i].commentURL +
                        "' target='_blank' class='no-underline'>" + comments[i].commentName + 
                        "</a>";
                    }                    
                    commentsData[i].title += "<br/><a href='mailto:" + comments[i].commentEmail +
                    "'>" + comments[i].commentEmail + "</a>";                
                    
                    commentsData[i].date = $.bowknot.getDate(comments[i].commentTime);
                }
                
                that.tablePagination.updateTablePagination(commentsData, pageNum, result.pagination);
                
                $("#loadMsg").text("");
            }
        });
    },
    
    /* 
     * 删除评论
     * @id 评论 id 
     * @type 评论类型：文章/自定义页面
     */
    del: function (id, type) {
        if (confirm(Label.confirmRemoveLabel + Label.commentLabel + "?")) {
            $("#loadMsg").text(Label.loadingLabel);
            
            $.ajax({
                url: latkeConfig.servePath + "/console/" + type.toLowerCase() + "/comment/" + id,
                type: "DELETE",
                cache: false,
                success: function(result, textStatus){
                    $("#tipMsg").text(result.msg);
                    if (!result.sc) {
                        $("#loadMsg").text("");
                        return;
                    }
                    
                    admin.commentList.getList(admin.commentList.pageInfo.currentPage);
                    
                    $("#loadMsg").text("");
                }
            });
        }
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["comment-list"] =  {
    "obj": admin.commentList,
    "init": admin.commentList.init,
    "refresh": admin.commentList.getList
}/*
 * Copyright (c) 2010-2015, b3log.org
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
 *  plugin manager for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.0.7, Mar 28, 2012
 */
var plugins = {};
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
/*
 * Copyright (c) 2010-2015, b3log.org
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
 * main for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.0.0.3, May 28, 2013
 */

/* main 相关操作 */
admin.main = {
};

/*
 * 注册到 admin 进行管理 
 */
admin.register.main =  {
    "obj": admin.main,
    "init": function () {
        admin.clearTip();
    },
    "refresh": function () {
        admin.clearTip();
    }
};
/*
 * Copyright (c) 2010-2015, b3log.org
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
 *  about for admin
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.4, May 28, 2013
 */

/* about 相关操作 */
admin.about = {
    init: function() {
        $.ajax({
            url: "http://rhythm.b3log.org/version/solo/latest/" + Label.version,
            type: "GET",
            cache: false,
            dataType: "jsonp",
            success: function(data, textStatus) {
                var version = data.soloVersion;
                if (version === Label.version) {
                    $("#aboutLatest").text(Label.upToDateLabel);
                } else {
                    $("#aboutLatest").html(Label.outOfDateLabel +
                            "<a href='" + data.soloDownload + "'>" + version + "</a>");
                }
            },
            complete: function(XHR, TS) {
                admin.clearTip();
            }
        });
    }
};

/*
 * 注册到 admin 进行管理 
 */
admin.register["about"] = {
    "obj": admin.about,
    "init": admin.about.init,
    "refresh": function() {
        admin.clearTip();
    }
};