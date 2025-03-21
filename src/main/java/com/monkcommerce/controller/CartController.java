package com.monkcommerce.controller;

import com.monkcommerce.model.Cart;
import com.monkcommerce.model.CartItem;
import com.monkcommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @PostMapping("/{cartId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Cart addItemToCart(@PathVariable Integer cartId, @RequestBody CartItem cartItem) {
        return cartService.addToCart(cartId, cartItem);
    }
}
