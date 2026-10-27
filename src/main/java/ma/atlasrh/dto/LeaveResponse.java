package ma.atlasrh.dto;

import ma.atlasrh.entity.LeaveStatus;

import java.time.LocalDate;

public record LeaveResponse(Long id, Long employeeId, String leaveType, LocalDate startDate, LocalDate endDate,
                            int requestedDays, String reason, LeaveStatus status, String comment) {
}
