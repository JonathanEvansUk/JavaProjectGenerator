package com.evans.generator.file.react;

import com.evans.generator.file.FileGenerator;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.ViewEntitiesGenerator.EntitiesList;
import java.util.List;

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
