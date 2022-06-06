package com.evans.codegen.file.java.application;

import com.evans.codegen.file.java.JavaClassTemplateData;
import java.util.List;

public record Application(String packageName, String className, List<String> imports) implements
    JavaClassTemplateData {
}
