package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.PackageJsonGenerator.PackageJson;

public class PackageJsonGenerator implements FileGenerator<PackageJson> {

  @Override
  public String templateName() {
    return "react/package.json.mustache";
  }

  @Override
  public String outputFileName(PackageJson templateData) {
    return "package.json";
  }

  @Override
  public String outputDirectory(PackageJson templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web";
  }

  public record PackageJson(String projectName) {}
}
