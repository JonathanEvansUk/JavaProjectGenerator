package com.evans.codegen.file.java.entity;

import com.evans.codegen.file.java.JavaFileGenerator;

public class EntityGenerator implements JavaFileGenerator<Entity> {

  @Override
  public String templateName() {
    return "entity.mustache";
  }
}
