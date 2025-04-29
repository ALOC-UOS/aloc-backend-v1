package com.aloc.aloc.user.service.facade;

import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.dto.response.CourseUserResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.course.service.UserCourseService;
import com.aloc.aloc.global.apipayload.exception.AlreadyExistException;
import com.aloc.aloc.global.apipayload.exception.NoContentException;
import com.aloc.aloc.global.image.ImageService;
import com.aloc.aloc.global.image.enums.ImageType;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.service.UserCourseProblemService;
import com.aloc.aloc.profilebackgroundcolor.dto.response.ProfileBackgroundColorResponseDto;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import com.aloc.aloc.scraper.BaekjoonRankScrapingService;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.dto.response.UserCourseResponseDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.entity.User;
import com.aloc.aloc.user.enums.Authority;
import com.aloc.aloc.user.service.UserService;
import com.aloc.aloc.user.service.UserSortingService;
import com.aloc.aloc.user.service.mapper.UserMapper;
import com.aloc.aloc.usercourse.dto.response.NewUserCourseResponseDto;
import com.aloc.aloc.usercourse.dto.response.SuccessUserCourseResponseDto;
import com.aloc.aloc.usercourse.dto.response.UserCourseProblemResponseDto;
import com.aloc.aloc.usercourse.entity.UserCourse;
import com.aloc.aloc.usercourse.entity.UserCourseProblem;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final CourseService courseService;
  private final ProfileBackgroundColorService profileBackgroundColorService;

  public List<UserDetailResponseDto> getUsers() {
    List<User> users = userService.getActiveUsers();

    if (users.isEmpty()) {
      throw new NoContentException("조회 가능한 유저가 없습니다.");
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
              List<UserCourseProblem> sortedProblems =
                  userCourse.getUserCourseProblemList().stream()
                      .sorted(
                          Comparator.comparing(
                              UserCourseProblem::getCreatedAt)) // createdAt 기준으로 오름차순 정렬
                      .toList();
              int todayProblemId = userCourseProblemService.getTodayProblemId(sortedProblems);
              List<ProblemResponseDto> problemResponseDtos =
                  userCourseProblemService.mapToProblemResponseDto(sortedProblems);
              return UserCourseResponseDto.of(
                  userCourse, userCourse.getCourse(), problemResponseDtos, todayProblemId);
            })
        .collect(Collectors.toList());
  }

  public List<NewUserCourseResponseDto> getUserCoursesNew(String oauthId) {
    User user = userService.getUser(oauthId);
    List<UserCourse> userCourses = userCourseService.getUserCoursesInProcessByUser(user);
    return userCourses.stream().map(NewUserCourseResponseDto::of).toList();
  }

  @Transactional
  public UserDetailResponseDto updateUser(String oauthId, UserRequestDto userRequestDto) {
    User user = userService.getUser(oauthId);

    if (userRequestDto.getBaekjoonId() != null
        && user.getAuthority().equals(Authority.ROLE_NEW_USER)) {

      userService.checkBaekjoonId(userRequestDto.getBaekjoonId());

      user.setBaekjoonId(userRequestDto.getBaekjoonId());

      int extractedRank = baekjoonRankScrapingService.extractBaekjoonRank(user.getBaekjoonId());
      user.setRank(extractedRank);
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

  public UserCourseProblemResponseDto getUserProblems(String oauthId, Long userCourseId) {
    User user = userService.getUser(oauthId);
    UserCourse userCourse = userCourseService.getUserCourseById(userCourseId);

    validateUserAndUserCourse(userCourse, user);

    List<UserCourseProblem> sortedProblems =
        userCourse.getUserCourseProblemList().stream()
            .sorted(Comparator.comparing(UserCourseProblem::getCreatedAt)) // createdAt 기준으로 오름차순 정렬
            .toList();
    int todayProblemId = userCourseProblemService.getTodayProblemId(sortedProblems);
    List<ProblemResponseDto> problemResponseDtos =
        userCourseProblemService.mapToProblemResponseDto(sortedProblems);
    return UserCourseProblemResponseDto.of(todayProblemId, problemResponseDtos);
  }

  private static void validateUserAndUserCourse(UserCourse userCourse, User user) {
    if (!userCourse.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("접근 권한이 없는 유저코스입니다.");
    }
  }

  public SuccessUserCourseResponseDto getUserCourse(String oauthId, Long userCourseId) {
    User user = userService.getUser(oauthId);
    UserCourse userCourse = userCourseService.getUserCourseById(userCourseId);
    Course course = userCourse.getCourse();
    validateUserAndUserCourse(userCourse, user);

    int clearRank = userCourseService.getClearRank(userCourse);
    int addedCoin = course.getProblemCnt() * 20;

    // 추천된 코스 리스트 조회
    List<Course> recommendedCourses = courseService.getRecommendedCourses(course);

    // 유저가 이미 수강한 코스 ID 목록 조회
    Set<Long> enrolledCourseIds =
        userCourseService.getUserCoursesByUser(user).stream()
            .map(uc -> uc.getCourse().getId())
            .collect(Collectors.toSet());

    // 아직 수강하지 않은 추천 코스 중에서, 생성 수 기준 정렬 후 상위 3개만 추출
    List<CourseResponseDto> courses =
        recommendedCourses.stream()
            .filter(c -> !enrolledCourseIds.contains(c.getId())) // 수강하지 않은 것만
            .sorted(Comparator.comparing(Course::getGenerateCnt).reversed()) // 최신 생성 순
            .limit(3)
            .map(c2 -> CourseResponseDto.of(c2, UserCourseState.NOT_STARTED))
            .toList();

    return SuccessUserCourseResponseDto.of(userCourse.getCourse(), clearRank, addedCoin, courses);
  }

  @Transactional
  public CourseUserResponseDto createUserCourse(Long courseId, String oauthId) {
    Course course = courseService.getCourseById(courseId);
    User user = userService.getUser(oauthId);

    checkEligibleToCreateUserCourse(user, course);

    UserCourse userCourse = userCourseService.createUserCourse(user, course);
    course.addGenerateCnt();
    return CourseUserResponseDto.of(userCourse);
  }

  private void checkEligibleToCreateUserCourse(User user, Course course) {
    List<UserCourse> userCourses =
        userCourseService.getAllByUserAndUserCourseState(user, UserCourseState.IN_PROGRESS);
    if (userCourses.size() >= 3) {
      throw new AlreadyExistException("최대 3개의 코스까지 진행할 수 있습니다.");
    }
    if (userCourses.stream().anyMatch(uc -> uc.getCourse().getId().equals(course.getId()))) {
      throw new IllegalArgumentException("이미 진행 중인 코스는 진행할 수 없습니다.");
    }

    if (userCourseService.existsByUserAndCourseAndUserCourseState(
        user, course, UserCourseState.SUCCESS)) {
      throw new AlreadyExistException("이미 성공한 코스는 진행할 수 없습니다.");
    }
  }

  @Transactional
  public CourseUserResponseDto closeUserCourse(Long courseId, String oauthId) {
    Course course = courseService.getCourseById(courseId);
    User user = userService.getUser(oauthId);
    UserCourse userCourse =
        userCourseService.getUserCourseByUserAndCourseAndUserCourseState(
            user, course, UserCourseState.IN_PROGRESS);

    userCourseService.closeUserCourse(userCourse);
    return CourseUserResponseDto.of(userCourse);
  }

  public Page<CourseResponseDto> getCoursesByUser(
      Pageable pageable, String oauthId, CourseType courseTypeOrNull) {
    User user = userService.getUser(oauthId);
    List<UserCourse> userCourses = userCourseService.getUserCoursesByUser(user);
    Page<Course> courses = courseService.getCoursePageByCourseType(pageable, courseTypeOrNull);

    Map<Long, UserCourse> latestUserCourseMap =
        userCourses.stream()
            .collect(
                Collectors.groupingBy(
                    uc -> uc.getCourse().getId(),
                    Collectors.collectingAndThen(
                        Collectors.maxBy(Comparator.comparing(UserCourse::getCreatedAt)),
                        optional -> optional.orElse(null))));

    return courses.map(
        course -> {
          UserCourse latestUserCourse = latestUserCourseMap.get(course.getId());
          UserCourseState state =
              (latestUserCourse != null)
                  ? latestUserCourse.getUserCourseState()
                  : UserCourseState.NOT_STARTED;
          return CourseResponseDto.of(course, state);
        });
  }

  @Transactional
  public ProfileBackgroundColorResponseDto changeColor(String oauthId) {
    User user = userService.getUser(oauthId);
    return profileBackgroundColorService.changeColor(user);
  }
}
