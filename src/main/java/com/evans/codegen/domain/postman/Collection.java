package com.evans.codegen.domain.postman;

import java.util.List;

public record Collection(Info info,
                         List<Items> item,
                         List<Event> event,
                         List<Variable> variable,
                         Auth auth) {

}
