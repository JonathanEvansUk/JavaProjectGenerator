package com.evans.codegen.generator;

import com.evans.codegen.domain.FieldDefinition;
import com.evans.codegen.domain.FieldDefinition.RelationalField;
import com.evans.codegen.domain.FieldDefinition.RelationalField.OneToManyField;
import com.evans.codegen.domain.Model;
import com.evans.codegen.file.react.AppJsGenerator;
import com.evans.codegen.file.react.AppJsGenerator.AppJs;
import com.evans.codegen.file.react.AppJsGenerator.WebField;
import com.evans.codegen.file.react.AppJsGenerator.WebModel;
import com.evans.codegen.file.react.CreateEntityGenerator;
import com.evans.codegen.file.react.CreateEntityGenerator.EntityForm;
import com.evans.codegen.file.react.EditEntityGenerator;
import com.evans.codegen.file.react.EntityListGenerator;
import com.evans.codegen.file.react.EntityListGenerator.EntityList;
import com.evans.codegen.file.react.IndexJsGenerator;
import com.evans.codegen.file.react.IndexJsGenerator.IndexJs;
import com.evans.codegen.file.react.PackageJsonGenerator;
import com.evans.codegen.file.react.PackageJsonGenerator.PackageJson;
import com.evans.codegen.file.react.ViewEntitiesGenerator;
import com.evans.codegen.file.react.ViewEntitiesGenerator.EntitiesList;
import com.evans.codegen.file.react.ViewSingleEntityGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.inject.Inject;

public class FrontendGenerator {

  private final PackageJsonGenerator packageJsonGenerator;
  private final AppJsGenerator appJsGenerator;
  private final IndexJsGenerator indexJsGenerator;
  private final CreateEntityGenerator createEntityGenerator;
  private final EntityListGenerator entityListGenerator;
  private final ViewEntitiesGenerator viewEntitiesGenerator;
  private final ViewSingleEntityGenerator viewSingleEntityGenerator;
  private final EditEntityGenerator editEntityGenerator;

  private final EntityFormMapper entityFormMapper;

  @Inject
  public FrontendGenerator(PackageJsonGenerator packageJsonGenerator,
      AppJsGenerator appJsGenerator,
      IndexJsGenerator indexJsGenerator,
      CreateEntityGenerator createEntityGenerator,
      EntityListGenerator entityListGenerator,
      ViewEntitiesGenerator viewEntitiesGenerator,
      ViewSingleEntityGenerator viewSingleEntityGenerator,
      EditEntityGenerator editEntityGenerator,
      EntityFormMapper entityFormMapper) {
    this.packageJsonGenerator = packageJsonGenerator;
    this.appJsGenerator = appJsGenerator;
    this.indexJsGenerator = indexJsGenerator;
    this.createEntityGenerator = createEntityGenerator;
    this.entityListGenerator = entityListGenerator;
    this.viewEntitiesGenerator = viewEntitiesGenerator;
    this.viewSingleEntityGenerator = viewSingleEntityGenerator;
    this.editEntityGenerator = editEntityGenerator;
    this.entityFormMapper = entityFormMapper;
  }

  private List<WebField> convert(List<FieldDefinition> fields) {
    return fields.stream()
        .map(field -> {
          if (field instanceof RelationalField relationalField) {
            return new WebField(
                field.name(),
                field.required(),
                field.type(),
                camelise(relationalField.associationModel().name())
            );
          }

          return new WebField(field.name(), field.required(), field.type());
        })
        .toList();
  }

  private String camelise(String string) {
    return string.substring(0, 1).toLowerCase() + string.substring(1);
  }

  public void generate(List<Model> models) throws IOException {
    var webModels = models.stream()
        .map(model -> new WebModel(model.name(), convert(model.fields())))
        .toList();

    packageJsonGenerator.generate(new PackageJson("MyApp"));
    appJsGenerator.generate(new AppJs(webModels));
    indexJsGenerator.generate(new IndexJs(webModels));
    viewEntitiesGenerator.generate(new EntitiesList(webModels));

    for (WebModel model : webModels) {

      EntityForm createEntityForm = entityFormMapper.createEntityForm(model,
          "Create a " + model.nameCapitalised());
      createEntityGenerator.generate(createEntityForm);

      EntityForm viewEntityForm = entityFormMapper.createEntityForm(model,
          "View " + model.nameCapitalised());
      viewSingleEntityGenerator.generate(viewEntityForm);

      EntityForm editEntityForm = entityFormMapper.createEntityForm(model,
          "Edit " + model.nameCapitalised());
      editEntityGenerator.generate(editEntityForm);

      entityListGenerator.generate(new EntityList(model));
    }

    String staticReactResourcesPath = "src/main/resources/react";
    Copy.files("index.html")
        .from(staticReactResourcesPath)
        .to("output/web/src");

    Copy.files("home.js")
        .from(staticReactResourcesPath)
        .to("output/web/src/routes");

    Copy.files("DeleteButton.js", "DeleteModal.js", "JsonFileUploadWidget.js", "JsonViewerTheme.js",
            "Utils.js")
        .from(staticReactResourcesPath)
        .to("output/web/src/utils");
  }

  record Copy(List<String> files) {

    public static Copy files(String... files) {
      return new Copy(List.of(files));
    }

    public CopyFrom from(String from) {
      return new CopyFrom(files, from);
    }

    record CopyFrom(List<String> files,
                    String from) {

      public void to(String to) throws IOException {
        Path outputDirectory = Path.of(to);
        Files.createDirectories(outputDirectory);
        for (String file : files) {
          Files.copy(Path.of(from).resolve(file),
              outputDirectory.resolve(file),
              StandardCopyOption.REPLACE_EXISTING);
        }

      }
    }
  }
}
