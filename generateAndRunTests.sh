#!/bin/sh
mvn exec:java -Dexec.mainClass=com.evans.codegen.Main && mvn -f output/pom.xml clean verify
