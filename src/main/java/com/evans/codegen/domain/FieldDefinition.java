package com.evans.codegen.domain;

import static com.evans.codegen.StringUtils.capitalise;

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
    ENUM,
    ONE_TO_MANY
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

  default boolean isOneToMany() {
    return type() == FieldType.ONE_TO_MANY;
  }

  default String nameCapitalised() {
    return capitalise(name());
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

  record StringField(String name,
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

  record OneToManyField(String name,
                        boolean required,
                        Model associationModel) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.ONE_TO_MANY;
    }
  }
}
