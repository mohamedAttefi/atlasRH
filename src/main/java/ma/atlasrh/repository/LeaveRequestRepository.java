package ma.atlasrh.repository;

import ma.atlasrh.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeId(Long id);

    List<LeaveRequest> findByEmployeeManagerIdAndStatus(Long id, LeaveStatus status);

    boolean existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long id, List<LeaveStatus> s, java.time.LocalDate end, java.time.LocalDate start);

    List<LeaveRequest> findByEmployeeIdAndLeaveTypeIdAndStatusAndStartDateBetween(Long employee, Long type, LeaveStatus status, java.time.LocalDate start, java.time.LocalDate end);
}
