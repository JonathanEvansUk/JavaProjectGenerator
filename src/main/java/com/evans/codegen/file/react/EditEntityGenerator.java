package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.CreateEntityGenerator.EntityForm;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EditEntityGenerator implements FileGenerator<EntityForm> {

  @Override
  public String templateName() {
    return "react/editEntity.mustache";
  }

  @Override
  public String outputFileName(EntityForm templateData) {
    return "Edit" + templateData.model().nameCapitalised() + ".js";
  }

  @Override
  public String outputDirectory(EntityForm templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes/"
        + templateData.model().name();
  }
}
