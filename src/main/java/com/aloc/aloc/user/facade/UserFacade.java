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
import com.aloc.aloc.user.dto.response.UserCourseSimpleResponseDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.mapper.UserMapper;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.user.service.UserSortingService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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

  /**
   * Retrieves the user's active courses in process and maps each course to a response DTO.
   *
   * <p>This method first obtains the user by the OAuth ID, then fetches all courses that are currently in process.
   * For each course, it sorts the associated problems by creation date, determines the problem ID assigned for today,
   * and maps the course along with its problems to a {@code UserCourseResponseDto}.
   *
   * @param oauthId the OAuth identifier for the user
   * @return a list of {@code UserCourseResponseDto} objects representing the user's active courses with current problem details
   */
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

  /**
   * Retrieves a simplified list of active courses for the specified user.
   *
   * <p>This method finds the user by the provided OAuth ID, retrieves the user's courses that are currently in process,
   * and converts each course into a simplified response data transfer object.
   *
   * @param oauthId the OAuth identifier of the user
   * @return a list of simplified course response DTOs; an empty list if no active courses are found
   */
  public List<UserCourseSimpleResponseDto> getSimpleUserCourses(String oauthId) {
    User user = userService.getUser(oauthId);
    List<UserCourse> userCourses = userCourseService.getUserCoursesInProcessByUser(user);

    return userCourses.stream().map(UserCourseSimpleResponseDto::of).toList();
  }

  /**
   * Updates a user's details based on the provided user request data.
   *
   * <p>If the request contains a new Baekjoon ID and the user currently has a new user role,
   * the method validates the Baekjoon ID, updates the user's rank based on the extracted Baekjoon rank,
   * and changes their authority to a regular user. Additionally, it updates the user's name if provided.
   *
   * @param oauthId the OAuth identifier of the user
   * @param userRequestDto the DTO containing updated user details
   * @return a response DTO representing the updated user information
   */
  @Transactional
  public UserDetailResponseDto updateUser(String oauthId, UserRequestDto userRequestDto) {

    User user = userService.getUser(oauthId);

    if (userRequestDto.getBaekjoonId() != null
        && user.getAuthority().equals(Authority.ROLE_NEW_USER)) {
      userService.checkBaekjoonId(userRequestDto.getBaekjoonId());
      user.setBaekjoonId(userRequestDto.getBaekjoonId());
      user.setRank(baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId()));
      user.setAuthority(Authority.ROLE_USER);
    }

    if (StringUtils.hasText(userRequestDto.getName())) {
      user.setName(userRequestDto.getName());
    }

    userService.saveUser(user);

    return userMapper.mapToUserDetailResponseDto(user);
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
}
