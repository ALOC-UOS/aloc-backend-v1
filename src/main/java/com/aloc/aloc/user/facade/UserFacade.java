package com.aloc.aloc.user.facade;

import com.aloc.aloc.course.entity.UserCourse;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.global.image.ImageService;
import com.aloc.aloc.global.image.enums.ImageType;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.problem.service.UserCourseProblemService;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.dto.response.UserCourseResponseDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.mapper.UserMapper;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.user.service.UserSortingService;
import com.aloc.aloc.usercourse.dto.response.UserCourseProblemResponseDto;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
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
    log.info("유저 정보 조회 시 리프레시 토큰 : {}", user.getRefreshToken());
    return userMapper.mapToUserDetailResponseDto(user);
  }

  public List<UserCourseResponseDto> getUserCourses(String oauthId) {
    User user = userService.getUser(oauthId);
    List<UserCourse> userCourses = userCourseService.getUserCoursesInProcessByUser(user);
    return userCourses.stream()
        .map(
            userCourse -> {
              List<UserCourseProblem> sortedProblems =
                  userCourse.getUserCourseProblemList().stream()
                      .sorted(
                          Comparator.comparing(
                              UserCourseProblem::getCreatedAt)) // createdAt 기준으로 오름차순 정렬
                      .toList();
              int todayProblemId = userCourseProblemService.getTodayProblemId(sortedProblems);
              List<ProblemResponseDto> problemResponseDtos =
                  userCourseProblemService.mapToProblemResponseDto(userCourse);
              return UserCourseResponseDto.of(
                  userCourse, userCourse.getCourse(), problemResponseDtos, todayProblemId);
            })
        .collect(Collectors.toList());
  }

  public List<com.aloc.aloc.usercourse.dto.response.UserCourseResponseDto> getUserCoursesNew(
      String oauthId) {
    User user = userService.getUser(oauthId);
    List<UserCourse> userCourses = userCourseService.getUserCoursesInProcessByUser(user);
    return userCourses.stream()
        .map(com.aloc.aloc.usercourse.dto.response.UserCourseResponseDto::of)
        .toList();
  }

  @Transactional
  public UserDetailResponseDto updateUser(String oauthId, UserRequestDto userRequestDto) {
    log.info("✅ [updateUser] 호출됨 - oauthId: {}", oauthId);
    log.info("📥 [요청 값] userRequestDto: {}", userRequestDto);

    User user = userService.getUser(oauthId);
    log.info("🔍 유저 조회 완료 - email: {}, authority: {}", user.getEmail(), user.getAuthority());

    // 백준 ID 등록 + 권한 변경
    if (userRequestDto.getBaekjoonId() != null
        && user.getAuthority().equals(Authority.ROLE_NEW_USER)) {
      log.info("🟡 백준 ID 존재 + ROLE_NEW_USER → 백준 ID 등록 및 권한 변경 처리");

      userService.checkBaekjoonId(userRequestDto.getBaekjoonId());
      log.info("✅ 백준 ID 중복 검사 통과: {}", userRequestDto.getBaekjoonId());

      user.setBaekjoonId(userRequestDto.getBaekjoonId());
      log.info("📌 백준 ID 설정됨: {}", user.getBaekjoonId());

      int extractedRank = baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId());
      user.setRank(extractedRank);
      log.info("📊 백준 랭크 추출 완료: {}", extractedRank);

      user.setAuthority(Authority.ROLE_USER);
      log.info("🔄 권한 변경됨 → ROLE_USER");
    }

    // 이름 설정
    if (StringUtils.hasText(userRequestDto.getName())) {
      log.info("📝 이름 업데이트: {}", userRequestDto.getName());
      user.setName(userRequestDto.getName());
    }

    // 저장
    userService.saveUser(user);
    log.info("💾 유저 정보 저장 완료 - userId: {}, refreshToken: {}", user.getId(), user.getRefreshToken());

    // 응답 생성
    UserDetailResponseDto response = userMapper.mapToUserDetailResponseDto(user);
    log.info("✅ [응답 반환] userDetailResponseDto: {}", response);

    return response;
  }

  @Transactional
  public UserDetailResponseDto updateUserProfileImage(String oauthId, MultipartFile profileImage)
      throws FileUploadException {
    User user = userService.getUser(oauthId);

    if (user.getProfileImageFileName() != null) {
      deleteProfileImage(oauthId, user.getProfileImageFileName());
    }

    if (profileImage != null) {
      uploadProfileImage(oauthId, profileImage);
    }
    return userMapper.mapToUserDetailResponseDto(user);
  }

  private void uploadProfileImage(String oauthId, MultipartFile profileImageFile)
      throws FileUploadException {
    Map<String, Object> metadata = createMetaData(oauthId);
    imageService.uploadImage(profileImageFile, ImageType.PROFILE, metadata);
  }

  private Map<String, Object> createMetaData(String oauthId) {
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("username", oauthId);
    return metadata;
  }

  private void deleteProfileImage(String oauthId, String fileName) {
    imageService.deleteImage(fileName, ImageType.PROFILE, createMetaData(oauthId));
  }

  @Transactional
  public void withdraw(String oauthId) {
    User user = userService.getUser(oauthId);

    user.setAuthority(Authority.ROLE_DELETE);
    user.setDeletedAt(LocalDateTime.now());

    List<UserCourse> userCourses = userCourseService.getUserCoursesByUser(user);
    userCourseService.deleteUserCourses(userCourses);
  }

  public UserCourseProblemResponseDto getUserProblems(String oauthId, Long userCourseId) {
    User user = userService.getUser(oauthId);
    UserCourse userCourse = userCourseService.getUserCourseById(userCourseId);

    List<UserCourseProblem> sortedProblems =
        userCourse.getUserCourseProblemList().stream()
            .sorted(Comparator.comparing(UserCourseProblem::getCreatedAt)) // createdAt 기준으로 오름차순 정렬
            .toList();
    int todayProblemId = userCourseProblemService.getTodayProblemId(sortedProblems);
    List<ProblemResponseDto> problemResponseDtos =
        userCourseProblemService.mapToProblemResponseDto(userCourse);
    return UserCourseProblemResponseDto.of(todayProblemId, problemResponseDtos);
  }
}
