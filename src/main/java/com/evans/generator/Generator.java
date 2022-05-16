package com.evans.generator;

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
import com.evans.generator.file.react.AppJsGenerator;
import com.evans.generator.file.react.AppJsGenerator.AppJs;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.EntityFormGenerator;
import com.evans.generator.file.react.EntityFormGenerator.EntityForm;
import com.evans.generator.file.react.EntityListGenerator;
import com.evans.generator.file.react.EntityListGenerator.EntityList;
import com.evans.generator.file.react.IndexJsGenerator;
import com.evans.generator.file.react.IndexJsGenerator.IndexJs;
import com.evans.generator.file.react.PackageJsonGenerator;
import com.evans.generator.file.react.PackageJsonGenerator.PackageJson;
import com.evans.generator.file.react.ViewEntitiesGenerator;
import com.evans.generator.file.react.ViewEntitiesGenerator.EntitiesList;
import com.evans.generator.file.react.ViewSingleEntityGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Generator {

  private final RepositoryGenerator repositoryGenerator;
  private final ServiceGenerator serviceGenerator;
  private final ControllerGenerator controllerGenerator;
  private final EntityGenerator entityGenerator;
  private final ApplicationGenerator applicationGenerator;

  private final MavenGenerator mavenGenerator;
  private final ApplicationPropertiesGenerator applicationPropertiesGenerator;

  private final PackageJsonGenerator packageJsonGenerator;
  private final AppJsGenerator appJsGenerator;
  private final IndexJsGenerator indexJsGenerator;
  private final EntityFormGenerator entityFormGenerator;
  private final EntityListGenerator entityListGenerator;
  private final ViewEntitiesGenerator viewEntitiesGenerator;
  private final ViewSingleEntityGenerator viewSingleEntityGenerator;

  public Generator(RepositoryGenerator repositoryGenerator, ServiceGenerator serviceGenerator,
      ControllerGenerator controllerGenerator, EntityGenerator entityGenerator,
      MavenGenerator mavenGenerator,
      ApplicationGenerator applicationGenerator,
      ApplicationPropertiesGenerator applicationPropertiesGenerator,
      PackageJsonGenerator packageJsonGenerator,
      AppJsGenerator appJsGenerator,
      IndexJsGenerator indexJsGenerator,
      EntityFormGenerator entityFormGenerator,
      EntityListGenerator entityListGenerator,
      ViewEntitiesGenerator viewEntitiesGenerator,
      ViewSingleEntityGenerator viewSingleEntityGenerator) {
    this.repositoryGenerator = repositoryGenerator;
    this.serviceGenerator = serviceGenerator;
    this.controllerGenerator = controllerGenerator;
    this.entityGenerator = entityGenerator;
    this.mavenGenerator = mavenGenerator;
    this.applicationGenerator = applicationGenerator;
    this.applicationPropertiesGenerator = applicationPropertiesGenerator;
    this.packageJsonGenerator = packageJsonGenerator;
    this.appJsGenerator = appJsGenerator;
    this.indexJsGenerator = indexJsGenerator;
    this.entityFormGenerator = entityFormGenerator;
    this.entityListGenerator = entityListGenerator;
    this.viewEntitiesGenerator = viewEntitiesGenerator;
    this.viewSingleEntityGenerator = viewSingleEntityGenerator;
  }

  //TODO find better name for this
  public record Model(String name,
                      Class<?> idType,
                      List<Field> fields) {

    static Model of(String name, List<Field> fields) {
      Class<?> idType = fields.stream()
          .filter(Field::isId)
          .findFirst()
          .map(field -> field.type)
          .orElseThrow(() -> new RuntimeException(
              "No ID field provided for model: " + name + ", fields: " + fields));

      return new Model(name, idType, fields);
    }
  }

  public record Field(String name,
                      Class<?> type,
                      String simpleTypeName,
                      boolean isId,
                      boolean required) {

    public Field(String name, Class<?> type, boolean isId, boolean required) {
      this(name, type, type.getSimpleName(), isId, isId || required);
    }

    public String getterName() {
      return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public String setterName() {
      return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }
  }


  public void generate(List<Model> models) throws IOException {
//    clearOutputDirectory();
    generateMaven();
    generateApplication();
    for (Model model : models) {
      generate(model);
    }

    var webModels = models.stream()
        .map(model -> new WebModel(model.name(), model.fields()))
        .toList();

    generateWebApp(webModels);
  }

  private void clearOutputDirectory() throws IOException {
    if (Files.exists(Path.of("output/"))) {
      Files.walk(Path.of("output/"))
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
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

  private void generateWebApp(List<WebModel> webModels) throws IOException {
    packageJsonGenerator.generate(new PackageJson("MyApp"));
    appJsGenerator.generate(new AppJs(webModels));
    indexJsGenerator.generate(new IndexJs(webModels));
    viewEntitiesGenerator.generate(new EntitiesList(webModels));

    for (WebModel model : webModels) {
      EntityForm entityForm = new EntityForm(model);
      entityFormGenerator.generate(entityForm);
      viewSingleEntityGenerator.generate(entityForm);

      entityListGenerator.generate(new EntityList(model));
    }

    Path webSrcPath = Path.of("output/web/src/");
    Files.createDirectories(webSrcPath);

    Files.copy(Path.of("src/main/resources/react/index.html"),
        webSrcPath.resolve("index.html"),
        StandardCopyOption.REPLACE_EXISTING
    );

    Files.copy(Path.of("src/main/resources/react/home.js"),
        webSrcPath.resolve("routes/home.js"),
        StandardCopyOption.REPLACE_EXISTING
    );

    Path webUtilsPath = webSrcPath.resolve("utils");
    Files.createDirectories(webUtilsPath);

    Files.copy(Path.of("src/main/resources/react/DeleteButton.js"),
        webUtilsPath.resolve("DeleteButton.js"),
        StandardCopyOption.REPLACE_EXISTING
    );

    Files.copy(Path.of("src/main/resources/react/DeleteModal.js"),
        webUtilsPath.resolve("DeleteModal.js"),
        StandardCopyOption.REPLACE_EXISTING
    );

//    Files.copy(Path.of("src/main/resources/react/index.js"),
//        webSrcPath.resolve("index.js"),
//        StandardCopyOption.REPLACE_EXISTING
//    );

//    Files.copy(Path.of("src/main/resources/react/App.js"),
//        webSrcPath.resolve("App.js"),
//        StandardCopyOption.REPLACE_EXISTING
//    );
  }
}
