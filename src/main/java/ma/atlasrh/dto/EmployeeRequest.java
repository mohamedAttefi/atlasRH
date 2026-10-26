package ma.atlasrh.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmployeeRequest(@NotBlank String matricule, @NotBlank String firstName, @NotBlank String lastName,
                              @Email @NotBlank String email, @NotBlank String position, @NotNull LocalDate hireDate,
                              @NotNull Long departmentId, Long managerId) {
}
