package com.evans.codegen.file.openapi;

import com.evans.codegen.file.FileGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OpenAPISpecGenerator implements FileGenerator<OpenAPISpec> {

    @Override
    public String templateName() {
        return "openapi/openapi.mustache";
    }

    @Override
    public String outputFileName(OpenAPISpec templateData) {
        return "openapi.yaml";
    }

    @Override
    public String outputDirectory(OpenAPISpec templateData) {
        return FileGenerator.super.outputDirectory(templateData) + "/src/main/resources";
    }
}
