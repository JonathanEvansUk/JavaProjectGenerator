package com.evans.codegen.file.react;

import com.evans.codegen.file.FileGenerator;
import com.evans.codegen.file.react.AppJsGenerator.WebModel;
import com.evans.codegen.file.react.IndexJsGenerator.IndexJs;
import java.util.List;

public class IndexJsGenerator implements FileGenerator<IndexJs> {

  @Override
  public String templateName() {
    return "react/index.js.mustache";
  }

  @Override
  public String outputFileName(IndexJs templateData) {
    return "index.js";
  }

  @Override
  public String outputDirectory(IndexJs templateData) {
    return FileGenerator.super.outputDirectory(templateData) + "web/src";
  }

  public record IndexJs(List<WebModel> models) {}
}
