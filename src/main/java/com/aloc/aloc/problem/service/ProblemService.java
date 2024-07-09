package com.aloc.aloc.problem.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.problem.dto.response.ProblemSolvedResponseDto;
import com.aloc.aloc.problem.entity.Problem;
import com.aloc.aloc.problem.entity.SolvedProblem;
import com.aloc.aloc.problem.repository.ProblemRepository;
import com.aloc.aloc.problem.repository.SolvedProblemRepository;
import com.aloc.aloc.problemtype.enums.Course;
import com.aloc.aloc.problemtype.enums.Routine;
import com.aloc.aloc.problemtype.repository.ProblemTypeRepository;
import com.aloc.aloc.user.User;
import com.aloc.aloc.user.dto.response.SolvedUserResponseDto;
import com.aloc.aloc.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemSolvingService problemSolvingService;
	private final UserRepository userRepository;
	private final ProblemRepository problemRepository;
	private final ProblemTypeRepository problemTypeRepository;
	private final SolvedProblemRepository solvedProblemRepository;
	private final ProblemMapper problemMapper;
	private final PasswordEncoder passwordEncoder;

	User findUser(String username) {
		return userRepository.findByGithubId(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자 정보가 없습니다."));
	}

	public List<ProblemResponseDto> getVisibleProblemsWithSolvingCount() {
		// 공개된 문제 목록을 정렬하여 가져옵니다.
		List<Problem> problems = problemRepository.findAllByHiddenIsFalseOrderByCreatedAtDesc();
		return problems.stream()
			.map(problemMapper::mapToProblemResponseDto)
			.collect(Collectors.toList());
	}

	public List<ProblemResponseDto> getVisibleProblemsByAlgorithm(int season, int algorithmId) {
		List<Problem> problems = problemRepository
			.findPublicProblemsByAlgorithm(season, algorithmId);
		return problems.stream()
			.map(problemMapper::mapToProblemResponseDto)
			.collect(Collectors.toList());
	}

	public List<SolvedUserResponseDto> getSolvedUserListByProblemId(Long problemId) {
		// 문제가 존재하는지 확인합니다.
		checkProblemExist(problemId);

		// 문제를 푼 사용자 목록을 가져옵니다.
		List<SolvedProblem> solvedProblems = solvedProblemRepository.findAllByProblemId(problemId);
		return solvedProblems.stream()
			.map(solvedProblem -> {
				User user = solvedProblem.getUser();
				return problemMapper.mapToSolvedUserResponseDto(user, solvedProblem);
			})
			.collect(Collectors.toList());
	}

	public void checkProblemExist(Long problemId) {
		Optional<Problem> problem = problemRepository.findById(problemId);
		if (problem.isEmpty()) {
			throw new IllegalArgumentException("해당 문제가 존재하지 않습니다.");
		}
	}


	public ProblemResponseDto findTodayProblemByCourse(Course course) {
		// 오늘의 문제 타입을 가져옵니다.
		Long problemTypeId =
			problemTypeRepository.findProblemTypeByCourseAndRoutine(course, Routine.DAILY)
				.orElseThrow(() -> new IllegalArgumentException("오늘의 문제 타입이 없습니다."))
				.getId();

		// 오늘의 문제를 가져옵니다.
		Problem todayProblem = problemRepository.findLatestPublicProblemByProblemTypeId(problemTypeId);

		// 오늘의 문제가 없으면 오류를 발생시킵니다.
		if (todayProblem == null) {
			throw new IllegalArgumentException("오늘의 문제가 없습니다.");
		}
		return problemMapper.mapToProblemResponseDto(todayProblem);
	}



	public void updateProblemHiddenFalse(Routine routine) {
		List<Problem> problems = problemRepository.findAllByHiddenIsTrueAndProblemType_RoutineOrderByIdAsc(routine);
		if (routine.equals(Routine.DAILY)) {
			Problem problem = problems.get(0);
			problem.setHidden(false);
			problemRepository.save(problem);
		} else {
			for (Problem problem : problems) {
				problem.setHidden(false);
			}
			problemRepository.saveAll(problems);
		}
	}

	public Long getProblemTypeIdByCourseAndRoutine(Course course, Routine routine) {
		return problemTypeRepository.findProblemTypeByCourseAndRoutine(course, routine)
			.orElseThrow(() -> new IllegalArgumentException("문제 타입이 없습니다."))
			.getId();
	}

	List<Problem> getProblemsByAlgorithmWeekAndProblemTypeId(Integer algorithmId, Long problemTypeId) {
		return problemRepository.findAllByAlgorithmWeekAndProblemTypeId(algorithmId, problemTypeId);
	}
}
