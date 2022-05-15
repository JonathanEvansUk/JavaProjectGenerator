package com.evans.generator.file.java.entity;

import com.evans.generator.file.java.JavaFileGenerator;

public class EntityGenerator implements JavaFileGenerator<Entity> {

  @Override
  public String templateName() {
    return "entity.mustache";
  }
}
