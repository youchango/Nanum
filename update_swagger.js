const fs = require('fs');
const path = require('path');

const baseDir = 'e:/GitHub/Nanum/src/main/java/com/nanum';

function walkDir(dir) {
    let files = fs.readdirSync(dir);
    for (let f of files) {
        let dirPath = path.join(dir, f);
        if (fs.statSync(dirPath).isDirectory()) {
            walkDir(dirPath);
        } else {
            if (dirPath.endsWith('Controller.java')) {
                let content = fs.readFileSync(dirPath, 'utf8');
                if (content.includes('@RestController') && !content.includes('@Tag')) {
                    const fileName = path.basename(dirPath, '.java');
                    const tagName = fileName.replace('Controller', '');

                    const importStmt = 'import io.swagger.v3.oas.annotations.tags.Tag;\n';
                    if (!content.includes(importStmt.trim())) {
                        const lastImportIdx = content.lastIndexOf('import ');
                        if (lastImportIdx !== -1) {
                            const endOfLine = content.indexOf('\n', lastImportIdx);
                            content = content.slice(0, endOfLine + 1) + importStmt + content.slice(endOfLine + 1);
                        }
                    }

                    const replacement = '@Tag(name = "' + tagName + '", description = "' + tagName + ' API")\n@RestController';
                    content = content.replace(/@RestController\b/, replacement);

                    fs.writeFileSync(dirPath, content, 'utf8');
                    console.log('Updated ' + fileName);
                }
            }
        }
    }
}

walkDir(baseDir);
console.log('Done');
