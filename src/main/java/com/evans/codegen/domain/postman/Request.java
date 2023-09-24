package com.evans.codegen.domain.postman;

public record Request(Url url,
                      String method,
                      String description,
                      Body body) {

  public Request(Url url, String method, String description) {
    this(url, method, description, null);
  }
}
