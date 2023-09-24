package com.evans.codegen.file.java.entity;

import static com.evans.codegen.StringUtils.capitalise;

import com.evans.codegen.domain.FieldDefinition.FieldType;
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
                     List<com.evans.codegen.domain.Entity> manyToOneSideEntities) implements JavaClassTemplateData {

  Set<Relationship> fieldRelationships() {
    return fields.stream().map(Field::relationship).collect(Collectors.toSet());
  }

  String entityNameCamel() {
    return className().substring(0, 1).toLowerCase() + className().substring(1);
  }

  boolean hasOneToMany() {
    return fieldRelationships().contains(Relationship.ONE_TO_MANY);
  }

  boolean hasManyToOne() {
    return fieldRelationships().contains(Relationship.MANY_TO_ONE);
  }

  boolean hasManyToOneSide() { return !manyToOneSideEntities().isEmpty(); }

  boolean hasManyToOneAnnotation() {
    return hasManyToOne() || hasManyToOneSide();
  }

  boolean hasEnumFields() {
    return !enums.isEmpty();
  }

  boolean hasJsonFields() {
    return fields.stream().anyMatch(Field::isJson);
  }

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

    boolean isManyToOne() {
      return relationship == Relationship.MANY_TO_ONE;
    }

    public boolean isRelationalField() {
      // TODO find better way
      return relationship != null;
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

    boolean isJson() { return fieldType == FieldType.JSON; }

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
