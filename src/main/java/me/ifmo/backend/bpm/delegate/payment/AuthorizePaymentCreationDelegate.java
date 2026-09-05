package me.ifmo.backend.bpm.delegate.payment;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.bpm.security.ProcessAuthorizationService;
import me.ifmo.backend.exceptions.BusinessException;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.INITIATOR;


@RequiredArgsConstructor

@Component
public class AuthorizePaymentCreationDelegate implements JavaDelegate {
    private final ProcessAuthorizationService authorizationService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String initiator = (String) delegateExecution.getVariable(INITIATOR);

        if (initiator == null || initiator.isBlank()) {
            throw new BusinessException("Process initiator is missing");
        }

        authorizationService.requirePrivilege(
                initiator,
                "PAYMENT_CREATE"
        );
    }
}
