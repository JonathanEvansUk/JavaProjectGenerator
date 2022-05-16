package com.evans.generator.file.react;

import com.evans.generator.file.FileGenerator;
import com.evans.generator.file.react.EntityFormGenerator.EntityForm;

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
