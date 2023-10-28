package com.evans.codegen.generator;

import static com.evans.codegen.StringUtils.capitalise;
import static java.util.stream.Collectors.toList;

import com.evans.codegen.domain.Entity;
import com.evans.codegen.domain.FieldDefinition;
import com.evans.codegen.domain.FieldDefinition.EnumField;
import com.evans.codegen.domain.FieldDefinition.FieldType;
import com.evans.codegen.domain.FieldDefinition.RelationalField;
import com.evans.codegen.domain.FieldDefinition.RelationalField.ManyToOneField;
import com.evans.codegen.domain.FieldDefinition.RelationalField.OneToManyField;
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
import com.evans.codegen.file.java.entity.DataEntity;
import com.evans.codegen.file.java.entity.DataEntity.Enum;
import com.evans.codegen.file.java.entity.DataEntity.Enum.Option;
import com.evans.codegen.file.java.entity.DataEntity.Field;
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
import lombok.RequiredArgsConstructor;

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

@RequiredArgsConstructor(onConstructor_ = @Inject)
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
  private final OpenAPISpecGenerator openAPISpecGenerator;
  private final DockerfileGenerator dockerfileGenerator;
  private final DockerComposeGenerator dockerComposeGenerator;

  public void generate(List<Entity> entities) throws IOException {
    String appName = "MyApp";
    String groupId = "com.evans";
    MavenProject mavenProject = new MavenProject(appName, groupId, "testproject",
        JavaVersion.JDK_17);
    generateMaven(mavenProject);
    generateApplication();
    generateDocker(mavenProject);
    generateOpenAPISpec(appName, entities);

    record NameAndPackage(String entityName,
                          String packageName) {

      static NameAndPackage forEntity(Entity entity, String basePackage) {
        return new NameAndPackage(entity.name(), generateEntityImport(entity, basePackage));
      }

      private static String generateEntityImport(Entity entity, String basePackage) {
        return basePackage + ".repository." + entity.name();
      }

      static NameAndPackage forDTO(Entity entity, String basePackage) {
        return new NameAndPackage(entity.name() + "DTO", generateDTOImport(entity, basePackage));
      }

      private static String generateDTOImport(Entity entity, String basePackage) {
        return basePackage + ".openapi.model." + entity.name() + "DTO";
      }
    }

    String basePackage = "com.evans";
    Map<String, String> importsByEntityName = entities.stream()
        .map(entity -> List.of(
            NameAndPackage.forEntity(entity, basePackage),
            NameAndPackage.forDTO(entity, basePackage)
        ))
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(
            NameAndPackage::entityName,
            NameAndPackage::packageName
        ));

    //for each entity, does it have a oneToMany field?
    // if yes, then we need to add the association entity as key and entity as list entry

    record OneToManyRelationshipsForEntity(Entity entity,
                                           List<Entity> oneToMany) {}

    var oneToManyRelationshipsForEntities = entities.stream()
        .map(entity -> new OneToManyRelationshipsForEntity(entity, entity.fields().stream()
            .filter(FieldDefinition::isOneToMany)
            .map(OneToManyField.class::cast)
            .map(OneToManyField::associationEntity)
            .toList()))
        .toList();

    record OneToManyRelationship(Entity oneSide,
                                 Entity manySide) {}

    List<OneToManyRelationship> oneToManyRelationships = oneToManyRelationshipsForEntities.stream()
        .flatMap(relationship -> relationship.oneToMany().stream()
            .map(oneToManyModel -> new OneToManyRelationship(relationship.entity(), oneToManyModel)))
        .toList();

    Map<Entity, List<Entity>> manyToOneRelationshipsForEntity = oneToManyRelationships.stream()
        .collect(Collectors.groupingBy(OneToManyRelationship::manySide, Collectors.mapping(
            OneToManyRelationship::oneSide, toList())));

    for (Entity entity : entities) {
      generate(entity, importsByEntityName,
          manyToOneRelationshipsForEntity.getOrDefault(entity, List.of()), groupId);
    }
  }

  private void generateOpenAPISpec(String appName, List<Entity> entities) throws IOException {

    List<OpenAPISpec.OpenAPIModel> openAPIModels = entities.stream()
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
  private OpenAPISpec.OpenAPIModel convert(Entity entity) {
    return new OpenAPISpec.OpenAPIModel(
        entity.name(),
        entity.nameCamel(),
        entity.fields().stream()
            .map(field ->
                new OpenAPISpec.OpenAPIModel.OpenAPIField(
                    field.name(),
                    resolveOpenAPIType(field),
                    resolveOpenAPIRef(field),
                    resolveOpenAPIFormat(field),
                    resolveOpenAPIItems(field)
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
      case ONE_TO_MANY -> "array";
      case MANY_TO_ONE -> null;
    };
  }

  private String resolveOpenAPIRef(FieldDefinition field) {
    return switch (field.type()) {
      case ONE_TO_MANY -> ((OneToManyField) field).associationEntity().name();
      case MANY_TO_ONE -> field.nameCapitalised();
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

  private boolean resolveOpenAPIItems(FieldDefinition fieldDefinition) {
    return switch (fieldDefinition.type()) {
      case ONE_TO_MANY -> true;
      default -> false;
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

  private void generate(Entity entity, Map<String, String> importsByEntityName,
                        List<Entity> manyToOneSideEntities, String groupId) throws IOException {
    // TODO add app name package - e.g com.evans.app
    String basePackage = groupId;
    String repositoryPackage = basePackage + ".repository";
    String entityImport = repositoryPackage + "." + entity.name();

    String entityNameCamel = entity.name().substring(0, 1).toLowerCase() + entity.name().substring(1);

    List<Field> fields = entity.fields().stream()
        .map(fieldDefinition -> createField(importsByEntityName, fieldDefinition))
        .toList();

    List<String> oneToManyEntityNames = entity.fields().stream()
        .filter(FieldDefinition::isOneToMany)
        .map(OneToManyField.class::cast)
        .map(OneToManyField::associationEntity)
        .map(Entity::name)
        .toList();

    List<String> oneToManyImports = oneToManyEntityNames.stream()
        .map(name -> repositoryPackage + "." + name)
        .toList();

    List<String> oneToManyDTOImports = oneToManyEntityNames.stream()
        .map(name -> name + "DTO")
        .map(importsByEntityName::get)
        .toList();

    List<String> oneToManyRepositoryImports = oneToManyEntityNames.stream()
        .map(name -> name + "Repository")
        .map(name -> repositoryPackage + "." + name)
        .toList();

    List<String> manyToOneEntityNames = entity.fields().stream()
        .filter(field -> field instanceof ManyToOneField)
        .map(ManyToOneField.class::cast)
        .map(ManyToOneField::associationEntity)
        .map(Entity::name)
        .toList();

    List<String> manyToOneImports = manyToOneEntityNames.stream()
        .map(name -> repositoryPackage + "." + name)
        .toList();

    List<String> manyToOneDTOImports = manyToOneEntityNames.stream()
        .map(name -> name + "DTO")
        .map(importsByEntityName::get)
        .toList();

    List<String> manyToOneRepositoryImports = manyToOneEntityNames.stream()
        .map(name -> name + "Repository")
        .map(name -> repositoryPackage + "." + name)
        .toList();

    TypeInformation entityIdTypeInformation = resolveEntityFieldImport(entity.idField(),
        importsByEntityName);

    String entityIdTypeImport = entityIdTypeInformation.name();
    String entityIdTypeSimpleName = entityIdTypeInformation.simpleName();

    Repository repository = new Repository(repositoryPackage, entity.name(),
        List.of(entityImport, entityIdTypeImport),
        entity.name(),
        entityIdTypeSimpleName,
        manyToOneSideEntities);
    repositoryGenerator.generate(repository);

    String repositoryType = entity.name() + "Repository";
    String repositoryName = entityNameCamel + "Repository";

    String repositoryImport = repositoryPackage + "." + repositoryType;

    String dtoType = entity.name() + "DTO";
    String dtoPackage = basePackage + ".controller" + ".dto";
    String dtoImport = basePackage + ".openapi.model." + dtoType;

    String servicePackage = basePackage + ".service";
    String dtoNameCamel = entityNameCamel + "DTO";

    String dtoConverterType = entity.name() + "DTOConverter";
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
        entity.name(),
        entityNameCamel,
        entityIdTypeSimpleName,
        dtoType,
        dtoNameCamel,
        dtoConverterType,
        dtoConverterName,
        repositoryType,
        repositoryName,
        fields,
        serviceImports,
        manyToOneSideEntities
    );
    serviceGenerator.generate(service);

    String serviceType = entity.name() + "Service";
    String serviceName = entityNameCamel + "Service";
    String serviceImport = servicePackage + "." + serviceType;

    Controller controller = new Controller(basePackage + ".controller",
        groupId,
        entity.name() + "Controller",
        entityNameCamel,
        entityIdTypeSimpleName,
        serviceType,
        serviceName,
        dtoType,
        List.of(dtoImport, serviceImport, entityIdTypeImport),
        manyToOneSideEntities);
    controllerGenerator.generate(controller);

    List<String> entityImports = entity.fields().stream()
        .filter(field -> field.type() != FieldType.ENUM)
        .map(field -> resolveEntityFieldImport(field, importsByEntityName))
        .map(TypeInformation::imports)
        .flatMap(Collection::stream)
        .map("import %s;"::formatted)
        // TODO could this distinct be a set instead?
        .distinct()
        .toList();

    List<EnumField> enumFields = entity.fields().stream()
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

    DataEntity dataEntity = new DataEntity(basePackage + ".repository", entity.name(), entityImports,
        fields, enums, manyToOneSideEntities);
    entityGenerator.generate(dataEntity);

    List<String> dtoImports = entity.fields()
        .stream()
        .filter(field -> field.type() != FieldType.ENUM)
        .map(field -> resolveDTOFieldImport(field, importsByEntityName))
        .map(TypeInformation::imports)
        .flatMap(Collection::stream)
        .map("import %s;"::formatted)
        // TODO could this distinct be a set instead?
        .distinct()
        .toList();

    List<Field> dtoFields = entity.fields().stream()
        .map(fieldDefinition -> createDTOField(importsByEntityName, fieldDefinition))
        .toList();

    DTO dto = new DTO(dtoPackage, dtoType, dtoFields, dtoImports);
    dtoGenerator.generate(dto);

    List<String> dtoConverterImports = Stream.of(oneToManyImports, manyToOneImports,
            List.of(entityImport, dtoImport), oneToManyDTOImports, manyToOneDTOImports)
        .flatMap(Collection::stream)
        .toList();

    List<Subconverter> dtoSubconverters = entity.fields().stream()
        .filter(field -> field instanceof RelationalField)
        .map(RelationalField.class::cast)
        .map(field -> {
          String associationEntityName = field.associationEntity().name();
          String associationEntityNameCamel = field.associationEntity().nameCamel();
          return new Subconverter(
              associationEntityName,
              associationEntityNameCamel + "DTOConverter",
              associationEntityName + "DTO",
              associationEntityName,
              field.name());
        })
        .toList();

    DTOConverter dtoConverter = new DTOConverter(dtoConverterPackage,
        dtoConverterType,
        entity.name(),
        entityNameCamel,
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
        entity.name(),
        List.of(serviceImport, dtoImport));
    controllerTestGenerator.generate(controllerTest);

    ControllerIT controllerIT = new ControllerIT(
        controller.packageName(),
        controller.className() + "IT",
        dtoNameCamel,
        dtoType,
        entityNameCamel,
        entity.name(),
        fields,
        manyToOneSideEntities,
        List.of(dtoImport));
    controllerITGenerator.generate(controllerIT);
  }

  private Field createField(Map<String, String> importsByEntityName,
      FieldDefinition fieldDefinition) {
    return createField(importsByEntityName, fieldDefinition, false);
  }

  private Field createDTOField(Map<String, String> importsByEntityName,
      FieldDefinition fieldDefinition) {
    return createField(importsByEntityName, fieldDefinition, true);
  }

  private Field createField(Map<String, String> importsByEntityName, FieldDefinition fieldDefinition,
      boolean forDto) {
    TypeInformation typeInformation = resolveFieldImport(fieldDefinition, importsByEntityName,
        forDto);
    Relationship relationship = resolveRelationship(fieldDefinition.type());
    String associationEntityName = null;
    if (fieldDefinition instanceof RelationalField relationalField) {
      associationEntityName = relationalField.associationEntity().name();
    }

    return new Field(
        fieldDefinition.name(),
        typeInformation.simpleName(),
        fieldDefinition.type(),
        relationship,
        associationEntityName,
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
      Map<String, String> importsByEntityName) {
    return resolveFieldImport(fieldDefinition, importsByEntityName, false);
  }

  private TypeInformation resolveDTOFieldImport(FieldDefinition fieldDefinition,
      Map<String, String> importsByEntityName) {
    return resolveFieldImport(fieldDefinition, importsByEntityName, true);
  }

  private TypeInformation resolveFieldImport(FieldDefinition fieldDefinition,
      Map<String, String> importsByEntityName, boolean forDto) {
    return switch (fieldDefinition.type()) {
      case DATE_TIME -> TypeInformation.of(Instant.class);
      case DATE -> TypeInformation.of(LocalDate.class);
      case BOOLEAN -> TypeInformation.of(Boolean.class);
      case STRING, JSON -> TypeInformation.of(String.class);
      case DOUBLE -> TypeInformation.of(Double.class);
      case ID -> TypeInformation.of(Long.class);
      case ENUM -> new TypeInformation(null, fieldDefinition.name(), List.of());
      case ONE_TO_MANY -> resolveOneToManyTypeInformation((OneToManyField) fieldDefinition,
          importsByEntityName, forDto);
      case MANY_TO_ONE -> resolveManyToOneTypeInformation((ManyToOneField) fieldDefinition,
          importsByEntityName, forDto);
    };
  }

  private TypeInformation resolveOneToManyTypeInformation(OneToManyField field,
      Map<String, String> importsByEntityName, boolean forDto) {

    String entityName = field.associationEntity().name();
    if (forDto) {
      entityName += "DTO";
    }

    String associationTypePackage = importsByEntityName.get(entityName);

    String type = "List<" + entityName + ">";

    return new TypeInformation(associationTypePackage, type,
        List.of(associationTypePackage, List.class.getName()));
  }

  private TypeInformation resolveManyToOneTypeInformation(ManyToOneField field,
      Map<String, String> importsByEntityName, boolean forDto) {

    String entityName = field.associationEntity().name();
    if (forDto) {
      entityName += "DTO";
    }

    String associationTypePackage = importsByEntityName.get(entityName);

    return new TypeInformation(associationTypePackage, entityName, List.of(associationTypePackage));
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
