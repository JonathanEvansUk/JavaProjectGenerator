package com.evans.generator.file.java.entity;

import com.evans.generator.Generator.Field;
import com.evans.generator.file.java.JavaClassTemplateData;
import java.util.List;

public record Entity(String packageName, String className, List<String> imports,
                     List<Field> fields) implements JavaClassTemplateData {}
