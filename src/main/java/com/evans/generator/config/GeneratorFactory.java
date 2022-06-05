package com.evans.generator.config;

import com.evans.generator.Generator;
import dagger.Component;

@Component(modules = {BackendFileGeneratorModule.class, FrontendFileGeneratorModule.class,
    ObjectMapperModule.class})
public interface GeneratorFactory {

  Generator getGenerator();
}
