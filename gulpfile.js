/*
 * Symphony - A modern community (forum/SNS/blog) platform written in Java.
 * Copyright (C) 2012-2017,  b3log.org & hacpai.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @file frontend tool.
 *
 * @author <a href="mailto:liliyuan@fangstar.net">Liyuan Li</a>
 * @version 1.6.0.0, Sep 1, 2018
 */

'use strict'

const gulp = require('gulp')
const concat = require('gulp-concat')
const uglify = require('gulp-uglify')
const cleanCSS = require('gulp-clean-css')
const clean = require('gulp-clean')
const sass = require('gulp-sass')
const rename = require('gulp-rename')
const minifycss = require('gulp-minify-css')
const gulpSequence = require('gulp-sequence');

gulp.task('watch', function () {
  gulp.watch('./src/main/webapp/skins/*/css/*.scss', ['sass'])
})

gulp.task('sass', function () {
  return gulp.src('./src/main/webapp/skins/*/css/*.scss').
    pipe(sass().on('error', sass.logError)).
    pipe(gulp.dest('./src/main/webapp/skins/'))
})

gulp.task('compress', function () {
  // min css
  gulp.src('./src/main/webapp/js/lib/CodeMirrorEditor/codemirror.css').
    pipe(cleanCSS()).
    pipe(concat('codemirror.min.css')).
    pipe(gulp.dest('./src/main/webapp/js/lib/CodeMirrorEditor/'))

  // concat js
  const jsJqueryUpload = [
    './src/main/webapp/js/lib/jquery/jquery.min.js',
    './src/main/webapp/js/lib/jquery/file-upload-9.10.1/vendor/jquery.ui.widget.js',
    './src/main/webapp/js/lib/jquery/file-upload-9.10.1/jquery.iframe-transport.js',
    './src/main/webapp/js/lib/jquery/file-upload-9.10.1/jquery.fileupload.js',
    './src/main/webapp/js/lib/jquery/jquery.bowknot.min.js',
    // codemirror
    './src/main/webapp/js/lib/CodeMirrorEditor/codemirror.js',
    './src/main/webapp/js/lib/CodeMirrorEditor/placeholder.js',
    './src/main/webapp/js/overwrite/codemirror/addon/hint/show-hint.js',
    './src/main/webapp/js/lib/CodeMirrorEditor/editor.js',
    './src/main/webapp/js/lib/to-markdown.js',
    './src/main/webapp/js/lib/highlight.js-9.6.0/highlight.pack.js']
  gulp.src(jsJqueryUpload).
    pipe(uglify()).
    pipe(concat('admin-lib.min.js')).
    pipe(gulp.dest('./src/main/webapp/js/lib/compress/'))

  // concat js
  const jsPjax = [
    './src/main/webapp/js/lib/jquery/jquery-3.1.0.min.js',
    './src/main/webapp/js/lib/jquery/jquery.pjax.js',
    './src/main/webapp/js/lib/nprogress/nprogress.js']
  gulp.src(jsPjax).
    pipe(uglify()).
    pipe(concat('pjax.min.js')).
    pipe(gulp.dest('./src/main/webapp/js/lib/compress/'))
})

gulp.task('build', function () {
  // minify css
  gulp.src('./src/main/webapp/skins/*/css/*.css').
    pipe(rename({suffix: '.min'})).
    pipe(minifycss()).
    pipe(gulp.dest('./src/main/webapp/skins/'))

  // minify js
  gulp.src('./src/main/webapp/skins/*/js/*.js').
    pipe(rename({suffix: '.min'})).
    pipe(uglify({preserveComments: 'license'})).
    pipe(gulp.dest('./src/main/webapp/skins/'))
})

gulp.task ('clean', function () {
  // clean css
  gulp.src('./src/main/webapp/skins/*/css/*.min.css').pipe(clean({force: true}));
  // clean js
  gulp.src('./src/main/webapp/skins/*/js/*.min.js').pipe(clean({force: true}));
});


gulp.task('default',  gulpSequence('sass', 'build', 'compress'))