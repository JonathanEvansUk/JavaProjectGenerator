package com.evans.generator;

import com.evans.generator.EntityFormMapper.JsonFormSchema.Type;
import com.evans.generator.Generator.Field;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.CreateEntityGenerator.EntityForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityFormMapper {

  private final ObjectMapper objectMapper;

  public EntityFormMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  // TODO extract to class
  public EntityForm createEntityForm(WebModel model, String formTitle)
      throws JsonProcessingException {
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

    var formSchema = new JsonFormSchema(
        formTitle,
        "Description",
        "object",
        properties,
        required
    );

    String schema = objectMapper.writeValueAsString(formSchema);

    return new EntityForm(model, schema);
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

  public record JsonFormSchema(String title,
                               String description,
                               String type,
                               Map<String, JsonFormSchema.Type> properties,
                               List<String> required) {

    record Type(List<String> type) {

      Type(String type) {
        this(List.of(type));
      }

      static JsonFormSchema.Type nullableType(String type) {
        return new JsonFormSchema.Type(List.of(type, "null"));
      }
    }
  }
}
