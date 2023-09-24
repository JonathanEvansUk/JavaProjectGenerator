package com.evans.codegen.file.java.service;

import com.evans.codegen.domain.Entity;
import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.file.java.entity.DataEntity.Field;
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
                      List<Entity> manyToOneSideEntities) implements JavaClassTemplateData {

  @Override
  public String className() {
    return entityType + "Service";
  }

  boolean hasOneToManyFields() {
    return fields.stream().map(Field::relationship)
        .anyMatch(relationship -> relationship == Relationship.ONE_TO_MANY);
  }

  boolean hasRelationalFields() {
    return fields.stream().anyMatch(Field::isRelationalField);
  }

  List<Field> oneToManyFields() {
    return fields.stream()
        .filter(field -> field.relationship() == Relationship.ONE_TO_MANY)
        .toList();
  }

  List<Field> manyToOneFields() {
    return fields.stream()
        .filter(field -> field.relationship() == Relationship.MANY_TO_ONE)
        .toList();
  }

  List<Field> relationalFields() {
    return fields.stream().filter(Field::isRelationalField).toList();
  }
}
