package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.AppJsGenerator.WebEntity;
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
    return templateData.entity().nameCapitalised() + "List.js";
  }

  @Override
  public String outputDirectory(EntityList templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes/"
        + templateData.entity().name();
  }

  public record EntityList(WebEntity entity) {}
}
