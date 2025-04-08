package com.aloc.aloc.usercourse.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.user.facade.UserFacade;
import com.aloc.aloc.usercourse.dto.response.NewUserCourseResponseDto;
import com.aloc.aloc.usercourse.dto.response.SuccessUserCourseResponseDto;
import com.aloc.aloc.usercourse.dto.response.UserCourseProblemResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Course API", description = "User Course API 입니다.")
public class UserCourseController {
  private final UserFacade userFacade;

  @GetMapping("/user-courses")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저 코스 목록 조회", description = "유저가 현재 진행 중인 코스 목록을 조회합니다.")
  public CustomApiResponse<List<NewUserCourseResponseDto>> getUserCourses(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.getUserCoursesNew(user.getUsername()));
  }

  @GetMapping("/user-courses/{userCourseId}/problems")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저 코스의 문제 목록 조회", description = "유저 코스 아이디로 해당 유저 코스에 속하는 문제 목록을 조회합니다.")
  public CustomApiResponse<UserCourseProblemResponseDto> getUserCourseProblem(
      @PathVariable(name = "userCourseId") Long userCourseId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(
        userFacade.getUserProblems(user.getUsername(), userCourseId));
  }

  @GetMapping("/user-courses/{userCourseId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "유저 코스 성공 정보 조회",
      description = "유저 코스 아이디로 성공했을때, 유저 코스 정보 및 추천 코스 목록을 조회합니다.")
  public CustomApiResponse<SuccessUserCourseResponseDto> getUserCourse(
      @PathVariable(name = "userCourseId") Long userCourseId,
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.getUserCourse(user.getUsername(), userCourseId));
  }
}
