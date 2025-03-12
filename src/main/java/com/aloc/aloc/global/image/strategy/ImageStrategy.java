package com.aloc.aloc.global.image.strategy;

import com.aloc.aloc.global.image.dto.ImageInfoDto;
import java.util.Map;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStrategy {
  ImageInfoDto upload(MultipartFile file, Map<String, Object> metadata) throws FileUploadException;

  void delete(String fileName, Map<String, Object> metadata);
}
