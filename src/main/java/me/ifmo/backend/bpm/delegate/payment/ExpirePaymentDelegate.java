package me.ifmo.backend.bpm.delegate.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.ifmo.backend.services.impl.PaymentTransactionService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.PAYMENT_ID;


@RequiredArgsConstructor

@Slf4j
@Component
public class ExpirePaymentDelegate implements JavaDelegate {
    private final PaymentTransactionService paymentTransactionService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Long paymentId = (Long) delegateExecution.getVariable(PAYMENT_ID);
        paymentTransactionService.expirePayment(paymentId);

        log.info("Payment successfully processed as EXPIRED: paymentId={}, processInstanceId={}",
                paymentId, delegateExecution.getProcessInstanceId());
    }
}
