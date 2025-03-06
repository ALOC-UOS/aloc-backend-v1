//package com.aloc.aloc.color.controller;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.aloc.aloc.color.dto.response.ColorResponseDto;
//import com.aloc.aloc.color.service.ColorService;
//import com.aloc.aloc.global.login.service.UserDetailsServiceImpl;
//import com.aloc.aloc.problemtype.enums.Course;
//import com.aloc.aloc.user.entity.User;
//import com.aloc.aloc.user.enums.Authority;
//import java.util.Collections;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class ColorControllerTest {
//  @MockBean private ColorService colorService;
//
//  @Autowired private MockMvc mockMvc;
//
//  @MockBean private UserDetailsServiceImpl userDetailsService;
//
//  @BeforeEach
//  void setUp() {
//    User user =
//        new User(
//            "user",
//            "userBaekjoon",
//            "githubId",
//            "20210002",
//            "password",
//            "userDiscord",
//            2,
//            "userNotion",
//            Course.FULL,
//            "oauthId",
//            "userId",
//            "nickname",
//            "profileImageUrl");
//    user.setAuthority(Authority.ROLE_USER);
//    UserDetails userDetails =
//        new org.springframework.security.core.userdetails.User(
//            user.getGithubId(),
//            user.getPassword(),
//            Collections.singletonList(new SimpleGrantedAuthority(user.getAuthority().toString())));
//
//    when(userDetailsService.loadUserByUsername("githubId")).thenReturn(userDetails);
//  }
//
//  @Test
//  @DisplayName("프로필 색상 변경 성공 테스트")
//  @WithMockUser(username = "githubId", roles = "USER")
//  void changeColor() throws Exception {
//    ColorResponseDto colorResponseDto = new ColorResponseDto(100, "Green", "#FFFFFF");
//    // given
//    when(colorService.changeColor("githubId")).thenReturn(colorResponseDto);
//
//    mockMvc
//        .perform(put("/api2/color/change").contentType(MediaType.APPLICATION_JSON))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.result.coin").value(100))
//        .andExpect(jsonPath("$.result.colorName").value("Green"))
//        .andExpect(jsonPath("$.result.colorCode").value("#FFFFFF"));
//  }
//}
