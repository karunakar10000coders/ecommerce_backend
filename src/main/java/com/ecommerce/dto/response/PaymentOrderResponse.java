package com.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentOrderResponse {
    private Long orderId;
    private String razorpayOrderId;
    private BigDecimal amount;
    private String currency;
    private String keyId;
}
