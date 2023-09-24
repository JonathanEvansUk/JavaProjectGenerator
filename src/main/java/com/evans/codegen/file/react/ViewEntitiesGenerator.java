package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.AppJsGenerator.WebModel;
import com.evans.codegen.file.react.ViewEntitiesGenerator.EntitiesList;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ViewEntitiesGenerator implements FileGenerator<EntitiesList> {

  @Override
  public String templateName() {
    return "react/entities.js.mustache";
  }

  @Override
  public String outputFileName(EntitiesList templateData) {
    return "entities.js";
  }

  @Override
  public String outputDirectory(EntitiesList templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src/routes";
  }

  public record EntitiesList(List<WebModel> models) {}
}
