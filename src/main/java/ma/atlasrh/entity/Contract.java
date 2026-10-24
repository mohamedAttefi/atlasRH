package ma.atlasrh.entity;

import java.math.BigDecimal;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "contracts")
public class Contract {
    @Column(nullable = false)
    private boolean      active = true;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long         id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee     employee;
    @Enumerated(EnumType.STRING)
    @Column(
        name     = "contract_type",
        nullable = false
    )
    private ContractType type;
    @Column(
        name     = "start_date",
        nullable = false
    )
    private LocalDate    startDate;
    @Column(name = "end_date")
    private LocalDate    endDate;
    @Column(
        name      = "gross_salary",
        nullable  = false,
        precision = 12,
        scale     = 2
    )
    private BigDecimal   grossSalary;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean v) {
        active = v;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee e) {
        employee = e;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate v) {
        endDate = v;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal v) {
        grossSalary = v;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate v) {
        startDate = v;
    }

    public ContractType getType() {
        return type;
    }

    public void setType(ContractType v) {
        type = v;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
