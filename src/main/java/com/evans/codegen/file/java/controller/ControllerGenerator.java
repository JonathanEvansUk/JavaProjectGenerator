package com.evans.codegen.file.java.controller;

import com.evans.codegen.file.java.JavaFileGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ControllerGenerator implements JavaFileGenerator<Controller> {

  @Override
  public String templateName() {
    return "java/main/controller.mustache";
  }
}
