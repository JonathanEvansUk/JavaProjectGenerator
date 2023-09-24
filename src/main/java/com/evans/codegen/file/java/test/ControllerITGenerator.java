package com.evans.codegen.file.java.test;

import com.evans.codegen.domain.Entity;
import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.file.java.JavaFileGenerator;
import com.evans.codegen.file.java.entity.DataEntity.Field;
import com.evans.codegen.file.java.test.ControllerITGenerator.ControllerIT;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
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
                             String entityName,
                             List<Field> fields,
                             List<Entity> manyToOneSideEntities,
                             List<String> imports) implements JavaClassTemplateData {}
}
