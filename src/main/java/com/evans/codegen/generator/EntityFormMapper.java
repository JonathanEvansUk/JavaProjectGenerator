package com.evans.codegen.generator;

import com.evans.codegen.domain.FieldDefinition.FieldType;
import com.evans.codegen.file.react.AppJsGenerator.WebField;
import com.evans.codegen.file.react.AppJsGenerator.WebModel;
import com.evans.codegen.file.react.CreateEntityGenerator.EntityForm;
import com.evans.codegen.generator.EntityFormMapper.JsonFormSchema.Property;
import com.evans.codegen.generator.EntityFormMapper.JsonFormSchema.Property.Items;
import com.evans.codegen.generator.EntityFormMapper.JsonFormSchema.Property.Type;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EntityFormMapper {

  private final ObjectMapper objectMapper;

  public EntityForm createEntityForm(WebModel model, String formTitle)
      throws JsonProcessingException {
    LinkedHashMap<String, Property> properties = model.fields().stream()
        // remove ID fields as we want this to be auto-generated and hidden from the user
        .filter(field -> field.type() != FieldType.ID)
        .collect(Collectors.toMap(WebField::name, this::createProperty, (u, v) -> {
          //TODO this should probably be validated earlier?
          throw new IllegalStateException("There cannot be 2 fields with the same name");
        }, LinkedHashMap::new));

    var required = model.fields().stream()
        // remove ID fields as we want this to be auto-generated and hidden from the user
        .filter(field -> field.type() != FieldType.ID)
        .filter(WebField::required)
        .map(WebField::name).toList();

    var formSchema = new JsonFormSchema(formTitle, "Description", "object", properties, required);

    String schema = objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        .writeValueAsString(formSchema);

    return new EntityForm(model, schema);
  }

  private Property createProperty(WebField field) {
    Type type = resolveType(field);
    String format = resolveFormat(field);

    if (field.type() == FieldType.ENUM) {
      //EnumField enumField = (EnumField) field;
      return new Property(type, format, field.enumOptions());
    }

    if (field.type() == FieldType.ONE_TO_MANY) {
      return new Property(type, format, new Items("integer"), List.of());
    }

    return new Property(type, format);
  }

  private Type resolveType(WebField field) {
    String fieldType = lookupDataType(field);
    if (field.required() || field.type() == FieldType.ONE_TO_MANY) {
      return new Type(fieldType);
    }

    return Type.nullableType(fieldType);
  }

  private String lookupDataType(WebField field) {
    return switch (field.type()) {
      case ID, MANY_TO_ONE -> "integer";
      case DATE, DATE_TIME, STRING, ENUM, JSON -> "string";
      case DOUBLE -> "number";
      case BOOLEAN -> "boolean";
      case ONE_TO_MANY -> "array";
    };
  }

  private String resolveFormat(WebField field) {
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
                    @JsonInclude(Include.NON_NULL) @JsonProperty("enum") List<String> enumOptions,
                    @JsonInclude(Include.NON_NULL) Items items,
                    @JsonInclude(Include.NON_NULL) @JsonProperty("default") Object defaultValue) {

      Property(Type type, String format) {
        this(type.type(), format, null, null, null);
      }

      Property(Type type, String format, List<String> options) {
        this(type.type(), format, options, null, null);
      }

      Property(Type type, String format, Items items, Object defaultValue) {
        this(type.type(), format, null, items, defaultValue);
      }



      record Type(List<String> type) {

        Type(String type) {
          this(List.of(type));
        }

        static Type nullableType(String type) {
          return new Type(List.of(type, "null"));
        }
      }

      record Items(String type) {

      }
    }
  }
}
