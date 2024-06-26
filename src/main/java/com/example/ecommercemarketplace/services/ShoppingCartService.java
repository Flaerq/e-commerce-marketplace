package com.example.ecommercemarketplace.services;

import com.example.ecommercemarketplace.dto.CartItemQuantityUpdateRequest;
import com.example.ecommercemarketplace.dto.CartItemRequestDto;
import com.example.ecommercemarketplace.dto.CartItemResponseDto;
import com.example.ecommercemarketplace.dto.ShoppingCartResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {

    ShoppingCartResponseDto addItemToShoppingCart(Authentication authentication, CartItemRequestDto cartItem);

    Page<CartItemResponseDto> getAllItemsByShoppingCart(Authentication authentication, Pageable pageable);

    void deleteCartItem(Authentication authentication, Long id);

    CartItemResponseDto updateCartItemQuantity(Authentication authentication, Long id, CartItemQuantityUpdateRequest updateRequest);

    void clearShoppingCart(Authentication authentication);
}
