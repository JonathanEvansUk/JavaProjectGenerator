package com.evans.generator;

import com.evans.generator.Generator.Field;
import com.evans.generator.Generator.Model;
import com.evans.generator.file.java.application.ApplicationGenerator;
import com.evans.generator.file.java.controller.ControllerGenerator;
import com.evans.generator.file.java.entity.EntityGenerator;
import com.evans.generator.file.java.repository.RepositoryGenerator;
import com.evans.generator.file.java.service.ServiceGenerator;
import com.evans.generator.file.maven.ApplicationPropertiesGenerator;
import com.evans.generator.file.maven.MavenGenerator;
import com.evans.generator.file.react.AppJsGenerator;
import com.evans.generator.file.react.CreateEntityGenerator;
import com.evans.generator.file.react.EditEntityGenerator;
import com.evans.generator.file.react.EntityListGenerator;
import com.evans.generator.file.react.IndexJsGenerator;
import com.evans.generator.file.react.PackageJsonGenerator;
import com.evans.generator.file.react.ViewEntitiesGenerator;
import com.evans.generator.file.react.ViewSingleEntityGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Running generator");

    ObjectMapper objectMapper = new ObjectMapper();

    RepositoryGenerator repositoryGenerator = new RepositoryGenerator();
    ServiceGenerator serviceGenerator = new ServiceGenerator();
    ControllerGenerator controllerGenerator = new ControllerGenerator();
    EntityGenerator entityGenerator = new EntityGenerator();

    ApplicationGenerator applicationGenerator = new ApplicationGenerator();

    MavenGenerator mavenGenerator = new MavenGenerator();
    ApplicationPropertiesGenerator applicationPropertiesGenerator = new ApplicationPropertiesGenerator();

    PackageJsonGenerator packageJsonGenerator = new PackageJsonGenerator();
    AppJsGenerator appJsGenerator = new AppJsGenerator();
    IndexJsGenerator indexJsGenerator = new IndexJsGenerator();
    CreateEntityGenerator createEntityGenerator = new CreateEntityGenerator();
    EntityListGenerator entityListGenerator = new EntityListGenerator();
    ViewEntitiesGenerator viewEntitiesGenerator = new ViewEntitiesGenerator();
    ViewSingleEntityGenerator viewSingleEntityGenerator = new ViewSingleEntityGenerator();
    EditEntityGenerator editEntityGenerator = new EditEntityGenerator();

    EntityFormMapper entityFormMapper = new EntityFormMapper(objectMapper);
    Generator generator = new Generator(repositoryGenerator, serviceGenerator, controllerGenerator,
        entityGenerator, mavenGenerator, applicationGenerator, applicationPropertiesGenerator,
        packageJsonGenerator, appJsGenerator, indexJsGenerator, createEntityGenerator,
        entityListGenerator, viewEntitiesGenerator, viewSingleEntityGenerator, editEntityGenerator,
        entityFormMapper);

    generator.generate(List.of(
        Model.of("Bill", List.of(
            new Field("id", Long.class, true, true),
            new Field("amount", Double.class, false, false))),
        Model.of("User", List.of(
            new Field("id", UUID.class, true, true)
        ))
    ));
  }
}
