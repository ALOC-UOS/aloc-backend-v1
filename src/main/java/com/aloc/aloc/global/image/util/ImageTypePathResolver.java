package com.aloc.aloc.global.image.util;

import com.aloc.aloc.global.image.enums.ImageType;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageTypePathResolver {
  private static final Logger logger = LoggerFactory.getLogger(ImageTypePathResolver.class);
  private final String baseUploadPath;

  public ImageTypePathResolver(@Value("${spring.upload.path:/upload/user}") String baseUploadPath) {
    if (baseUploadPath == null || baseUploadPath.isEmpty()) {
      logger.error("baseUploadPath is null or empty!");
    } else {
      logger.info("baseUploadPath is: " + baseUploadPath);
    }
    this.baseUploadPath = baseUploadPath;
  }

  public Path resolvePath(ImageType imageType) {
    return Paths.get(baseUploadPath, imageType.name().toLowerCase()).toAbsolutePath().normalize();
  }
}
