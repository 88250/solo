var fs = require('fs'),
path = require('path');
 
var getAllFiles = function (root){
    var res = [],
    files = fs.readdirSync(root);
    
    files.forEach(function(file){
        var pathname = root+'/'+file,
        stat = fs.lstatSync(pathname);
        
        if (!stat.isDirectory()){
            if (file.indexOf(".ftl") < 0 && file.indexOf(".properties") < 0 && root.indexOf("js/tools") < 0) {
                res.push(pathname);
            }
        } else {
            res = res.concat(getAllFiles(pathname));
        }
    });
    return res;
};

var mkdirsSync = function(dirpath) {
    if(fs.existsSync(dirpath)){
        return;
    }
    var dirs = dirpath.split('/');
    var dir = '';
    for(var i = 0; i < dirs.length; i++) {
        dir += dirs[i] + '/';
        if(!fs.existsSync(dir)){
            fs.mkdirSync(dir);
        }
    }
};

var css = getAllFiles("../../css"),
image = getAllFiles("../../images"),
js = getAllFiles("../../js"),
plugin = getAllFiles("../../plugins"),
skins = getAllFiles("../../skins");
(function () {    
    mkdirsSync("static/css");
    
    for (var i = 0; i < css.length; i++) {
        if (css[i].indexOf(".min.css") > -1) {
            fs.writeFileSync(css[i].replace("../..", "static"), fs.readFileSync(css[i]), "UTF-8");
        }
    }
    
    for (var i = 0; i < image.length; i++) {
        mkdirsSync(path.dirname(image[i].replace("../..", "static")));
        fs.writeFileSync(image[i].replace("../..", "static"), fs.readFileSync(image[i]), "UTF-8");
    }
    
    for (var i = 0; i < js.length; i++) {
        var pathdir = path.dirname(js[i].replace("../..", "static"));
        if (pathdir.indexOf("/js/tools") < 0) {
            mkdirsSync(pathdir);
        }
        
        if ((pathdir.indexOf("/js/admin") > -1 && path.basename(js[i]) === "latkeAdmin.min.js")
            || pathdir.indexOf("/js/lib") > -1 
            || (pathdir.split("/").length == 2 && js[i].indexOf(".min.js") > -1)) {
            fs.writeFileSync(js[i].replace("../..", "static"), fs.readFileSync(js[i]), "UTF-8");
        }
    }
    
    for (var i = 0; i < plugin.length; i++) {
        mkdirsSync(path.dirname(plugin[i].replace("../..", "static")));
        fs.writeFileSync(plugin[i].replace("../..", "static"), fs.readFileSync(plugin[i]), "UTF-8");
    }
    
    for (var i = 0; i < skins.length; i++) {
        if (path.basename(skins[i]) === "ease-ie8.css" || path.basename(skins[i]) === "ease.css" 
            || path.basename(skins[i]) === "ease.js") {
            
        } else {
            mkdirsSync(path.dirname(skins[i].replace("../..", "static")));
            fs.writeFileSync(skins[i].replace("../..", "static"), fs.readFileSync(skins[i]), "UTF-8");
        }
    }
    
    fs.writeFileSync("static/favicon.ico", fs.readFileSync("../../favicon.ico"), "UTF-8");
    fs.writeFileSync("static/favicon.png", fs.readFileSync("../../favicon.png"), "UTF-8");
})();