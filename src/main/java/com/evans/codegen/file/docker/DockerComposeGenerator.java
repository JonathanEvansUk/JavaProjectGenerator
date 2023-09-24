package com.evans.codegen.file.docker;

import com.evans.codegen.file.FileGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
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
