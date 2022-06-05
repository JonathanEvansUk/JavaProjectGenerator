package com.evans.generator.config;

import com.evans.generator.file.react.AppJsGenerator;
import com.evans.generator.file.react.CreateEntityGenerator;
import com.evans.generator.file.react.EditEntityGenerator;
import com.evans.generator.file.react.EntityListGenerator;
import com.evans.generator.file.react.IndexJsGenerator;
import com.evans.generator.file.react.PackageJsonGenerator;
import com.evans.generator.file.react.ViewEntitiesGenerator;
import com.evans.generator.file.react.ViewSingleEntityGenerator;
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
