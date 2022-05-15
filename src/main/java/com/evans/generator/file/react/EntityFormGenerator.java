package com.evans.generator.file.react;

import com.evans.generator.Generator.Field;
import com.evans.generator.file.FileGenerator;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.EntityFormGenerator.EntityForm;
import com.evans.generator.file.react.EntityFormGenerator.JsonFormSchema.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityFormGenerator implements FileGenerator<EntityForm> {

  @Override
  public String templateName() {
    return "react/entityForm.mustache";
  }

  @Override
  public String outputFileName(EntityForm templateData) {
    return templateData.model().name().toLowerCase() + ".js";
  }

  @Override
  public String outputDirectory(EntityForm templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes";
  }

  public record EntityForm(WebModel model) {

    public String schema() throws JsonProcessingException {

      var properties = model.fields()
          .stream()
          .collect(Collectors.toMap(
              Field::name,
              this::resolveType,
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

    private Type resolveType(Field field) {
      Class<?> type = field.type();

      Set<Class<?>> integerClasses = Set.of(Integer.class, Long.class);

      if (integerClasses.contains(type)) {
        return new Type("integer");
      }

      if (type.equals(Double.class)) {
        return new Type("number");
      }

      if (type.equals(String.class)) {
        return new Type("string");
      }

      //unknown
      return new Type("string");
    }

  }

  public record JsonFormSchema(String title,
                               String description,
                               String type,
                               Map<String, Type> properties,
                               List<String> required) {

    record Type(String type) {}

  }
}
