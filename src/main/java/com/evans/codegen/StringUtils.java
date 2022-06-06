package com.evans.codegen;

public class StringUtils {

  private StringUtils() {
  }

  public static String capitalise(String string) {
    return string.substring(0, 1).toUpperCase() + string.substring(1);
  }
}
