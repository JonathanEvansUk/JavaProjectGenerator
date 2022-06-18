package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.AppJsGenerator.WebModel;
import com.evans.codegen.file.react.CreateEntityGenerator.EntityForm;

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

  //TODO the model needs to have a new Field class instead of the FieldDefinition
  public record EntityForm(WebModel model,
                           String schema) {}
}
