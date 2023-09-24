package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.AppJsGenerator.WebModel;
import com.evans.codegen.file.react.EntityListGenerator.EntityList;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
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
