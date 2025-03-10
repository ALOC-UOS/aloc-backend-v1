package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserCourseProblem;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;
import com.aloc.aloc.user.entity.User;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProblemMapper {

  SolvedUserResponseDto mapToSolvedUserResponseDto(User user, UserCourseProblem userCourseProblem) {
    return SolvedUserResponseDto.builder()
        .username(user.getName())
        .baekjoonId(user.getBaekjoonId())
        .profileColor(user.getProfileColor())
        .profileImageFileName(user.getProfileImageFileName())
        .rank(user.getRank())
        .coin(user.getCoin())
        .solvedAt(userCourseProblem.getSolvedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        .build();
  }

  // TODO: 수정
  List<ProblemSolvedResponseDto> mapSolvedProblemToDtoList(
      List<UserCourseProblem> solvedProblemList) {
    return solvedProblemList.stream()
        .map(
            solvedProblem -> {
              Problem problem = solvedProblem.getProblem();
              return ProblemSolvedResponseDto.builder()
                  .id(problem.getId())
                  .problemId(problem.getProblemId())
                  .problemTitle(problem.getTitle())
                  .problemDifficulty(problem.getRank())
                  .isSolved(true)
                  .build();
            })
        .collect(Collectors.toList());
  }

  // TODO: 수정
  public ProblemSolvedResponseDto mapToProblemSolvedResponseDto(Problem problem, boolean isSolved) {
    return ProblemSolvedResponseDto.builder()
        .id(problem.getId())
        .problemId(problem.getProblemId())
        .problemTitle(problem.getTitle())
        .problemDifficulty(problem.getRank())
        .isSolved(isSolved)
        .build();
  }
}
