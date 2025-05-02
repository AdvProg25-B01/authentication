package id.ac.ui.cs.advprog.authentication.dto;

import id.ac.ui.cs.advprog.authentication.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}