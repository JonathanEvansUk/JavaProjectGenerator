package com.evans.codegen.file.react;

import static com.evans.codegen.StringUtils.capitalise;

import com.evans.codegen.domain.FieldDefinition.FieldType;
import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.AppJsGenerator.AppJs;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AppJsGenerator implements FileGenerator<AppJs> {

  @Override
  public String templateName() {
    return "react/App.js.mustache";
  }

  @Override
  public String outputFileName(AppJs templateData) {
    return "App.js";
  }

  @Override
  public String outputDirectory(AppJs templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src";
  }

  public record AppJs(List<WebModel> models) {

  }

  public record WebModel(String name,
                         List<WebField> fields) {

    public WebModel {
      name = name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public String nameCapitalised() {
      return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    boolean hasOneToManyFields() {
      return fields.stream().anyMatch(WebField::isOneToMany);
    }

    boolean hasManyToOneFields() {
      return fields.stream().anyMatch(WebField::isManyToOne);
    }

    boolean hasRelationalFields() {
      return fields.stream().anyMatch(WebField::isRelational);
    }

    boolean hasJsonFields() {
      return fields.stream().anyMatch(WebField::isJson);
    }

    List<WebField> oneToManyFields() {
      return fields.stream().filter(WebField::isOneToMany).toList();
    }

    List<WebField> manyToOneFields() {
      return fields.stream().filter(WebField::isManyToOne).toList();
    }

    List<WebField> relationalFields() {
      return fields.stream().filter(WebField::isRelational).toList();
    }

    List<WebField> jsonFields() {
      return fields.stream().filter(WebField::isJson).toList();
    }
  }

  public record WebField(String name,
                         boolean required,
                         FieldType type,
                         String associationModelType,
                         List<String> enumOptions) {

    private static final Set<FieldType> FIELDS_TO_DISPLAY_AS_TEXT = Set.of(
        FieldType.ID,
        FieldType.DOUBLE,
        FieldType.STRING,
        FieldType.ENUM
    );

    public WebField(String name, boolean required, FieldType type) {
      this(name, required, type, null, List.of());
    }

    public WebField(String name, boolean required, FieldType type, String associationModelType) {
      this(name, required, type, associationModelType, List.of());
    }

    boolean isBoolean() {
      return type() == FieldType.BOOLEAN;
    }

    boolean isDate() {
      return type() == FieldType.DATE_TIME;
    }

    boolean isOneToMany() {
      return type() == FieldType.ONE_TO_MANY;
    }

    boolean isJson() {
      return type() == FieldType.JSON;
    }

    String nameCapitalised() {
      return capitalise(name());
    }

    String associationModelTypeCapitalised() {
      return capitalise(associationModelType());
    }

    boolean isManyToOne() {
      return type() == FieldType.MANY_TO_ONE;
    }

    boolean isRelational() {
      return isOneToMany() || isManyToOne();
    }

    boolean displayAsText() {
      return FIELDS_TO_DISPLAY_AS_TEXT.contains(type());
    }
  }
}
