package com.evans.generator.domain;

import java.util.List;

//TODO find better name for this
public record Model(String name,
                    Class<?> idType,
                    List<Field> fields) {

  public static Model of(String name, List<Field> fields) {
    Class<?> idType = fields.stream()
        .filter(Field::isId)
        .findFirst()
        .map(Field::type)
        .orElseThrow(() -> new RuntimeException(
            "No ID field provided for model: " + name + ", fields: " + fields));

    return new Model(name, idType, fields);
  }
}
