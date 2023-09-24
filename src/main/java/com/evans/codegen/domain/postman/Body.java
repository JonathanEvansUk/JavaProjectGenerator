package com.evans.codegen.domain.postman;

import java.util.Map;

public record Body(String mode,
                   String raw,
                   Map<String, Map<String, String>> options) {}
