/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-2019, b3log.org & hacpai.com
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

admin.editors = {}

/*
 * @description Create SoloEditor can use all editor.
 * @constructor
 * @param conf 编辑器初始化参数
 * @param conf.id 编辑器渲染元素 id
 * @param conf.height 编辑器种类
 */
var SoloEditor = function (conf) {
  this.conf = conf
  return this.init()
}

$.extend(SoloEditor.prototype, {
  /*
   * @description 初始化编辑器
   */
  init: function () {
    this.editor = new Vditor(this.conf.id, {
      cache: true,
      preview: {
        delay: 500,
        show: false,
        url: `${latkeConfig.servePath}/console/markdown/2html`,
        parse: (element) => {
          if (element.style.display === 'none') {
            return
          }

          Util.parseMarkdown('content-reset');
          if (!Label.markedAvailable) {
            hljs.initHighlighting.called = false;
            hljs.initHighlighting();
          }
        },
      },
      upload: {
        max: 10 * 1024 * 1024,
        url: `${latkeConfig.servePath}/upload`,
      },
      height: this.conf.height,
      counter: 102400,
      resize: {
        enable: false,
      },
      lang: Label.localeString,
      classes: {
        preview: 'content-reset',
      },
    })
    return this.editor
  },
  /*
   * @description 获取编辑器值
   * @returns {string} 编辑器值
   */
  getContent: function () {
    return this.editor.getValue()
  },
  /*
   * @description 设置编辑器值
   * @param {string} content 编辑器回填内容
   */
  setContent: function (content) {
    this.editor.setValue(content)
  },
  /*
   * @description 移除编辑器值
   */
  remove: function () {
    document.getElementById(this.editor.vditor.id).outerHTML = ''
  },
})

admin.editors.articleEditor = {}
admin.editors.abstractEditor = {}
admin.editors.pageEditor = {}
