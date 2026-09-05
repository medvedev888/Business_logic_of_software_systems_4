package me.ifmo.backend.bpm.delegate.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ifmo.backend.services.impl.PaymentTransactionService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.PROVIDER_PAYMENT_ID;


@RequiredArgsConstructor

@Slf4j
@Component
public class CompletePaidPaymentDelegate implements JavaDelegate {
    private final PaymentTransactionService paymentTransactionService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String providerPaymentId = (String) delegateExecution.getVariable(PROVIDER_PAYMENT_ID);
        paymentTransactionService.processPaidWebhook(providerPaymentId);

        log.info("Payment successfully processed as PAID: providerPaymentId={}, processInstanceId={}",
                providerPaymentId, delegateExecution.getProcessInstanceId());
    }
}
