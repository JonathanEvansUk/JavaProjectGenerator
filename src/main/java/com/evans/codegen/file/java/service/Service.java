package com.evans.codegen.file.java.service;

import com.evans.codegen.file.java.JavaClassTemplateData;
import java.util.List;

public record Service(String packageName,
                      String entityType,
                      String entityNameCamel,
                      String entityIdType,
                      String dtoType,
                      String dtoNameCamel,
                      String dtoConverterType,
                      String dtoConverterName,
                      String repositoryType,
                      String repositoryName,
                      List<String> imports) implements JavaClassTemplateData {

  @Override
  public String className() {
    return entityType + "Service";
  }
}
