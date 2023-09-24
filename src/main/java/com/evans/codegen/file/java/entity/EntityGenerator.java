package com.evans.codegen.file.java.entity;

import com.evans.codegen.file.java.JavaFileGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EntityGenerator implements JavaFileGenerator<DataEntity> {

  @Override
  public String templateName() {
    return "java/main/entity.mustache";
  }
}
