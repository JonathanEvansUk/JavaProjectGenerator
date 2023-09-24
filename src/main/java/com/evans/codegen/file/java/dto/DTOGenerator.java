package com.evans.codegen.file.java.dto;

import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.file.java.JavaFileGenerator;
import com.evans.codegen.file.java.dto.DTOGenerator.DTO;
import com.evans.codegen.file.java.entity.DataEntity.Field;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DTOGenerator implements JavaFileGenerator<DTO> {

  @Override
  public String templateName() {
    return "java/main/dto.mustache";
  }

  public record DTO(String packageName,
                    String className,
                    List<Field> fields,
                    List<String> imports) implements JavaClassTemplateData {}
}
