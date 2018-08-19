/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2018, b3log.org & hacpai.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
/**
 * @fileoverview editor
 *
 * @author <a href="http://vanessa.b3log.org">Liyuan Li</a>
 * @version 1.1.0.5, Nov 8, 2016
 */

admin.editors = {};

/*
 * @description Create SoloEditor can use all editor. 
 *                e.g: TinyMCE, wnd 
 * @constructor
 * @param conf 编辑器初始化参数
 * @param conf.kind 编辑器类型 simple/all
 * @param conf.id 编辑器渲染元素 id
 * @param conf.language 编辑器使用语言
 * @param conf.type 编辑器种类
 * @param conf.codeMirrorLanguage codeMirror 编辑器当前解析语言
 */
var SoloEditor = function (conf) {
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

$.extend(SoloEditor.prototype, {
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
