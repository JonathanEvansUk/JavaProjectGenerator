package com.evans.codegen;

import com.evans.codegen.config.DaggerGeneratorFactory;
import com.evans.codegen.domain.FieldDefinition;
import com.evans.codegen.domain.FieldDefinition.DateField;
import com.evans.codegen.domain.FieldDefinition.DateTimeField;
import com.evans.codegen.domain.FieldDefinition.EnumField;
import com.evans.codegen.domain.FieldDefinition.IdField;
import com.evans.codegen.domain.FieldDefinition.JsonField;
import com.evans.codegen.domain.FieldDefinition.RelationalField.ManyToOneField;
import com.evans.codegen.domain.FieldDefinition.StringField;
import com.evans.codegen.domain.Model;
import com.evans.codegen.generator.Generator;
import java.io.IOException;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Running generator");

    Generator generator = DaggerGeneratorFactory.create().getGenerator();

//    Model comment = Model.of("Comment",
//        List.of(
//            new IdField("id", true),
//            new StringField("review", true, "5 Stars!")
//        ));
//
//    Model author = Model.of("Author",
//        List.of(
//            new IdField("id", true),
//            new StringField("firstname", true, "Jon")
//        ));
//
//    Model post = Model.of("Post",
//        List.of(
//            new IdField("id", true),
//            new StringField("title", true, "My first blog post!"),
//            new DateTimeField("dateAdded", true, "2022-12-25T00:00:00Z"),
//            new DateField("availableUntil", true, "2023-01-01"),
//            new OneToManyField("comments", false, comment),
//            new OneToManyField("authors", false, author)
//        ));

//    generator.generate(List.of(post, comment, author));

    Model authFile = Model.of(
        "AuthFile",
        List.of(
            new IdField("id", true),
            new StringField("name", true, "dev"),
            new JsonField("file", true)
        )
    );

    Model device = Model.of(
        "Device",
        List.of(
            new IdField("id", true),
            new StringField("deviceId", true, "12345"),
            new StringField("reference", true, "Jons box"),
            new ManyToOneField("authFile", true, authFile)
        )
    );

    generator.generate(List.of(authFile, device));

    System.out.println("Finished generating");
  }
}
