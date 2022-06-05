package com.evans.generator;

import com.evans.generator.config.DaggerGeneratorFactory;
import com.evans.generator.domain.FieldDefinition.BooleanField;
import com.evans.generator.domain.FieldDefinition.DateField;
import com.evans.generator.domain.FieldDefinition.DateTimeField;
import com.evans.generator.domain.FieldDefinition.DoubleField;
import com.evans.generator.domain.FieldDefinition.EnumField;
import com.evans.generator.domain.FieldDefinition.IdField;
import com.evans.generator.domain.Model;
import java.io.IOException;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Running generator");

    Generator generator = DaggerGeneratorFactory.create().getGenerator();

    generator.generate(
        List.of(
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
        )
    );

    System.out.println("Finished generating");
  }
}
