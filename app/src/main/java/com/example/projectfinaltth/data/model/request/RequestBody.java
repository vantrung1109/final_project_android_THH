package com.example.projectfinaltth.data.model.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestBody {
    private String keyword;
    private String topic;
    private int page;
    private String sort;
}
