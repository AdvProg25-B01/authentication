package id.ac.ui.cs.advprog.authentication.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}