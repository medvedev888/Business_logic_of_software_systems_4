package me.ifmo.backend.bpm.delegate.payment;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.services.impl.PaymentTransactionService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.FAILURE_REASON;
import static me.ifmo.backend.bpm.ProcessVariables.PROVIDER_PAYMENT_ID;


@RequiredArgsConstructor

@Component
public class CompleteFailedPaymentDelegate implements JavaDelegate {
    private final PaymentTransactionService paymentTransactionService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String providerPaymentId = (String) delegateExecution.getVariable(PROVIDER_PAYMENT_ID);
        String failureReason = (String) delegateExecution.getVariable(FAILURE_REASON);

        paymentTransactionService.processFailedWebhook(providerPaymentId, failureReason);
    }
}

