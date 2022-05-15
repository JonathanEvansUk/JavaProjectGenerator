package com.evans.generator.file.java.application;

import com.evans.generator.file.java.JavaFileGenerator;

public class ApplicationGenerator implements JavaFileGenerator<Application> {

  @Override
  public String templateName() {
    return "application.mustache";
  }
}
