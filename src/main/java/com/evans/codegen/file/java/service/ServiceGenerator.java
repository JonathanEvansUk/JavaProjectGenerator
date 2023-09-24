package com.evans.codegen.file.java.service;

import com.evans.codegen.file.java.JavaFileGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ServiceGenerator implements JavaFileGenerator<Service> {

  @Override
  public String templateName() {
    return "java/main/service.mustache";
  }
}
