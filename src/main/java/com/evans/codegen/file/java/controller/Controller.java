package com.evans.codegen.file.java.controller;

import com.evans.codegen.file.java.JavaClassTemplateData;
import java.util.List;

public record Controller(String packageName,
                         String className,
                         String entityNameCamel,
                         String entityIdType,
                         String serviceType,
                         String serviceName,
                         String dtoType,
                         List<String> imports) implements JavaClassTemplateData {}
