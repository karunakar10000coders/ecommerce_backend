package com.ecommerce.service;

import com.ecommerce.dto.request.ReviewRequest;
import com.ecommerce.dto.response.ReviewResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.Review;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.util.MapperUtil;
import com.ecommerce.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final SecurityUtil securityUtil;
    private final MapperUtil mapperUtil;

    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(mapperUtil::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        User user = securityUtil.getCurrentUser();
        Product product = productService.findProduct(request.getProductId());

        if (reviewRepository.findByUserIdAndProductId(user.getId(), product.getId()).isPresent()) {
            throw new BadRequestException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return mapperUtil.toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        User user = securityUtil.getCurrentUser();
        if (!review.getUser().getId().equals(user.getId()) && user.getRole().name().equals("USER")) {
            throw new BadRequestException("Unauthorized");
        }
        reviewRepository.delete(review);
    }
}
