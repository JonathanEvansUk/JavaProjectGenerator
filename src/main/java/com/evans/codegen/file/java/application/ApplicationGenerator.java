package com.evans.codegen.file.java.application;

import com.evans.codegen.file.java.JavaFileGenerator;

public class ApplicationGenerator implements JavaFileGenerator<Application> {

  @Override
  public String templateName() {
    return "application.mustache";
  }
}
