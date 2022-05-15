package com.evans.generator.file.react;

import com.evans.generator.file.FileGenerator;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.EntityListGenerator.EntityList;

public class EntityListGenerator implements FileGenerator<EntityList> {

  @Override
  public String templateName() {
    return "react/entityList.mustache";
  }

  @Override
  public String outputFileName(EntityList templateData) {
    return templateData.model().nameCapitalised() + "List.js";
  }

  @Override
  public String outputDirectory(EntityList templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes/"
        + templateData.model().name();
  }

  public record EntityList(WebModel model) {}
}
