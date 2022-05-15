package com.evans.generator.file.java.service;

import com.evans.generator.file.java.JavaFileGenerator;

public class ServiceGenerator implements JavaFileGenerator<Service> {

  @Override
  public String templateName() {
    return "service.mustache";
  }
}
