package com.aloc.aloc.global.image.util;

import java.util.UUID;

public class FileNameGenerator {
  public static String generateUniqueFileName(String originalFileName) {
    return UUID.randomUUID() + "_" + originalFileName;
  }
}
