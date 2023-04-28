package com.evans.codegen.file.docker;

import com.evans.codegen.file.FileGenerator;

public class DockerComposeGenerator implements FileGenerator<DockerCompose> {

  @Override
  public String templateName() {
    return "docker/docker-compose.yml.mustache";
  }

  @Override
  public String outputFileName(DockerCompose templateData) {
    return "docker-compose.yml";
  }
}
