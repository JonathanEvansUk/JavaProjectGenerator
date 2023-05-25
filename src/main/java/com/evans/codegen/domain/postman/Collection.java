package com.evans.codegen.domain.postman;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record Collection(Info info, List<Items> item, List<Event> event, List<Variable> variable, Auth auth) {


  public record Info(String name,

                     @JsonProperty("_postman_id") String postmanId, String description, String schema) {
  }


  public sealed interface Items {

    record Folder(String name, String description, List<Item> item) implements Items {
    }

    record Item(
            String id,
            String name,
            List<Variable> variable,
            List<Event> event,
            Request request
    ) implements Items {
    }

  }

  public record Event() {
  }

  public record Variable() {
  }

  public record Request(Url url, String method, String description, Body body) {
    public Request(Url url, String method, String description) {
      this(url, method, description, null);
    }
  }

  public record Body(String mode, String raw, Map<String, Map<String, String>> options) {}

  public record Url(String raw, String host, int port, List<String> path, List<Entry> variable) {
    public Url(String host, int port) {
      this(toRawUrl(host, port), host, port, Collections.emptyList(), Collections.emptyList());
    }

    public Url(String host, int port, List<String> path) {
      this(toRawUrl(host, port), host, port, path, Collections.emptyList());
    }

    public Url(String host, int port, List<String> path, List<Entry> variable) {
      this(toRawUrl(host, port), host, port, path, variable);
    }

    private static String toRawUrl(String host, int port) {
      return "%s:%d".formatted(host, port);
    }
  }

  public record Entry(String key, String value) {
  }

  record Auth() {
  }


}
