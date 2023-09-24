package com.evans.codegen.file.java.dto;

import static com.evans.codegen.StringUtils.capitalise;

import com.evans.codegen.file.java.JavaClassTemplateData;
import com.evans.codegen.file.java.JavaFileGenerator;
import com.evans.codegen.file.java.dto.DTOConverterGenerator.DTOConverter;
import com.evans.codegen.file.java.entity.Entity.Field;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DTOConverterGenerator implements JavaFileGenerator<DTOConverter> {

  @Override
  public String templateName() {
    return "java/main/dtoConverter.mustache";
  }

  public record DTOConverter(String packageName,
                             String className,
                             String entityType,
                             String entityNameCamel,
                             String dtoType,
                             String dtoNameCamel,
                             List<Subconverter> subconverters,
                             List<Field> fields,
                             List<String> imports) implements JavaClassTemplateData {

    boolean hasSubconverters() {
      return !subconverters().isEmpty();
    }

    public record Subconverter(String modelName,
                               String name,
                               String dtoType,
                               String entityType,
                               String fieldName) {

      String type() {
        return capitalise(name());
      }

      String dtoTypeCamel() {
        return dtoType().substring(0, 1).toLowerCase() + dtoType().substring(1);
      }

      String entityTypeCamel() {
        return entityType().substring(0, 1).toLowerCase() + entityType().substring(1);
      }
    }
  }
}
