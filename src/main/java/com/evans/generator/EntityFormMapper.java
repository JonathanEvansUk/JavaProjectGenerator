package com.evans.generator;

import com.evans.generator.EntityFormMapper.JsonFormSchema.Property;
import com.evans.generator.EntityFormMapper.JsonFormSchema.Property.Type;
import com.evans.generator.domain.FieldDefinition;
import com.evans.generator.domain.FieldDefinition.EnumField;
import com.evans.generator.domain.FieldDefinition.FieldType;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.CreateEntityGenerator.EntityForm;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class EntityFormMapper {

  private final ObjectMapper objectMapper;

  @Inject
  public EntityFormMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public EntityForm createEntityForm(WebModel model, String formTitle)
      throws JsonProcessingException {
    LinkedHashMap<String, Property> properties = model.fields().stream()
        .collect(Collectors.toMap(FieldDefinition::name, this::createProperty, (u, v) -> {
          //TODO this should probably be validated earlier?
          throw new IllegalStateException("There cannot be 2 fields with the same name");
        }, LinkedHashMap::new));

    var required = model.fields().stream().filter(FieldDefinition::required)
        .map(FieldDefinition::name).toList();

    var formSchema = new JsonFormSchema(formTitle, "Description", "object", properties, required);

    String schema = objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        .writeValueAsString(formSchema);

    return new EntityForm(model, schema);
  }

  private Property createProperty(FieldDefinition field) {
    Type type = resolveType(field);
    String format = resolveFormat(field);

    if (field.type() == FieldType.ENUM) {
      EnumField enumField = (EnumField) field;
      return new Property(type, format, enumField.options());
    }

    return new Property(type, format);
  }

  private Type resolveType(FieldDefinition field) {
    String fieldType = lookupDataType(field);
    if (field.required()) {
      return new Type(fieldType);
    }

    return Type.nullableType(fieldType);
  }

  private String lookupDataType(FieldDefinition field) {
    return switch (field.type()) {

      case ID -> "integer";
      case DATE, DATE_TIME, STRING, ENUM -> "string";
      case DOUBLE -> "number";
      case BOOLEAN -> "boolean";
    };
  }

  private String resolveFormat(FieldDefinition field) {
    return switch (field.type()) {
      case DATE -> "date";
      case DATE_TIME -> "date-time";

      // TODO remove this null
      default -> null;
    };
  }

  public record JsonFormSchema(String title,
                               String description,
                               String type,
                               Map<String, Property> properties,
                               List<String> required) {


    record Property(List<String> type,
                    @JsonInclude(Include.NON_NULL) String format,
                    @JsonInclude(Include.NON_NULL) @JsonProperty("enum") List<String> enumOptions) {

      Property(Type type, String format) {
        this(type.type(), format, null);
      }

      Property(Type type, String format, List<String> options) {
        this(type.type(), format, options);
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
