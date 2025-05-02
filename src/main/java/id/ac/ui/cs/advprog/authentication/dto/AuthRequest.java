package id.ac.ui.cs.advprog.authentication.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}