package com.evans.codegen.config;

import com.evans.codegen.generator.Generator;
import dagger.Component;

@Component(modules = {ObjectMapperModule.class})
public interface GeneratorFactory {

  Generator getGenerator();
}
