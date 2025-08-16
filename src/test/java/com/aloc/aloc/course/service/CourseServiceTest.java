package com.aloc.aloc.course.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import java.util.NoSuchElementException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


import com.aloc.aloc.course.dto.request.CourseRequestDto;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.entity.Course;
import com.aloc.aloc.course.enums.CourseType;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.repository.CourseRepository;
import com.aloc.aloc.scraper.ProblemScrapingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
	@Mock private CourseRepository courseRepository;
	@Mock private ProblemScrapingService problemScrapingService;

	@InjectMocks private CourseService courseService;

	private Course stubCourseWithOf(Long id, String title, CourseType type,
									int problemCnt, int minRank, int maxRank, int duration) {
		// 1) 요청 DTO 채우기
		CourseRequestDto req = new CourseRequestDto();
		setFieldQuiet(req, "title", title);
		setFieldQuiet(req, "description", "test desc");
		setFieldQuiet(req, "type", type);
		setFieldQuiet(req, "problemCnt", problemCnt);
		setFieldQuiet(req, "minRank", minRank);
		setFieldQuiet(req, "maxRank", maxRank);
		setFieldQuiet(req, "duration", duration);
		setFieldQuiet(req, "algorithmIdList", java.util.List.of());

		// 2) 실제 팩토리로 Course 생성
		Course course = Course.of(req);

		// 3) averageRank 보장: calculateAverageRank() 있으면 호출, 없으면 직접 세팅
		try {
			var m = Course.class.getDeclaredMethod("calculateAverageRank");
			m.setAccessible(true);
			m.invoke(course);
		} catch (NoSuchMethodException e) {
			// 메소드가 없으면 (min+max)/2로 직접 주입
			int avg = (minRank + maxRank) / 2;
			setFieldQuiet(course, "averageRank", Integer.valueOf(avg));
		} catch (ReflectiveOperationException e) {
			throw new AssertionError("averageRank 계산 호출 실패", e);
		}

		// 4) 테스트용 id 주입
		setFieldQuiet(course, "id", id);
		return course;
	}

	private static void setFieldQuiet(Object target, String name, Object value) {
		try {
			java.lang.reflect.Field f = target.getClass().getDeclaredField(name);
			f.setAccessible(true);
			f.set(target, value);
		} catch (ReflectiveOperationException e) {
			throw new AssertionError("테스트 필드 주입 실패: " + name, e);
		}
	}

	@Test
	void getCourseNormalCase(){
		//given
		Pageable pageable = PageRequest.of(0, 2);

		Course course1 = stubCourseWithOf(1L, "test1", CourseType.DEADLINE,
			10, 800, 1200, 30);
		Course course2 = stubCourseWithOf(2L, "test2", CourseType.DAILY,
			12, 900, 1300, 28);


		Page<Course> mockCoursePage = new PageImpl<>(List.of(course1, course2),pageable, 2);
		when(courseRepository.findAll(pageable)).thenReturn(mockCoursePage);

		//when
		Page<CourseResponseDto> result = courseService.getCourses(pageable, null);

		//then
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
		assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
		assertThat(result.getContent().get(0).getStatus()).isEqualTo(UserCourseState.NOT_STARTED);
		assertThat(result.getContent().get(1).getStatus()).isEqualTo(UserCourseState.NOT_STARTED);
	}

	@Test
	void getCourseEmpryCase(){
		//given
		Pageable pageable = PageRequest.of(0, 2);
		Page<Course> emptyPage = Page.empty(pageable);
		when(courseRepository.findAll(pageable)).thenReturn(emptyPage);

		//when
		Page<CourseResponseDto> result = courseService.getCourses(pageable, null);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isZero();
	}

	@Test
	void updateCourseNormalCase(){
		//given
		Long id = 1L;
		Course mockCourse = mock(Course.class);
		when(courseRepository.findById(id)).thenReturn(Optional.of(mockCourse));
		when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

		//when
		Course updated = courseService.updateCourse(id);

		//then
		assertThat(updated).isSameAs(mockCourse);
	}

	@Test
	void updateCourseEmptyCase(){
		Long id = 1L;
		when(courseRepository.findById(id)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> courseService.updateCourse(id))
			.isInstanceOf(NoSuchElementException.class)
			.hasMessageContaining("해당 코스 아이디로 된 코스가 존재하지 않습니다.");

		verify(courseRepository, times(1)).findById(id);
		verify(courseRepository, never()).save(any());
	}
}
