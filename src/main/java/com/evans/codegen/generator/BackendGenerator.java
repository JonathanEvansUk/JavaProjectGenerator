package com.evans.codegen.generator;

import static com.evans.codegen.StringUtils.capitalise;
import static java.util.stream.Collectors.toList;

import com.evans.codegen.domain.FieldDefinition;
import com.evans.codegen.domain.FieldDefinition.EnumField;
import com.evans.codegen.domain.FieldDefinition.FieldType;
import com.evans.codegen.domain.FieldDefinition.RelationalField;
import com.evans.codegen.domain.FieldDefinition.RelationalField.ManyToOneField;
import com.evans.codegen.domain.FieldDefinition.RelationalField.OneToManyField;
import com.evans.codegen.domain.Model;
import com.evans.codegen.file.docker.DockerCompose;
import com.evans.codegen.file.docker.DockerComposeGenerator;
import com.evans.codegen.file.docker.Dockerfile;
import com.evans.codegen.file.docker.DockerfileGenerator;
import com.evans.codegen.file.java.application.Application;
import com.evans.codegen.file.java.application.ApplicationGenerator;
import com.evans.codegen.file.java.controller.Controller;
import com.evans.codegen.file.java.controller.ControllerGenerator;
import com.evans.codegen.file.java.dto.DTOConverterGenerator;
import com.evans.codegen.file.java.dto.DTOConverterGenerator.DTOConverter;
import com.evans.codegen.file.java.dto.DTOConverterGenerator.DTOConverter.Subconverter;
import com.evans.codegen.file.java.dto.DTOGenerator;
import com.evans.codegen.file.java.dto.DTOGenerator.DTO;
import com.evans.codegen.file.java.entity.Entity;
import com.evans.codegen.file.java.entity.Entity.Enum;
import com.evans.codegen.file.java.entity.Entity.Enum.Option;
import com.evans.codegen.file.java.entity.Entity.Field;
import com.evans.codegen.file.java.entity.EntityGenerator;
import com.evans.codegen.file.java.repository.Repository;
import com.evans.codegen.file.java.repository.RepositoryGenerator;
import com.evans.codegen.file.java.service.Service;
import com.evans.codegen.file.java.service.ServiceGenerator;
import com.evans.codegen.file.java.test.ControllerITGenerator;
import com.evans.codegen.file.java.test.ControllerITGenerator.ControllerIT;
import com.evans.codegen.file.java.test.ControllerTestGenerator;
import com.evans.codegen.file.java.test.ControllerTestGenerator.ControllerTest;
import com.evans.codegen.file.maven.ApplicationPropertiesGenerator;
import com.evans.codegen.file.maven.ApplicationPropertiesGenerator.ApplicationProperties;
import com.evans.codegen.file.maven.MavenGenerator;
import com.evans.codegen.file.maven.MavenGenerator.MavenProject;
import com.evans.codegen.file.maven.MavenGenerator.MavenProject.JavaVersion;
import com.evans.codegen.file.maven.TestApplicationMysqlPropertiesGenerator;
import com.evans.codegen.file.maven.TestApplicationMysqlPropertiesGenerator.TestApplicationMysqlProperties;
import com.evans.codegen.file.openapi.OpenAPISpec;
import com.evans.codegen.file.openapi.OpenAPISpecGenerator;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;

public class BackendGenerator {

  private final RepositoryGenerator repositoryGenerator;
  private final ServiceGenerator serviceGenerator;
  private final ControllerGenerator controllerGenerator;
  private final EntityGenerator entityGenerator;
  private final ApplicationGenerator applicationGenerator;
  private final DTOGenerator dtoGenerator;
  private final DTOConverterGenerator dtoConverterGenerator;
  private final ControllerTestGenerator controllerTestGenerator;
  private final ControllerITGenerator controllerITGenerator;

  private final MavenGenerator mavenGenerator;
  private final ApplicationPropertiesGenerator applicationPropertiesGenerator;
  private final TestApplicationMysqlPropertiesGenerator testApplicationMysqlPropertiesGenerator;
  private final DockerfileGenerator dockerfileGenerator;
  private final DockerComposeGenerator dockerComposeGenerator;
  private final OpenAPISpecGenerator openAPISpecGenerator;

  @Inject
  public BackendGenerator(
      RepositoryGenerator repositoryGenerator,
      ServiceGenerator serviceGenerator,
      ControllerGenerator controllerGenerator,
      EntityGenerator entityGenerator,
      ApplicationGenerator applicationGenerator,
      DTOGenerator dtoGenerator,
      DTOConverterGenerator dtoConverterGenerator,
      ControllerTestGenerator controllerTestGenerator,
      ControllerITGenerator controllerITGenerator,
      MavenGenerator mavenGenerator,
      ApplicationPropertiesGenerator applicationPropertiesGenerator,
      TestApplicationMysqlPropertiesGenerator testApplicationMysqlPropertiesGenerator,
      OpenAPISpecGenerator openAPISpecGenerator,
      DockerfileGenerator dockerfileGenerator,
      DockerComposeGenerator dockerComposeGenerator) {
    this.repositoryGenerator = repositoryGenerator;
    this.serviceGenerator = serviceGenerator;
    this.controllerGenerator = controllerGenerator;
    this.entityGenerator = entityGenerator;
    this.applicationGenerator = applicationGenerator;
    this.dtoGenerator = dtoGenerator;
    this.dtoConverterGenerator = dtoConverterGenerator;
    this.controllerTestGenerator = controllerTestGenerator;
    this.controllerITGenerator = controllerITGenerator;
    this.mavenGenerator = mavenGenerator;
    this.applicationPropertiesGenerator = applicationPropertiesGenerator;
    this.testApplicationMysqlPropertiesGenerator = testApplicationMysqlPropertiesGenerator;
    this.openAPISpecGenerator = openAPISpecGenerator;
    this.dockerfileGenerator = dockerfileGenerator;
    this.dockerComposeGenerator = dockerComposeGenerator;
  }

  public void generate(List<Model> models) throws IOException {
    String appName = "MyApp";
    String groupId = "com.evans";
    MavenProject mavenProject = new MavenProject(appName, groupId, "testproject",
        JavaVersion.JDK_17);
    generateMaven(mavenProject);
    generateApplication();
    generateDocker(mavenProject);
    generateOpenAPISpec(appName, models);

    record NameAndPackage(String modelName,
                          String packageName) {

      static NameAndPackage forModel(Model model, String basePackage) {
        return new NameAndPackage(model.name(), generateModelImport(model, basePackage));
      }

      private static String generateModelImport(Model model, String basePackage) {
        return basePackage + ".repository." + model.name();
      }

      static NameAndPackage forDTO(Model model, String basePackage) {
        return new NameAndPackage(model.name() + "DTO", generateDTOImport(model, basePackage));
      }

      private static String generateDTOImport(Model model, String basePackage) {
        return basePackage + ".openapi.model." + model.name() + "DTO";
      }
    }

    String basePackage = "com.evans";
    Map<String, String> importsByModelName = models.stream()
        .map(model -> List.of(
            NameAndPackage.forModel(model, basePackage),
            NameAndPackage.forDTO(model, basePackage)
        ))
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(
            NameAndPackage::modelName,
            NameAndPackage::packageName
        ));

    //find all oneToMany relationships, then find the many side
    Map<Model, List<Model>> manyToOneRelationshipsByModel;

    //for each model, does it have a oneToMany field?
    // if yes, then we need to add the association model as key and model as list entry

    record OneToManyRelationshipsForModel(Model model,
                                          List<Model> oneToMany) {}

    var oneToManyRelationshipsForModels = models.stream()
        .map(model -> new OneToManyRelationshipsForModel(model, model.fields().stream()
            .filter(FieldDefinition::isOneToMany)
            .map(OneToManyField.class::cast)
            .map(OneToManyField::associationModel)
            .toList()))
        .toList();

    record OneToManyRelationship(Model oneSide,
                                 Model manySide) {}

    List<OneToManyRelationship> oneToManyRelationships = oneToManyRelationshipsForModels.stream()
        .flatMap(relationship -> relationship.oneToMany().stream()
            .map(oneToManyModel -> new OneToManyRelationship(relationship.model(), oneToManyModel)))
        .toList();

    Map<Model, List<Model>> manyToOneRelationshipsForModel = oneToManyRelationships.stream()
        .collect(Collectors.groupingBy(OneToManyRelationship::manySide, Collectors.mapping(
            OneToManyRelationship::oneSide, toList())));

    for (Model model : models) {
      generate(model, importsByModelName,
          manyToOneRelationshipsForModel.getOrDefault(model, List.of()), groupId);
    }
  }

  private void generateOpenAPISpec(String appName, List<Model> models) throws IOException {

    List<OpenAPISpec.OpenAPIModel> openAPIModels = models.stream()
        .map(this::convert)
        .toList();

    OpenAPISpec openAPISpec = new OpenAPISpec(
        appName,
        "Example Project Summary",
        "Example Project Description",
        "com.evans",
        openAPIModels
    );

    openAPISpecGenerator.generate(openAPISpec);
  }

  // TODO rename and extract
  private OpenAPISpec.OpenAPIModel convert(Model model) {
    return new OpenAPISpec.OpenAPIModel(
        model.name(),
        model.nameCamel(),
        model.fields().stream()
            .map(field ->
                new OpenAPISpec.OpenAPIModel.OpenAPIField(
                    field.name(),
                    resolveOpenAPIType(field),
                    resolveOpenAPIRef(field),
                    resolveOpenAPIFormat(field)
                )
            )
            .toList()
    );
  }

  private String resolveOpenAPIType(FieldDefinition field) {
    return switch (field.type()) {
      case STRING, JSON, DATE_TIME, DATE -> "string";
      case ID -> "integer";
      case DOUBLE -> "number";
      case BOOLEAN -> "boolean";
      case ENUM -> "enum";
      case ONE_TO_MANY, MANY_TO_ONE -> null;
    };
  }

  private String resolveOpenAPIRef(FieldDefinition field) {
    return switch (field.type()) {
      case ONE_TO_MANY, MANY_TO_ONE -> field.nameCapitalised();
      default -> null;
    };
  }

  private String resolveOpenAPIFormat(FieldDefinition field) {
    return switch (field.type()) {
      case ID -> "int64";
      case DATE -> "date";
      case DATE_TIME -> "date-time";
      case DOUBLE -> "double";
      default -> null;
    };
  }

  private void generateMaven(MavenProject mavenProject) throws IOException {
    mavenGenerator.generate(mavenProject);

    ApplicationProperties applicationProperties = new ApplicationProperties(mavenProject.appName());
    applicationPropertiesGenerator.generate(applicationProperties);

    TestApplicationMysqlProperties testApplicationMysqlProperties = new TestApplicationMysqlProperties(
        mavenProject.appName());
    testApplicationMysqlPropertiesGenerator.generate(testApplicationMysqlProperties);
  }

  private void generateApplication() throws IOException {
    Application application = new Application("com.evans", "MyApp", Collections.emptyList());
    applicationGenerator.generate(application);
  }

  private void generateDocker(MavenProject mavenProject) throws IOException {
    Dockerfile dockerfile = new Dockerfile(mavenProject.artifactId());
    dockerfileGenerator.generate(dockerfile);

    DockerCompose dockerCompose = new DockerCompose(mavenProject.artifactId(), "MyApp");
    dockerComposeGenerator.generate(dockerCompose);

  }

  private void generate(Model model, Map<String, String> importsByModelName,
      List<Model> manyToOneSideModels, String groupId) throws IOException {
    // TODO add app name package - e.g com.evans.app
    String basePackage = groupId;
    String repositoryPackage = basePackage + ".repository";
    String entityImport = repositoryPackage + "." + model.name();

    String modelNameCamel = model.name().substring(0, 1).toLowerCase() + model.name().substring(1);

    List<Field> fields = model.fields().stream()
        .map(fieldDefinition -> createField(importsByModelName, fieldDefinition))
        .toList();

    List<String> oneToManyModelNames = model.fields().stream()
        .filter(FieldDefinition::isOneToMany)
        .map(OneToManyField.class::cast)
        .map(OneToManyField::associationModel)
        .map(Model::name)
        .toList();

    List<String> oneToManyImports = oneToManyModelNames.stream()
        .map(name -> repositoryPackage + "." + name)
        .toList();

    List<String> oneToManyDTOImports = oneToManyModelNames.stream()
        .map(name -> name + "DTO")
        .map(importsByModelName::get)
        .toList();

    List<String> oneToManyRepositoryImports = oneToManyModelNames.stream()
        .map(name -> name + "Repository")
        .map(name -> repositoryPackage + "." + name)
        .toList();

    List<String> manyToOneModelNames = model.fields().stream()
        .filter(field -> field instanceof ManyToOneField)
        .map(ManyToOneField.class::cast)
        .map(ManyToOneField::associationModel)
        .map(Model::name)
        .toList();

    List<String> manyToOneImports = manyToOneModelNames.stream()
        .map(name -> repositoryPackage + "." + name)
        .toList();

    List<String> manyToOneDTOImports = manyToOneModelNames.stream()
        .map(name -> name + "DTO")
        .map(importsByModelName::get)
        .toList();

    List<String> manyToOneRepositoryImports = manyToOneModelNames.stream()
        .map(name -> name + "Repository")
        .map(name -> repositoryPackage + "." + name)
        .toList();

    TypeInformation entityIdTypeInformation = resolveEntityFieldImport(model.idField(),
        importsByModelName);

    String entityIdTypeImport = entityIdTypeInformation.name();
    String entityIdTypeSimpleName = entityIdTypeInformation.simpleName();

    Repository repository = new Repository(repositoryPackage, model.name(),
        List.of(entityImport, entityIdTypeImport),
        model.name(),
        entityIdTypeSimpleName,
        manyToOneSideModels);
    repositoryGenerator.generate(repository);

    String repositoryType = model.name() + "Repository";
    String repositoryName = modelNameCamel + "Repository";

    String repositoryImport = repositoryPackage + "." + repositoryType;

    String dtoType = model.name() + "DTO";
    String dtoPackage = basePackage + ".controller" + ".dto";
    String dtoImport = basePackage + ".openapi.model." + dtoType;

    String servicePackage = basePackage + ".service";
    String dtoNameCamel = modelNameCamel + "DTO";

    String dtoConverterType = model.name() + "DTOConverter";
    String dtoConverterPackage = servicePackage + ".converter";
    String dtoConverterImport = dtoConverterPackage + "." + dtoConverterType;
    String dtoConverterName =
        dtoConverterType.substring(0, 1).toLowerCase() + dtoConverterType.substring(1);

    List<String> serviceImports = Stream.of(
            List.of(entityImport, dtoImport, dtoConverterImport, repositoryImport, entityIdTypeImport),
            oneToManyDTOImports, oneToManyRepositoryImports, manyToOneRepositoryImports)
        .flatMap(Collection::stream)
        .toList();

    Service service = new Service(servicePackage,
        model.name(),
        modelNameCamel,
        entityIdTypeSimpleName,
        dtoType,
        dtoNameCamel,
        dtoConverterType,
        dtoConverterName,
        repositoryType,
        repositoryName,
        fields,
        serviceImports,
        manyToOneSideModels
    );
    serviceGenerator.generate(service);

    String serviceType = model.name() + "Service";
    String serviceName = modelNameCamel + "Service";
    String serviceImport = servicePackage + "." + serviceType;

    Controller controller = new Controller(basePackage + ".controller",
        groupId,
        model.name() + "Controller",
        modelNameCamel,
        entityIdTypeSimpleName,
        serviceType,
        serviceName,
        dtoType,
        List.of(dtoImport, serviceImport, entityIdTypeImport),
        manyToOneSideModels);
    controllerGenerator.generate(controller);

    List<String> entityImports = model.fields().stream()
        .filter(field -> field.type() != FieldType.ENUM)
        .map(field -> resolveEntityFieldImport(field, importsByModelName))
        .map(TypeInformation::imports)
        .flatMap(Collection::stream)
        .map("import %s;"::formatted)
        // TODO could this distinct be a set instead?
        .distinct()
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

    Entity entity = new Entity(basePackage + ".repository", model.name(), entityImports,
        fields, enums, manyToOneSideModels);
    entityGenerator.generate(entity);

    List<String> dtoImports = model.fields()
        .stream()
        .filter(field -> field.type() != FieldType.ENUM)
        .map(field -> resolveDTOFieldImport(field, importsByModelName))
        .map(TypeInformation::imports)
        .flatMap(Collection::stream)
        .map("import %s;"::formatted)
        // TODO could this distinct be a set instead?
        .distinct()
        .toList();

    List<Field> dtoFields = model.fields().stream()
        .map(fieldDefinition -> createDTOField(importsByModelName, fieldDefinition))
        .toList();

    DTO dto = new DTO(dtoPackage, dtoType, dtoFields, dtoImports);
    dtoGenerator.generate(dto);

    List<String> dtoConverterImports = Stream.of(oneToManyImports, manyToOneImports,
            List.of(entityImport, dtoImport), oneToManyDTOImports, manyToOneDTOImports)
        .flatMap(Collection::stream)
        .toList();

    List<Subconverter> dtoSubconverters = model.fields().stream()
        .filter(field -> field instanceof RelationalField)
        .map(RelationalField.class::cast)
        .map(field -> {
          String associationModelName = field.associationModel().name();
          String associationModelNameCamel = field.associationModel().nameCamel();
          return new Subconverter(
              associationModelName,
              associationModelNameCamel + "DTOConverter",
              associationModelName + "DTO",
              associationModelName,
              field.name());
        })
        .toList();

    DTOConverter dtoConverter = new DTOConverter(dtoConverterPackage,
        dtoConverterType,
        model.name(),
        modelNameCamel,
        dtoType,
        dtoNameCamel,
        dtoSubconverters,
        fields,
        dtoConverterImports);
    dtoConverterGenerator.generate(dtoConverter);

    String controllerNameCamel =
        controller.className().substring(0, 1).toLowerCase() + controller.className().substring(1);

    ControllerTest controllerTest = new ControllerTest(
        controller.packageName(),
        controller.className() + "Test",
        controller.className(),
        controllerNameCamel,
        serviceType,
        serviceName,
        dtoNameCamel,
        dtoType,
        model.name(),
        List.of(serviceImport, dtoImport));
    controllerTestGenerator.generate(controllerTest);

    ControllerIT controllerIT = new ControllerIT(
        controller.packageName(),
        controller.className() + "IT",
        dtoNameCamel,
        dtoType,
        modelNameCamel,
        model.name(),
        fields,
        manyToOneSideModels,
        List.of(dtoImport));
    controllerITGenerator.generate(controllerIT);
  }

  private Field createField(Map<String, String> importsByModelName,
      FieldDefinition fieldDefinition) {
    return createField(importsByModelName, fieldDefinition, false);
  }

  private Field createDTOField(Map<String, String> importsByModelName,
      FieldDefinition fieldDefinition) {
    return createField(importsByModelName, fieldDefinition, true);
  }

  private Field createField(Map<String, String> importsByModelName, FieldDefinition fieldDefinition,
      boolean forDto) {
    TypeInformation typeInformation = resolveFieldImport(fieldDefinition, importsByModelName,
        forDto);
    Relationship relationship = resolveRelationship(fieldDefinition.type());
    String associationModelName = null;
    if (fieldDefinition instanceof RelationalField relationalField) {
      associationModelName = relationalField.associationModel().name();
    }

    return new Field(
        fieldDefinition.name(),
        typeInformation.simpleName(),
        fieldDefinition.type(),
        relationship,
        associationModelName,
        fieldDefinition.example()
    );
  }

  private Relationship resolveRelationship(FieldType fieldType) {
    return switch (fieldType) {
      case ONE_TO_MANY -> Relationship.ONE_TO_MANY;
      case MANY_TO_ONE -> Relationship.MANY_TO_ONE;
      default -> null;
    };
  }

  public enum Relationship {
    ONE_TO_ONE,
    ONE_TO_MANY,
    MANY_TO_ONE,
    MANY_TO_MANY
  }

  private TypeInformation resolveEntityFieldImport(FieldDefinition fieldDefinition,
      Map<String, String> importsByModelName) {
    return resolveFieldImport(fieldDefinition, importsByModelName, false);
  }

  private TypeInformation resolveDTOFieldImport(FieldDefinition fieldDefinition,
      Map<String, String> importsByModelName) {
    return resolveFieldImport(fieldDefinition, importsByModelName, true);
  }

  private TypeInformation resolveFieldImport(FieldDefinition fieldDefinition,
      Map<String, String> importsByModelName, boolean forDto) {
    return switch (fieldDefinition.type()) {
      case DATE_TIME -> TypeInformation.of(Instant.class);
      case DATE -> TypeInformation.of(LocalDate.class);
      case BOOLEAN -> TypeInformation.of(Boolean.class);
      case STRING, JSON -> TypeInformation.of(String.class);
      case DOUBLE -> TypeInformation.of(Double.class);
      case ID -> TypeInformation.of(Long.class);
      case ENUM -> new TypeInformation(null, fieldDefinition.name(), List.of());
      case ONE_TO_MANY -> resolveOneToManyTypeInformation((OneToManyField) fieldDefinition,
          importsByModelName, forDto);
      case MANY_TO_ONE -> resolveManyToOneTypeInformation((ManyToOneField) fieldDefinition,
          importsByModelName, forDto);
    };
  }

  private TypeInformation resolveOneToManyTypeInformation(OneToManyField field,
      Map<String, String> importsByModelName, boolean forDto) {

    String modelName = field.associationModel().name();
    if (forDto) {
      modelName += "DTO";
    }

    String associationTypePackage = importsByModelName.get(modelName);

    String type = "List<" + modelName + ">";

    return new TypeInformation(associationTypePackage, type,
        List.of(associationTypePackage, List.class.getName()));
  }

  private TypeInformation resolveManyToOneTypeInformation(ManyToOneField field,
      Map<String, String> importsByModelName, boolean forDto) {

    String modelName = field.associationModel().name();
    if (forDto) {
      modelName += "DTO";
    }

    String associationTypePackage = importsByModelName.get(modelName);

    return new TypeInformation(associationTypePackage, modelName, List.of(associationTypePackage));
  }

  record TypeInformation(String name,
                         String simpleName,
                         List<String> imports) {

    TypeInformation {
      simpleName = capitalise(simpleName);
    }


    static TypeInformation of(Class<?> clazz) {
      return new TypeInformation(clazz.getName(), clazz.getSimpleName(), List.of(clazz.getName()));
    }
  }
}
