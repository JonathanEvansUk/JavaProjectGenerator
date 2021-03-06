package com.evans.codegen.file.maven;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.maven.ApplicationPropertiesGenerator.ApplicationProperties;

public class ApplicationPropertiesGenerator implements FileGenerator<ApplicationProperties> {

  @Override
  public String templateName() {
    return "maven/application.properties.mustache";
  }

  @Override
  public String outputFileName(ApplicationProperties templateData) {
    return "application.properties";
  }

  @Override
  public String outputDirectory(ApplicationProperties templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "/src/main/resources";
  }

  public record ApplicationProperties(String appName) {}
}
