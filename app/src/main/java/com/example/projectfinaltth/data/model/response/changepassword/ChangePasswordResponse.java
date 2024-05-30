package com.example.projectfinaltth.data.model.response.changepassword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {
    private boolean success;
    private String message;
}
