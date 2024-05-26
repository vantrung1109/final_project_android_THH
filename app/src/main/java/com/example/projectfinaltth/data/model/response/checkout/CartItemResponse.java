package com.example.projectfinaltth.data.model.response.checkout;

import com.example.projectfinaltth.data.model.response.cart.CartItem;

import java.util.Collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    String _id;
    String cartId;
    String courseId;
    String createdAt;
    String updatedAt;
    int __v;


}
