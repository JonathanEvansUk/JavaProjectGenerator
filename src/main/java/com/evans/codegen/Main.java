package com.evans.codegen;

import com.evans.codegen.config.DaggerGeneratorFactory;
import com.evans.codegen.domain.Entity;
import com.evans.codegen.domain.FieldDefinition.BooleanField;
import com.evans.codegen.domain.FieldDefinition.DateField;
import com.evans.codegen.domain.FieldDefinition.DateTimeField;
import com.evans.codegen.domain.FieldDefinition.DoubleField;
import com.evans.codegen.domain.FieldDefinition.IdField;
import com.evans.codegen.generator.Generator;
import java.io.IOException;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Running generator");

    Generator generator = DaggerGeneratorFactory.create().getGenerator();

    List<Entity> entities = List.of(
        Entity.of("Bill",
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

    generator.generate(entities);

    System.out.println("Finished generating");
  }
}
