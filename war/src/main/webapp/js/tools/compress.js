var fs = require("fs"),
exec = require("child_process").exec;
// js compress
var jsPaths = ["ease"];
for (var i = 0; i < jsPaths.length; i++) {
    var content = "";
    exec("uglifyjs ../../skins/" + jsPaths[i] + "/js/" + jsPaths[i] + ".js > ../../skins/" + jsPaths[i] + "/js/" + jsPaths[i] + ".min.js", function (error, stdout, stderr) {
        if (error !== null) {
            console.log(error);
        } 	
    });
}

// css compress
var csses = ["ease", "ease-ie8"];

for (var i = 0; i < csses.length; i++) {
    var content = "";
    exec("lessc -compress ../../skins/ease/css/" + csses[i] + ".css > ../../skins/ease/css/" + csses[i] + ".min.css", function (error, stdout, stderr) {
        if (error !== null) {
            console.log(error);
        } 	
    });
}