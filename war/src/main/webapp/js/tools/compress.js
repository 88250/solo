var fs = require("fs"),
exec = require("child_process").exec;
// b3log js compress
exec("uglifyjs ../../js/common.js > ../../js/common.min.js", function (error, stdout, stderr) {
        if (error !== null) {
            console.log(error);
        } 	
});
exec("uglifyjs ../../js/page.js > ../../js/page.min.js", function (error, stdout, stderr) {
        if (error !== null) {
            console.log(error);
        } 	
});
// combi admin js
var adminJs = fs.readFileSync("../../js/admin/admin.js");
adminJs += fs.readFileSync("../../js/admin/editor.js");
adminJs += fs.readFileSync("../../js/admin/editorTinyMCE.js");
adminJs += fs.readFileSync("../../js/admin/editorKindEditor.js");
adminJs += fs.readFileSync("../../js/admin/editorCodeMirror.js");
adminJs += fs.readFileSync("../../js/admin/tablePaginate.js");
adminJs += fs.readFileSync("../../js/admin/article.js");
adminJs += fs.readFileSync("../../js/admin/comment.js");
adminJs += fs.readFileSync("../../js/admin/articleList.js");
adminJs += fs.readFileSync("../../js/admin/draftList.js");
adminJs += fs.readFileSync("../../js/admin/pageList.js");
adminJs += fs.readFileSync("../../js/admin/others.js");
adminJs += fs.readFileSync("../../js/admin/linkList.js");
adminJs += fs.readFileSync("../../js/admin/preference.js");
adminJs += fs.readFileSync("../../js/admin/pluginList.js");
adminJs += fs.readFileSync("../../js/admin/userList.js");
adminJs += fs.readFileSync("../../js/admin/commentList.js");
adminJs += fs.readFileSync("../../js/admin/plugin.js");
adminJs += fs.readFileSync("../../js/admin/main.js");
adminJs += fs.readFileSync("../../js/admin/about.js");
fs.writeFileSync("../../js/admin/latkeAdmin.js", adminJs, "UTF-8");
// compress admin js
exec("uglifyjs ../../js/admin/latkeAdmin.js > ../../js/admin/latkeAdmin.min.js", function (error, stdout, stderr) {
        if (error !== null) {
            console.log(error);
        } 	
});

// b3log css compress
var b3logCsses = ["default-admin", "default-base", "default-init"];
for (var i = 0; i < b3logCsses.length; i++) {
    exec("lessc -compress ../../css/" + b3logCsses[i] + ".css > ../../css/" + b3logCsses[i] + ".min.css", function (error, stdout, stderr) {
        if (error !== null) {
            console.log(error);
        } 	
    });
}

// skin js compress
var jsPaths = ["ease"];
for (var i = 0; i < jsPaths.length; i++) {
    exec("uglifyjs ../../skins/" + jsPaths[i] + "/js/" + jsPaths[i] + ".js > ../../skins/" + jsPaths[i] + "/js/" + jsPaths[i] + ".min.js", function (error, stdout, stderr) {
        if (error !== null) {
            console.log(error);
        } 	
    });
}

// skin css compress
var csses = ["ease", "ease-ie8"];
for (var i = 0; i < csses.length; i++) {
    exec("lessc -compress ../../skins/ease/css/" + csses[i] + ".css > ../../skins/ease/css/" + csses[i] + ".min.css", function (error, stdout, stderr) {
        if (error !== null) {
            console.log(error);
        } 	
    });
}