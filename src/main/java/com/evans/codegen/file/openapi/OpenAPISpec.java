package com.evans.codegen.file.openapi;

import java.util.List;

public record OpenAPISpec(String projectName, String projectSummary, String projectDescription,
                          List<OpenAPIModel> models) {
  public record OpenAPIModel(String name, String nameCamel, List<OpenAPIField> fields) {

    public record OpenAPIField(String name, String type, String ref) {
      boolean hasType() {
        return type != null;
      }

      boolean hasRef() {
        return ref != null;
      }
    }
  }
}
