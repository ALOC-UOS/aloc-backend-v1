package com.aloc.aloc.profilebackgroundcolor.enums;

/**
 * 프로필 배경 색상 타입을 정의하는 Enum
 * - COMMON: 일반 색상 (85% 확률)
 * - RARE: 희귀 색상 (10% 확률, 86~95%)  
 * - SPECIAL: 특별 색상 (5% 확률, 96~100%)
 */
public enum ColorType {
    COMMON("common"),
    RARE("rare"), 
    SPECIAL("special");
    
    private final String value;
    
    ColorType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}