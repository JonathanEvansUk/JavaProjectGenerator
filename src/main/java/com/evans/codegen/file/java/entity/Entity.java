package com.evans.codegen.file.java.entity;

import static com.evans.codegen.StringUtils.capitalise;

import com.evans.codegen.domain.Model;
import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.generator.BackendGenerator.Relationship;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record Entity(String packageName,
                     String className,
                     List<String> imports,
                     List<Field> fields,
                     List<Enum> enums,
                     List<Model> manyToOneSideModels) implements JavaClassTemplateData {

  Set<Relationship> fieldRelationships() {
    return fields.stream().map(Field::relationship).collect(Collectors.toSet());
  }

  String entityNameCamel() {
    return className().substring(0, 1).toLowerCase() + className().substring(1);
  }

  boolean hasOneToMany() {
    return fieldRelationships().contains(Relationship.ONE_TO_MANY);
  }

  boolean hasManyToOne() { return !manyToOneSideModels().isEmpty(); }

  public record Field(String name,
                      String simpleTypeName,
                      boolean isId,
                      boolean isDate,
                      boolean isBoolean,
                      Relationship relationship,
                      String associationModelType) {

    public Field(String name, String simpleTypeName, boolean isId, boolean isDate,
        boolean isBoolean, Relationship relationship) {
      this(name, simpleTypeName, isId, isDate, isBoolean, relationship, null);
    }

    String getterName() {
      return "get" + name().substring(0, 1).toUpperCase() + name().substring(1);
    }

    String setterName() {
      return "set" + name().substring(0, 1).toUpperCase() + name().substring(1);
    }

    boolean isOneToMany() {
      return relationship == Relationship.ONE_TO_MANY;
    }

    String associationModelName() {
      return associationModelType().substring(0, 1).toLowerCase()
          + associationModelType().substring(1);
    }

    String nameCapitalised() {
      return capitalise(name);
    }
  }

  public record Enum(String name,
                     List<Option> options) {

    public record Option(String name,
                         String value) {}
  }
}
