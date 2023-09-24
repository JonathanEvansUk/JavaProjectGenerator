package com.evans.codegen.domain.postman;

import java.util.List;

public sealed interface Items {

  record Folder(String name,
                String description,
                List<Item> item) implements Items {
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
