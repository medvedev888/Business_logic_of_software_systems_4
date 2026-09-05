package me.ifmo.backend.bpm.delegate.payment;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.entities.Payment;
import me.ifmo.backend.services.impl.PaymentTransactionService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.*;


@RequiredArgsConstructor

@Component
public class CreatePaymentDelegate implements JavaDelegate {
    private final PaymentTransactionService paymentTransactionService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Long enrollmentId = (Long) delegateExecution.getVariable(ENROLLMENT_ID);

        Payment payment = paymentTransactionService.createPayment(enrollmentId);

        delegateExecution.setVariable(PAYMENT_ID, payment.getId());

        delegateExecution.setVariable(
                PROVIDER_PAYMENT_ID,
                payment.getProviderPaymentId()
        );

        delegateExecution.setVariable(
                PAYMENT_URL,
                payment.getPaymentUrl()
        );
    }
}