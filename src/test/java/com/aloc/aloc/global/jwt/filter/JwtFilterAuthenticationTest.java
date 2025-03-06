// package com.aloc.aloc.global.jwt.filter;
//
// import static org.assertj.core.api.Assertions.assertThat;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// import com.aloc.aloc.auth.dto.RefreshTokenDto;
// import com.aloc.aloc.global.jwt.service.JwtService;
// import com.aloc.aloc.user.entity.User;
// import com.aloc.aloc.user.repository.UserRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import jakarta.persistence.EntityManager;
// import jakarta.servlet.http.Cookie;
// import java.util.HashMap;
// import java.util.Map;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult;
// import org.springframework.transaction.annotation.Transactional;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional
// @ActiveProfiles("test")
// public class JwtFilterAuthenticationTest {
//
//  @Autowired MockMvc mockMvc;
//
//  @Autowired UserRepository userRepository;
//
//  @Autowired EntityManager em;
//
//  @Autowired JwtService jwtService;
//
//  BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//
//  @Value("${jwt.secret}")
//  private String secret;
//
//  @Value("${jwt.access.header}")
//  private String accessHeader;
//
//  @Value("${jwt.refresh.header}")
//  private String refreshHeader;
//
//  private final String githubId = "github";
//  private final String password = "password";
//
//  private static final String LOGIN_URL = "/api2/login";
//  private static final String REFRESH_URL = "/api2/refresh";
//  private static final String TEST_URL = "/api2/weekly-problems";
//
//  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
//  private static final String BEARER = "Bearer ";
//
//  private final ObjectMapper objectMapper = new ObjectMapper();
//
//  private void clear() {
//    em.flush();
//    em.clear();
//  }
//
//  @BeforeEach
//  public void init() {
//    userRepository.save(
//        User.builder()
//            .username("홍길동")
//            .password(bCryptPasswordEncoder.encode(password))
//            .githubId(githubId)
//            .baekjoonId("baekjoon")
//            .studentId("20")
//            .notionEmail("notion@uos.ac.kr")
//            .discordId("discord")
//            .build());
//    clear();
//  }
//
//  private Map getGithubIdPasswordMap() {
//    Map<String, String> map = new HashMap<>();
//    map.put("githubId", githubId);
//    map.put("password", password);
//    return map;
//  }
//
//  private Map getAccessAndRefreshToken() throws Exception {
//
//    Map map = getGithubIdPasswordMap();
//    MvcResult result =
//        mockMvc
//            .perform(
//                post(LOGIN_URL)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(map)))
//            .andReturn();
//    String accessToken = result.getResponse().getHeader(accessHeader);
//    String refreshToken = null;
//    Cookie[] cookies = result.getResponse().getCookies();
//    for (Cookie cookie : cookies) {
//      if ("refreshToken".equals(cookie.getName())) {
//        refreshToken = cookie.getValue();
//        break;
//      }
//    }
//    Map<String, String> tokenMap = new HashMap<>();
//    tokenMap.put(accessHeader, accessToken);
//    tokenMap.put(refreshHeader, refreshToken);
//
//    return tokenMap;
//  }
//
//  @Test
//  public void accessRefreshDoesntExist() throws Exception {
//    // when, then
//    mockMvc
//        .perform(get(LOGIN_URL + "123")) // login 이 아닌 다른 임의의 주소
//        .andExpect(status().isNotFound());
//  }
//
//  @Test
//  public void accessTokenOnlyExist() throws Exception {
//    // given
//    Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
//    String accessToken = (String) accessAndRefreshToken.get(accessHeader);
//
//    // when, then
//    mockMvc
//        .perform(
//            get(LOGIN_URL + "123")
//                .header(accessHeader, BEARER + accessToken)) // login이 아닌 다른 임의의 주소
//        .andExpectAll(status().isNotFound()); // 없는 주소로 보냈으므로 NotFound
//  }
//
//  @Test
//  public void invalidAccessTokenAuthFailedWith403() throws Exception {
//    // given
//    Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
//    String accessToken = (String) accessAndRefreshToken.get(accessHeader);
//
//    // when, then
//    mockMvc
//        .perform(
//            get(LOGIN_URL + "123").header(accessHeader, accessToken + "1")) // login이 아닌 다른 임의의 주소
//        .andExpectAll(status().isNotFound()); // 없는 주소로 보냈으므로 NotFound
//  }
//
//  @Test
//  public void validRefreshTokenWithAccessTokenThenReissueAccessTokenWith200() throws Exception {
//    // given
//    Map accessAndRefreshToken = getAccessAndRefreshToken();
//    String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
//
//    // when, then
//    MvcResult result =
//        mockMvc
//            .perform(
//                get(REFRESH_URL)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .content(objectMapper.writeValueAsString(new RefreshTokenDto(refreshToken)))
//                    .header(accessHeader, BEARER))
//            .andExpect(status().isOk())
//            .andReturn();
//
//    // accessToken은 재발급
//    assertThat(result.getResponse().getStatus()).isEqualTo(200);
//  }
//
//  @Test
//  public void invalidRefreshTokenWithValidAccessTokenThen401() throws Exception {
//    // given
//    Map accessAndRefreshToken = getAccessAndRefreshToken();
//    String accessToken = (String) accessAndRefreshToken.get(accessHeader);
//    String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
//
//    // when, then
//    MvcResult result =
//        mockMvc
//            .perform(
//                get(TEST_URL)
//                    .header(refreshHeader, BEARER + refreshToken + 1)
//                    .header(accessHeader, BEARER + accessToken + 1))
//            .andExpect(status().isUnauthorized()) // 모든 토큰이 유효하지 않음
//            .andReturn();
//
//    String responseAccessToken = result.getResponse().getHeader(accessHeader);
//    String responseRefreshToken = result.getResponse().getHeader(refreshHeader);
//
//    assertThat(responseAccessToken).isNull(); // accessToken은 재발급되지 않음
//    assertThat(responseRefreshToken).isNull(); // refreshToken은 재발급되지 않음
//  }
//
//  @Test
//  public void invalidRefreshTokenWithInvalidAccessTokenThenReturn404() throws Exception {
//    // given
//    Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
//    String accessToken = (String) accessAndRefreshToken.get(accessHeader);
//    String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
//
//    // when, then
//    MvcResult result =
//        mockMvc
//            .perform(
//                get(LOGIN_URL + "123")
//                    .header(refreshHeader, BEARER + refreshToken + 1)
//                    .header(accessHeader, BEARER + accessToken + 1))
//            .andExpect(status().isNotFound()) // 없는 주소로 보냈으므로 NotFound
//            .andReturn();
//
//    String responseAccessToken = result.getResponse().getHeader(accessHeader);
//    String responseRefreshToken = result.getResponse().getHeader(refreshHeader);
//
//    assertThat(responseAccessToken).isNull(); // accessToken은 재발급되지 않음
//    assertThat(responseRefreshToken).isNull(); // refreshToken은 재발급되지 않음
//  }
//
//  @Test
//  public void requestWithLoginUrlThenFilterOff() throws Exception {
//    // given
//    Map<String, String> accessAndRefreshToken = getAccessAndRefreshToken();
//    String accessToken = (String) accessAndRefreshToken.get(accessHeader);
//    String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
//
//    // when, then
//    MvcResult result =
//        mockMvc
//            .perform(
//                post(LOGIN_URL) // get인 경우 config에서 permitAll을 했기에 notFound
//                    .header(refreshHeader, BEARER + refreshToken)
//                    .header(accessHeader, BEARER + accessToken))
//            .andExpect(status().isBadRequest())
//            .andReturn();
//  }
// }
