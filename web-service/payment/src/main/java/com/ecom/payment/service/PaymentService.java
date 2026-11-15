package com.ecom.payment.service;

import com.ecom.payment.dto.PaymentRequest;
import com.ecom.payment.entity.Payment;
import com.ecom.payment.entity.PaymentStatus;
import com.ecom.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {

        /*
         * Idempotency:
         * If payment for this order already exists,
         * don't create another payment.
         */
        var existing = paymentRepository
                .findByOrderId(request.orderId());

        if (existing.isPresent()) {
            return existing.get();
        }

        Payment payment = new Payment(
                request.orderId(),
                request.amount());

        /*
         * Simulate successful payment for now.
         * Later we'll add failure scenarios.
         */
        payment.setStatus(PaymentStatus.SUCCESS);

        return paymentRepository.save(payment);
    }
}
