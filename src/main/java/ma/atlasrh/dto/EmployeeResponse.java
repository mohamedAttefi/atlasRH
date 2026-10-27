package ma.atlasrh.dto;

import java.time.LocalDate;

public record EmployeeResponse(Long id, String matricule, String firstName, String lastName, String email,
                               String position, LocalDate hireDate, Long departmentId, String departmentName,
                               Long managerId) {
}
