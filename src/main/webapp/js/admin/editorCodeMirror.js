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
};