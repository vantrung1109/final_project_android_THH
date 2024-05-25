package com.example.projectfinaltth.data.model.response.checkout;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    List<CartItemResponse> cartItems;
}
