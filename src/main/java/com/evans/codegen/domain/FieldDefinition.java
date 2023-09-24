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
    ONE_TO_MANY,
    MANY_TO_ONE,
    JSON
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

  default boolean isManyToOne() {
    return type() == FieldType.MANY_TO_ONE;
  }

  default String nameCapitalised() {
    return capitalise(name());
  }

  boolean required();

  String example();

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

    @Override
    public String example() {
      return "1";
    }
  }

  record DoubleField(String name,
                     boolean required,
                     String example) implements FieldDefinition {

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

    @Override
    public String example() {
      // TODO handle case where options is empty
      return options.get(0);
    }
  }

  record StringField(String name,
                     boolean required,
                     String example) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.STRING;
    }
  }

  record DateField(String name,
                   boolean required,
                   String example) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.DATE;
    }
  }

  record DateTimeField(String name,
                       boolean required,
                       String example) implements FieldDefinition {

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

    @Override
    public String example() {
      return Boolean.TRUE.toString();
    }
  }

  record JsonField(String name,
                   boolean required) implements FieldDefinition {

    @Override
    public FieldType type() {
      return FieldType.JSON;
    }

    @Override
    public String example() {
      return """
          {
            "test": "json"
          }
          """;
    }
  }

  sealed interface RelationalField extends FieldDefinition {

    Entity associationEntity();

    record OneToManyField(String name,
                          boolean required,
                          Entity associationEntity) implements RelationalField {

      @Override
      public FieldType type() {
        return FieldType.ONE_TO_MANY;
      }

      //TODO better way to do this?
      @Override
      public String example() {
        return null;
      }
    }

    record ManyToOneField(String name,
                          boolean required,
                          Entity associationEntity) implements RelationalField {

      @Override
      public FieldType type() {
        return FieldType.MANY_TO_ONE;
      }

      @Override
      public String example() {
        return null;
      }
    }
  }


}
