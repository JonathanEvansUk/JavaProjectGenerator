package com.evans.codegen.file.java.controller;

import com.evans.codegen.file.java.JavaClassTemplateData;
import java.util.List;

public record Controller(String packageName,
                         String entityType,
                         String entityNameCamel,
                         String entityIdType,
                         String serviceType,
                         String serviceName,
                         List<String> imports) implements
    JavaClassTemplateData {

  @Override
  public String className() {
    return entityType + "Controller";
  }
}
