package com.evans.generator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.evans.generator.config.DaggerGeneratorFactory;
import com.evans.generator.domain.FieldDefinition.BooleanField;
import com.evans.generator.domain.FieldDefinition.DateField;
import com.evans.generator.domain.FieldDefinition.DateTimeField;
import com.evans.generator.domain.FieldDefinition.DoubleField;
import com.evans.generator.domain.FieldDefinition.EnumField;
import com.evans.generator.domain.FieldDefinition.IdField;
import com.evans.generator.domain.Model;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class GeneratorIT {

  @Test
  public void generate() throws IOException {
    Generator generator = DaggerGeneratorFactory.create().getGenerator();

    // define models
    List<Model> models = List.of(
        Model.of("Bill",
            List.of(
                new IdField("id", true),
                new DoubleField("amount", false),
                new DateField("dateReceived", false),
                new BooleanField("paid", false),
                new DateTimeField("datePaid", false),
                new EnumField("paymentType", List.of("Credit", "Debit"), true)
            )
        )
    );

    // generate code
    generator.generate(models);

    // verify that output folder contains expected output
    Path expectedOutputFolder = Path.of("src/test/resources/testOutputs/bill");

    Path output = Path.of("output");

    try (Stream<Path> walk = Files.walk(expectedOutputFolder)) {
      walk.forEach(file -> {
        Path relativePath = expectedOutputFolder.relativize(file);

        //verify that relativePath exists in /output
        Path outputFilePath = output.resolve(relativePath);
        assertTrue(Files.exists(outputFilePath));

        if (Files.isRegularFile(file)) {
          compareFiles(file, outputFilePath);
        }
      });
    }
  }

  private void compareFiles(Path expectedPath, Path actualPath) {
    try {
      var expected = Files.readAllBytes(expectedPath);
      var actual = Files.readAllBytes(actualPath);

      assertArrayEquals(expected, actual);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Failed to compare files: " + expectedPath + ", " + actualPath);
    }
  }
}
