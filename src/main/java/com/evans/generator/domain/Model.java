package com.evans.generator.domain;

import com.evans.generator.domain.FieldDefinition.FieldType;
import java.util.List;

//TODO find better name for this
public record Model(String name,
                    FieldType idType,
                    FieldDefinition idField,
                    List<FieldDefinition> fields) {

  public static Model of(String name, List<FieldDefinition> fields) {
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

    return new Model(name, idType, idField, fields);
  }
}
