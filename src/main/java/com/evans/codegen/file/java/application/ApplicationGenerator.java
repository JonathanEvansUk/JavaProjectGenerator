package com.evans.codegen.file.java.application;

import com.evans.codegen.file.java.JavaFileGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ApplicationGenerator implements JavaFileGenerator<Application> {

  @Override
  public String templateName() {
    return "java/main/application.mustache";
  }
}
