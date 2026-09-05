package me.ifmo.backend.bpm.delegate.enrollment;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.entities.Enrollment;
import me.ifmo.backend.services.EnrollmentService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.*;


@RequiredArgsConstructor

@Component
public class CreateEnrollmentDelegate implements JavaDelegate {
    private final EnrollmentService enrollmentService;

    @Override
    public void execute(DelegateExecution execution) {
        Long userId = (Long) execution.getVariable(TARGET_USER_ID);
        Long courseId = (Long) execution.getVariable(COURSE_ID);

        Enrollment enrollment = enrollmentService.createPendingEnrollment(userId, courseId);

        execution.setVariable(ENROLLMENT_ID, enrollment.getId());
    }
}
