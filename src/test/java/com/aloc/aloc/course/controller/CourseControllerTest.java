package com.aloc.aloc.course.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aloc.aloc.common.fixture.TestFixture;
import com.aloc.aloc.course.dto.response.CourseResponseDto;
import com.aloc.aloc.course.enums.UserCourseState;
import com.aloc.aloc.course.service.CourseService;
import com.aloc.aloc.user.service.facade.UserFacade;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserFacade userFacade;

  @MockBean private CourseService courseService;

  @MockBean private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @WithMockUser
  void getCoursesForLoginUser() throws Exception {
    // given
    Page<CourseResponseDto> mockPage =
        new PageImpl<>(
            List.of(TestFixture.getMockCourseResponseDtoByStatus(UserCourseState.IN_PROGRESS)));
    given(userFacade.getCoursesByUser(any(), any(), any())).willReturn(mockPage);

    // when & then
    mockMvc
        .perform(get("/api/courses?page=0&size=9&sort=createdAt,desc")) // 로그인 사용자 모킹
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result.content[0].title").value("코스1"))
        .andExpect(jsonPath("$.result.content[0].status").value("IN_PROGRESS"));
  }

  @Test
  @WithAnonymousUser
  void getCoursesForLogoutUser() throws Exception {
    // given
    Page<CourseResponseDto> mockPage =
        new PageImpl<>(
            List.of(TestFixture.getMockCourseResponseDtoByStatus(UserCourseState.NOT_STARTED)));
    given(courseService.getCourses(any(), any())).willReturn(mockPage);

    // when & then
    mockMvc
        .perform(get("/api/courses?page=0&size=9&sort=createdAt,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result.content[0].status").value("NOT_STARTED"));
  }
}
