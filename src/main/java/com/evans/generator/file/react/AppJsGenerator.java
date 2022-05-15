package com.evans.generator.file.react;

import com.evans.generator.Generator.Field;
import com.evans.generator.file.FileGenerator;
import com.evans.generator.file.react.AppJsGenerator.AppJs;
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

  public record WebModel(String name, List<Field> fields) {

    public WebModel {
      name = name.toLowerCase();
    }

    public String nameCapitalised() {
      return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
  }
}
