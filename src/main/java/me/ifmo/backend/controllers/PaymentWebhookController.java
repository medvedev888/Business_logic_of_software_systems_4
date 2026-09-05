package me.ifmo.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.ifmo.backend.DTO.payment.PaymentWebhookRequest;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static me.ifmo.backend.bpm.ProcessVariables.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/webhook")
public class PaymentWebhookController {
    private final RuntimeService runtimeService;

    @PostMapping
    public ResponseEntity<Void> handlePaymentWebhook(@RequestBody @Valid PaymentWebhookRequest request) {
        String status = request.getStatus().trim().toUpperCase();

        runtimeService.createMessageCorrelation("PAYMENT_RESULT")
                .processInstanceVariableEquals(PROVIDER_PAYMENT_ID, request.getProviderPaymentId())
                .setVariable(PAYMENT_STATUS, status)
                .setVariable(FAILURE_REASON, request.getFailureReason())
                .correlate();

        return ResponseEntity.ok().build();
    }
}