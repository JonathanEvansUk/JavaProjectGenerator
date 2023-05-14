package com.evans.codegen.file.java.controller;

import com.evans.codegen.domain.Model;
import com.evans.codegen.file.java.JavaClassTemplateData;
import java.util.List;

public record Controller(String packageName,
                         String groupId,
                         String className,
                         String entityNameCamel,
                         String entityIdType,
                         String serviceType,
                         String serviceName,
                         String dtoType,
                         List<String> imports,
                         List<Model> manyToOneSideModels) implements JavaClassTemplateData {
  String entityName() {
    return entityNameCamel().substring(0, 1).toUpperCase() + entityNameCamel().substring(1);
  }
}
