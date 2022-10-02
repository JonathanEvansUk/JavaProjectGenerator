package com.evans.codegen.file.java.repository;

import com.evans.codegen.file.java.JavaFileGenerator;

public class RepositoryGenerator implements JavaFileGenerator<Repository> {

  @Override
  public String templateName() {
    return "java/main/repository.mustache";
  }
}
