package com.evans.codegen.file.java.repository;

import com.evans.codegen.domain.Entity;
import com.evans.codegen.file.java.JavaClassTemplateData;
import java.util.List;

public record Repository(String packageName, String entityName, List<String> imports,
                         String entityType, String entityIdType,
                         List<Entity> manyToOneSideEntities) implements JavaClassTemplateData {

  @Override
  public String className() {
    return entityName + "Repository";
  }
}
