package com.aloc.aloc.user.controller;

import com.aloc.aloc.course.dto.response.CourseUserResponseDto;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.profilebackgroundcolor.dto.response.ProfileBackgroundColorResponseDto;
import com.aloc.aloc.profilebackgroundcolor.service.ProfileBackgroundColorService;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.dto.response.UserCourseResponseDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.facade.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "User API 입니다.")
public class UserController {
  private final UserFacade userFacade;
  private final CourseService courseService;
  private final ProfileBackgroundColorService profileBackgroundColorService;

  @GetMapping("/users")
  @Operation(summary = "유저 목록 조회", description = "전체 유저 목록을 조회합니다.")
  public CustomApiResponse<List<UserDetailResponseDto>> getUsers() {
    return CustomApiResponse.onSuccess(userFacade.getUsers());
  }

  @GetMapping("/user")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저 정보 조회", description = "유저의 개인 정보 목록을 조회합니다.")
  public CustomApiResponse<UserDetailResponseDto> getUser(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.getUser(user.getUsername()));
  }

  @SecurityRequirement(name = "JWT Auth")
  @PatchMapping("/user")
  @ApiResponse(responseCode = "200", description = "success")
  @Operation(summary = "회원정보 업데이트", description = "회원 정보를 업데이트합니다.")
  public CustomApiResponse<UserDetailResponseDto> updateUser(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestBody UserRequestDto userRequestDto) {
    return CustomApiResponse.onSuccess(userFacade.updateUser(user.getUsername(), userRequestDto));
  }

  @SecurityRequirement(name = "JWT Auth")
  @PatchMapping(value = "/user/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public CustomApiResponse<UserDetailResponseDto> updateUserProfileImage(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile)
      throws FileUploadException {
    return CustomApiResponse.onSuccess(
        userFacade.updateUserProfileImage(user.getUsername(), profileImageFile));
  }

  @DeleteMapping("/user/withdraw")
  @SecurityRequirement(name = "JWT Auth")
  @ApiResponse(responseCode = "200", description = "success")
  @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 진행합니다.")
  public void withdraw(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
    userFacade.withdraw(user.getUsername());
  }

  @PutMapping("/user/profile-background-color")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "프로필 색상 변경", description = "프로필 색상을 변경합니다.")
  public CustomApiResponse<ProfileBackgroundColorResponseDto> changeColor(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        profileBackgroundColorService.changeColor(user.getUsername()));
  }

  @GetMapping("/user/courses")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저의 코스 목록 불러오기", description = "유저의 코스목록을 불러옵니다.")
  public CustomApiResponse<List<UserCourseResponseDto>> getUserCourses(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.getUserCourses(user.getUsername()));
  }

  @PostMapping("/user/course/{courseId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저가 코스선택", description = "유저가 코스를 선택합니다.")
  public CustomApiResponse<CourseUserResponseDto> createUserCourse(
      @PathVariable Long courseId, @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        courseService.createUserCourse(courseId, user.getUsername()));
  }

  @PatchMapping("/user/course/{courseId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저 코스 포기", description = "유저가 특정 코스를 포기합니다.")
  public CustomApiResponse<CourseUserResponseDto> closeUserCourse(
      @PathVariable Long courseId, @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(courseService.closeUserCourse(courseId, user.getUsername()));
  }
}
