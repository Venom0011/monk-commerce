package com.monkcommerce.service;

import com.monkcommerce.model.Cart;
import com.monkcommerce.model.CartItem;
import com.monkcommerce.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {


    private Logger logger= LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    public Cart addToCart(Integer cartId,CartItem cartItem){
        Optional<Cart> cartOptional=cartRepository.findById(cartId);
        Cart cart=null;
        if(cartOptional.isPresent()){
             cart=cartOptional.get();
        }else {
            cart=new Cart();
            cart.setTotalAmount(0.0);
            cartRepository.save(cart);
        }
        cart.addCartItem(cartItem);
        double totalAmount=cart.getCartItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
        return cart;
    }
}
