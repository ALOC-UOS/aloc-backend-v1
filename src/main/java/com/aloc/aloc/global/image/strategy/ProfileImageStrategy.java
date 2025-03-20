package com.aloc.aloc.global.image.strategy;

import com.aloc.aloc.global.image.dto.ImageInfoDto;
import com.aloc.aloc.global.image.enums.ImageType;
import com.aloc.aloc.global.image.util.FileNameGenerator;
import com.aloc.aloc.global.image.util.ImageTypePathResolver;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.service.UserService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class ProfileImageStrategy implements ImageStrategy {
  private final FileStrategy fileStrategy;
  private final ImageTypePathResolver pathResolver;
  private final UserService userService;

  @Override
  public ImageInfoDto upload(MultipartFile file, Map<String, Object> metadata)
      throws FileUploadException {
    String userId = getUserId(metadata);

    String fileName = FileNameGenerator.generateUniqueFileName();
    Path uploadPath = pathResolver.resolvePath(ImageType.PROFILE);
    try {
      Path fullPath = fileStrategy.storeFile(file, uploadPath, fileName);
      User user = userService.getUser(userId);
      user.setProfileImageFileName(fileName);
      userService.saveUser(user);

      return new ImageInfoDto(ImageType.PROFILE, fileName, fullPath);
    } catch (IOException e) {
      throw new FileUploadException("Failed to upload profile image", e);
    }
  }

  private static String getUserId(Map<String, Object> metadata) {
    String userId = (String) metadata.get("username");
    if (userId == null) {
      throw new IllegalArgumentException("User ID is required for profile image upload");
    }
    return userId;
  }

  @Override
  public void delete(String fileName, Map<String, Object> metadata) {
    String userId = getUserId(metadata);
    Path uploadPath = pathResolver.resolvePath(ImageType.PROFILE);
    if (fileStrategy.deleteFile(uploadPath, fileName)) {
      User user = userService.getUser(userId);
      user.setProfileImageFileName(null);
      userService.saveUser(user);
    } else {
      throw new RuntimeException("파일 삭제에 실패하였습니다.");
    }
  }
}
