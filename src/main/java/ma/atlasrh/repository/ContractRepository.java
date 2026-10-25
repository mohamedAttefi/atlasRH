package ma.atlasrh.repository;

import ma.atlasrh.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByEmployeeIdAndActiveTrue(Long id);

    List<Contract> findByEmployeeIdOrderByStartDateDesc(Long id);
}
