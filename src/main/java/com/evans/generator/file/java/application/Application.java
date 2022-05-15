package com.evans.generator.file.java.application;

import com.evans.generator.file.java.JavaClassTemplateData;
import java.util.List;

public record Application(String packageName, String className, List<String> imports) implements
    JavaClassTemplateData {
}
