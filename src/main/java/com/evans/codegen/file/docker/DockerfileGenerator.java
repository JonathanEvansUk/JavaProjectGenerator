package com.evans.codegen.file.docker;

import com.evans.codegen.file.FileGenerator;

public class DockerfileGenerator implements FileGenerator<Dockerfile> {


  @Override
  public String templateName() {
    return "docker/Dockerfile.mustache";
  }

  @Override
  public String outputFileName(Dockerfile templateData) {
    return "Dockerfile";
  }
}
