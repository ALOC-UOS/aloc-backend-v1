package com.aloc.aloc.user.entity;

public record UserOAuthProfile(
    String oauthId, String userId, String nickname, String profileImageUrl) {}
