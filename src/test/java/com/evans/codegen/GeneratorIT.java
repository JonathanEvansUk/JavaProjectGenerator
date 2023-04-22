package com.evans.codegen;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.evans.codegen.config.DaggerGeneratorFactory;
import com.evans.codegen.domain.FieldDefinition.BooleanField;
import com.evans.codegen.domain.FieldDefinition.DateField;
import com.evans.codegen.domain.FieldDefinition.DateTimeField;
import com.evans.codegen.domain.FieldDefinition.DoubleField;
import com.evans.codegen.domain.FieldDefinition.IdField;
import com.evans.codegen.domain.Model;
import com.evans.codegen.generator.Generator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
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
                new DoubleField("amount", false, "12.34"),
                new DateField("dateReceived", false, "2023-01-03"),
                new BooleanField("paid", false),
                new DateTimeField("datePaid", false, "2023-01-03T10:15:30.00Z")
//                new EnumField("paymentType", List.of("Credit", "Debit"), true)
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

    // run maven verify on the generated project
    InvocationRequest request = new DefaultInvocationRequest()
        .setPomFile(new File("output/pom.xml"))
        .setGoals(List.of("clean", "verify"))
        .setDebug(false)
        .setJavaHome(new File(System.getenv("JAVA_HOME")))
        .setMavenHome(new File(System.getProperty("maven.home")));

    Invoker invoker = new DefaultInvoker();
    try {
      InvocationResult result = invoker.execute(request);

      if (result.getExitCode() != 0) {
        if (result.getExecutionException() != null) {
          System.out.println(result.getExecutionException().getMessage());
        }

        fail();
      }
    } catch (MavenInvocationException e) {
      throw new RuntimeException(e);
    }
  }

  private void compareFiles(Path expectedPath, Path actualPath) {
    try {
      var expected = Files.readAllBytes(expectedPath);
      var actual = Files.readAllBytes(actualPath);

      assertArrayEquals(expected, actual,
          "Expected: " + expectedPath + ", to match: " + actualPath);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Failed to compare files: " + expectedPath + ", " + actualPath);
    }
  }
}
