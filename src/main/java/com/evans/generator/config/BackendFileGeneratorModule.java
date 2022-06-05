package com.evans.generator.config;

import com.evans.generator.file.java.application.ApplicationGenerator;
import com.evans.generator.file.java.controller.ControllerGenerator;
import com.evans.generator.file.java.entity.EntityGenerator;
import com.evans.generator.file.java.repository.RepositoryGenerator;
import com.evans.generator.file.java.service.ServiceGenerator;
import com.evans.generator.file.maven.ApplicationPropertiesGenerator;
import com.evans.generator.file.maven.MavenGenerator;
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
  public MavenGenerator mavenGenerator() {
    return new MavenGenerator();
  }

  @Provides
      public ApplicationPropertiesGenerator applicationPropertiesGenerator() {
    return new ApplicationPropertiesGenerator();
  }
}
