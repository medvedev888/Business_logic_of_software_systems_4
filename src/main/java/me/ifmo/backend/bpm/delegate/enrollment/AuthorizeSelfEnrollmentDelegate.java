package me.ifmo.backend.bpm.delegate.enrollment;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.bpm.security.ProcessAuthorizationService;
import me.ifmo.backend.entities.User;
import me.ifmo.backend.exceptions.BusinessException;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.INITIATOR;
import static me.ifmo.backend.bpm.ProcessVariables.TARGET_USER_ID;


@RequiredArgsConstructor

@Component
public class AuthorizeSelfEnrollmentDelegate implements JavaDelegate {
    private final ProcessAuthorizationService processAuthorizationService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String initiator = (String) delegateExecution.getVariable(INITIATOR);

        if (initiator == null || initiator.isBlank()) {
            throw new BusinessException("Process initiator is missing");
        }

        processAuthorizationService.requirePrivilege(
                initiator,
                "ENROLLMENT_CREATE"
        );

        User user = processAuthorizationService.getUserByCamundaUserId(initiator);

        delegateExecution.setVariable(TARGET_USER_ID, user.getId());
    }
}
