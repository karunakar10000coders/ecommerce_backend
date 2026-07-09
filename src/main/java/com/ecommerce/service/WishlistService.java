package com.ecommerce.service;

import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.entity.Wishlist;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.WishlistRepository;
import com.ecommerce.util.MapperUtil;
import com.ecommerce.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final SecurityUtil securityUtil;
    private final MapperUtil mapperUtil;

    public List<ProductResponse> getWishlist() {
        User user = securityUtil.getCurrentUser();
        return wishlistRepository.findByUserId(user.getId()).stream()
                .map(w -> mapperUtil.toProductResponse(w.getProduct()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToWishlist(Long productId) {
        User user = securityUtil.getCurrentUser();
        Product product = productService.findProduct(productId);

        if (wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            throw new BadRequestException("Product already in wishlist");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeFromWishlist(Long productId) {
        User user = securityUtil.getCurrentUser();
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not in wishlist"));
        wishlistRepository.delete(wishlist);
    }

    public boolean isInWishlist(Long productId) {
        User user = securityUtil.getCurrentUser();
        return wishlistRepository.existsByUserIdAndProductId(user.getId(), productId);
    }
}
