/*
 * Solo - A small and beautiful blogging system written in Java.
 * Copyright (c) 2010-present, b3log.org
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
 * @version 1.4.1.0, Feb 23, 2020
 */
admin.editors = {}

/*
 * @description Create SoloEditor can use all editor.
 * @constructor
 * @param conf 编辑器初始化参数
 * @param conf.id 编辑器渲染元素 id
 * @param conf.height 编辑器种类
 */
window.SoloEditor = function (conf) {
  this.conf = conf
  this.init()
}

$.extend(SoloEditor.prototype, {
  /*
   * @description 初始化编辑器
   */
  init: function () {
    const options = {
      typewriterMode: this.conf.typewriterMode,
      cache: true,
      tab: '\t',
      preview: {
        delay: 500,
        mode: this.conf.previewMode,
        url: Label.servePath + '/console/markdown/2html',
        hljs: {
          enable: !Label.luteAvailable,
          style: Label.hljsStyle,
        },
        parse: function(element) {
          if (element.style.display === 'none') {
            return
          }
          Util.parseMarkdown()
        },
      },
      upload: {
        max: 10 * 1024 * 1024,
        url: Label.uploadURL,
        token: Label.uploadToken,
        filename: function (name) {
          return  name.replace(/[^(a-zA-Z0-9\u4e00-\u9fa5\.)]/g, '').
            replace(/[\?\\/:|<>\*\[\]\(\)\$%\{\}@~]/g, '').
            replace('/\\s/g', '')
        }
      },
      height: this.conf.height,
      counter: 102400,
      resize: {
        enable: this.conf.resize,
      },
      lang: Label.localeString,
    }

    if ($(window).width() < 768) {
      options.toolbar = [
        'emoji',
        'bold',
        'italic',
        'link',
        'list',
        'check',
        'upload',
        'wysiwyg',
        'preview',
        'fullscreen',
        'help',
      ]
      options.resize.enable = false
    }

    if (typeof Vditor === 'undefined') {
      Util.loadVditor(() => {
        this.editor = new Vditor(this.conf.id, options)
      });
    } else {
      this.editor = new Vditor(this.conf.id, options)
    }

    if (typeof this.conf.fun === 'function') {
      this.conf.fun()
    }
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
