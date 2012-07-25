var version = "",
newVersion = "";

process.argv.forEach(function (val, index, array) {  
    if (index === 2) {
        version = val;
    }

    if (index === 3) {
        newVersion = val;
    }
});  

var fs = require("fs");
var skins = ["ease", "mobile"];

for (var i = 0; i < skins.length; i++) {
    var fileName = "../../skins/" + skins[i] + "/skin.properties";

    var file = fs.readFileSync(fileName, "UTF-8");
    fs.writeFileSync(fileName, file.replace("forSolo=" + version, "forSolo=" + newVersion), "UTF-8");
}