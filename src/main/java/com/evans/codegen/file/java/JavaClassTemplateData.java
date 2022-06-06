package com.evans.codegen.file.java;

import java.util.List;

public interface JavaClassTemplateData {

  String packageName();
  String className();
  List<String> imports();
}
