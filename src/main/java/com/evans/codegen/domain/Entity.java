package com.evans.codegen.domain;

import com.evans.codegen.domain.FieldDefinition.FieldType;
import java.util.List;

//TODO find better name for this
public record Entity(String name,
                     FieldType idType,
                     FieldDefinition idField,
                     List<FieldDefinition> fields) {

  public static Entity of(String name, List<FieldDefinition> fields) {
    var idField = fields.stream()
        .filter(FieldDefinition::isId)
        .findFirst()
        .orElseThrow(() -> new RuntimeException(
            "No ID field provided for model: " + name + ", fields: " + fields));

    var idType = fields.stream()
        .filter(FieldDefinition::isId)
        .map(FieldDefinition::type)
        .findFirst()
        .orElseThrow(() -> new RuntimeException(
            "No ID field provided for model: " + name + ", fields: " + fields));

    return new Entity(name, idType, idField, fields);
  }

  public String nameCamel() {
    return name().substring(0, 1).toLowerCase() + name().substring(1);
  }
}
