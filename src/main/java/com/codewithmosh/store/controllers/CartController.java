package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.AddItemToCartRequest;
import com.codewithmosh.store.dtos.CartDto;
import com.codewithmosh.store.dtos.CartItemDto;
import com.codewithmosh.store.dtos.UpdateCartItemRequest;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.ProductNotFoundException;
import com.codewithmosh.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {
  private final CartService cartService;

  @PostMapping
  public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder) {
    var cartDto = cartService.getCart();
    var uri = uriBuilder.path("/carts/{id}").build().toUri();

    return ResponseEntity.created(uri).body(cartDto);
  }

  @PostMapping("/{cartId}/items")
  public ResponseEntity<CartItemDto> addToCart(
      @PathVariable UUID cartId, @RequestBody AddItemToCartRequest request) {
    var cartItemDto = cartService.addToCart(cartId, request.getProductId());

    return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
  }

  @GetMapping("/{cartId}")
  public ResponseEntity<CartDto> getCart(@PathVariable UUID cartId) {
    var cartDto = cartService.getCart(cartId);

    return ResponseEntity.ok(cartDto);
  }

  @PutMapping("/{cartId}/items/{productId}")
  public ResponseEntity<?> updateItem(
      @PathVariable(name = "cartId") UUID cartId,
      @PathVariable(name = "productId") Long productId,
      @Valid @RequestBody UpdateCartItemRequest request) {

    var cartItem = cartService.updateItem(cartId, productId, request.getQuantity());

    return ResponseEntity.ok(cartItem);
  }

  @DeleteMapping("/{cartId}/items/{productId}")
  public ResponseEntity<Void> removeItem(
      @PathVariable("cartId") UUID cartId, @PathVariable("productId") Long productId) {

    cartService.removeItem(cartId, productId);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{cartId}")
  public ResponseEntity<Void> clearCart(@PathVariable UUID cartId) {
    cartService.removeCart(cartId);

    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(CartNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleCartNotFound() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("Errors", "Cart not found."));
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleProductNotFound() {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("Errors", "Product not found in the cart."));
  }
}
