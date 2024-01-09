package com.evans.codegen.generator;

import com.evans.codegen.domain.Entity;
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

  public String generate(String appName, List<Entity> entities) throws JsonProcessingException {

    Info info = new Info(
            appName,
            UUID.randomUUID().toString(),
            "description",
            POSTMAN_COLLECTION_SCHEMA_URL
    );

    String baseUrl = "localhost:8080/";

    List<Items> items = entities.stream()
            .map(entity -> {
              try {
                return createRequestFolder(baseUrl, entity);
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

  private Items createRequestFolder(String baseUrl, Entity entity) throws JsonProcessingException {
    Url entityUrl = new Url("localhost", 8080, List.of(entity.nameCamel()));

    Item getAllRequest = new Item(
            UUID.randomUUID().toString(),
            "Get all " + entity.name(),
            List.of(),
            List.of(),
            new Request(
                    entityUrl,
                    "GET",
                    "Request to get all " + entity.name())
    );


    ObjectNode objectNode = objectMapper.createObjectNode();
    entity.fields()
            .forEach(field -> objectNode.put(field.name(), field.example()));

    String id = "1";
    Url byIdUrl = new Url(
            "localhost", 8080,
            List.of(entity.nameCamel(), ":id"),
            List.of(new Entry("id", id)));

    String createRequestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
    Item createRequest = new Item(
            UUID.randomUUID().toString(),
            "Create " + entity.name(),
            List.of(),
            List.of(),
            new Request(entityUrl, "POST", "Request to create a " + entity.name(),
                    new Body(
                            "raw",
                            createRequestBody,
                            Map.of("raw", Map.of("language", "json"))
                    ))
    );

    Item getByIdRequest = new Item(
            UUID.randomUUID().toString(),
            "Get " + entity.name() + " by id",
            List.of(),
            List.of(),
            new Request(byIdUrl, "GET", "Request to get a " + entity.name() + " by id")
    );

    Item updateByIdRequest = new Item(
            UUID.randomUUID().toString(),
            "Update " + entity.name() + " by id",
            List.of(),
            List.of(),
            new Request(byIdUrl, "PUT", "Request to update a " + entity.name() + " by id")
    );

    Item deleteByIdRequest = new Item(
            UUID.randomUUID().toString(),
            "Delete " + entity.name() + " by id",
            List.of(),
            List.of(),
            new Request(byIdUrl, "DELETE", "Request to delete a " + entity.name() + " by id")
    );

    return new Items.Folder(entity.name(),
            "Description for " + entity.name(),
            List.of(getAllRequest, createRequest, getByIdRequest, updateByIdRequest, deleteByIdRequest));
  }

}
