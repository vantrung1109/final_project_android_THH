package com.example.projectfinaltth.data.model.response.cart;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartListItemResponse {
    @SerializedName("cartItems")
    private List<CartItem> cartItems;
}
