package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.AppJsGenerator.WebEntity;
import com.evans.codegen.file.react.CreateEntityGenerator.EntityForm;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CreateEntityGenerator implements FileGenerator<EntityForm> {

  @Override
  public String templateName() {
    return "react/createEntity.mustache";
  }

  @Override
  public String outputFileName(EntityForm templateData) {
    return "Create" + templateData.entity().nameCapitalised() + ".js";
  }

  @Override
  public String outputDirectory(EntityForm templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes/"
        + templateData.entity().name();
  }

  public record EntityForm(WebEntity entity,
                           String schema) {}
}
