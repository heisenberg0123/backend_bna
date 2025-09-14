package com.example.reporting.dtos;

import com.example.reporting.entities.UserRole;

public class AuthenticationResponse {

    private  String jwt;
    private UserRole userRole;
    private Long userId;
    private String profile ;

    public AuthenticationResponse() {

    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public AuthenticationResponse(String jwt, UserRole userRole, Long userId, String profile) {
        this.jwt = jwt;
        this.userRole = userRole;
        this.userId = userId;
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "jwt='" + jwt + '\'' +
                ", userRole=" + userRole +
                ", userId=" + userId +
                ", profile='" + profile + '\'' +
                '}';
    }
}
