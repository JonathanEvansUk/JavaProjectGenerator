package com.evans.generator.file.java.controller;

import com.evans.generator.file.java.JavaFileGenerator;

public class ControllerGenerator implements JavaFileGenerator<Controller> {

  @Override
  public String templateName() {
    return "controller.mustache";
  }
}
