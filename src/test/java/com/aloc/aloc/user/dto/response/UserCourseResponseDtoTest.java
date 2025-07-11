package com.aloc.aloc.user.dto.response;

import static org.assertj.core.api.Assertions.*;

import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.problem.dto.response.ProblemResponseDto;
import com.aloc.aloc.usercourse.entity.UserCourse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserCourseResponseDtoTest {

  @Test
  @DisplayName("UserCourse와 Course, 문제 목록으로 UserCourseResponseDto 생성 성공")
  void of() {
    // given
    Course course = Course.builder()
        .title("테스트 코스")
        .courseType(CourseType.DAILY)
        .problemCnt(5)
        .build();
    // Course ID는 빌더에서 설정할 수 없으므로 테스트에서는 임의 값 사용

    UserCourse userCourse = UserCourse.builder()
        .course(course)
        .userCourseState(UserCourseState.IN_PROGRESS)
        .closedAt(LocalDateTime.of(2024, 12, 31, 23, 59, 59))
        .build();

    List<ProblemResponseDto> problems = List.of(
        createMockProblemResponseDto(1001, "문제1"),
        createMockProblemResponseDto(1002, "문제2")
    );
    int todayProblemId = 1001;

    // when
    UserCourseResponseDto result = UserCourseResponseDto.of(userCourse, course, problems, todayProblemId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(course.getId()); // course에서 가져온 ID
    assertThat(result.getTitle()).isEqualTo("테스트 코스");
    assertThat(result.getCourseType()).isEqualTo(CourseType.DAILY);
    assertThat(result.getProblemCnt()).isEqualTo(5);
    assertThat(result.getTodayProblemId()).isEqualTo(1001);
    assertThat(result.getClosedAt()).isEqualTo(LocalDateTime.of(2024, 12, 31, 23, 59, 59));
    assertThat(result.getProblems()).hasSize(2);
    assertThat(result.getProblems().get(0).getTitle()).isEqualTo("문제1");
  }

  @Test
  @DisplayName("빈 문제 목록으로 DTO 생성")
  void of_WithEmptyProblems() {
    // given
    Course course = Course.builder()
        .title("빈 코스")
        .courseType(CourseType.DEADLINE)
        .problemCnt(0)
        .build();

    UserCourse userCourse = UserCourse.builder()
        .course(course)
        .userCourseState(UserCourseState.IN_PROGRESS)
        .build();

    List<ProblemResponseDto> emptyProblems = List.of();
    int todayProblemId = 0;

    // when
    UserCourseResponseDto result = UserCourseResponseDto.of(userCourse, course, emptyProblems, todayProblemId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(course.getId());
    assertThat(result.getTitle()).isEqualTo("빈 코스");
    assertThat(result.getCourseType()).isEqualTo(CourseType.DEADLINE);
    assertThat(result.getProblemCnt()).isEqualTo(0);
    assertThat(result.getTodayProblemId()).isEqualTo(0);
    assertThat(result.getProblems()).isEmpty();
    assertThat(result.getClosedAt()).isNull();
  }

  private ProblemResponseDto createMockProblemResponseDto(int problemId, String title) {
    return ProblemResponseDto.builder()
        .problemId(problemId)
        .title(title)
        .rank(15)
        .build();
  }
}
