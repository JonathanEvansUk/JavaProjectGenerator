package com.evans.generator.file.react;

import com.evans.generator.Generator.Field;
import com.evans.generator.file.FileGenerator;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.CreateEntityGenerator.EntityForm;
import com.evans.generator.file.react.CreateEntityGenerator.JsonFormSchema.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateEntityGenerator implements FileGenerator<EntityForm> {

  @Override
  public String templateName() {
    return "react/createEntity.mustache";
  }

  @Override
  public String outputFileName(EntityForm templateData) {
    return "Create" + templateData.model().nameCapitalised() + ".js";
  }

  @Override
  public String outputDirectory(EntityForm templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes/"
        + templateData.model().name();
  }

  public record EntityForm(WebModel model) {

    public String schema() throws JsonProcessingException {

      var properties = model.fields()
          .stream()
          .collect(Collectors.toMap(
              Field::name,
              this::resolveTypes,
              (u, v) -> {
                //TODO this should probably be validated earlier?
                throw new IllegalStateException("There cannot be 2 fields with the same name");
              },
              LinkedHashMap::new
          ));

      var required = model.fields().stream()
          .filter(Field::required)
          .map(Field::name)
          .toList();

      var schema = new JsonFormSchema(
          "Create a " + model.nameCapitalised(),
          "Description",
          "object",
          properties,
          required
      );

      return new ObjectMapper().writeValueAsString(schema);
    }

    private Type resolveTypes(Field field) {
      String fieldType = resolveType(field);
      if (field.required()) {
        return new Type(fieldType);
      }

      return Type.nullableType(fieldType);
    }

    private String resolveType(Field field) {
      Class<?> type = field.type();

      Set<Class<?>> integerClasses = Set.of(Integer.class, Long.class);

      if (integerClasses.contains(type)) {
        return "integer";
      }

      if (type.equals(Double.class)) {
        return "number";
      }

      if (type.equals(String.class)) {
        return "string";
      }

      //unknown
      return "string";
    }

  }

  public record JsonFormSchema(String title,
                               String description,
                               String type,
                               Map<String, Type> properties,
                               List<String> required) {

    record Type(List<String> type) {

      Type(String type) {
        this(List.of(type));
      }

      static Type nullableType(String type) {
        return new Type(List.of(type, "null"));
      }
    }
  }
}
