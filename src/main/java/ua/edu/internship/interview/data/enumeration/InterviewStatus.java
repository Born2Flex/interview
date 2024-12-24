package ua.edu.internship.interview.data.enumeration;

import lombok.Getter;
import java.util.EnumSet;
import java.util.Set;

@Getter
public enum InterviewStatus {
    PLANNED,
    ACTIVE,
    COMPLETED,
    CANCELLED;

    private Set<InterviewStatus> transitions;

    static {
        PLANNED.transitions = EnumSet.of(ACTIVE, CANCELLED);
        ACTIVE.transitions = EnumSet.of(COMPLETED, CANCELLED);
        COMPLETED.transitions = EnumSet.noneOf(InterviewStatus.class);
        CANCELLED.transitions = EnumSet.noneOf(InterviewStatus.class);
    }
}
