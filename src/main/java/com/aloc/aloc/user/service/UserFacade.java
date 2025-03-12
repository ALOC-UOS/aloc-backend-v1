package com.aloc.aloc.user.service;

import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.global.image.ImageService;
import com.aloc.aloc.global.image.enums.ImageType;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.problem.service.UserCourseProblemService;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.dto.request.ProfileImageRequestDto;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.dto.response.UserCourseResponseDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserFacade {

  private final UserSortingService userSortingService;
  private final UserMapper userMapper;
  private final UserService userService;
  private final BaekjoonRankScrapingService baekjoonRankScrapingService;
  private final ImageService imageService;
  private final UserCourseService userCourseService;
  private final UserCourseProblemService userCourseProblemService;

  public List<UserDetailResponseDto> getUsers() {
    List<User> users = userService.getActiveUsers();
    if (users.isEmpty()) {
      return List.of();
    }
    List<User> sortedUserList = userSortingService.sortUserList(users);
    return sortedUserList.stream()
        .map(userMapper::mapToUserDetailResponseDto)
        .collect(Collectors.toList());
  }

  public UserDetailResponseDto getUser(String oauthId) {
    User user = userService.getUser(oauthId);
    return userMapper.mapToUserDetailResponseDto(user);
  }

  public List<UserCourseResponseDto> getUserCourses(String oauthId) {
    User user = userService.getUser(oauthId);
    List<UserCourse> userCourses = userCourseService.getUserCoursesInProcessByUser(user);
    return userCourses.stream()
        .map(
            userCourse -> {
              int todayProblemId =
                  userCourseProblemService.getTodayProblemId(userCourse.getUserCourseProblemList());
              List<ProblemResponseDto> problemResponseDtos = mapToProblemResponseDto(userCourse);
              return UserCourseResponseDto.of(
                  userCourse, userCourse.getCourse(), problemResponseDtos, todayProblemId);
            })
        .collect(Collectors.toList());
  }

  private List<ProblemResponseDto> mapToProblemResponseDto(UserCourse userCourse) {
    return userCourse.getUserCourseProblemList().stream()
        .map(
            ucp -> {
              List<UserCourseProblem> userCourseProblems =
                  userCourseProblemService.getSolvedUserCourseProblemByProblem(ucp.getProblem());
              return ProblemResponseDto.of(ucp, ucp.getProblem(), userCourseProblems);
            })
        .toList();
  }

  @Transactional
  public UserDetailResponseDto updateUser(String oauthId, UserRequestDto userRequestDto) {

    User user = userService.getUser(oauthId);

    // 필드별 null 체크 후 업데이트
    if (userRequestDto.getBaekjoonId() != null) {
      user.setBaekjoonId(userRequestDto.getBaekjoonId());
      user.setRank(baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId()));
    }

    if (userRequestDto.getName() != null) {
      user.setName(userRequestDto.getName());
    }

    userService.saveUser(user);

    return userMapper.mapToUserDetailResponseDto(user);
  }

  @Transactional
  public UserDetailResponseDto updateUserProfileImage(
      String oauthId, ProfileImageRequestDto profileImageRequestDto) throws FileUploadException {
    User user = userService.getUser(oauthId);

    if (user.getProfileImageFileName() != null) {
      deleteProfileImage(oauthId, user.getProfileImageFileName());
    }

    if (profileImageRequestDto.getProfileImageFile() != null) {
      uploadProfileImage(oauthId, profileImageRequestDto.getProfileImageFile());
    }
    return userMapper.mapToUserDetailResponseDto(user);
  }

  private void uploadProfileImage(String oauthId, MultipartFile profileImageFile)
      throws FileUploadException {
    Map<String, Object> metadata = createMetaData(oauthId);
    imageService.uploadImage(profileImageFile, ImageType.PROFILE, metadata);
  }

  private static Map<String, Object> createMetaData(String oauthId) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("username", oauthId);
    return metadata;
  }

  private void deleteProfileImage(String oauthId, String fileName) {
    imageService.deleteImage(fileName, ImageType.PROFILE, createMetaData(oauthId));
  }
}
