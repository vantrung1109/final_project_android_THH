package com.example.projectfinaltth.data.model.request.password;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    private String password;
    private String newPassword;
    private String confirmPassword;
}
