package com.ecom.payment.service;

import com.ecom.payment.entity.Payment;
import com.ecom.payment.entity.PaymentStatus;
import com.ecom.payment.event.PaymentSuccessEvent;
import com.ecom.payment.messaging.PaymentEventProducer;
import com.ecom.payment.repository.PaymentRepository;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentEventProducer paymentEventProducer) {

        this.paymentRepository = paymentRepository;
        this.paymentEventProducer = paymentEventProducer;
    }

    @Transactional
    public Payment processPayment(
            Long orderId,
            BigDecimal amount) {

        var existing = paymentRepository.findByOrderId(orderId);

        if (existing.isPresent()) {
            return existing.get();
        }

        Payment payment = new Payment(
                orderId,
                amount);

        payment.setStatus(PaymentStatus.SUCCESS);

        Payment saved = paymentRepository.save(payment);

        paymentEventProducer.publishSuccess(
                new PaymentSuccessEvent(
                        saved.getOrderId(),
                        saved.getId(),
                        saved.getAmount()));

        return saved;
    }
}
