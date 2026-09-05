package me.ifmo.backend.bpm.delegate.payment;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.services.PaymentService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.PAYMENT_EXPIRED;
import static me.ifmo.backend.bpm.ProcessVariables.PAYMENT_ID;


@RequiredArgsConstructor

@Component
public class CheckPaymentExpiredDelegate implements JavaDelegate {
    private final PaymentService paymentService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Long paymentId = (Long) delegateExecution.getVariable(PAYMENT_ID);

        delegateExecution.setVariable(PAYMENT_EXPIRED, paymentService.isPaymentExpired(paymentId));
    }
}
