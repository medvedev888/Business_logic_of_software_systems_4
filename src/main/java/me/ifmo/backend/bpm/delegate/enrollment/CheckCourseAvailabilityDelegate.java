package me.ifmo.backend.bpm.delegate.enrollment;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.services.CourseService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static me.ifmo.backend.bpm.ProcessVariables.COURSE_AVAILABLE;
import static me.ifmo.backend.bpm.ProcessVariables.COURSE_ID;


@RequiredArgsConstructor

@Component
public class CheckCourseAvailabilityDelegate implements JavaDelegate {
    private final CourseService courseService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        Long courseId = (Long) delegateExecution.getVariable(COURSE_ID);

        boolean available = courseService.isCourseAvailableForEnrollment(courseId);

        delegateExecution.setVariable(COURSE_AVAILABLE, available);
    }
}
