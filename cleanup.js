const fs = require('fs');
const path = require('path');

const commonStarImports = {
  'jakarta.persistence.*': [
    'Entity', 'Table', 'Id', 'GeneratedValue', 'GenerationType', 'Column', 'ManyToOne', 'OneToMany', 'JoinColumn', 'CascadeType', 'FetchType', 'ManyToMany', 'JoinTable', 'OneToOne', 'PrePersist', 'PreUpdate', 'EmbeddedId', 'MapsId', 'Enumerated', 'EnumType'
  ],
  'jakarta.validation.constraints.*': [
    'NotBlank', 'NotNull', 'Size', 'Email', 'Min', 'Max', 'NotEmpty', 'Positive', 'PositiveOrZero'
  ],
  'org.springframework.web.bind.annotation.*': [
    'RestController', 'RequestMapping', 'GetMapping', 'PostMapping', 'PutMapping', 'DeleteMapping', 'PatchMapping', 'PathVariable', 'RequestBody', 'RequestParam', 'ResponseStatus', 'CrossOrigin'
  ],
  'lombok.*': [
    'Getter', 'Setter', 'NoArgsConstructor', 'AllArgsConstructor', 'Builder', 'Data', 'RequiredArgsConstructor', 'ToString', 'EqualsAndHashCode', 'Slf4j'
  ],
  'com.arrupeteca.persistence.repository.*': [
    'AutorRepository', 'CategoriaRepository', 'EdificioRepository', 'EditorialRepository', 'EjemplarLibroRepository', 'FormatoRepository', 'GeneroRepository', 'IdiomaRepository', 'LibroRepository', 'MuebleRepository', 'NacionalidadRepository', 'ObraRepository', 'PaisRepository', 'PlantaRepository', 'SalonRepository', 'TipoAutoriaRepository', 'ObraAutorRepository'
  ],
  'com.arrupeteca.persistence.entity.*': [
    'Autor', 'Categoria', 'Edificio', 'Editorial', 'EjemplarLibro', 'Formato', 'Genero', 'Idioma', 'Libro', 'Mueble', 'Nacionalidad', 'Obra', 'Pais', 'Planta', 'Salon', 'TipoAutoria', 'ObraAutor'
  ]
};

function processFile(filePath) {
  let content = fs.readFileSync(filePath, 'utf8');

  // 1. Tabs to Spaces
  content = content.replace(/\t/g, '    ');

  // 2. Extract package and imports
  const lines = content.split(/\r?\n/);
  const packageMatch = content.match(/package\s+([\w\.]+);/);
  const packageName = packageMatch ? packageMatch[1] : '';

  let imports = [];
  let otherLines = [];
  let reachedImports = false;
  let passedImports = false;

  for (let line of lines) {
    if (line.startsWith('import ')) {
      imports.push(line.replace('import ', '').replace(';', '').trim());
      reachedImports = true;
    } else if (line.startsWith('package ')) {
      // ignore
    } else {
      if (reachedImports && line.trim() === '' && !passedImports) {
        // skip blank lines between imports
      } else {
        if (reachedImports) passedImports = true;
        otherLines.push(line);
      }
    }
  }

  const fileBody = otherLines.join('\n');

  // 3. Expand and Filter Imports
  let expandedImports = new Set();
  imports.forEach(imp => {
    if (imp.endsWith('.*')) {
      if (commonStarImports[imp]) {
        commonStarImports[imp].forEach(sub => {
          if (fileBody.match(new RegExp('\\b' + sub + '\\b'))) {
            expandedImports.add(imp.replace('*', sub));
          }
        });
      } else {
        expandedImports.add(imp);
      }
    } else {
      const className = imp.split('.').pop();
      if (fileBody.match(new RegExp('\\b' + className + '\\b')) || imp.includes('static ')) {
        expandedImports.add(imp);
      }
    }
  });

  // 4. Sort Imports
  const sortedImports = Array.from(expandedImports).sort();
  const groups = { java: [], javax: [], external: [], project: [] };
  sortedImports.forEach(imp => {
    if (imp.startsWith('java.')) groups.java.push(imp);
    else if (imp.startsWith('javax.') || imp.startsWith('jakarta.')) groups.javax.push(imp);
    else if (imp.startsWith('com.arrupeteca.')) groups.project.push(imp);
    else groups.external.push(imp);
  });

  let importSection = '';
  if (groups.java.length > 0) importSection += groups.java.map(i => `import ${i};`).join('\n') + '\n\n';
  if (groups.javax.length > 0) importSection += groups.javax.map(i => `import ${i};`).join('\n') + '\n\n';
  if (groups.external.length > 0) importSection += groups.external.map(i => `import ${i};`).join('\n') + '\n\n';
  if (groups.project.length > 0) importSection += groups.project.map(i => `import ${i};`).join('\n') + '\n\n';

  let newBody = otherLines.join('\n').trim();

  // 5. OperatorWrap (ESCAPED correctly now)
  const ops = ['&&', '\\|\\|', '\\+', '-'];
  ops.forEach(op => {
    const regex = new RegExp(`\\s*${op}\\s*\\n\\s*`, 'g');
    const literalOp = op.replace(/\\/g, '');
    newBody = newBody.replace(regex, `\n        ${literalOp} `);
  });

  // 6. WhitespaceAround
  newBody = newBody.replace(/if\(/g, 'if (')
                   .replace(/for\(/g, 'for (')
                   .replace(/while\(/g, 'while (')
                   .replace(/switch\(/g, 'switch (')
                   .replace(/catch\(/g, 'catch (')
                   .replace(/}\s*else/g, '} else')
                   .replace(/else\s*{/g, 'else {');

  // 7. LineLength
  const bodyLines = newBody.split('\n');
  const fixedBodyLines = bodyLines.map(line => {
    if (line.length > 120 && line.includes('(') && line.includes(',')) {
       return line.replace(/,\s+/g, ',\n            ');
    }
    return line;
  });

  const finalContent = `package ${packageName};\n\n${importSection.trim()}\n\n${fixedBodyLines.join('\n')}\n`;
  fs.writeFileSync(filePath, finalContent);
}

function getAllFiles(dir, allFiles) {
  const files = fs.readdirSync(dir);
  allFiles = allFiles || [];
  files.forEach(file => {
    const name = path.join(dir, file);
    if (fs.statSync(name).isDirectory()) {
      allFiles = getAllFiles(name, allFiles);
    } else if (name.endsWith('.java')) {
      allFiles.push(name);
    }
  });
  return allFiles;
}

const target = process.argv[2];
if (target) {
    processFile(target);
} else {
    const rootDir = 'arrupeteca/src/main/java';
    const allFiles = getAllFiles(rootDir);
    allFiles.forEach(file => {
      console.log(`Processing ${file}...`);
      try {
        processFile(file);
      } catch (e) {
        console.error(`Error processing ${file}: ${e.message}`);
      }
    });
}
