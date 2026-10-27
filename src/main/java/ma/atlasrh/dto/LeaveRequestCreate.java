package ma.atlasrh.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record LeaveRequestCreate(@NotNull Long employeeId, @NotNull Long leaveTypeId, @NotNull LocalDate startDate,
                                 @NotNull LocalDate endDate, @NotBlank @Size(max = 500) String reason) {
}
