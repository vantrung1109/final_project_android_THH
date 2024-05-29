package com.example.projectfinaltth.data.model.response.user;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    String id;
    String email;
    String name;
    String role; // Giả sử role có trong phản hồi này
}
