package com.evans.generator.domain;

import java.util.List;

public sealed interface FieldDefinition {

  String name();

  FieldType type();

  enum FieldType {
    ID,
    DOUBLE,
    DATE,
    DATE_TIME,
    BOOLEAN,
    STRING,
    ENUM
  }

  default boolean isId() {
    return false;
  }

  default boolean isBoolean() {
    return type() == FieldType.BOOLEAN;
  }

  default boolean isDate() {
    return type() == FieldType.DATE_TIME;
  }

  boolean required();


  record IdField(String name,
                 boolean required) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.ID;
    }

    @Override
    public boolean isId() {
      return true;
    }
  }

  record DoubleField(String name,
                     boolean required) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.DOUBLE;
    }
  }

  record EnumField(String name,
                   List<String> options,
                   boolean required) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.ENUM;
    }
  }

  record StringFieldDefinition(String name,
                               String simpleTypeName,
                               boolean required) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.STRING;
    }
  }

  record DateField(String name,
                   boolean required) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.DATE;
    }
  }

  record DateTimeField(String name,
                       boolean required) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.DATE_TIME;
    }
  }

  record BooleanField(String name,
                      boolean required) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.BOOLEAN;
    }
  }
}
