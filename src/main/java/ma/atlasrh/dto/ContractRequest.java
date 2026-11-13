package ma.atlasrh.dto;

import jakarta.validation.constraints.*;
import ma.atlasrh.entity.ContractType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractRequest(@NotNull Long employeeId, @NotNull ContractType type, @NotNull LocalDate startDate,
                              LocalDate endDate, @NotBlank @DecimalMin("0.01") BigDecimal grossSalary) {
}
