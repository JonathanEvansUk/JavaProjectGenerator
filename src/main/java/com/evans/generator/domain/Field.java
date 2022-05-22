package com.evans.generator.domain;

import java.time.Instant;

public record Field(String name,
                    Class<?> type,
                    String simpleTypeName,
                    boolean isId,
                    boolean required) {

  public Field(String name, Class<?> type, boolean isId, boolean required) {
    this(name, type, type.getSimpleName(), isId, isId || required);
  }

  public String getterName() {
    return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  public String setterName() {
    return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  public boolean isDate() {
    return type.equals(Instant.class);
  }

  public boolean isBoolean() {
    return type.equals(Boolean.class);
  }
}