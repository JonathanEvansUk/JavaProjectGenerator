package com.evans.codegen.file.react;

import static com.evans.codegen.StringUtils.capitalise;

import com.evans.codegen.domain.FieldDefinition.FieldType;
import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.AppJsGenerator.AppJs;
import java.util.List;

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

  public record AppJs(List<WebModel> models) {}

  public record WebModel(String name,
                         List<WebField> fields) {

    public WebModel {
      name = name.toLowerCase();
    }

    public String nameCapitalised() {
      return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
  }

  public record WebField(String name,
                         boolean required,
                         FieldType type,
                         String associationModelType,
                         List<String> enumOptions) {

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

    String nameCapitalised() {
      return capitalise(name());
    }

    String associationModelTypeCapitalised() {
      return capitalise(associationModelType());
    }
  }
}
