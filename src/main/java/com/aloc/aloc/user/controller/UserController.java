package com.aloc.aloc.user.controller;

import com.aloc.aloc.course.dto.response.CourseUserResponseDto;
import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.global.apipayload.status.SuccessStatus;
import com.aloc.aloc.user.dto.request.UserRequestDto;
import com.aloc.aloc.user.dto.response.UserColorChangeResponseDto;
import com.aloc.aloc.user.dto.response.UserCourseResponseDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.service.facade.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api")
public class UserController {
  private final UserFacade userFacade;

  @GetMapping("/users")
  @Operation(
      summary = "유저 목록 조회",
      description = "활성화된 모든 유저 목록을 정렬된 순서로 조회합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 유저 목록을 반환하였습니다.",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = UserDetailResponseDto.class)))),
        @ApiResponse(responseCode = "204", description = "조회 가능한 유저가 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
      })
  public CustomApiResponse<List<UserDetailResponseDto>> getUsers() {
    return CustomApiResponse.onSuccess(userFacade.getUsers());
  }

  @GetMapping("/user")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "유저 정보 조회",
      description = "유저의 개인 정보 목록을 조회합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "유저 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
        @ApiResponse(responseCode = "404", description = "유저 정보를 찾을 수 없음")
      })
  public CustomApiResponse<UserDetailResponseDto> getUser(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.getUser(user.getUsername()));
  }

  @SecurityRequirement(name = "JWT Auth")
  @PatchMapping("/user")
  @Operation(
      summary = "회원정보 업데이트",
      description = "회원 정보를 업데이트합니다. 최초 업데이트 시 백준 ID가 등록되며, 랭크 정보도 함께 설정됩니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "회원정보 업데이트 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "요청 값이 잘못됨 (예: 이미 존재하는 백준 ID)"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "해당 사용자가 존재하지 않음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류 (JSON 파싱 실패 등)")
      })
  public CustomApiResponse<UserDetailResponseDto> updateUser(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestBody UserRequestDto userRequestDto) {
    return CustomApiResponse.onSuccess(userFacade.updateUser(user.getUsername(), userRequestDto));
  }

  @SecurityRequirement(name = "JWT Auth")
  @PatchMapping(value = "/user/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      summary = "프로필 이미지 업데이트",
      description = "유저의 프로필 이미지를 수정합니다. 기존 이미지가 존재할 경우 삭제 후 업로드합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "프로필 이미지 업데이트 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "지원하지 않는 이미지 타입 또는 잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "유저가 존재하지 않음"),
        @ApiResponse(responseCode = "500", description = "파일 업로드 중 서버 오류")
      })
  public CustomApiResponse<UserDetailResponseDto> updateUserProfileImage(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile)
      throws FileUploadException {
    return CustomApiResponse.onSuccess(
        userFacade.updateUserProfileImage(user.getUsername(), profileImageFile));
  }

  @DeleteMapping("/user")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "회원 탈퇴",
      description = "회원 탈퇴를 진행합니다. 실제 삭제가 아닌 소프트 딜리트 방식으로 처리됩니다.",
      responses = {
        @ApiResponse(responseCode = "200", description = "회원 탈퇴 완료"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "사용자 정보 없음")
      })
  public void withdraw(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
    userFacade.withdraw(user.getUsername());
  }

  @PatchMapping("/user/profile-background-color")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "프로필 색상 변경",
      description = "유저의 코인을 차감하여 프로필 배경 색상을 무작위로 변경합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "색상 변경 성공",
            content =
                @Content(schema = @Schema(implementation = UserColorChangeResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "코인이 부족하거나 잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "사용자 정보 없음")
      })
  public CustomApiResponse<UserColorChangeResponseDto> changeColor(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.changeColor(user.getUsername()));
  }

  @GetMapping("/user/courses")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "유저의 코스 목록 불러오기",
      description = "로그인한 사용자의 진행 중인 코스 목록을 조회합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "성공적으로 코스 목록이 조회됨",
            content =
                @Content(
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = UserCourseResponseDto.class)))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "해당 사용자가 존재하지 않음")
      })
  public CustomApiResponse<List<UserCourseResponseDto>> getUserCourses(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.getUserCourses(user.getUsername()));
  }

  @PostMapping("/user/courses/{courseId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "유저가 코스 선택",
      description = "사용자가 특정 코스를 선택하여 학습을 시작합니다.",
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "코스 선택 성공",
            content = @Content(schema = @Schema(implementation = CourseUserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 선택된 코스)"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 접근"),
        @ApiResponse(responseCode = "409", description = "3개의 코스를 진행중이거나 이미 성공한 코스"),
        @ApiResponse(responseCode = "404", description = "해당 코스 또는 유저 정보 없음")
      })
  public CustomApiResponse<CourseUserResponseDto> createUserCourse(
      @PathVariable Long courseId, @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.of(
        SuccessStatus._CREATED, userFacade.createUserCourse(courseId, user.getUsername()));
  }

  @PatchMapping("/user/courses/{courseId}")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(
      summary = "유저 코스 포기",
      description = "사용자가 선택한 진행 중인 코스를 포기합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "코스 포기 성공",
            content = @Content(schema = @Schema(implementation = CourseUserResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "이미 종료된 코스거나 요청이 잘못됨"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "코스 또는 유저 정보 없음")
      })
  public CustomApiResponse<CourseUserResponseDto> closeUserCourse(
      @PathVariable Long courseId, @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    return CustomApiResponse.onSuccess(userFacade.closeUserCourse(courseId, user.getUsername()));
  }
}
