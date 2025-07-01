package com.aloc.aloc.admin.dto.request;

import com.aloc.aloc.user.enums.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AdminRoleChangeRequestDto {

	@Schema(description = "권한을 변경할 대상 사용자명")
	private List<UUID> userIds;

	@Schema(description = "새로 부여할 권한", example = "ROLE_ADMIN")
	private Authority role;

}
