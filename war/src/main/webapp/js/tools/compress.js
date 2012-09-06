var fs = require("fs"),
exec = require("child_process").exec;

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