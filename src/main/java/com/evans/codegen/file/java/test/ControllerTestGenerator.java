package com.evans.codegen.file.java.test;

import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.file.java.JavaFileGenerator;
import com.evans.codegen.file.java.test.ControllerTestGenerator.ControllerTest;
import java.util.List;

public class ControllerTestGenerator implements JavaFileGenerator<ControllerTest> {

  @Override
  public String templateName() {
    return "java/test/controllerTest.mustache";
  }

  @Override
  public boolean generatesTestClass() {
    return true;
  }

  public record ControllerTest(String packageName,
                               String className,
                               String controllerType,
                               String controllerName,
                               String serviceType,
                               String serviceName,
                               String dtoName,
                               String dtoType,
                               List<String> imports) implements JavaClassTemplateData {

  }
}
