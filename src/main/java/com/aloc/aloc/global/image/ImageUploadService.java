package com.aloc.aloc.global.image;

import com.aloc.aloc.global.image.dto.ImageInfoDto;
import com.aloc.aloc.global.image.enums.ImageType;
import com.aloc.aloc.global.image.strategy.ImageUploadStrategy;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageUploadService {
  private final Map<ImageType, ImageUploadStrategy> strategies;

  public ImageUploadService(List<ImageUploadStrategy> strategyList) {
    strategies =
        strategyList.stream()
            .collect(
                Collectors.toMap(
                    strategy ->
                        ImageType.valueOf(
                            strategy
                                .getClass()
                                .getSimpleName()
                                .replace("ImageUploadStrategy", "")
                                .toUpperCase()),
                    Function.identity()));
  }

  @Transactional
  public ImageInfoDto uploadImage(
      MultipartFile file, ImageType imageType, Map<String, Object> metadata)
      throws FileUploadException {
    ImageUploadStrategy strategy = strategies.get(imageType);
    validateStrategyExists(imageType, strategy);
    return strategy.upload(file, metadata);
  }

  private static void validateStrategyExists(ImageType imageType, ImageUploadStrategy strategy) {
    if (strategy == null) {
      throw new IllegalArgumentException("Unsupported image type: " + imageType);
    }
  }
}
