package com.aloc.aloc.global.image.strategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStrategy {
  public Path storeFile(MultipartFile file, Path directory, String fileName) throws IOException {
    if (!Files.exists(directory)) {
      Files.createDirectories(directory);
    }
    Path targetLocation = directory.resolve(fileName);
    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    return targetLocation;
  }

  public boolean deleteFile(Path directory, String fileName) {

    try {
      Path targetLocation = directory.resolve(fileName);
      return Files.deleteIfExists(targetLocation); // ✅ 파일이 존재하면 삭제 후 true 반환
    } catch (IOException e) {
      e.printStackTrace();
      return false; // ✅ 삭제 실패
    }
  }
}
