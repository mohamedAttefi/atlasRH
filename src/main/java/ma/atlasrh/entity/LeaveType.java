package ma.atlasrh.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(name = "days_per_year", nullable = false)
    private int daysPerYear;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDaysPerYear() {
        return daysPerYear;
    }
}
