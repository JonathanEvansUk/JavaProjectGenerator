package com.evans.generator;

public class Utils {

  private Utils() {
  }

  public static String capitalise(String string) {
    return string.substring(0, 1).toUpperCase() + string.substring(1);
  }
}
