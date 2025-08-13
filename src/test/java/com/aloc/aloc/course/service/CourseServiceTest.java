package com.aloc.aloc.course.service;

import com.aloc.aloc.course.repository.CourseRepository;
import com.aloc.aloc.scraper.ProblemScrapingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
	@Mock private CourseRepository courseRepository;
	@Mock private ProblemScrapingService problemScrapingService;

	@InjectMocks private CourseService courseService;

	@Test
	void getCourseNormalCase(){
		//given
		Pageable pageable = PageRequest.of(1, 2);

	}
}
