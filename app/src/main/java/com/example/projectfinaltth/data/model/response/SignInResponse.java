package com.example.projectfinaltth.data.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
    String email;
    String name;
    String token;
    String role;
    String cartId;
}
