package com.monkcommerce.controller;

import com.monkcommerce.model.Cart;
import com.monkcommerce.model.Coupon;
import com.monkcommerce.model.CouponDTO;
import com.monkcommerce.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    public List<Coupon> getCoupons() {
        return couponService.getAllCoupons();
    }


    @GetMapping("/{id}")
    public Optional<Coupon> getCoupon(@PathVariable Integer id) {
        return couponService.getCouponById(id);
    }

    @PostMapping("/apply/{couponId}/cart/{cartId}")
    public ResponseEntity<?> applyCoupon(@PathVariable Integer couponId, @PathVariable Integer cartId) {
        try {
            Cart cart = couponService.applyCouponToCart(couponId, cartId);
            return ResponseEntity.ok(cart);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/apply/{couponId}/cart/{cartId}/product/{productId}")
    public ResponseEntity<?> applyCouponToProduct(
            @PathVariable Integer couponId,
            @PathVariable Integer cartId,
            @PathVariable Integer productId) {
        try {
            Cart cart = couponService.applyCouponToProduct(couponId, cartId, productId);
            return ResponseEntity.ok(cart);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/apply/b2g1/{couponId}/cart/{cartId}")
    public ResponseEntity<?> applyCouponBuy2Get1(@PathVariable Integer couponId,@PathVariable Integer cartId){
        try {
            Cart updatedCart = couponService.applyB2G1CouponToCart(couponId, cartId);
            if (updatedCart == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND   ).body("Coupon could not be applied. Ensure the coupon and cart are valid.");
            }
            return  ResponseEntity.ok(updatedCart);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createCoupon(@RequestBody CouponDTO couponDTO) {
        try{
            return ResponseEntity.ok(couponService.createCoupon(couponDTO));
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteCoupoun(Integer coupounId){
        return ResponseEntity.ok(couponService.deleteCoupon(coupounId));
    }


}
