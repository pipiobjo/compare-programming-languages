const fs = require('fs');
const glob = require('glob');
const path = require("path");



glob('./dist/reports/**/*.json', (err, files) => {
    if (err) {
        return console.error(err)
    }

    // Print all files
    console.log(files)
    // ['api/http.ts', 'api/routes.ts', 'api/models/user.ts']

    // Iterate over all files
    let resultJSON = {};
    files.forEach(file => {
        console.log(file)
        const parentPath = path.dirname(file);
        const dirName = path.basename(parentPath);
        console.log("dirName", dirName);
        if(!resultJSON[dirName]){
            resultJSON[dirName]={};
        }
        let basename = path.basename(file, '.json');
        console.log("basename", basename)
        const relativePath = path.relative("./dist", file);
        console.log("relativePath=", relativePath);
        resultJSON[dirName][basename] = relativePath;
    })


    const data = JSON.stringify(resultJSON)

// write file to disk
    fs.writeFile('./src/loadtest-results.json', data, 'utf8', err => {
        if (err) {
            console.log(`Error writing file: ${err}`)
        } else {
            console.log(`File is written successfully!`)
        }
    })

})