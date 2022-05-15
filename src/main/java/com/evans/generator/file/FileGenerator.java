package com.evans.generator.file;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface FileGenerator<T> {

  default void generate(T templateData) throws IOException {
    String templateText = Files.readString(Path.of("src/main/resources", templateName()));

    Template template = Mustache.compiler().compile(templateText);

    String fileText = template.execute(templateData);
    System.out.println(fileText);

    // write to file
    String outputDirectory = outputDirectory(templateData);
    Files.createDirectories(Paths.get(outputDirectory));

    try (BufferedWriter bufferedWriter = new BufferedWriter(
        new FileWriter(new File(outputDirectory, outputFileName(templateData))))) {
      bufferedWriter.write(fileText);
    }
  }

  String templateName();

  String outputFileName(T templateData);

  default String outputDirectory(T templateData) {
    return "output/";
  }
}
