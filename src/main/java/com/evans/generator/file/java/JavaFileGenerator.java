package com.evans.generator.file.java;

import com.evans.generator.file.FileGenerator;

public interface JavaFileGenerator<T extends JavaClassTemplateData> extends FileGenerator<T> {

  @Override
  default String outputDirectory(T templateData) {
    String packageAsPath = templateData.packageName().replace(".", "/");
    String outputDirectory = "output/" + "src/main/java/" + packageAsPath;

    return outputDirectory;
  }

  @Override
  default String outputFileName(T templateData) {
    return outputFileName(templateData.className());
  }

  default String outputFileName(String className) {
    return className + ".java";
  }
}
