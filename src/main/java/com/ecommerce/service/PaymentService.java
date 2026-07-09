package com.ecommerce.service;

import com.ecommerce.dto.request.PaymentVerifyRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.dto.response.PaymentOrderResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.util.SecurityUtil;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;
    private final SecurityUtil securityUtil;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Transactional
    public PaymentOrderResponse createRazorpayOrder(Long orderId) {
        Order order = orderService.findOrder(orderId);

        if (!order.getUser().getId().equals(securityUtil.getCurrentUser().getId())) {
            throw new BadRequestException("Unauthorized");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is not pending payment");
        }

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject options = new JSONObject();
            options.put("amount", order.getTotalAmount().multiply(BigDecimal.valueOf(100)).intValue());
            options.put("currency", "INR");
            options.put("receipt", "order_" + order.getId());

            com.razorpay.Order razorpayOrder = client.orders.create(options);
            String razorpayOrderId = razorpayOrder.get("id");

            orderService.updateRazorpayOrderId(orderId, razorpayOrderId);

            return PaymentOrderResponse.builder()
                    .orderId(orderId)
                    .razorpayOrderId(razorpayOrderId)
                    .amount(order.getTotalAmount())
                    .currency("INR")
                    .keyId(razorpayKeyId)
                    .build();
        } catch (RazorpayException e) {
            throw new BadRequestException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Transactional
    public OrderResponse verifyPayment(PaymentVerifyRequest request) {
        Order order = orderService.findOrder(request.getOrderId());

        if (!order.getUser().getId().equals(securityUtil.getCurrentUser().getId())) {
            throw new BadRequestException("Unauthorized");
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (!isValid) {
                throw new BadRequestException("Invalid payment signature");
            }

            return orderService.markOrderPaid(request.getOrderId(), request.getRazorpayPaymentId());
        } catch (RazorpayException e) {
            throw new BadRequestException("Payment verification failed: " + e.getMessage());
        }
    }
}
