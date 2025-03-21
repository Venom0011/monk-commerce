# Coupon Service Documentation

## Implemented Cases

### 1. **Create Coupon**
- Allows creating a new coupon with a specified type, discount, condition, and valid date range.

### 2. **Get All Coupons**
- Fetches all the coupons available in the system.

### 3. **Get Coupon by ID**
- Retrieves a coupon by its unique ID.

### 4. **Apply Coupon to Cart**
- Applies a coupon to a cart based on the cart total amount, with validation to ensure the coupon has not expired.

### 5. **Apply Coupon to Product**
- Applies a coupon to a specific product in the cart, ensuring the coupon type is "product-wise" and that it is not expired.

### 6. **Apply Buy 2 Get 1 (B2G1) Coupon**
- Applies a "Buy 2 Get 1" coupon to the cart, ensuring the coupon type is valid, the repetition limit is not exceeded, and the coupon has not expired.

### 7. **Apply Applicable Coupons to Cart**
- Applies all active coupons (with valid start and end dates) to the cart based on the coupon type ("cart-wise", "product-wise", or "B2G1").

### 8. **Update Coupon**
- Allows updating the properties of an existing coupon, such as discount, condition, and valid date range.

### 9. **Delete Coupon**
- Deletes a coupon from the system by its ID, ensuring the coupon exists before deletion.

## Error Handling

- **Expired Coupon**: Any coupon with an expiration date in the past cannot be applied.
- **Repetition Limit**: Ensures that the coupon's repetition limit is not exceeded, especially in "Buy 2 Get 1" cases.
- **Invalid Coupon Type**: Only applicable coupon types ("cart-wise", "product-wise", "b2g1") are processed.

## Assumptions

- The API assumes that the product and coupon IDs are valid and exist in the system.
- Coupons are only valid for one cart session at a time (i.e., cannot be reused in multiple carts).

## Limitations:
- The current implementation only handles basic coupon types (cart-wise, product-wise, BxGy). Advanced features like multi 
  level discount stacking are not covered.
