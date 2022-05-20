package com.evans.generator.file.react;

import com.evans.generator.file.FileGenerator;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.CreateEntityGenerator.EntityForm;

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

  public record EntityForm(WebModel model,
                           String schema) {}
}
