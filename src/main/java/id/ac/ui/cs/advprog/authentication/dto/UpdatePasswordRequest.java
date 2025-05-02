package id.ac.ui.cs.advprog.authentication.dto;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String email;
    private String oldPassword;
    private String newPassword;
}
