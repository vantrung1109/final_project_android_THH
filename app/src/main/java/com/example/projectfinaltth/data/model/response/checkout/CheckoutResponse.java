package com.example.projectfinaltth.data.model.response.checkout;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {
    private String message;
    private String invoiceId;
}
