package com.evans.codegen.generator;

import static com.evans.codegen.StringUtils.capitalise;
import static java.util.stream.Collectors.toList;

import com.evans.codegen.domain.FieldDefinition;
import com.evans.codegen.domain.FieldDefinition.EnumField;
import com.evans.codegen.domain.FieldDefinition.FieldType;
import com.evans.codegen.domain.FieldDefinition.OneToManyField;
import com.evans.codegen.domain.Model;
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
import com.evans.codegen.file.maven.ApplicationPropertiesGenerator;
import com.evans.codegen.file.maven.ApplicationPropertiesGenerator.ApplicationProperties;
import com.evans.codegen.file.maven.MavenGenerator;
import com.evans.codegen.file.maven.MavenGenerator.MavenProject;
import com.evans.codegen.file.maven.MavenGenerator.MavenProject.JavaVersion;
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

  private final MavenGenerator mavenGenerator;
  private final ApplicationPropertiesGenerator applicationPropertiesGenerator;

  @Inject
  public BackendGenerator(
      RepositoryGenerator repositoryGenerator,
      ServiceGenerator serviceGenerator,
      ControllerGenerator controllerGenerator,
      EntityGenerator entityGenerator,
      ApplicationGenerator applicationGenerator,
      DTOGenerator dtoGenerator,
      DTOConverterGenerator dtoConverterGenerator,
      MavenGenerator mavenGenerator,
      ApplicationPropertiesGenerator applicationPropertiesGenerator) {
    this.repositoryGenerator = repositoryGenerator;
    this.serviceGenerator = serviceGenerator;
    this.controllerGenerator = controllerGenerator;
    this.entityGenerator = entityGenerator;
    this.applicationGenerator = applicationGenerator;
    this.dtoGenerator = dtoGenerator;
    this.dtoConverterGenerator = dtoConverterGenerator;
    this.mavenGenerator = mavenGenerator;
    this.applicationPropertiesGenerator = applicationPropertiesGenerator;
  }

  public void generate(List<Model> models) throws IOException {
    generateMaven();
    generateApplication();

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
        return basePackage + ".controller.dto." + model.name() + "DTO";
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
          manyToOneRelationshipsForModel.getOrDefault(model, List.of()));
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

  private void generate(Model model, Map<String, String> importsByModelName,
      List<Model> manyToOneSideModels) throws IOException {
    String basePackage = "com.evans";
    String repositoryPackage = basePackage + ".repository";
    String entityImport = repositoryPackage + "." + model.name();

    String modelNameCamel = model.name().substring(0, 1).toLowerCase() + model.name().substring(1);

    List<Field> fields = model.fields().stream()
        .map(fieldDefinition -> createField(importsByModelName, fieldDefinition))
        .toList();

    List<String> oneToManyImports = model.fields()
        .stream()
        .filter(FieldDefinition::isOneToMany)
        .map(field -> (OneToManyField) field)
        .map(OneToManyField::associationModel)
        .map(Model::name)
        .map(name -> repositoryPackage + "." + name)
        .toList();

    List<String> oneToManyDTOimports = model.fields().stream()
        .filter(FieldDefinition::isOneToMany)
        .map(OneToManyField.class::cast)
        .map(OneToManyField::associationModel)
        .map(Model::name)
        .map(name -> name + "DTO")
        .map(importsByModelName::get)
        .toList();

    List<String> oneToManyRepositoryImports = model.fields().stream()
        .filter(FieldDefinition::isOneToMany)
        .map(OneToManyField.class::cast)
        .map(OneToManyField::associationModel)
        .map(Model::name)
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
    String dtoImport = dtoPackage + "." + dtoType;

    String servicePackage = basePackage + ".service";
    String dtoNameCamel = modelNameCamel + "DTO";

    String dtoConverterType = model.name() + "DTOConverter";
    String dtoConverterPackage = servicePackage + ".converter";
    String dtoConverterImport = dtoConverterPackage + "." + dtoConverterType;
    String dtoConverterName =
        dtoConverterType.substring(0, 1).toLowerCase() + dtoConverterType.substring(1);

    List<String> serviceImports = Stream.of(
            List.of(entityImport, dtoImport, dtoConverterImport, repositoryImport, entityIdTypeImport),
            oneToManyDTOimports, oneToManyRepositoryImports)
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
        .toList();

    List<Field> dtoFields = model.fields().stream()
        .map(fieldDefinition -> createDTOField(importsByModelName, fieldDefinition))
        .toList();

    DTO dto = new DTO(dtoPackage, dtoType, dtoFields, dtoImports);
    dtoGenerator.generate(dto);

    List<String> dtoConverterImports = Stream.of(oneToManyImports, List.of(entityImport, dtoImport),
            oneToManyDTOimports)
        .flatMap(Collection::stream)
        .toList();

    List<Subconverter> dtoSubconverters = model.fields().stream()
        .filter(FieldDefinition::isOneToMany)
        .map(field -> {
          Model associationModel = ((OneToManyField) field).associationModel();
          return new Subconverter(
              associationModel.name(),
              associationModel.nameCamel() + "DTOConverter",
              associationModel.name() + "DTO",
              associationModel.name(),
              field.nameCapitalised());
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
    if (fieldDefinition.type() == FieldType.ONE_TO_MANY) {
      associationModelName = ((OneToManyField) fieldDefinition).associationModel().name();
    }

    return new Field(
        fieldDefinition.name(),
        typeInformation.simpleName(),
        fieldDefinition.isId(),
        fieldDefinition.type() == FieldType.DATE_TIME,
        fieldDefinition.type() == FieldType.BOOLEAN,
        relationship,
        associationModelName
    );
  }

  private Relationship resolveRelationship(FieldType fieldType) {
    return switch (fieldType) {
      case ONE_TO_MANY -> Relationship.ONE_TO_MANY;
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
      case STRING -> TypeInformation.of(String.class);
      case DOUBLE -> TypeInformation.of(Double.class);
      case ID -> TypeInformation.of(Long.class);
      case ENUM -> new TypeInformation(null, fieldDefinition.name(), List.of());
      case ONE_TO_MANY -> resolveOneToManyTypeInformation((OneToManyField) fieldDefinition,
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
