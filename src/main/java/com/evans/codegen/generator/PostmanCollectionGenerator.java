package com.evans.codegen.generator;

import com.evans.codegen.domain.Model;
import com.evans.codegen.domain.postman.Body;
import com.evans.codegen.domain.postman.Collection;
import com.evans.codegen.domain.postman.Entry;
import com.evans.codegen.domain.postman.Info;
import com.evans.codegen.domain.postman.Items;
import com.evans.codegen.domain.postman.Items.Item;
import com.evans.codegen.domain.postman.Request;
import com.evans.codegen.domain.postman.Url;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;

public class PostmanCollectionGenerator {

  private static final String POSTMAN_COLLECTION_SCHEMA_URL = "https://schema.getpostman.com/json/collection/v2.1" +
          ".0/collection.json";
  private final ObjectMapper objectMapper;

  @Inject
  public PostmanCollectionGenerator(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String generate(String appName, List<Model> models) throws JsonProcessingException {

    Info info = new Info(
            appName,
            UUID.randomUUID().toString(),
            "description",
            POSTMAN_COLLECTION_SCHEMA_URL
    );

    String baseUrl = "localhost:8080/";

    List<Items> items = models.stream()
            .map(model -> {
              try {
                return createRequestFolder(baseUrl, model);
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            })
            .toList();

    Collection collection = new Collection(
            info,
            items,
            List.of(),
            List.of(),
            null
    );

    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collection);
  }

  private Items createRequestFolder(String baseUrl, Model model) throws JsonProcessingException {
    Url entityUrl = new Url("localhost", 8080, List.of(model.nameCamel()));

    Item getAllRequest = new Item(
            UUID.randomUUID().toString(),
            "Get all " + model.name(),
            List.of(),
            List.of(),
            new Request(
                    entityUrl,
                    "GET",
                    "Request to get all " + model.name())
    );


    ObjectNode objectNode = objectMapper.createObjectNode();
    model.fields()
            .forEach(field -> objectNode.put(field.name(), field.example()));

    String id = "1";
    Url byIdUrl = new Url(
            "localhost", 8080,
            List.of(model.nameCamel(), ":id"),
            List.of(new Entry("id", id)));

    String createRequestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
    Item createRequest = new Item(
            UUID.randomUUID().toString(),
            "Create " + model.name(),
            List.of(),
            List.of(),
            new Request(entityUrl, "POST", "Request to create a " + model.name(),
                    new Body(
                            "raw",
                            createRequestBody,
                            Map.of("raw", Map.of("language", "json"))
                    ))
    );

    Item getByIdRequest = new Item(
            UUID.randomUUID().toString(),
            "Get " + model.name() + " by id",
            List.of(),
            List.of(),
            new Request(byIdUrl, "GET", "Request to get a " + model.name() + " by id")
    );

    Item updateByIdRequest = new Item(
            UUID.randomUUID().toString(),
            "Update " + model.name() + " by id",
            List.of(),
            List.of(),
            new Request(byIdUrl, "PUT", "Request to update a " + model.name() + " by id")
    );

    Item deleteByIdRequest = new Item(
            UUID.randomUUID().toString(),
            "Delete " + model.name() + " by id",
            List.of(),
            List.of(),
            new Request(byIdUrl, "DELETE", "Request to delete a " + model.name() + " by id")
    );

    return new Items.Folder(model.name(),
            "Description for " + model.name(),
            List.of(getAllRequest, createRequest, getByIdRequest, updateByIdRequest, deleteByIdRequest));
  }

}
