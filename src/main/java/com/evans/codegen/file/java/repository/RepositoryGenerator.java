package com.evans.codegen.file.java.repository;

import com.evans.codegen.file.java.JavaFileGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)

public class RepositoryGenerator implements JavaFileGenerator<Repository> {

  @Override
  public String templateName() {
    return "java/main/repository.mustache";
  }
}
