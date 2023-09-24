package com.evans.codegen.domain.postman;

import java.util.Collections;
import java.util.List;

public record Url(String raw,
                  String host,
                  int port,
                  List<String> path,
                  List<Entry> variable) {

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
