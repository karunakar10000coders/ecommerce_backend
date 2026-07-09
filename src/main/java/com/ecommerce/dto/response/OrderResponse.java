package com.ecommerce.dto.response;

import com.ecommerce.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String razorpayOrderId;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private String userName;
    private String userEmail;
}
