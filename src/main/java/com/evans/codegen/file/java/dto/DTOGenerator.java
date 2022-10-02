package com.evans.codegen.file.java.dto;

import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.file.java.JavaFileGenerator;
import com.evans.codegen.file.java.dto.DTOGenerator.DTO;
import com.evans.codegen.file.java.entity.Entity.Field;
import java.util.List;

public record DTOGenerator() implements JavaFileGenerator<DTO> {

  @Override
  public String templateName() {
    return "java/main/dto.mustache";
  }

  public record DTO(String packageName,
                    String className,
                    List<Field> fields,
                    List<String> imports) implements JavaClassTemplateData {}
}
