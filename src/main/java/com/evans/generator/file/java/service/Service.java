package com.evans.generator.file.java.service;

import com.evans.generator.file.java.JavaClassTemplateData;
import java.util.List;

public record Service(String packageName,
                      String entityType,
                      String entityNameCamel,
                      String entityIdType,
                      String repositoryType,
                      String repositoryName,
                      List<String> imports) implements JavaClassTemplateData {

  @Override
  public String className() {
    return entityType + "Service";
  }
}
