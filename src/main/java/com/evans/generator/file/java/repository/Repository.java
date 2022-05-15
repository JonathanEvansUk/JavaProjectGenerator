package com.evans.generator.file.java.repository;

import com.evans.generator.file.java.JavaClassTemplateData;
import java.util.List;

public record Repository(String packageName, String entityName, List<String> imports,
                         String entityType, String entityIdType) implements JavaClassTemplateData {

  @Override
  public String className() {
    return entityName + "Repository";
  }
}
