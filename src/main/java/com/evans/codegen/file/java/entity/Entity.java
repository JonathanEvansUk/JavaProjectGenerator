package com.evans.codegen.file.java.entity;

import static com.evans.codegen.StringUtils.capitalise;

import com.evans.codegen.domain.FieldDefinition.FieldType;
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
                      FieldType fieldType,
                      Relationship relationship,
                      String associationModelType,
                      String example) {

    String getterName() {
      return "get" + name().substring(0, 1).toUpperCase() + name().substring(1);
    }

    String setterName() {
      return "set" + name().substring(0, 1).toUpperCase() + name().substring(1);
    }

    boolean isOneToMany() {
      return relationship == Relationship.ONE_TO_MANY;
    }

    boolean isId() {
      return fieldType == FieldType.ID;
    }

    boolean isDouble() {
      return fieldType == FieldType.DOUBLE;
    }

    boolean isDate() {
      return fieldType == FieldType.DATE;
    }

    boolean isDateTime() {
      return fieldType == FieldType.DATE_TIME;
    }

    boolean isBoolean() {
      return fieldType == FieldType.BOOLEAN;
    }

    boolean isString() {
      return fieldType == FieldType.STRING;
    }

    boolean isEnum() {
      return fieldType == FieldType.ENUM;
    }

    boolean hasExample() {
      return example != null;
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
