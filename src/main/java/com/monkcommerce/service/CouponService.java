package com.monkcommerce.service;

import com.monkcommerce.model.*;
import com.monkcommerce.repository.CartRepository;
import com.monkcommerce.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CouponRepository couponRepository;

    public List<Coupon> getAllCoupons(){
        return couponRepository.findAll();
    }

    public Optional<Coupon> getCouponById(Integer id) {
        return couponRepository.findById(id);
    }

    public Coupon createCoupon(CouponDTO couponDTO) {

        if(couponDTO==null){
            throw new RuntimeException("Coupon cannot be null");
        }
        Coupon coupon = new Coupon();

        coupon.setType(couponDTO.getType());
        coupon.setDiscount(couponDTO.getDiscount());
        coupon.setCondition(couponDTO.getCondition());
        coupon.setRepetitionLimit(couponDTO.getRepetitionLimit());
        coupon.setStartDate(couponDTO.getStartDate());
        coupon.setEndDate(couponDTO.getEndDate());
        coupon.setBuyProducts(couponDTO.getBuyProducts());
        coupon.setGetProducts(couponDTO.getGetProducts());

        return couponRepository.save(coupon);
    }

    public Cart applyCouponToCart(Integer couponId, Integer cartId) {

        if (couponId == null || cartId == null) {
            throw new RuntimeException("Coupon ID and Cart ID cannot be null");
        }


        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with ID: " + couponId));


        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));


        if (coupon.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon has expired and cannot be applied.");
        }

        if ("cart-wise".equals(coupon.getType())) {
            if (cart.getTotalAmount() <= 100) {
                throw new RuntimeException("Cart total must be greater than 100 for this coupon");
            }
            double discountAmount = cart.getTotalAmount() * coupon.getDiscount() / 100;
            cart.setTotalAmount(cart.getTotalAmount() - discountAmount);
        }

        return cartRepository.save(cart);
    }

    public Cart applyCouponToProduct(Integer couponId, Integer cartId, Integer productId) {

        if (couponId == null || cartId == null || productId == null) {
            throw new RuntimeException("Coupon ID, Cart ID, and Product ID cannot be null");
        }


        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("Coupon not found with ID: " + couponId));

        if (coupon.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Coupon has expired and cannot be applied.");
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));


        List<CartItem> matchingItems = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .collect(Collectors.toList());

        if (matchingItems.isEmpty()) {
            throw new RuntimeException("Product not found in cart with ID: " + productId);
        }


        if (!"product-wise".equals(coupon.getType())) {
            throw new RuntimeException("Coupon is not applicable for product-wise discounts");
        }


        for (CartItem cartItem : matchingItems) {
            double discountAmount = cartItem.getPrice() * coupon.getDiscount() / 100;
            cartItem.setPrice(cartItem.getPrice() - discountAmount);
        }


        double totalAmount = cart.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(totalAmount);

        return cartRepository.save(cart);
    }

    public Cart applyB2G1CouponToCart(Integer couponId, Integer cartId) {
        Optional<Coupon> couponOptional = couponRepository.findById(couponId);
        Optional<Cart> cartOptional = cartRepository.findById(cartId);

        if (couponOptional.isPresent() && cartOptional.isPresent()) {
            Coupon coupon = couponOptional.get();
            Cart cart = cartOptional.get();


            if (!"b2g1".equals(coupon.getType())) {
                throw new RuntimeException("Coupon is not of type B2G1");
            }

            if (coupon.getRepetitionLimit() < 0) {
                throw new RuntimeException("Repetition limit cannot be negative");
            }

            if (coupon.getEndDate().isBefore(LocalDate.now())) {
                throw new RuntimeException("Coupon has expired and cannot be applied.");
            }

            List<String> buyProducts = coupon.getBuyProducts();
            List<String> getProducts = coupon.getGetProducts();

            // Count how many "buy" products exist in the cart
            Map<String, Integer> cartProductCount = new HashMap<>();
            for (CartItem cartItem : cart.getCartItems()) {
                cartProductCount.put(cartItem.getProductName(), cartItem.getQuantity());
            }

            // Find the valid product sets for B2G1 application
            int totalBuyProducts = 0;
            List<CartItem> buyItems = new ArrayList<>();
            List<CartItem> getItems = new ArrayList<>();

            for (CartItem cartItem : cart.getCartItems()) {
                if (buyProducts.contains(cartItem.getProductName())) {
                    buyItems.add(cartItem);
                    totalBuyProducts += cartItem.getQuantity();
                } else if (getProducts.contains(cartItem.getProductName())) {
                    getItems.add(cartItem);
                }
            }

            if (buyItems.size() < 2) {
                throw new RuntimeException("Coupon not applicable: Less than 2 buy products in cart");
            }

            int freeProductsCount = 0;

            if (totalBuyProducts >= 2 && !getItems.isEmpty()) {
                freeProductsCount = Math.min(totalBuyProducts / 2, coupon.getRepetitionLimit());
            }

            if (freeProductsCount > 0) {
                int freeProductsAssigned = 0;

                for (CartItem cartItem : getItems) {
                    if (freeProductsAssigned < freeProductsCount) {
                        cartItem.setPrice(0.0);  // Assuming the free product is priced at 0
                        freeProductsAssigned++;
                    }
                }


                double totalAmount = cart.getCartItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .sum();
                cart.setTotalAmount(totalAmount);
                cartRepository.save(cart);
            }

            return cart;
        }

        return null;
    }



    public Cart applyApplicableCouponsToCart(Integer cartId,Integer productId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));


        LocalDate currentDate = LocalDate.now();
        List<Coupon> activeCoupons = couponRepository.findByStartDateBeforeAndEndDateAfter(currentDate, currentDate);


        for (Coupon coupon : activeCoupons) {
            switch (coupon.getType()) {
                case "cart-wise":
                    applyCouponToCart(cart.getId(), coupon.getId());
                    break;
                case "product-wise":
                    applyCouponToProduct(cart.getId(), coupon.getId(), productId);
                    break;
                case "b2g1":
                    applyB2G1CouponToCart(cart.getId(), coupon.getId());
                    break;
                default:
                    throw new RuntimeException("Unsupported coupon type: " + coupon.getType());
            }
        }
        return cartRepository.save(cart);
    }
        public Coupon updateCoupon(Integer couponId,CouponUpdateDTO couponUpdateDTO){

        if (couponUpdateDTO == null) {
            throw new RuntimeException("Coupon update data cannot be null");
        }

        Coupon coupon=couponRepository.findById(couponId).orElseThrow(()->new RuntimeException("Coupon not exists"));

        coupon.setDiscount(couponUpdateDTO.getDiscount());
        coupon.setCondition(couponUpdateDTO.getCondition());
        coupon.setType(couponUpdateDTO.getType());
        coupon.setStartDate(couponUpdateDTO.getStartDate());
        coupon.setEndDate(couponUpdateDTO.getEndDate());
        coupon.setRepetitionLimit(couponUpdateDTO.getRepetitionLimit());

        return couponRepository.save(coupon);
    }

    public String deleteCoupon(Integer couponId){
        Coupon coupon=couponRepository.findById(couponId).orElseThrow(()->new RuntimeException("Coupon not exists"));
        if(coupon!=null) {
            couponRepository.deleteById(couponId);
            return "Coupon Deleted";
        }
        return  "Coupon Not Deleted";
        }
    }

