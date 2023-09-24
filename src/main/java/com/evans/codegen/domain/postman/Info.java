package com.evans.codegen.domain.postman;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Info(String name,

                   @JsonProperty("_postman_id") String postmanId,
                   String description,
                   String schema) {
}
