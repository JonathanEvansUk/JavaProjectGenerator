package com.evans.codegen;

import com.evans.codegen.config.DaggerGeneratorFactory;
import com.evans.codegen.domain.Entity;
import com.evans.codegen.domain.FieldDefinition.*;
import com.evans.codegen.domain.FieldDefinition.RelationalField.ManyToOneField;
import com.evans.codegen.domain.FieldDefinition.RelationalField.OneToManyField;
import com.evans.codegen.generator.Generator;
import org.apache.maven.shared.invoker.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorIT {

  /**
   * This test is used to strictly verify the output matches the expected output.
   * This test is useful when refactoring the generator logic to ensure you are still outputting the same data.
   */
  @Test
  public void verifyFilesAreExactlyGeneratedToMatchOutput() throws IOException {
    Generator generator = DaggerGeneratorFactory.create().getGenerator();

    // define entities
    List<Entity> entities = basicEntityWithNoRelationships();

    // generate code
    generator.generate(entities);

    // verify that output folder contains expected output
    Path expectedOutputFolder = Path.of("src/test/resources/testOutputs/bill");

    Path output = Path.of("output");

    try (Stream<Path> walk = Files.walk(expectedOutputFolder)) {
      walk.forEach(file -> {
        Path relativePath = expectedOutputFolder.relativize(file);

        //verify that relativePath exists in /output
        Path outputFilePath = output.resolve(relativePath);
        assertTrue(Files.exists(outputFilePath), "Expected file to exist: " + outputFilePath);

        if (Files.isRegularFile(file)) {
          compareFiles(file, outputFilePath);
        }
      });
    }
  }

  private static Stream<List<Entity>> entities() {
    return Stream.of(
        entitiesWithManyToOneRelationship(),
        entitiesWithOneToManyRelationship()
    );
  }

  private static List<Entity> entitiesWithManyToOneRelationship() {
    Entity author = Entity.of("Author",
        List.of(
            new IdField("id", true),
            new StringField("name", false, "John Doe"),
            new StringField("email", false, "")
        )
    );

    Entity post = Entity.of("Post",
        List.of(
            new IdField("id", true),
            new StringField("title", false, "Hello World"),
            new ManyToOneField("author", false, author)
        ));

    return List.of(
        post,
        author
    );
  }

  private static List<Entity> entitiesWithOneToManyRelationship() {
    Entity post = Entity.of("Post",
        List.of(
            new IdField("id", true),
            new StringField("title", false, "Hello World")
        ));

    Entity author = Entity.of("Author",
        List.of(
            new IdField("id", true),
            new StringField("name", false, "John Doe"),
            new OneToManyField("posts", false, post)
        )
    );


    return List.of(
        post,
        author
    );
  }

  @ParameterizedTest
  @MethodSource("entities")
  public void shouldGenerateWithoutAnyFailures(List<Entity> entities) {
    Generator generator = DaggerGeneratorFactory.create().getGenerator();

    assertDoesNotThrow(() -> generator.generate(entities));
  }

  @ParameterizedTest
  @MethodSource("entities")
  public void generate(List<Entity> entities) throws IOException {
    Generator generator = DaggerGeneratorFactory.create().getGenerator();

    // generate code
    generator.generate(entities);

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

  private static List<Entity> basicEntityWithNoRelationships() {
    return List.of(
        Entity.of("Bill",
            List.of(
                new IdField("id", true),
                new DoubleField("amount", false, "12.34"),
                new DateField("dateReceived", false, "2023-01-03"),
                new BooleanField("paid", false),
                new DateTimeField("datePaid", false, "2023-01-03T10:15:30.00Z")
            )
        )
    );
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
