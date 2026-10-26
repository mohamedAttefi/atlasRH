package ma.atlasrh.dto;

import ma.atlasrh.entity.ContractType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractResponse(Long id, Long employeeId, ContractType type, LocalDate startDate, LocalDate endDate,
                               BigDecimal grossSalary, boolean active) {
}
