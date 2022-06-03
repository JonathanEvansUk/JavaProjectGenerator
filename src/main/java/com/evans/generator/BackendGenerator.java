package com.evans.generator;

import static com.evans.generator.Utils.capitalise;

import com.evans.generator.domain.FieldDefinition;
import com.evans.generator.domain.FieldDefinition.EnumField;
import com.evans.generator.domain.FieldDefinition.FieldType;
import com.evans.generator.domain.Model;
import com.evans.generator.file.java.application.Application;
import com.evans.generator.file.java.application.ApplicationGenerator;
import com.evans.generator.file.java.controller.Controller;
import com.evans.generator.file.java.controller.ControllerGenerator;
import com.evans.generator.file.java.entity.Entity;
import com.evans.generator.file.java.entity.Entity.Enum;
import com.evans.generator.file.java.entity.Entity.Enum.Option;
import com.evans.generator.file.java.entity.Entity.Field;
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
import java.time.Instant;
import java.time.LocalDate;
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

    TypeInformation entityIdTypeInformation = resolveFieldImport(model.idField());

    String entityIdTypeImport = entityIdTypeInformation.name();
    String entityIdTypeSimpleName = entityIdTypeInformation.simpleName();

    Repository repository = new Repository(repositoryPackage, model.name(),
        List.of(entityImport, entityIdTypeImport),
        model.name(),
        entityIdTypeSimpleName);
    repositoryGenerator.generate(repository);

    String repositoryType = model.name() + "Repository";
    String repositoryName = modelNameCamel + "Repository";

    String repositoryImport = repositoryPackage + "." + repositoryType;

    String servicePackage = basePackage + ".service";
    Service service = new Service(servicePackage,
        model.name(),
        modelNameCamel,
        entityIdTypeSimpleName,
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
        entityIdTypeSimpleName,
        serviceType,
        serviceName,
        List.of(entityImport, serviceImport, entityIdTypeImport));
    controllerGenerator.generate(controller);

    var entityImports = model.fields().stream()
        .filter(field -> field.type() != FieldType.ENUM)
        .map(this::resolveFieldImport)
        .map(TypeInformation::name)
        .map("import %s;"::formatted)
        .toList();

    List<EnumField> enumFields = model.fields().stream()
        .filter(field -> field.type() == FieldType.ENUM)
        .map(EnumField.class::cast)
        .toList();

    List<Enum> enums = enumFields.stream()
        .map(enumField ->
            new Enum(
                capitalise(enumField.name()),
                enumField.options().stream()
                    .map(option -> new Option(option.toUpperCase(), option))
                    .toList()))
        .toList();

    List<Field> fields = model.fields().stream()
        .map(fieldDefinition -> {
          TypeInformation typeInformation = resolveFieldImport(fieldDefinition);
          return new Field(
              fieldDefinition.name(),
              typeInformation.simpleName(),
              fieldDefinition.isId(),
              fieldDefinition.type() == FieldType.DATE_TIME,
              fieldDefinition.type() == FieldType.BOOLEAN);
        })
        .toList();

    Entity entity = new Entity(basePackage + ".repository", model.name(), entityImports,
        fields, enums);
    entityGenerator.generate(entity);
  }

  private TypeInformation resolveFieldImport(FieldDefinition fieldDefinition) {
    return switch (fieldDefinition.type()) {
      case DATE_TIME -> TypeInformation.of(Instant.class);
      case DATE -> TypeInformation.of(LocalDate.class);
      case BOOLEAN -> TypeInformation.of(Boolean.class);
      case STRING -> TypeInformation.of(String.class);
      case DOUBLE -> TypeInformation.of(Double.class);
      case ID -> TypeInformation.of(Long.class);
      case ENUM -> new TypeInformation(null, fieldDefinition.name());
    };
  }

  record TypeInformation(String name,
                         String simpleName) {

    TypeInformation {
      simpleName = capitalise(simpleName);
    }

    static TypeInformation of(Class<?> clazz) {
      return new TypeInformation(clazz.getName(), clazz.getSimpleName());
    }
  }
}
