package com.aloc.aloc.global.image.dto;

import com.aloc.aloc.global.image.enums.ImageType;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ImageInfoDto {
  private ImageType imageType;
  private String imageName;
  private Path fullPath;
}
