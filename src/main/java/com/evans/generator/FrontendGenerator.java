package com.evans.generator;

import com.evans.generator.domain.Model;
import com.evans.generator.file.react.AppJsGenerator;
import com.evans.generator.file.react.AppJsGenerator.AppJs;
import com.evans.generator.file.react.AppJsGenerator.WebModel;
import com.evans.generator.file.react.CreateEntityGenerator;
import com.evans.generator.file.react.CreateEntityGenerator.EntityForm;
import com.evans.generator.file.react.EditEntityGenerator;
import com.evans.generator.file.react.EntityListGenerator;
import com.evans.generator.file.react.EntityListGenerator.EntityList;
import com.evans.generator.file.react.IndexJsGenerator;
import com.evans.generator.file.react.IndexJsGenerator.IndexJs;
import com.evans.generator.file.react.PackageJsonGenerator;
import com.evans.generator.file.react.PackageJsonGenerator.PackageJson;
import com.evans.generator.file.react.ViewEntitiesGenerator;
import com.evans.generator.file.react.ViewEntitiesGenerator.EntitiesList;
import com.evans.generator.file.react.ViewSingleEntityGenerator;
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

  public void generate(List<Model> models) throws IOException {
    var webModels = models.stream()
        .map(model -> new WebModel(model.name(), model.fields()))
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

    Copy.files("DeleteButton.js", "DeleteModal.js")
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
