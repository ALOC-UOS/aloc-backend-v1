package com.aloc.aloc.domain.user.dto.response;

import com.aloc.aloc.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

	private String username;
	private String githubId;
	private String baekjoonId;
	private String profileColor;
	private String studentId;
	private Integer profileNumber;
	private Integer rank;
	private Integer coin;
}
