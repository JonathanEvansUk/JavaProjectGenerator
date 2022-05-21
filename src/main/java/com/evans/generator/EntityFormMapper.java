package com.evans.generator;

import com.evans.generator.EntityFormMapper.JsonFormSchema.Property;
import com.evans.generator.EntityFormMapper.JsonFormSchema.Property.Type;
import com.evans.generator.domain.Field;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.CreateEntityGenerator.EntityForm;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
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

  public EntityForm createEntityForm(WebModel model, String formTitle)
      throws JsonProcessingException {
    LinkedHashMap<String, Property> properties = model.fields()
        .stream()
        .collect(Collectors.toMap(
            Field::name,
            this::createProperty,
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

  private Property createProperty(Field field) {
    Type type = resolveType(field);
    String format = resolveFormat(field);

    return new Property(type, format);
  }

  private Type resolveType(Field field) {
    String fieldType = lookupDataType(field);
    if (field.required()) {
      return new Type(fieldType);
    }

    return Type.nullableType(fieldType);
  }

  private String lookupDataType(Field field) {
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

    // unknown
    return "string";
  }

  private String resolveFormat(Field field) {
    Class<?> type = field.type();

    if (type.equals(Instant.class)) {
      return "date-time";
    }

    // TODO not sure what this should be?
    return null;
  }

  public record JsonFormSchema(String title,
                               String description,
                               String type,
                               Map<String, Property> properties,
                               List<String> required) {


    record Property(List<String> type,
                    @JsonInclude(Include.NON_NULL) String format) {

      Property(Type type, String format) {
        this(type.type(), format);
      }

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
}
