package com.evans.generator.file.java.repository;

import com.evans.generator.file.java.JavaFileGenerator;

public class RepositoryGenerator implements JavaFileGenerator<Repository> {

  @Override
  public String templateName() {
    return "repository.mustache";
  }
}
