package com.evans.generator;

import com.evans.generator.domain.Field;
import com.evans.generator.domain.Model;
import com.evans.generator.file.java.application.Application;
import com.evans.generator.file.java.application.ApplicationGenerator;
import com.evans.generator.file.java.controller.Controller;
import com.evans.generator.file.java.controller.ControllerGenerator;
import com.evans.generator.file.java.entity.Entity;
import com.evans.generator.file.java.entity.EntityGenerator;
import com.evans.generator.file.java.repository.Repository;
import com.evans.generator.file.java.repository.RepositoryGenerator;
import com.evans.generator.file.java.service.Service;
import com.evans.generator.file.java.service.ServiceGenerator;
import com.evans.generator.file.maven.ApplicationPropertiesGenerator;
import com.evans.generator.file.maven.ApplicationPropertiesGenerator.ApplicationProperties;
import com.evans.generator.file.maven.MavenGenerator;
import com.evans.generator.file.maven.MavenGenerator.MavenProject;
import com.evans.generator.file.maven.MavenGenerator.MavenProject.JavaVersion;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class BackendGenerator {

  private final RepositoryGenerator repositoryGenerator;
  private final ServiceGenerator serviceGenerator;
  private final ControllerGenerator controllerGenerator;
  private final EntityGenerator entityGenerator;
  private final ApplicationGenerator applicationGenerator;

  private final MavenGenerator mavenGenerator;
  private final ApplicationPropertiesGenerator applicationPropertiesGenerator;

  public BackendGenerator(
      RepositoryGenerator repositoryGenerator,
      ServiceGenerator serviceGenerator,
      ControllerGenerator controllerGenerator,
      EntityGenerator entityGenerator,
      ApplicationGenerator applicationGenerator,
      MavenGenerator mavenGenerator,
      ApplicationPropertiesGenerator applicationPropertiesGenerator) {
    this.repositoryGenerator = repositoryGenerator;
    this.serviceGenerator = serviceGenerator;
    this.controllerGenerator = controllerGenerator;
    this.entityGenerator = entityGenerator;
    this.applicationGenerator = applicationGenerator;
    this.mavenGenerator = mavenGenerator;
    this.applicationPropertiesGenerator = applicationPropertiesGenerator;
  }

  public void generate(List<Model> models) throws IOException {
    generateMaven();
    generateApplication();
    for (Model model : models) {
      generate(model);
    }
  }

  private void generateMaven() throws IOException {
    MavenProject mavenProject = new MavenProject("com.evans", "testproject", JavaVersion.JDK_18);
    mavenGenerator.generate(mavenProject);

    ApplicationProperties applicationProperties = new ApplicationProperties("MyApp");
    applicationPropertiesGenerator.generate(applicationProperties);
  }

  private void generateApplication() throws IOException {
    Application application = new Application("com.evans", "MyApp", Collections.emptyList());
    applicationGenerator.generate(application);
  }

  private void generate(Model model) throws IOException {
    String basePackage = "com.evans";
    String repositoryPackage = basePackage + ".repository";
    String entityImport = repositoryPackage + "." + model.name();

    String modelNameCamel = model.name().substring(0, 1).toLowerCase() + model.name().substring(1);

    String entityIdTypeImport = model.idType().getName();
    Repository repository = new Repository(repositoryPackage, model.name(),
        List.of(entityImport, entityIdTypeImport),
        model.name(),
        model.idType().getSimpleName());
    repositoryGenerator.generate(repository);

    String repositoryType = model.name() + "Repository";
    String repositoryName = modelNameCamel + "Repository";

    String repositoryImport = repositoryPackage + "." + repositoryType;

    String servicePackage = basePackage + ".service";
    Service service = new Service(servicePackage,
        model.name(),
        modelNameCamel,
        model.idType().getSimpleName(),
        repositoryType,
        repositoryName,
        List.of(entityImport, repositoryImport, entityIdTypeImport));
    serviceGenerator.generate(service);

    String serviceType = model.name() + "Service";
    String serviceName = modelNameCamel + "Service";

    String serviceImport = servicePackage + "." + serviceType;
    Controller controller = new Controller(basePackage + ".controller",
        model.name(),
        modelNameCamel,
        model.idType().getSimpleName(),
        serviceType,
        serviceName,
        List.of(entityImport, serviceImport, entityIdTypeImport));
    controllerGenerator.generate(controller);

    var entityImports = model.fields().stream()
        .map(Field::type)
        .map(Class::getName)
        .map("import %s;"::formatted)
        .toList();

    Entity entity = new Entity(basePackage + ".repository", model.name(), entityImports,
        model.fields());
    entityGenerator.generate(entity);
  }
}
