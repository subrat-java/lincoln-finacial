package com.example.Auth_Service.dto;

public record AuthResponse(String token,
                           String username,
                           String role)
{}
