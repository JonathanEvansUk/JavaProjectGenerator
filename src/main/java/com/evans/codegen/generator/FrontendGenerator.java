package com.evans.codegen.generator;

import com.evans.codegen.domain.FieldDefinition;
import com.evans.codegen.domain.FieldDefinition.RelationalField;
import com.evans.codegen.domain.Entity;
import com.evans.codegen.file.react.AppJsGenerator;
import com.evans.codegen.file.react.AppJsGenerator.AppJs;
import com.evans.codegen.file.react.AppJsGenerator.WebField;
import com.evans.codegen.file.react.AppJsGenerator.WebEntity;
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
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
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

  private List<WebField> convert(List<FieldDefinition> fields) {
    return fields.stream()
        .map(field -> {
          if (field instanceof RelationalField relationalField) {
            return new WebField(
                field.name(),
                field.required(),
                field.type(),
                camelise(relationalField.associationEntity().name())
            );
          }

          return new WebField(field.name(), field.required(), field.type());
        })
        .toList();
  }

  private String camelise(String string) {
    return string.substring(0, 1).toLowerCase() + string.substring(1);
  }

  public void generate(List<Entity> entities) throws IOException {
    var webEntities = entities.stream()
        .map(entity -> new WebEntity(entity.name(), convert(entity.fields())))
        .toList();

    packageJsonGenerator.generate(new PackageJson("MyApp"));
    appJsGenerator.generate(new AppJs(webEntities));
    indexJsGenerator.generate(new IndexJs(webEntities));
    viewEntitiesGenerator.generate(new EntitiesList(webEntities));

    for (WebEntity entity : webEntities) {

      EntityForm createEntityForm = entityFormMapper.createEntityForm(entity,
          "Create a " + entity.nameCapitalised());
      createEntityGenerator.generate(createEntityForm);

      EntityForm viewEntityForm = entityFormMapper.createEntityForm(entity,
          "View " + entity.nameCapitalised());
      viewSingleEntityGenerator.generate(viewEntityForm);

      EntityForm editEntityForm = entityFormMapper.createEntityForm(entity,
          "Edit " + entity.nameCapitalised());
      editEntityGenerator.generate(editEntityForm);

      entityListGenerator.generate(new EntityList(entity));
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

    Copy.files(".dockerignore", "Dockerfile", "nginx.conf")
        .from("src/main/resources/docker/frontend")
        .to("output/web");
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
