package com.evans.codegen;

import com.evans.codegen.config.DaggerGeneratorFactory;
import com.evans.codegen.domain.FieldDefinition.IdField;
import com.evans.codegen.domain.FieldDefinition.OneToManyField;
import com.evans.codegen.domain.FieldDefinition.StringField;
import com.evans.codegen.domain.Model;
import com.evans.codegen.generator.Generator;
import java.io.IOException;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Running generator");

    Generator generator = DaggerGeneratorFactory.create().getGenerator();

    Model comment = Model.of("Comment",
        List.of(
            new IdField("id", true),
            new StringField("review", true)
        ));

    Model author = Model.of("Author",
        List.of(
            new IdField("id", true),
            new StringField("firstname", true)
        ));

    Model post = Model.of("Post",
        List.of(
            new IdField("id", true),
            new StringField("title", true),
            new OneToManyField("comments", false, comment),
            new OneToManyField("authors", false, author)
        ));

    generator.generate(List.of(post, comment, author));

    System.out.println("Finished generating");
  }
}
