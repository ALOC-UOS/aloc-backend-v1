package com.aloc.aloc.problem.service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.UserProblem;
import com.aloc.aloc.problem.enums.UserProblemStatus;
import com.aloc.aloc.problem.repository.UserProblemRepository;
import com.aloc.aloc.problemtag.ProblemTag;
import com.aloc.aloc.tag.Tag;
import com.aloc.aloc.tag.dto.TagSimpleDto;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;
import com.aloc.aloc.user.entity.User;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProblemMapper {
  private final UserProblemRepository userProblemRepository;

  @Value("${app.season}")
  private Integer season;

  public ProblemResponseDto mapToProblemResponseDto(Problem problem) {
    return ProblemResponseDto.builder()
        .id(problem.getId())
        .problemId(problem.getProblemId())
        .title(problem.getTitle())
        .tags(mapToTagSimpleDtoList(problem.getProblemTagList()))
        .difficulty(problem.getDifficulty())
        .solvingCount(
            userProblemRepository.countSolvingUsersByProblemId(
                problem.getId(), season, UserProblemStatus.SOLVED))
        .build();
  }

  private List<TagSimpleDto> mapToTagSimpleDtoList(List<ProblemTag> problemTags) {
    return problemTags.stream().map(this::mapToTagSimpleDto).collect(Collectors.toList());
  }

  private TagSimpleDto mapToTagSimpleDto(ProblemTag problemTag) {
    Tag tag = problemTag.getTag();
    return TagSimpleDto.builder()
        .id(tag.getId())
        .koreanName(tag.getKoreanName())
        .englishName(tag.getEnglishName())
        .build();
  }

  SolvedUserResponseDto mapToSolvedUserResponseDto(User user, UserProblem userProblem) {
    return SolvedUserResponseDto.builder()
        .username(user.getName())
        .baekjoonId(user.getBaekjoonId())
        .profileColor(user.getProfileColor())
        .profileImageUrl(user.getProfileImageUrl())
        .rank(user.getRank())
        .coin(user.getCoin())
        .solvedAt(userProblem.getSolvedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        .build();
  }

  // TODO: 수정
  List<ProblemSolvedResponseDto> mapSolvedProblemToDtoList(List<UserProblem> solvedProblemList) {
    return solvedProblemList.stream()
        .map(
            solvedProblem -> {
              Problem problem = solvedProblem.getProblem();
              return ProblemSolvedResponseDto.builder()
                  .id(problem.getId())
                  .problemId(problem.getProblemId())
                  .problemTitle(problem.getTitle())
                  .problemDifficulty(problem.getDifficulty())
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
        .problemDifficulty(problem.getDifficulty())
        .isSolved(isSolved)
        .build();
  }
}
