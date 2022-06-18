package com.evans.codegen.config;

import com.evans.codegen.file.java.application.ApplicationGenerator;
import com.evans.codegen.file.java.controller.ControllerGenerator;
import com.evans.codegen.file.java.dto.DTOConverterGenerator;
import com.evans.codegen.file.java.dto.DTOGenerator;
import com.evans.codegen.file.java.entity.EntityGenerator;
import com.evans.codegen.file.java.repository.RepositoryGenerator;
import com.evans.codegen.file.java.service.ServiceGenerator;
import com.evans.codegen.file.maven.ApplicationPropertiesGenerator;
import com.evans.codegen.file.maven.MavenGenerator;
import dagger.Module;
import dagger.Provides;

@Module
public class BackendFileGeneratorModule {

  @Provides
  public RepositoryGenerator repositoryGenerator() {
    return new RepositoryGenerator();
  }

  @Provides
  public ServiceGenerator serviceGenerator() {
    return new ServiceGenerator();
  }

  @Provides
  public ControllerGenerator controllerGenerator() {
    return new ControllerGenerator();
  }

  @Provides
  public EntityGenerator entityGenerator() {
    return new EntityGenerator();
  }

  @Provides
  public ApplicationGenerator applicationGenerator() {
    return new ApplicationGenerator();
  }

  @Provides
  public DTOGenerator dtoGenerator() {
    return new DTOGenerator();
  }

  @Provides
  public DTOConverterGenerator dtoConverterGenerator() {
    return new DTOConverterGenerator();
  }

  @Provides
  public MavenGenerator mavenGenerator() {
    return new MavenGenerator();
  }

  @Provides
  public ApplicationPropertiesGenerator applicationPropertiesGenerator() {
    return new ApplicationPropertiesGenerator();
  }
}
