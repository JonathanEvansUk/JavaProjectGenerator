package com.evans.codegen.file.maven;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.maven.TestApplicationMysqlPropertiesGenerator.TestApplicationMysqlProperties;

// Testcontainers specific application properties with mysql profile
public class TestApplicationMysqlPropertiesGenerator
    implements FileGenerator<TestApplicationMysqlProperties> {

  @Override
  public String templateName() {
    return "maven/application-mysql.properties.mustache";
  }

  @Override
  public String outputFileName(TestApplicationMysqlProperties templateData) {
    return "application-mysql.properties";
  }

  @Override
  public String outputDirectory(TestApplicationMysqlProperties templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "/src/test/resources";
  }

  public record TestApplicationMysqlProperties(String appName) {}
}
