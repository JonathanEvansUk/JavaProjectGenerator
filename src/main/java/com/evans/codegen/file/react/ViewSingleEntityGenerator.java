package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.CreateEntityGenerator.EntityForm;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ViewSingleEntityGenerator implements FileGenerator<EntityForm> {

  @Override
  public String templateName() {
    return "react/viewEntity.mustache";
  }

  @Override
  public String outputFileName(EntityForm templateData) {
    return "View" + templateData.entity().nameCapitalised() + ".js";
  }

  @Override
  public String outputDirectory(EntityForm templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes/"
        + templateData.entity().name();
  }
}
