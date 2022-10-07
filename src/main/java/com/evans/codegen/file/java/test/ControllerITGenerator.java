package com.evans.codegen.file.java.test;

import com.evans.codegen.domain.Model;
import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.file.java.JavaFileGenerator;
import com.evans.codegen.file.java.entity.Entity.Field;
import com.evans.codegen.file.java.test.ControllerITGenerator.ControllerIT;
import java.util.List;

public class ControllerITGenerator implements JavaFileGenerator<ControllerIT> {

  @Override
  public String templateName() {
    return "java/test/controllerIT.mustache";
  }

  @Override
  public boolean generatesTestClass() {
    return true;
  }

  public record ControllerIT(String packageName,
                             String className,
                             String dtoName,
                             String dtoType,
                             String entityNameCamel,//TODO rename this maybe to reflect the fact it is url
                             List<Field> fields,
                             List<Model> manyToOneSideModels,
                             List<String> imports) implements JavaClassTemplateData {}
}
