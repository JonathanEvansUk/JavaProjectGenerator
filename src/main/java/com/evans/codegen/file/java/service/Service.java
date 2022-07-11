package com.evans.codegen.file.java.service;

import com.evans.codegen.domain.Model;
import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.file.java.entity.Entity.Field;
import com.evans.codegen.generator.BackendGenerator.Relationship;
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
                      List<Field> fields,
                      List<String> imports,
                      List<Model> manyToOneSideModels) implements JavaClassTemplateData {

  @Override
  public String className() {
    return entityType + "Service";
  }

  boolean hasOneToManyFields() {
    return fields.stream().map(Field::relationship)
        .anyMatch(relationship -> relationship == Relationship.ONE_TO_MANY);
  }

  List<Field> oneToManyFields() {
    return fields.stream()
        .filter(field -> field.relationship() == Relationship.ONE_TO_MANY)
        .toList();
  }
}
