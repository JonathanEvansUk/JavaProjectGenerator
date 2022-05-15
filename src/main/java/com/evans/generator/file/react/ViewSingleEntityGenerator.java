package com.evans.generator.file.react;

import com.evans.generator.file.FileGenerator;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.ViewSingleEntityGenerator.ViewSingleEntity;

public class ViewSingleEntityGenerator implements FileGenerator<ViewSingleEntity> {

  @Override
  public String templateName() {
    return "react/viewEntity.mustache";
  }

  @Override
  public String outputFileName(ViewSingleEntity templateData) {
    return "View" + templateData.model().nameCapitalised() + ".js";
  }

  @Override
  public String outputDirectory(ViewSingleEntity templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes"
        + templateData.model().name();
  }

  record ViewSingleEntity(WebModel model) {}
}
