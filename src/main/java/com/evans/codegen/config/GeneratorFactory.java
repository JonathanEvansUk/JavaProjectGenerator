package com.evans.codegen.config;

import com.evans.codegen.generator.Generator;
import dagger.Component;

@Component(modules = {BackendFileGeneratorModule.class, FrontendFileGeneratorModule.class,
    ObjectMapperModule.class})
public interface GeneratorFactory {

  Generator getGenerator();
}
