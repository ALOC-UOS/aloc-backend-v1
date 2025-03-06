// package com.aloc.aloc.notion.service;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import com.aloc.aloc.notion.NotionProperty;
// import com.aloc.aloc.notion.dto.response.StudyScheduleResponseDto;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
//
// @SpringBootTest
// @ActiveProfiles("test")
// public class NotionServiceIntegrationTest {
//  @Autowired private NotionService notionService;
//  @Autowired private NotionProperty notionProperty;
//
//  @Test
//  void getStudySchedule() {
//    StudyScheduleResponseDto response = notionService.getStudySchedule();
//    assertNotNull(response);
//    assertTrue(response.getWeek() > 0);
//    assertNotNull(response.getDate());
//    assertNotNull(response.getLocation());
//  }
// }
