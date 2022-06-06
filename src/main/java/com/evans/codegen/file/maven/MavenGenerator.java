package com.evans.codegen.file.maven;

import com.evans.codegen.file.maven.MavenGenerator.MavenProject;
import com.evans.codegen.file.FileGenerator;

public class MavenGenerator implements FileGenerator<MavenProject> {

  public record MavenProject(String groupId, String artifactId, JavaVersion javaVersion) {

    public enum JavaVersion {
      JDK_1_8("1.8"),
      JDK_18("18");

      private final String value;

      JavaVersion(String value) {
        this.value = value;
      }

      public String getValue() {
        return value;
      }
    }
  }

  @Override
  public String templateName() {
    return "maven/pom.xml.mustache";
  }

  @Override
  public String outputFileName(MavenProject templateData) {
    return "pom.xml";
  }
}
