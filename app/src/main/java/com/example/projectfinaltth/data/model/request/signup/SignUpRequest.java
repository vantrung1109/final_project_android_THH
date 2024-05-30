package com.example.projectfinaltth.data.model.request.signup;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    String email;
    String name;
    String password;
    String confirmPassword;
}
