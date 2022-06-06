package com.evans.codegen.file.java.entity;

import com.evans.codegen.file.java.JavaClassTemplateData;
import java.util.List;

public record Entity(String packageName,
                     String className,
                     List<String> imports,
                     List<Field> fields,
                     List<Enum> enums) implements JavaClassTemplateData {

  public record Field(String name,
                      String simpleTypeName,
                      boolean isId,
                      boolean isDate,
                      boolean isBoolean) {

    String getterName() {
      return "get" + name().substring(0, 1).toUpperCase() + name().substring(1);
    }

    String setterName() {
      return "set" + name().substring(0, 1).toUpperCase() + name().substring(1);
    }
  }

  public record Enum(String name,
                     List<Option> options) {

    public record Option(String name,
                         String value) {}
  }
}
