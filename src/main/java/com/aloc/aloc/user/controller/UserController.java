package com.aloc.aloc.user.controller;

import com.aloc.aloc.global.apipayload.CustomApiResponse;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.service.ProblemFacade;
import com.aloc.aloc.user.dto.request.UserCoinDto;
import com.aloc.aloc.user.dto.request.UserPasswordDto;
import com.aloc.aloc.user.dto.response.UserDetailResponseDto;
import com.aloc.aloc.user.service.UserFacade;
import com.aloc.aloc.user.service.UserRegistrationService;
import com.aloc.aloc.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api2")
@Tag(name = "User API", description = "User API 입니다.")
public class UserController {
  private final UserService userService;
  private final UserFacade userFacade;
  private final ProblemFacade problemFacade;
  private final UserRegistrationService userRegistrationService;

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

  @GetMapping("/user/admin")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "유저가 admin인지 확인", description = "유저가 admin인지 조회합니다")
  public CustomApiResponse<String> checkUserAdmin(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) {
    userService.checkAdmin(user.getUsername());
    return CustomApiResponse.onSuccess("User is an admin");
  }

  @PostMapping("/authorize/{githubId}")
  @SecurityRequirement(name = "JWT Auth")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "스터디 멤버로 등록되었습니다."),
    @ApiResponse(responseCode = "400", description = "해당 깃허브 아이디로 가입된 사용자가 없습니다."),
    @ApiResponse(responseCode = "400", description = "이미 등록된 멤버입니다.")
  })
  @Operation(summary = "유저 추가", description = "스터디 멤버로 추가합니다.")
  public CustomApiResponse<String> addUser(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @Parameter(description = "깃허브 ID", required = true) @PathVariable("githubId")
          String githubId) {
    return CustomApiResponse.onSuccess(
        userRegistrationService.addUser(user.getUsername(), githubId));
  }

  @PutMapping("/user/course")
  @SecurityRequirement(name = "JWT Auth")
  @Operation(summary = "코스 변경 요청", description = "하프 코스에서 풀 코스로의 변경을 요청합니다.")
  public CustomApiResponse<String> changeCourse(
      @Parameter(hidden = true) @AuthenticationPrincipal User user) throws AccessDeniedException {
    return CustomApiResponse.onSuccess(userService.changeCourse(user.getUsername()));
  }

  @GetMapping("/user/{githubId}/unsolved-problems")
  @Operation(
      summary = "유저의 풀지 않은 문제 조회",
      description = "유저가 풀지 않은 문제를 조회합니다. 시즌이 null이면 모든 시즌을 조회합니다.")
  public CustomApiResponse<List<ProblemSolvedResponseDto>> getUnsolvedProblemList(
      @Parameter(required = true) @PathVariable() String githubId,
      @Parameter(description = "조회할 시즌 (선택, 기본값: 모든 시즌)") @RequestParam(required = false)
          Integer season) {
    return CustomApiResponse.onSuccess(
        problemFacade.getUnsolvedProblemListByUser(githubId, season));
  }

  @GetMapping("/user/{githubId}/solved-problems")
  @Operation(summary = "유저의 이미 푼 문제 조회", description = "유저가 푼 문제를 조회합니다. 시즌이 null이면 모든 시즌을 조회합니다.")
  public CustomApiResponse<List<ProblemSolvedResponseDto>> getUserSolvedProblemList(
      @Parameter(description = "유저 깃허브 아이디", required = true) @PathVariable() String githubId,
      @Parameter(description = "조회할 시즌 (선택, 기본값: 모든 시즌)") @RequestParam(required = false)
          Integer season) {
    return CustomApiResponse.onSuccess(problemFacade.getSolvedProblemListByUser(githubId, season));
  }

  @SecurityRequirement(name = "JWT Auth")
  @PostMapping("/user/check-password")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "유저의 패스워드가 일치합니다."),
    @ApiResponse(responseCode = "400", description = "일치하지 않는 패스워드입니다.")
  })
  @Operation(
      summary = "유저의 비밀번호 일치 여부 확인",
      description = "request의 비밀번호와 db의 유저 비밀번호와 일치하는지 확인합니다.")
  public CustomApiResponse<String> checkUserPassword(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestBody @Valid UserPasswordDto userPasswordDto) {
    return CustomApiResponse.onSuccess(
        userService.checkUserPassword(user.getUsername(), userPasswordDto));
  }

  @SecurityRequirement(name = "JWT Auth")
  @PatchMapping("/user/reset-password")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "비밀번호가 변경되었습니다."),
    @ApiResponse(responseCode = "500", description = "비밀번호 변경을 실패했습니다.")
  })
  @Operation(summary = "유저 비밀번호 변경", description = "유저의 비밀번호를 변경합니다.")
  public CustomApiResponse<String> changeUserPassword(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestBody @Valid UserPasswordDto userPasswordDto) {
    return CustomApiResponse.onSuccess(
        userService.updateUserPassword(user.getUsername(), userPasswordDto));
  }

  @SecurityRequirement(name = "JWT Auth")
  @PatchMapping("/user/change-coin")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "유저의 코인이 업데이트되었습니다."),
    @ApiResponse(responseCode = "401", description = "관리자만 가능합니다."),
    @ApiResponse(responseCode = "500", description = "유저의 코인 변경을 실패했습니다.")
  })
  @Operation(summary = "유저 코인 업데이트", description = "유저의 코인을 업데이트합니다.")
  public CustomApiResponse<String> updateUserCoin(
      @Parameter(hidden = true) @AuthenticationPrincipal User user,
      @RequestBody @Valid UserCoinDto userCoinDto) {
    return CustomApiResponse.onSuccess(userService.updateUserCoin(user.getUsername(), userCoinDto));
  }
}
