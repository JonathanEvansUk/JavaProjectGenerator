package com.evans.codegen.generator;

import com.evans.codegen.domain.Model;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Generator {

  private final BackendGenerator backendGenerator;
  private final FrontendGenerator frontendGenerator;

  public void generate(List<Model> models) throws IOException {
//    clearOutputDirectory();
    backendGenerator.generate(models);
    frontendGenerator.generate(models);
  }

  private void clearOutputDirectory() throws IOException {
    if (Files.exists(Path.of("output/"))) {
      Files.walk(Path.of("output/"))
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }
  }
}
