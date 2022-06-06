package com.evans.codegen.config;

import com.evans.codegen.file.react.AppJsGenerator;
import com.evans.codegen.file.react.CreateEntityGenerator;
import com.evans.codegen.file.react.EditEntityGenerator;
import com.evans.codegen.file.react.EntityListGenerator;
import com.evans.codegen.file.react.IndexJsGenerator;
import com.evans.codegen.file.react.PackageJsonGenerator;
import com.evans.codegen.file.react.ViewEntitiesGenerator;
import com.evans.codegen.file.react.ViewSingleEntityGenerator;
import dagger.Module;
import dagger.Provides;

@Module
public class FrontendFileGeneratorModule {

  @Provides
  public PackageJsonGenerator packageJsonGenerator() {
    return new PackageJsonGenerator();
  }

  @Provides
  public AppJsGenerator appJsGenerator() {
    return new AppJsGenerator();
  }

  @Provides
  public IndexJsGenerator indexJsGenerator() {
    return new IndexJsGenerator();
  }

  @Provides
  public CreateEntityGenerator createEntityGenerator() {
    return new CreateEntityGenerator();
  }

  @Provides
  public EntityListGenerator entityListGenerator() {
    return new EntityListGenerator();
  }

  @Provides
  public ViewEntitiesGenerator viewEntitiesGenerator() {
    return new ViewEntitiesGenerator();
  }

  @Provides
  public ViewSingleEntityGenerator viewSingleEntityGenerator() {
    return new ViewSingleEntityGenerator();
  }

  @Provides
  public EditEntityGenerator editEntityGenerator() {
    return new EditEntityGenerator();
  }
}
