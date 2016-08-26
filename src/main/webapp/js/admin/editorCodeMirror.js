/*
 * Copyright (c) 2010-2016, b3log.org & hacpai.com
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
 * @version 1.2.1.4, Aug 26, 2016
 */

Util.processClipBoard = function (text, cm) {
    var text = toMarkdown(text, {converters: [
        ], gfm: true});

    // ascii 160 替换为 30
    text = $('<div>' + text + '</div>').text().replace(/\n{2,}/g, '\n\n').replace(/ /g, ' ');
    return $.trim(text);
};
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
        // init codemirror
        var commentEditor = new CodeMirrorEditor({
            element: document.getElementById(conf.id),
            dragDrop: false,
            lineWrapping: true,
            toolbar: [
                {name: 'bold'},
                {name: 'italic'},
                '|',
                {name: 'link'},
                {name: 'quote'},
                {name: 'unordered-list'},
                {name: 'ordered-list'},
                '|',
                {name: 'redo'},
                {name: 'undo'},
                '|',
                {name: 'preview'}
            ],
            extraKeys: {
                "Ctrl-/": "autocompleteEmoji"
            },
            status: false
        });
        commentEditor.render();
        this[conf.id] = commentEditor.codemirror;

        // after render, call back function
        if (typeof (conf.fun) === "function") {
            conf.fun();
        }
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
        $('.editor-toolbar').remove();
    }
};