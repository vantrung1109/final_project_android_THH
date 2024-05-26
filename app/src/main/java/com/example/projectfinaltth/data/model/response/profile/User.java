package com.example.projectfinaltth.data.model.response.profile;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User{
    @SerializedName("_id")
    private String id;

    private String cloudinary;
    private String picture;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String otp;
    private String role;
    private boolean status;
    private String description;
    private String createdAt;
    private String updatedAt;
    private int __v;
}
